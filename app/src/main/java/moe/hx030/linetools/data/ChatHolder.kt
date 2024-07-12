package moe.hx030.linetools.data

import moe.hx030.linetools.Storage
import java.util.ArrayList
import java.util.HashMap

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 */
object ChatHolder {

    /**
     * An array of sample (placeholder) items.
     */
    val ITEMS: MutableList<Chat> = ArrayList()

    /**
     * A map of sample (placeholder) items, by ID.
     */
    val ITEM_MAP: MutableMap<String, Chat> = HashMap()

    init {
        val chats = Storage.listChats()
        if (chats != null) {
            ITEMS.addAll(chats)
        }
    }
}