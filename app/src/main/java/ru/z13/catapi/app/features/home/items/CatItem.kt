package ru.z13.catapi.app.features.home.items

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import ru.z13.catapi.app.R
import ru.z13.catapi.app.models.CatData

/**
 * @author Yura F (yura-f.github.io)
 */
open class CatItem: AbstractItem<CatItem.ViewHolder>() {

    var data: CatData? = null
    var isFavorite: Boolean = false

    override val type: Int = R.id.cat_item_id
    override val layoutRes: Int = R.layout.item_cat

    fun withData(data: CatData): CatItem {
        this.data = data
        return this
    }

    fun setFavorite(value: Boolean): CatItem {
        this.isFavorite = value
        return this
    }

    override var identifier: Long
        get() = data?.id?.toLong() ?: super.identifier
        set(value) {}

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    class ViewHolder(view: View) : FastAdapter.ViewHolder<CatItem>(view) {
        val likeBtn: CheckBox = view.findViewById(R.id.likeBtn)
        val saveBtn: ImageButton = view.findViewById(R.id.saveBtn)
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val gradientView: View = view.findViewById(R.id.gradientView)

        override fun bindView(item: CatItem, payloads: List<Any>) {
            val context = itemView.context

            gradientView.visibility = View.VISIBLE
            likeBtn.visibility = View.VISIBLE

            item.data?.let { data ->
                likeBtn.isChecked = item.isFavorite

                val circularProgress = CircularProgressDrawable(context)
                circularProgress.strokeWidth = 5f
                circularProgress.centerRadius = 24f
                circularProgress.setColorSchemeColors(R.color.colorPrimary, R.color.colorAccent)
                circularProgress.start()

                Glide.with(context)
                    .load(data.url)
                    .fitCenter()
                    .placeholder(circularProgress)
                    .error(R.drawable.bg_placeholder_error)
                    .listener(object: RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            saveBtn.visibility = View.GONE
                            return false
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            saveBtn.visibility = View.VISIBLE
                            return false
                        }
                    })
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageView)
            }
        }

        override fun unbindView(item: CatItem) {
            saveBtn.visibility = View.GONE
            gradientView.visibility = View.GONE

            Glide.with(itemView.context).clear(imageView)
            imageView.setImageDrawable(null)
        }
    }
}