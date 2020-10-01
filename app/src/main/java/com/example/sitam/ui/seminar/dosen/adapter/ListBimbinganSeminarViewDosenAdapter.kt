package com.example.sitam.ui.seminar.dosen.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sitam.R
import com.example.sitam.models.seminar.DataBimbinganSeminarDosen
import com.example.sitam.models.seminar.DataListBimbinganSeminarDosen
import kotlinx.android.synthetic.main.item_row_bimbingan_proposal_mhs_view_dosen.view.*
import kotlinx.android.synthetic.main.item_row_seminar_view_dosen.view.*
import kotlinx.android.synthetic.main.item_row_seminar_view_dosen.view.tv_item_pembimbing_seminar_dosen
import kotlinx.android.synthetic.main.item_row_seminar_view_dosen.view.tv_item_seminar_totnilai_dosen
import kotlinx.android.synthetic.main.item_row_seminar_view_dosen.view.tv_item_tgl_sidang_seminar_dosen

class ListBimbinganSeminarViewDosenAdapter: RecyclerView.Adapter<ListBimbinganSeminarViewDosenAdapter.ListBimbinganSeminarViewDosenHolder>() {
    class ListBimbinganSeminarViewDosenHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallBack = object : DiffUtil.ItemCallback<DataListBimbinganSeminarDosen>() {
        override fun areItemsTheSame(
            oldItem: DataListBimbinganSeminarDosen,
            newItem: DataListBimbinganSeminarDosen
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: DataListBimbinganSeminarDosen,
            newItem: DataListBimbinganSeminarDosen
        ): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, differCallBack)



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListBimbinganSeminarViewDosenHolder {
        return ListBimbinganSeminarViewDosenHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_row_bimbingan_seminar_mhs_view_dosen,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: ListBimbinganSeminarViewDosenHolder,
        position: Int
    ) {
        val seminar = differ.currentList[position]
        holder.itemView.apply {
            tv_bimbingan_create_dosen.text = seminar.created_at
            tv_bimbingan_file_name_dosen.text = seminar.file_revisi
            tv_bimbingan_catatan_dosen.text = seminar.catatan
            tv_bimbingan_status_dosen.text = seminar.status

            setOnClickListener {
                onItemClickListener?.let {
                    it(seminar)
                }
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    private var onItemClickListener: ((DataListBimbinganSeminarDosen) -> Unit)? = null

    fun setOnClickListener(listener: (DataListBimbinganSeminarDosen) -> Unit) {
        onItemClickListener = listener
    }

}