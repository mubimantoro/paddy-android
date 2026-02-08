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
import com.example.sipaddy.utils.Constants
import com.example.sipaddy.utils.formatDateTime
import com.example.sipaddy.utils.toStatusColor
import com.example.sipaddy.utils.toStatusText

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
                dateTv.text = item.createdAt.formatDateTime()

                // Status chip
                statusChip.text = item.status.toStatusText()
                statusChip.setChipBackgroundColorResource(item.status.toStatusColor())

                if (item.image.isNotEmpty()) {
                    val imageUrl = "${Constants.BASE_URL}${item.image}"
                    Glide.with(itemView.context)
                        .load(imageUrl)
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