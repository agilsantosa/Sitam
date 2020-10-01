package com.example.sitam.ui.ta.dosen.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sitam.R
import com.example.sitam.models.proposal.DataProposalDosen
import com.example.sitam.models.ta.DataTugasAkhirDosen
import kotlinx.android.synthetic.main.item_row_ta_mhs_view_dosen.view.*

class ListTugasAkhirMahasiswaAdapter:
    RecyclerView.Adapter<ListTugasAkhirMahasiswaAdapter.TugasAkhirViewHolder>() {

    class TugasAkhirViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallBack = object : DiffUtil.ItemCallback<DataTugasAkhirDosen>() {
        override fun areItemsTheSame(
            oldItem: DataTugasAkhirDosen,
            newItem: DataTugasAkhirDosen
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: DataTugasAkhirDosen,
            newItem: DataTugasAkhirDosen
        ): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, differCallBack)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TugasAkhirViewHolder {
        return TugasAkhirViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_row_ta_mhs_view_dosen,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TugasAkhirViewHolder, position: Int) {
        val ta = differ.currentList[position]
        holder.itemView.apply {
            tv_item_npm_ta_dosen.text = ta.mahasiswa
            tv_item_topik_ta_dosen.text = ta.topik_tugas_akhir
            tv_item_konsentrasi_ta_dosen.text = ta.konsentrasi
            tv_item_thn_pengajuan_ta_dosen.text = ta.tahun_pengajuan.toString()
            tv_item_status_ta_dosen.text = ta.status
            tv_item_anda_sbg_dosen.text = ta.`as`

            setOnClickListener {
                onItemClickListener?.let {
                    it(ta)
                }
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    private var onItemClickListener: ((DataTugasAkhirDosen) -> Unit)? = null

    fun setOnClickListener(listener: (DataTugasAkhirDosen) -> Unit) {
        onItemClickListener = listener
    }
}