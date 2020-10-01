package com.example.sitam.ui.chat.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sitam.R
import com.example.sitam.models.pesan.DataPesan
import kotlinx.android.synthetic.main.chat_item.view.*

class PesanMhsAdapter: RecyclerView.Adapter<PesanMhsAdapter.PesanViewHolder>(){
    inner class PesanViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)

    private val differCallBack = object : DiffUtil.ItemCallback<DataPesan>(){
        override fun areItemsTheSame(oldItem: DataPesan, newItem: DataPesan): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DataPesan, newItem: DataPesan): Boolean {
            return oldItem == newItem
        }
    }

    val differ= AsyncListDiffer(this, differCallBack)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PesanViewHolder {
        return PesanViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.chat_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PesanViewHolder, position: Int) {
        val listChat = differ.currentList[position]
        holder.itemView.apply {
            Log.i("TAG", "onBindViewHolder: ${listChat.pesan}")
            chat_name_sender.text = listChat.from
            chat_date.text = listChat.created_at
            chat_item_content_body.text = listChat.pesan
        }
    }

    override fun getItemCount(): Int = differ.currentList.size


}