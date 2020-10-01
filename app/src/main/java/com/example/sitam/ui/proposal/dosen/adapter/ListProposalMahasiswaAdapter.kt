package com.example.sitam.ui.proposal.dosen.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sitam.R
import com.example.sitam.models.proposal.DataProposalDosen
import kotlinx.android.synthetic.main.item_row_proposal_mhs_view_dosen.view.*

class ListProposalMahasiswaAdapter: RecyclerView.Adapter<ListProposalMahasiswaAdapter.ProposalViewHolder>() {

    class ProposalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallBack = object : DiffUtil.ItemCallback<DataProposalDosen>() {
        override fun areItemsTheSame(
            oldItem: DataProposalDosen,
            newItem: DataProposalDosen
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: DataProposalDosen,
            newItem: DataProposalDosen
        ): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, differCallBack)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProposalViewHolder {
        return ProposalViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_row_proposal_mhs_view_dosen,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProposalViewHolder, position: Int) {
        val proposal = differ.currentList[position]
        holder.itemView.apply {
            tv_item_npm_proposal_dosen.text = proposal.mahasiswa
            tv_item_judul_prop_dosen.text = proposal.judul_proposal
            tv_item_konsentrasi_prop_dosen.text = proposal.konsentrasi
            tv_item_topik_prop_dosen.text = proposal.topik
            tv_item_status_prop_dosen.text = proposal.status

            setOnClickListener {
                onItemClickListener?.let {
                    it(proposal)
                }
            }
        }
    }


    override fun getItemCount(): Int = differ.currentList.size

    private var onItemClickListener: ((DataProposalDosen) -> Unit)? = null

    fun setOnClickListener(listener: (DataProposalDosen) -> Unit) {
        onItemClickListener = listener
    }
}