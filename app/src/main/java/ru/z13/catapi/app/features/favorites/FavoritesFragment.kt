package ru.z13.catapi.app.features.favorites

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.adapters.GenericFastItemAdapter
import com.mikepenz.fastadapter.adapters.GenericItemAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.scroll.EndlessRecyclerOnScrollListener
import com.mikepenz.fastadapter.ui.items.ProgressItem
import kotlinx.android.synthetic.main.fragment_favorites.*
import moxy.ktx.moxyPresenter
import moxy.viewstate.strategy.alias.AddToEndSingle
import ru.z13.catapi.app.App
import ru.z13.catapi.app.R
import ru.z13.catapi.app.features.base.BaseFragment
import ru.z13.catapi.app.features.base.BaseView
import ru.z13.catapi.app.features.favorites.items.FavoriteCatItem
import ru.z13.catapi.app.utils.diff.YFastAdapterDiffUtil
import javax.inject.Inject
import javax.inject.Provider

/**
 * @author Yura F (yura-f.github.io)
 */

@AddToEndSingle
interface FavoritesView: BaseView {
    fun setData(items: List<FavoriteCatItem>, result: DiffUtil.DiffResult)
}

class FavoritesFragment: BaseFragment(), FavoritesView {
    override val layoutRes: Int =  R.layout.fragment_favorites

    companion object {
        fun newInstance(): FavoritesFragment {
            return FavoritesFragment()
        }
    }

    private lateinit var catsAdapter: GenericFastItemAdapter
    private lateinit var footerAdapter: GenericItemAdapter
    private lateinit var endlessRecyclerOnScrollListener: EndlessRecyclerOnScrollListener

    @Inject
    lateinit var presenterProvider: Provider<FavoritesPresenter>
    private val presenter by moxyPresenter { presenterProvider.get() }

    override fun onAttach(context: Context) {
        App.appComponent.inject(this)

        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initRv()

        swipeRefreshLayout.setOnRefreshListener { presenter.loadCurrentPage() }
    }

    private fun initRv() {
        catsAdapter = FastItemAdapter()
        footerAdapter = ItemAdapter.items()

        catsAdapter.addAdapter(1, footerAdapter)

        val layoutManager = LinearLayoutManager(activity)
        rvCats.layoutManager = layoutManager
        rvCats.adapter = catsAdapter

        endlessRecyclerOnScrollListener = object : EndlessRecyclerOnScrollListener(footerAdapter) {
            override fun onLoadMore(currentPage: Int) {
                rvCats.post {
                    footerAdapter.clear()

                    val progressItem = ProgressItem()
                    progressItem.isEnabled = false

                    footerAdapter.add(progressItem)
                }

                val handler = Handler()
                handler.postDelayed({
                    if(currentPage > 0) {
                        presenter.goToNextPage()
                    }else{
                        presenter.loadCurrentPage()
                    }
                }, 1000)
            }
        }
        rvCats.addOnScrollListener(endlessRecyclerOnScrollListener)
    }

    override fun setData(items: List<FavoriteCatItem>, result: DiffUtil.DiffResult) {
        if(catsAdapter.adapterItems.isEmpty()){
            catsAdapter.setNewList(items)
        }else{
            YFastAdapterDiffUtil[catsAdapter.itemAdapter, items] = result
        }

        checkEmptyFeed()
    }

    private fun checkEmptyFeed() {
        emptyText.visibility = if (catsAdapter.itemCount > 0) {
            View.GONE
        }else {
            View.VISIBLE
        }
    }

    override fun showProgress() {
    }

    override fun hideProgress() {
        swipeRefreshLayout.isRefreshing = false

        footerAdapter.clear()
        checkEmptyFeed()
    }

    override fun onDestroyView() {
        rvCats.removeOnScrollListener(endlessRecyclerOnScrollListener)

        super.onDestroyView()
    }
}