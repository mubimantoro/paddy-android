package com.example.sipaddy.presentation.pengaduantanaman.history

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sipaddy.R
import com.example.sipaddy.data.model.response.PengaduanTanamanResponse
import com.example.sipaddy.databinding.ItemPengaduanTanamanHistoryBinding
import com.example.sipaddy.utils.DateFormatter
import com.example.sipaddy.utils.PengaduanTanamanStatus

class HistoryPengaduanTanamanAdapter(
    private val onItemClick: (PengaduanTanamanResponse) -> Unit
) : ListAdapter<PengaduanTanamanResponse, HistoryPengaduanTanamanAdapter.HistoryPengaduanViewHolder>(
    DIFF_CALLBACK
) {

    class HistoryPengaduanViewHolder(
        private val binding: ItemPengaduanTanamanHistoryBinding,
        private val onItemClick: (PengaduanTanamanResponse) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.M)
        fun bind(item: PengaduanTanamanResponse) {
            binding.apply {
                item.image.let { imageUrl ->
                    Glide.with(itemView.context).load(imageUrl)
                        .placeholder(R.drawable.sample_scan)
                        .error(R.drawable.sample_scan).centerCrop().into(imageIv)
                }

                statusTv.text = item.status
                dateTv.text = DateFormatter.formatIsoDate(item.createdAt)

                val statusColor = when (item.status.lowercase()) {
                    "pending" -> itemView.context.getColor(R.color.orange)
                    "diproses" -> itemView.context.getColor(R.color.blue)
                    "selesai" -> itemView.context.getColor(R.color.status_resolved)
                    else -> itemView.context.getColor(R.color.orange)
                }
                statusTv.setTextColor(statusColor)

                root.setOnClickListener {
                    onItemClick(item)
                }
            }
        }


        private fun setStatusBadge(status: String?) {
            with(binding.statusTv) {
                when (status) {
                    PengaduanTanamanStatus.PENDING -> {
                        text = context.getString(R.string.pending_label)
                        setBackgroundResource(R.drawable.status_pending_bg)
                    }

                    PengaduanTanamanStatus.ASSIGNED -> {
                        text = context.getString(R.string.status_assigned_label)
                        setBackgroundResource(R.drawable.status_assigned_label)
                    }

                    PengaduanTanamanStatus.VERIFIED -> {
                        text = context.getString(R.string.verifikasi_status_label)
                        setBackgroundResource(R.drawable.status_verified_bg)
                    }

                    PengaduanTanamanStatus.HANDLED -> {
                        text = context.getString(R.string.handled_status_label)
                        setBackgroundResource(R.drawable.status_handled_bg)
                    }

                    PengaduanTanamanStatus.COMPLETED -> {
                        text = context.getString(R.string.resolved_label)
                        setBackgroundResource(R.drawable.status_completed_bg)
                    }

                    else -> {
                        text = context.getString(R.string.pending_label)
                        setBackgroundResource(R.drawable.status_pending_bg)
                    }
                }
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): HistoryPengaduanViewHolder {
        val binding = ItemPengaduanTanamanHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return HistoryPengaduanViewHolder(binding, onItemClick)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: HistoryPengaduanViewHolder, position: Int) {
        holder.bind(getItem(position))
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