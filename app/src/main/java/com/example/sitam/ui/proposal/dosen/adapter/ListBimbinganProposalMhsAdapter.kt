package com.example.sitam.ui.proposal.dosen.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sitam.R
import com.example.sitam.models.proposal.DataListProposal
import kotlinx.android.synthetic.main.item_row_bimbingan_proposal_mhs_view_dosen.view.*
import kotlinx.android.synthetic.main.item_row_proposal_mhs_view_dosen.view.*

class ListBimbinganProposalMhsAdapter: RecyclerView.Adapter<ListBimbinganProposalMhsAdapter.ProposalViewHolder>() {

    class ProposalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallBack = object : DiffUtil.ItemCallback<DataListProposal>() {
        override fun areItemsTheSame(
            oldItem: DataListProposal,
            newItem: DataListProposal
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: DataListProposal,
            newItem: DataListProposal
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
                R.layout.item_row_bimbingan_proposal_mhs_view_dosen,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProposalViewHolder, position: Int) {
        val proposal = differ.currentList[position]
        holder.itemView.apply {
            tv_bimbingan_create_dosen.text = proposal.created_at
            tv_bimbingan_file_name_dosen.text = proposal.file_revisi
            tv_bimbingan_catatan_dosen.text = proposal.catatan
            tv_bimbingan_status_dosen.text = proposal.status

            setOnClickListener {
                onItemClickListener?.let {
                    it(proposal)
                }
            }
        }
    }


    override fun getItemCount(): Int = differ.currentList.size

    private var onItemClickListener: ((DataListProposal) -> Unit)? = null

    fun setOnClickListener(listener: (DataListProposal) -> Unit) {
        onItemClickListener = listener
    }
}