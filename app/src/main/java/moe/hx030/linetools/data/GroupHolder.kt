package moe.hx030.linetools.data

import moe.hx030.linetools.Storage
import java.util.ArrayList
import java.util.HashMap

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 * TODO: Replace all uses of this class before publishing your app.
 */
object GroupHolder {

    /**
     * An array of sample (placeholder) items.
     */
    val ITEMS: MutableList<GroupItem> = ArrayList()

    /**
     * A map of sample (placeholder) items, by ID.
     */
    val ITEM_MAP: MutableMap<String, GroupItem> = HashMap()

    private val COUNT = 25

    init {
        // Add some sample items.
        var groups = Storage.listGroups()
        if (groups == null || groups.isEmpty()) {
            for (i in 1..COUNT) {
                addItem(createItem(i))
            }
        } else {
            for (grp: Group in groups)
                addItem(GroupItem((1 + groups.indexOf(grp)).toString(), grp.name, grp.id))
        }
    }

    private fun addItem(item: GroupItem) {
        ITEMS.add(item)
        ITEM_MAP.put(item.id, item)
    }

    private fun createItem(position: Int): GroupItem {
        return GroupItem(position.toString(), "Item " + position, makeDetails(position))
    }

    private fun makeDetails(position: Int): String {
        val builder = StringBuilder()
        builder.append("Details about Item: ").append(position)
        for (i in 0..position - 1) {
            builder.append("\nMore details information here.")
        }
        return builder.toString()
    }

    /**
     * A placeholder item representing a piece of content.
     */
    data class GroupItem(val id: String, val content: String, val details: String) {
        override fun toString(): String = content
    }
}