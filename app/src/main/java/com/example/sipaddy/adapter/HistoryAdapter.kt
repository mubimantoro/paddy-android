package com.example.sipaddy.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sipaddy.R
import com.example.sipaddy.data.network.response.HistoryItem
import com.example.sipaddy.databinding.ItemHistoryBinding
import com.example.sipaddy.presentation.history.HistoryFragmentDirections
import com.example.sipaddy.utils.DateFormatter

class HistoryAdapter() : ListAdapter<HistoryItem, HistoryAdapter.HistoryViewHolder>(DIFF_CALLBACK) {

    class HistoryViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HistoryItem) {
            with(binding) {
                diseaseNameTv.text = item.label
                diseaseDateTv.text = item.createdAt?.let { DateFormatter.formatIsoDate(it) }
                Glide.with(itemView.context)
                    .load(item.photoUrl)
                    .placeholder(R.drawable.sample_scan)
                    .error(R.drawable.sample_scan)
                    .into(historyIv)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = getItem(position)
        holder.bind(history)
        holder.itemView.setOnClickListener {
            val toResultFragment =
                HistoryFragmentDirections.actionNavigationHistoryToNavigationResult(
                    null,
                    null,
                    history
                )
            holder.itemView.findNavController().navigate(toResultFragment)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<HistoryItem>() {
            override fun areItemsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean {
                return oldItem == newItem
            }

        }
    }
}