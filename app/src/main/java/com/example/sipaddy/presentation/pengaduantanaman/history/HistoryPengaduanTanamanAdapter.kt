package com.example.sipaddy.presentation.pengaduantanaman.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sipaddy.R
import com.example.sipaddy.data.network.response.PengaduanTanamanItem
import com.example.sipaddy.databinding.ItemPengaduanTanamanHistoryBinding
import com.example.sipaddy.utils.DateFormatter

class HistoryPengaduanTanamanAdapter(
) :
    ListAdapter<PengaduanTanamanItem, HistoryPengaduanTanamanAdapter.HistoryPengaduanTanamanViewHolder>(
        DIFF_CALLBACK
    ) {

    class HistoryPengaduanTanamanViewHolder(private val binding: ItemPengaduanTanamanHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PengaduanTanamanItem) {
            with(binding) {
                kelompokTaniTv.text = item.kelompokTani ?: "-"
                val location = buildString {
                    item.kecamatan?.let { append("Kec. $it") }
                    if (item.kecamatan != null && item.kabupaten != null) {
                        append(", ")
                    }
                    item.kabupaten?.let { append("Kab. $it") }
                }
                locationTv.text = location.ifEmpty { "-" }
                dateTv.text = item.createdAt?.let {
                    DateFormatter.formatIsoDate(it)
                } ?: "-"

                setStatusBadge(item.status)

                Glide.with(itemView.context)
                    .load(item.image)
                    .placeholder(R.drawable.sample_scan)
                    .error(R.drawable.sample_scan)
                    .centerCrop()
                    .into(pengaduanTanamanIv)

            }
        }


        private fun setStatusBadge(status: String?) {
            with(binding.statusTv) {
                when (status) {
                    "Pending" -> {
                        text = context.getString(R.string.pending_label)
                        setBackgroundResource(R.drawable.badge_pending_bg)
                    }

                    "in_progress", "diproses" -> {
                        text = context.getString(R.string.in_progress_label)
                        setBackgroundResource(R.drawable.badge_in_progress_bg)
                    }

                    "resolved", "selesai" -> {
                        text = context.getString(R.string.resolved_label)
                        setBackgroundResource(R.drawable.badge_resolved_bg)
                    }

                    else -> {
                        text = context.getString(R.string.pending_label)
                        setBackgroundResource(R.drawable.badge_pending_bg)
                    }

                }
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistoryPengaduanTanamanViewHolder {
        val binding = ItemPengaduanTanamanHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HistoryPengaduanTanamanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryPengaduanTanamanViewHolder, position: Int) {
        val pengaduanTanaman = getItem(position)
        holder.bind(pengaduanTanaman)
        holder.itemView.setOnClickListener {
            val toDetail =
                HistoryPengaduanTanamanFragmentDirections.actionHistoryPengaduanTanamanFragmentToDetailPengaduanTanamanFragment(
                    pengaduanTanaman
                )
            holder.itemView.findNavController().navigate(toDetail)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PengaduanTanamanItem>() {
            override fun areItemsTheSame(
                oldItem: PengaduanTanamanItem,
                newItem: PengaduanTanamanItem
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: PengaduanTanamanItem,
                newItem: PengaduanTanamanItem
            ): Boolean {
                return oldItem == newItem
            }

        }
    }


}