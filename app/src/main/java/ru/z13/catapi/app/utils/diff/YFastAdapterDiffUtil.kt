package ru.z13.catapi.app.utils.diff

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.IAdapterExtension
import com.mikepenz.fastadapter.adapters.ModelAdapter
import com.mikepenz.fastadapter.diff.DiffCallback
import com.mikepenz.fastadapter.utils.ComparableItemListImpl
import java.util.*

/**
 * https://github.com/mikepenz/FastAdapter/blob/a0d22ee7a483cbbda46038e04f387f81971666d5/library/src/main/java/com/mikepenz/fastadapter/commons/utils/FastAdapterDiffUtil.java
 *
 * @author Yura F (yura-f.github.io)
 */

object YFastAdapterDiffUtil {
    private fun <A : ModelAdapter<Model, Item>, Model, Item : GenericItem> setItems(adapter: A, items: List<Item>): A {
        if (adapter.isUseIdDistributor) {
            adapter.idDistributor.checkIds(items)
        }

        // The FastAdapterDiffUtil does not handle expanded items. Call collapse if possible
        collapseIfPossible(adapter.fastAdapter)

        //if we have a comparator then sort
        if (adapter.itemList is ComparableItemListImpl<*>) {
            Collections.sort(items, (adapter.itemList as ComparableItemListImpl<Item>).comparator)
        }

        //remember the old items
        val adapterItems = adapter.adapterItems

        //make sure the new items list is not a reference of the already mItems list
        if (items !== adapterItems) {
            //remove all previous items
            if (adapterItems.isNotEmpty()) {
                adapterItems.clear()
            }

            //add all new items to the list
            adapterItems.addAll(items)
        }

        return adapter
    }

    fun <Item : GenericItem> calculateDiff(oldItems: List<Item>,
                                           newItems: List<Item>,
                                           callback: DiffCallback<Item> = YDiffCallbackImpl(),
                                           detectMoves: Boolean = true): DiffUtil.DiffResult {
        return DiffUtil.calculateDiff(FastAdapterCallback(oldItems, newItems, callback), detectMoves)
    }

    /** Uses Reflection to collapse all items if this adapter uses expandable items */
    private fun <Item : GenericItem> collapseIfPossible(fastAdapter: FastAdapter<Item>?) {
        fastAdapter ?: return
        try {
            val c: Class<IAdapterExtension<Item>> = Class.forName("com.mikepenz.fastadapter.expandable.ExpandableExtension") as Class<IAdapterExtension<Item>>
            val extension = fastAdapter.getExtension(c)
            if (extension != null) {
                val method = extension.javaClass.getMethod("collapse")
                method.invoke(extension)
            }
        } catch (ignored: Exception) {
            //
        }
    }

    /**
     * Dispatches a [DiffUtil.DiffResult] to the given Adapter.
     *
     * @param adapter the adapter to dispatch the updates to
     * @param result  the computed [DiffUtil.DiffResult]
     * @return the adapter to allow chaining
     */
    operator fun <A : ModelAdapter<Model, Item>, Model, Item : GenericItem> set(adapter: A, result: DiffUtil.DiffResult): A {
        result.dispatchUpdatesTo(FastAdapterListUpdateCallback(adapter))
        return adapter
    }

    operator fun <A : ModelAdapter<Model, Item>, Model, Item : GenericItem> set(adapter: A, items: List<Item>): A {
        return setItems(adapter, items)
    }

    operator fun <A : ModelAdapter<Model, Item>, Model, Item : GenericItem> set(adapter: A, items: List<Item>, result: DiffUtil.DiffResult): A {
        setItems(adapter, items)

        return set(adapter, result)
    }

    /**
     * Convenient implementation for the [DiffUtil.Callback] to simplify difference calculation using [FastAdapter] items.
     *
     * @param Item the item type in the adapter
     */
    private class FastAdapterCallback<Item : GenericItem> internal constructor(private val oldItems: List<Item>, private val newItems: List<Item>, private val callback: DiffCallback<Item>) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldItems.size
        }

        override fun getNewListSize(): Int {
            return newItems.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return callback.areItemsTheSame(oldItems[oldItemPosition], newItems[newItemPosition])
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return callback.areContentsTheSame(oldItems[oldItemPosition], newItems[newItemPosition])
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            val result = callback.getChangePayload(oldItems[oldItemPosition], oldItemPosition, newItems[newItemPosition], newItemPosition)
            return result ?: super.getChangePayload(oldItemPosition, newItemPosition)
        }
    }

    /**
     * Default implementation of the [ListUpdateCallback] to apply changes to the adapter and notify about the changes.
     */
    private class FastAdapterListUpdateCallback<A : ModelAdapter<Model, Item>, Model, Item : GenericItem> internal constructor(private val adapter: A) : ListUpdateCallback {

        private val preItemCountByOrder: Int
            get() = adapter.fastAdapter?.getPreItemCountByOrder(adapter.order) ?: 0

        override fun onInserted(position: Int, count: Int) {
            adapter.fastAdapter?.notifyAdapterItemRangeInserted(preItemCountByOrder + position, count)
        }

        override fun onRemoved(position: Int, count: Int) {
            adapter.fastAdapter?.notifyAdapterItemRangeRemoved(preItemCountByOrder + position, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            adapter.fastAdapter?.notifyAdapterItemMoved(preItemCountByOrder + fromPosition, toPosition)
        }

        override fun onChanged(position: Int, count: Int, payload: Any?) {
            adapter.fastAdapter?.notifyAdapterItemRangeChanged(preItemCountByOrder + position, count, payload)
        }
    }
}
