package ru.z13.catapi.app.features.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.adapters.GenericFastItemAdapter
import com.mikepenz.fastadapter.adapters.GenericItemAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mikepenz.fastadapter.scroll.EndlessRecyclerOnScrollListener
import com.mikepenz.fastadapter.ui.items.ProgressItem
import kotlinx.android.synthetic.main.fragment_home.*
import moxy.ktx.moxyPresenter
import moxy.viewstate.strategy.alias.AddToEndSingle
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnNeverAskAgain
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions
import ru.z13.catapi.app.App
import ru.z13.catapi.app.R
import ru.z13.catapi.app.features.base.BaseFragment
import ru.z13.catapi.app.features.base.BaseView
import ru.z13.catapi.app.features.home.items.CatItem
import ru.z13.catapi.app.utils.diff.YFastAdapterDiffUtil
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Provider


/**
 * @author Yura Fedorchenko (z-13.github.io)
 */

@AddToEndSingle
interface HomeView: BaseView {
    fun setData(items: List<CatItem>, result: DiffUtil.DiffResult)
}

@RuntimePermissions
class HomeFragment: BaseFragment(),
    HomeView {
    override val layoutRes: Int =  R.layout.fragment_home

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    private lateinit var catsAdapter: GenericFastItemAdapter
    private lateinit var footerAdapter: GenericItemAdapter
    private lateinit var endlessRecyclerOnScrollListener: EndlessRecyclerOnScrollListener

    @Inject
    lateinit var presenterProvider: Provider<HomePresenter>
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

        catsAdapter.addEventHook(object : ClickEventHook<CatItem>() {
            override fun onBindMany(viewHolder: RecyclerView.ViewHolder): List<View>? {
                return if (viewHolder is CatItem.ViewHolder) {
                    listOf(viewHolder.likeBtn, viewHolder.saveBtn)
                } else {
                    return super.onBindMany(viewHolder)
                }
            }

            override fun onClick(v: View, position: Int, fastAdapter: FastAdapter<CatItem>, item: CatItem) {
                when(v.id){
                    R.id.likeBtn -> {
                        item.isFavorite = !item.isFavorite
                        presenter.onClickFavoriteBtn(item.data, item.isFavorite)
                    }

                    R.id.saveBtn -> {
                        item.data?.let {
                            saveImageWithPermissionCheck(it.url, it.labelId)
                        }
                    }
                }

            }
        })

        val staggeredGrid = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        staggeredGrid.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        rvCats.layoutManager = staggeredGrid
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

    override fun setData(items: List<CatItem>, result: DiffUtil.DiffResult) {
        if(catsAdapter.adapterItems.isEmpty()){
            catsAdapter.setNewList(items)
        }else{
            YFastAdapterDiffUtil[catsAdapter.itemAdapter, items] = result
        }

        checkEmptyFeed()
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun saveImage(url: String, labelId: String) {
        Glide.with(this)
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {

                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    saveImage(resource, labelId)
                }
            })
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun onStorageDenied() {
        context?.let {
            Toast.makeText(it, R.string.permission_write_denied, Toast.LENGTH_SHORT).show()
        }

    }

    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun onStorageNeverAskAgain() {
        context?.let {
            Toast.makeText(it, R.string.permission_storage_never_ask_again, Toast.LENGTH_SHORT).show()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    private fun saveImage(image: Bitmap, labelId: String): String? {
        var savedImagePath: String? = null
        val imageFileName = "CAT_".plus(labelId).plus(".jpg")
        val storageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString())
        var success = true

        if (!storageDir.exists()) {
            success = storageDir.mkdirs()
        }
        if (success) {
            val imageFile = File(storageDir, imageFileName)
            savedImagePath = imageFile.absolutePath
            try {
                val fOut: OutputStream = FileOutputStream(imageFile)
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                fOut.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            galleryAddPic(savedImagePath)

            context?.let {
                Toast.makeText(it, R.string.image_has_been_saved, Toast.LENGTH_SHORT).show()
            }
        }
        return savedImagePath
    }

    private fun galleryAddPic(imagePath: String) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val f = File(imagePath)
        val contentUri: Uri = Uri.fromFile(f)
        mediaScanIntent.data = contentUri

        activity?.sendBroadcast(mediaScanIntent)
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