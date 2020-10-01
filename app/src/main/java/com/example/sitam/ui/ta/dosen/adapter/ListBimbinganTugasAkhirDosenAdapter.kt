package com.example.sitam.ui.ta.dosen.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sitam.R
import com.example.sitam.models.ta.DataListBimbinganTaMhs
import kotlinx.android.synthetic.main.item_row_bimbingan_proposal_mhs_view_dosen.view.*

class ListBimbinganTugasAkhirDosenAdapter : RecyclerView.Adapter<ListBimbinganTugasAkhirDosenAdapter.TugasAkhirViewHolder>(){
    class TugasAkhirViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

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



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TugasAkhirViewHolder {
        return TugasAkhirViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_row_bimbingan_proposal_mhs_view_dosen,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TugasAkhirViewHolder, position: Int) {
        val ta = differ.currentList[position]
        holder.itemView.apply {
            tv_bimbingan_create_dosen.text = ta.created_at
            tv_bimbingan_file_name_dosen.text = ta.file_revisi
            tv_bimbingan_catatan_dosen.text = ta.catatan
            tv_bimbingan_status_dosen.text = ta.status

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