package com.example.sitam.ui.kolokium.mhs.adaper

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sitam.R
import com.example.sitam.models.kolokium.DataListBimbinganKolokiumMhs
import kotlinx.android.synthetic.main.item_row_bimbingan_kolokium_mhs.view.*

class ListBimbinganKolokiumViewMhsAdapter :
    RecyclerView.Adapter<ListBimbinganKolokiumViewMhsAdapter.KolokiumMhsViewHolder>() {

    class KolokiumMhsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallBack = object : DiffUtil.ItemCallback<DataListBimbinganKolokiumMhs>() {
        override fun areItemsTheSame(
            oldItem: DataListBimbinganKolokiumMhs,
            newItem: DataListBimbinganKolokiumMhs
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: DataListBimbinganKolokiumMhs,
            newItem: DataListBimbinganKolokiumMhs
        ): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KolokiumMhsViewHolder {
        return KolokiumMhsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_row_bimbingan_kolokium_mhs,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: KolokiumMhsViewHolder, position: Int) {
        val kolokium = differ.currentList[position]
        holder.itemView.apply {
            tv_item_status_kolokium.text = kolokium.status
            tv_item_catatan_kolokium.text = kolokium.catatan
            tv_item_filename_kolokium.text = kolokium.file_revisi
            tv_item_create_kolokium.text = kolokium.created_at
            setOnClickListener {
                onItemClickListener?.let {
                    it(kolokium)
                }
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    private var onItemClickListener: ((DataListBimbinganKolokiumMhs) -> Unit)? = null

    fun setOnClickListener(listener: (DataListBimbinganKolokiumMhs) -> Unit) {
        onItemClickListener = listener
    }
}