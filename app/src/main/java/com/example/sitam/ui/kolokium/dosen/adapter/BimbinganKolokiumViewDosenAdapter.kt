package com.example.sitam.ui.kolokium.dosen.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sitam.R
import com.example.sitam.models.kolokium.DataBimbinganKolokiumDosen
import com.example.sitam.models.seminar.DataBimbinganSeminarDosen
import kotlinx.android.synthetic.main.item_row_kolokium_view_dosen.view.*
import kotlinx.android.synthetic.main.item_row_seminar_view_dosen.view.*
import kotlinx.android.synthetic.main.item_row_seminar_view_dosen.view.tv_item_npm_seminar_dosen

class BimbinganKolokiumViewDosenAdapter: RecyclerView.Adapter<BimbinganKolokiumViewDosenAdapter.KolokiumViewDosenHolder>() {
    class KolokiumViewDosenHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallBack = object : DiffUtil.ItemCallback<DataBimbinganKolokiumDosen>() {
        override fun areItemsTheSame(
            oldItem: DataBimbinganKolokiumDosen,
            newItem: DataBimbinganKolokiumDosen
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: DataBimbinganKolokiumDosen,
            newItem: DataBimbinganKolokiumDosen
        ): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, differCallBack)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KolokiumViewDosenHolder {
        return  KolokiumViewDosenHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_row_kolokium_view_dosen,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: KolokiumViewDosenHolder, position: Int) {
        val kolokium = differ.currentList[position]
        holder.itemView.apply {
            tv_item_npm_kolokium_dosen.text = kolokium.mahasiswa
            tv_item_pembimbing_kolokium_dosen.text = kolokium.pembimbing1
            tv_item_tgl_sidang_kolokium_dosen.text = kolokium.tanggal_pelaksanaan ?: "Belum ditentukan"
            tv_item_kolokium_totnilai_dosen.text = kolokium.approval
            tv_item_kolokium_as_dosen.text = kolokium.`as`

            setOnClickListener {
                onItemClickListener?.let {
                    it(kolokium)
                }
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    private var onItemClickListener: ((DataBimbinganKolokiumDosen) -> Unit)? = null

    fun setOnClickListener(listener: (DataBimbinganKolokiumDosen) -> Unit) {
        onItemClickListener = listener
    }
}