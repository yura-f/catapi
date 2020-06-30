package ru.z13.catapi.app.features.home

import io.reactivex.subjects.PublishSubject
import ru.z13.catapi.app.data.State
import ru.z13.catapi.app.features.base.BasePresenter
import ru.z13.catapi.app.features.home.items.CatItem
import ru.z13.catapi.app.interactors.CatsInteractor
import ru.z13.catapi.app.models.CatData
import ru.z13.catapi.app.models.Page
import ru.z13.catapi.app.shedulers.SchedulersFacade
import ru.z13.catapi.app.utils.diff.YFastAdapterDiffUtil
import javax.inject.Inject


/**
 * @author Yura Fedorchenko (z-13.github.io)
 */
class HomePresenter @Inject constructor(private val schedulersFacade: SchedulersFacade,
                                        private val interactor: CatsInteractor): BasePresenter<HomeView>() {

    private var items = linkedSetOf<CatItem>()
    private val page = Page(25)
    private val catsSubject: PublishSubject<Page> = PublishSubject.create()
    private val favoriteSubject: PublishSubject<Pair<Int, Boolean>> = PublishSubject.create()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        addSubscription(catsSubject
            .startWith(page)
            .doOnNext { viewState.showProgress() }
            .observeOn(schedulersFacade.io())
            .flatMap { newPage ->
                interactor.loadCats(newPage)
                    .map {
                        val fullItems = items + it.value
                        Pair(it, YFastAdapterDiffUtil.calculateDiff(items.toList(), fullItems.toList()))
                    }
            }
            .observeOn(schedulersFacade.ui())
            .subscribe({ pair ->
                val data = pair.first
                val newItems = data.value
                val diffResult = pair.second

                when(data.state){
                    is State.Success -> {
                        viewState.hideProgress()
                    }
                    is State.Error -> {
                        viewState.hideProgress()
                        viewState.showError(data.state.error)
                    }
                }

                items.addAll(newItems)
                viewState.setData(items = items.toList(), result = diffResult)


            },{
                viewState.showError(it)
            })
        )

        addSubscription(favoriteSubject
            .observeOn(schedulersFacade.io())
            .flatMap { pair ->
                val catId = pair.first
                val isAdd = pair.second

                if(isAdd) interactor.addFavorite(catId)
                else interactor.removeFavorite(catId)
            }
            .observeOn(schedulersFacade.ui())
            .subscribe({
                //OK
            },{
                viewState.showError(it)
            })
        )
    }

    fun goToNextPage(){
        if(items.size >= page.offset + page.limit) {
            page.next()

            catsSubject.onNext(page)
        }else{
            viewState.hideProgress()
        }
    }

    fun loadCurrentPage() {
        catsSubject.onNext(page)
    }

    fun onClickFavoriteBtn(data: CatData?, isFavorite: Boolean) {
        data?.let {
            favoriteSubject.onNext(Pair(it.id, isFavorite))
        }
    }
}