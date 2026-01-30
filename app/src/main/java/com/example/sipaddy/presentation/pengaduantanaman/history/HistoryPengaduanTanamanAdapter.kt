package com.example.sipaddy.presentation.pengaduantanaman.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sipaddy.R
import com.example.sipaddy.data.model.response.PengaduanTanamanResponse
import com.example.sipaddy.databinding.ItemPengaduanTanamanHistoryBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class HistoryPengaduanTanamanAdapter(
    private val onItemClick: (PengaduanTanamanResponse) -> Unit
) : ListAdapter<PengaduanTanamanResponse, HistoryPengaduanTanamanAdapter.HistoryPengaduanViewHolder>(
    DIFF_CALLBACK
) {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): HistoryPengaduanViewHolder {
        val binding = ItemPengaduanTanamanHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return HistoryPengaduanViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: HistoryPengaduanViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class HistoryPengaduanViewHolder(
        private val binding: ItemPengaduanTanamanHistoryBinding,
        private val onItemClick: (PengaduanTanamanResponse) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PengaduanTanamanResponse) {
            with(binding) {
                // Deskripsi
                deskripsiTv.text = item.deskripsi

                // Date
                dateTv.text = formatDate(item.createdAt)

                // Status chip
                statusChip.text = item.status
                val statusColor = getStatusColor(item.status)
                statusChip.setChipBackgroundColorResource(statusColor)

                if (item.image.isNotEmpty()) {
                    Glide.with(itemView.context)
                        .load(item.image)
                        .placeholder(R.drawable.ic_image_placeholder)
                        .error(R.drawable.ic_image_placeholder)
                        .into(pengaduanIv)

                } else {
                    pengaduanIv.setImageResource(R.drawable.ic_image_placeholder)
                }

                root.setOnClickListener {
                    onItemClick(item)
                }

            }
        }

        fun formatDate(dateString: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")
                val date = inputFormat.parse(dateString)

                val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
                date?.let { outputFormat.format(it) } ?: dateString
            } catch (e: Exception) {
                dateString
            }
        }

        fun getStatusColor(status: String): Int {
            return when (status.lowercase()) {
                "pending" -> R.color.status_pending
                "verified" -> R.color.status_verified
                "in_progress" -> R.color.status_in_progress
                "completed" -> R.color.status_completed
                "rejected" -> R.color.status_rejected
                else -> R.color.status_pending
            }
        }

    }


    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PengaduanTanamanResponse>() {
            override fun areItemsTheSame(
                oldItem: PengaduanTanamanResponse, newItem: PengaduanTanamanResponse
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: PengaduanTanamanResponse, newItem: PengaduanTanamanResponse
            ): Boolean {
                return oldItem == newItem
            }

        }
    }


}