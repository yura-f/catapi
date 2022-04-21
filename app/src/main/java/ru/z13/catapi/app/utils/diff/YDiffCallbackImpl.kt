package ru.z13.catapi.app.utils.diff

import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.diff.DiffCallback

/**
 * https://github.com/mikepenz/FastAdapter/blob/8fdccfdff82bb3ed0b9f0cf62c9817c34cea5c49/library-extensions-diff/src/main/java/com/mikepenz/fastadapter/diff/DiffCallbackImpl.kt
 *
 * @author Yura F (yura-f.github.io)
 */
internal class YDiffCallbackImpl<Item : IItem<*>> : DiffCallback<Item> {
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem.identifier == newItem.identifier
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: Item, oldItemPosition: Int, newItem: Item, newItemPosition: Int): Any? {
        return null
    }
}