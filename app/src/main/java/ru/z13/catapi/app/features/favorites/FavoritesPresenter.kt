package ru.z13.catapi.app.features.favorites

import io.reactivex.subjects.PublishSubject
import ru.z13.catapi.app.data.State
import ru.z13.catapi.app.domain.DomainEvents
import ru.z13.catapi.app.features.base.BasePresenter
import ru.z13.catapi.app.features.favorites.items.FavoriteCatItem
import ru.z13.catapi.app.interactors.CatsInteractor
import ru.z13.catapi.app.models.Page
import ru.z13.catapi.app.shedulers.SchedulersFacade
import ru.z13.catapi.app.utils.diff.YFastAdapterDiffUtil
import javax.inject.Inject

/**
 * @author Yura Fedorchenko (z-13.github.io)
 */
class FavoritesPresenter @Inject constructor(private val schedulersFacade: SchedulersFacade,
                                             private val interactor: CatsInteractor,
                                             private val domainEvents: DomainEvents): BasePresenter<FavoritesView>() {

    private var items = linkedSetOf<FavoriteCatItem>()
    private val page = Page(25)
    private val catsSubject: PublishSubject<Page> = PublishSubject.create()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        addSubscription(domainEvents.subscribeOnAppEvent()
            .subscribe{
                if(it == DomainEvents.AppEvent.CAT_FAVORITES_UPDATED){
                    loadCurrentPage()
                }
            })

        addSubscription(catsSubject
            .startWith(page)
            .doOnNext { viewState.showProgress() }
            .observeOn(schedulersFacade.io())
            .flatMap { newPage ->
                interactor.loadFavoriteCats(newPage)
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
}