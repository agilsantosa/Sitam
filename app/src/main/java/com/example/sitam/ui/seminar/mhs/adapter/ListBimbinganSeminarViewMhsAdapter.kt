package com.example.sitam.ui.seminar.mhs.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sitam.R
import com.example.sitam.models.seminar.DataBimbinganSeminar
import kotlinx.android.synthetic.main.item_row_bimbingan_seminar_mhs.view.*

class ListBimbinganSeminarViewMhsAdapter: RecyclerView.Adapter<ListBimbinganSeminarViewMhsAdapter.SeminarViewHolder>() {
    class SeminarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallBack = object : DiffUtil.ItemCallback<DataBimbinganSeminar>() {
        override fun areItemsTheSame(
            oldItem: DataBimbinganSeminar,
            newItem: DataBimbinganSeminar
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: DataBimbinganSeminar,
            newItem: DataBimbinganSeminar
        ): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, differCallBack)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeminarViewHolder {
       return SeminarViewHolder(
           LayoutInflater.from(parent.context).inflate(
               R.layout.item_row_bimbingan_seminar_mhs,
               parent,
               false
           )
       )
    }

    override fun onBindViewHolder(holder: SeminarViewHolder, position: Int) {
        val seminar = differ.currentList[position]
        holder.itemView.apply {
            tv_item_status_seminar.text = seminar.status
            tv_item_catatan_seminar.text = seminar.catatan
            tv_item_filename_seminar.text = seminar.file_revisi
            tv_item_create_seminar.text = seminar.created_at
            setOnClickListener {
                onItemClickListener?.let {
                    it(seminar)
                }
            }
        }
    }

    override fun getItemCount(): Int =differ.currentList.size

    private var onItemClickListener: ((DataBimbinganSeminar) -> Unit)? = null

    fun setOnClickListener(listener: (DataBimbinganSeminar) -> Unit) {
        onItemClickListener = listener
    }
}