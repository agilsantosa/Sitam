package com.example.sitam.ui.proposal.mhs.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sitam.R
import com.example.sitam.models.proposal.DataListProposal
import kotlinx.android.synthetic.main.item_row_bimbingan_prop_mhs.view.*


class ListBimbinganProposalMhsAdapter :
    RecyclerView.Adapter<ListBimbinganProposalMhsAdapter.BimbinganProposalViewHolder>() {

    class BimbinganProposalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

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
    ): BimbinganProposalViewHolder {
        return BimbinganProposalViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_row_bimbingan_prop_mhs,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: BimbinganProposalViewHolder,
        position: Int
    ) {
        val proposal = differ.currentList[position]
        holder.itemView.apply {
            tv_bimbingan_create.text = proposal.created_at
            tv_bimbingan_file_name.text = proposal.file_revisi
            tv_bimbingan_catatan.text = proposal.catatan
            tv_bimbingan_status.text = proposal.status

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