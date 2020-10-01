package com.example.sitam.ui.seminar.dosen.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sitam.R
import com.example.sitam.models.seminar.DataBimbinganSeminarDosen
import kotlinx.android.synthetic.main.item_row_seminar_view_dosen.view.*

class BimbinganSeminarViewDosenAdapter: RecyclerView.Adapter<BimbinganSeminarViewDosenAdapter.SeminarViewDosenHolder>() {
    class SeminarViewDosenHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallBack = object : DiffUtil.ItemCallback<DataBimbinganSeminarDosen>() {
        override fun areItemsTheSame(
            oldItem: DataBimbinganSeminarDosen,
            newItem: DataBimbinganSeminarDosen
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: DataBimbinganSeminarDosen,
            newItem: DataBimbinganSeminarDosen
        ): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, differCallBack)



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SeminarViewDosenHolder {
        return SeminarViewDosenHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_row_seminar_view_dosen,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: SeminarViewDosenHolder,
        position: Int
    ) {
        val seminar = differ.currentList[position]
        holder.itemView.apply {
            tv_item_npm_seminar_dosen.text = seminar.mahasiswa
            tv_item_pembimbing_seminar_dosen.text = seminar.pembimbing
            tv_item_tgl_sidang_seminar_dosen.text = seminar.tanggal_sidang
            tv_item_seminar_totnilai_dosen.text = seminar.approval
            tv_item_seminar_as_dosen.text = seminar.`as`

            setOnClickListener {
                onItemClickListener?.let {
                    it(seminar)
                }
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    private var onItemClickListener: ((DataBimbinganSeminarDosen) -> Unit)? = null

    fun setOnClickListener(listener: (DataBimbinganSeminarDosen) -> Unit) {
        onItemClickListener = listener
    }

}