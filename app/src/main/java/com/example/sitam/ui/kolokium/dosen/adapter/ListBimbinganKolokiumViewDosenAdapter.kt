package com.example.sitam.ui.kolokium.dosen.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sitam.R
import com.example.sitam.models.kolokium.DataListBimbinganKolokiumDosen
import com.example.sitam.models.seminar.DataListBimbinganSeminarDosen
import kotlinx.android.synthetic.main.item_row_bimbingan_proposal_mhs_view_dosen.view.*

class ListBimbinganKolokiumViewDosenAdapter: RecyclerView.Adapter<ListBimbinganKolokiumViewDosenAdapter.ListBimbinganKolokiumViewDosenHolder>() {
    class ListBimbinganKolokiumViewDosenHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallBack = object : DiffUtil.ItemCallback<DataListBimbinganKolokiumDosen>() {
        override fun areItemsTheSame(
            oldItem: DataListBimbinganKolokiumDosen,
            newItem: DataListBimbinganKolokiumDosen
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: DataListBimbinganKolokiumDosen,
            newItem: DataListBimbinganKolokiumDosen
        ): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, differCallBack)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListBimbinganKolokiumViewDosenHolder {
        return ListBimbinganKolokiumViewDosenHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_row_list_bimbingan_kolokium_view_dosen,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ListBimbinganKolokiumViewDosenHolder, position: Int) {
        val kolokium = differ.currentList[position]
        holder.itemView.apply {
            tv_bimbingan_create_dosen.text = kolokium.created_at
            tv_bimbingan_file_name_dosen.text = kolokium.file_revisi
            tv_bimbingan_catatan_dosen.text = kolokium.catatan
            tv_bimbingan_status_dosen.text = kolokium.status

            setOnClickListener {
                onItemClickListener?.let {
                    it(kolokium)
                }
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    private var onItemClickListener: ((DataListBimbinganKolokiumDosen) -> Unit)? = null

    fun setOnClickListener(listener: (DataListBimbinganKolokiumDosen) -> Unit) {
        onItemClickListener = listener
    }
}