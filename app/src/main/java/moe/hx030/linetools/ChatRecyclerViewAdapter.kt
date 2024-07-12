package moe.hx030.linetools

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import moe.hx030.linetools.data.Chat

import moe.hx030.linetools.databinding.FragmentChatEntryBinding

class ChatRecyclerViewAdapter(
    private val values: List<Chat>
) : RecyclerView.Adapter<ChatRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = FragmentChatEntryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        view.root.setOnClickListener {
            val chat_id = view.detail.text
            Log.d("030-clk", "id: $chat_id")
            Utils.showToast(chat_id.toString())
        }
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = "${item.title}\n${item.last_created_time}"
        holder.contentView.text = item.last_message
        holder.detailView.text = item.id
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentChatEntryBinding) : RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemNumber
        val contentView: TextView = binding.content
        val detailView: TextView = binding.detail

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

}