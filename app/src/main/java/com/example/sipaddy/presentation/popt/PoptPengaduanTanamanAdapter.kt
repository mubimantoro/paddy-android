package com.example.sipaddy.presentation.popt

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sipaddy.R
import com.example.sipaddy.data.network.response.PengaduanTanamanItem
import com.example.sipaddy.databinding.ItemPengaduanTanamanPoptBinding
import com.example.sipaddy.utils.DateFormatter

class PoptPengaduanTanamanAdapter() :
    ListAdapter<PengaduanTanamanItem, PoptPengaduanTanamanAdapter.ViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PoptPengaduanTanamanAdapter.ViewHolder {
        val binding = ItemPengaduanTanamanPoptBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PoptPengaduanTanamanAdapter.ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ViewHolder(private val binding: ItemPengaduanTanamanPoptBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PengaduanTanamanItem) {
            with(binding) {
                kelompokTaniTv.text = item.kelompokTani
                alamatTv.text = "${item.alamat}, ${item.kecamatan}, ${item.kabupaten}"
                deskripsiTv.text = item.deskripsi
                tanggalTv.text = item.createdAt?.let {
                    DateFormatter.formatIsoDate(it)
                } ?: "-"
                // tvPelapor.text = "Pelapor: ${item.userId. ?: item.userUsername ?: "-"}"

                setStatusBadge(item.status)

                Glide.with(itemView.context)
                    .load(item.image)
                    .placeholder(R.drawable.sample_scan)
                    .error(R.drawable.sample_scan)
                    .centerCrop()
                    .into(imageIv)

            }
        }

        private fun setStatusBadge(status: String?) {
            val context = itemView.context
            when (status?.lowercase()) {
                "pending" -> {
                    binding.tvStatus.text = context.getString(R.string.status_pending_label)
                    binding.tvStatus.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.yellow)
                    )
                }

                "assigned" -> {
                    binding.tvStatus.text = context.getString(R.string.status_assigned_label)
                    binding.tvStatus.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.blue)
                    )
                }

                /* "in_progress" -> {
                    binding.tvStatus.text = "Diproses"
                    binding.tvStatus.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.primary)
                    )
                } */

                /* "completed" -> {
                    binding.tvStatus.text = "Selesai"
                    binding.tvStatus.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.success)
                    )
                } */

                /* "rejected" -> {
                    binding.tvStatus.text = "Ditolak"
                    binding.tvStatus.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.danger)
                    )
                } */

                else -> {
                    binding.tvStatus.text = status
                    binding.tvStatus.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.red)
                    )
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PengaduanTanamanItem>() {
            override fun areItemsTheSame(
                oldItem: PengaduanTanamanItem,
                newItem: PengaduanTanamanItem
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: PengaduanTanamanItem,
                newItem: PengaduanTanamanItem
            ): Boolean = oldItem == newItem

        }
    }
}