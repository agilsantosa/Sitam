package com.example.sitam.ui.ta.mhs.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sitam.R
import com.example.sitam.models.seminar.DataBimbinganSeminar
import com.example.sitam.models.ta.DataListBimbinganTaMhs
import kotlinx.android.synthetic.main.item_row_bimbingan_seminar_mhs.view.*

class ListBimbinganTaViewMhsAdapter :
    RecyclerView.Adapter<ListBimbinganTaViewMhsAdapter.TugasAkhirMhsViewHolder>() {

    class TugasAkhirMhsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallBack = object : DiffUtil.ItemCallback<DataListBimbinganTaMhs>() {
        override fun areItemsTheSame(
            oldItem: DataListBimbinganTaMhs,
            newItem: DataListBimbinganTaMhs
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: DataListBimbinganTaMhs,
            newItem: DataListBimbinganTaMhs
        ): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, differCallBack)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TugasAkhirMhsViewHolder {
        return TugasAkhirMhsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_row_bimbingan_seminar_mhs,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TugasAkhirMhsViewHolder, position: Int) {
        val ta = differ.currentList[position]
        holder.itemView.apply {
            tv_item_catatan_seminar.text = ta.catatan
            tv_item_status_seminar.text = ta.status
            tv_item_filename_seminar.text = ta.file_revisi
            tv_item_create_seminar.text = ta.created_at
            setOnClickListener {
                onItemClickListener?.let {
                    it(ta)
                }
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    private var onItemClickListener: ((DataListBimbinganTaMhs) -> Unit)? = null

    fun setOnClickListener(listener: (DataListBimbinganTaMhs) -> Unit) {
        onItemClickListener = listener
    }

}