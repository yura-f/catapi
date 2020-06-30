package ru.z13.catapi.app.features.favorites.items

import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import ru.z13.catapi.app.R
import ru.z13.catapi.app.models.CatData

/**
 * @author Yura Fedorchenko (z-13.github.io)
 */
open class FavoriteCatItem: AbstractItem<FavoriteCatItem.ViewHolder>() {

    var data: CatData? = null
    var isFavorite: Boolean = false

    override val type: Int = R.id.cat_item_id
    override val layoutRes: Int = R.layout.item_cat

    fun withData(data: CatData): FavoriteCatItem {
        this.data = data
        return this
    }

    fun setFavorite(value: Boolean): FavoriteCatItem {
        this.isFavorite = value
        return this
    }

    override var identifier: Long
        get() = data?.id?.toLong() ?: super.identifier
        set(value) {}

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    class ViewHolder(view: View) : FastAdapter.ViewHolder<FavoriteCatItem>(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)

        override fun bindView(item: FavoriteCatItem, payloads: List<Any>) {
            val context = itemView.context

            item.data?.let { data ->
                val circularProgress = CircularProgressDrawable(context)
                circularProgress.strokeWidth = 5f
                circularProgress.centerRadius = 24f
                circularProgress.setColorSchemeColors(R.color.colorPrimary)
                circularProgress.start()

                Glide.with(context)
                    .load(data.url)
                    .fitCenter()
                    .placeholder(circularProgress)
                    .error(R.drawable.bg_placeholder_error)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageView)
            }
        }

        override fun unbindView(item: FavoriteCatItem) {
            Glide.with(itemView.context).clear(imageView)
            imageView.setImageDrawable(null)
        }
    }
}