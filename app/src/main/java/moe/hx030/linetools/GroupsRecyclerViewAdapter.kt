package moe.hx030.linetools

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView

import moe.hx030.linetools.data.GroupHolder.GroupItem
import moe.hx030.linetools.databinding.FragmentChatEntryBinding

/**
 * [RecyclerView.Adapter] that can display a [GroupItem].
 * TODO: Replace the implementation with code for your data type.
 */
class GroupsRecyclerViewAdapter(
    private val values: List<GroupItem>
) : RecyclerView.Adapter<GroupsRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentChatEntryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = item.id
        holder.contentView.text = item.content
        holder.detailView.text = item.details
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentChatEntryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemNumber
        val contentView: TextView = binding.content
        val detailView: TextView = binding.detail

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

}