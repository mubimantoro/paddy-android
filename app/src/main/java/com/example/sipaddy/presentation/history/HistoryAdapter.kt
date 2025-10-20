package com.example.sipaddy.presentation.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sipaddy.R
import com.example.sipaddy.data.network.response.DiseaseItem
import com.example.sipaddy.databinding.ItemHistoryBinding
import com.example.sipaddy.utils.DateFormatter

class HistoryAdapter : ListAdapter<DiseaseItem, HistoryAdapter.HistoryViewHolder>(DIFF_CALLBACK) {

    class HistoryViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DiseaseItem) {
            with(binding) {
                diseaseNameTv.text = item.label ?: "-"
                diseaseDateTv.text = item.createdAt?.let { DateFormatter.formatIsoDate(it) } ?: "-"
                Glide.with(itemView.context)
                    .load(item.imageUrl)
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
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DiseaseItem>() {
            override fun areItemsTheSame(oldItem: DiseaseItem, newItem: DiseaseItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: DiseaseItem, newItem: DiseaseItem): Boolean {
                return oldItem == newItem
            }

        }
    }
}