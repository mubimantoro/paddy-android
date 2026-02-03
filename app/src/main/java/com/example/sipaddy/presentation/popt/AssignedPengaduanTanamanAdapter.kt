package com.example.sipaddy.presentation.popt

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sipaddy.R
import com.example.sipaddy.data.model.response.AssignedPengaduanTanamanResponse
import com.example.sipaddy.databinding.ItemAssignedPengaduanTanamanBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class AssignedPengaduanTanamanAdapter(
    private val onItemClick: (AssignedPengaduanTanamanResponse) -> Unit
) : ListAdapter<AssignedPengaduanTanamanResponse, AssignedPengaduanTanamanAdapter.ViewHolder>(
    DIFF_CALLBACK
) {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemAssignedPengaduanTanamanBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }


    inner class ViewHolder(
        private val binding: ItemAssignedPengaduanTanamanBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AssignedPengaduanTanamanResponse) {
            with(binding) {
                // Deskripsi
                deskripsiTv.text = item.deskripsi

                // Pelapor
                pelaporTv.text = "Pelapor: ${item.pelaporNama}"

                // Kelompok Tani
                kelompokTaniTv.text = item.kelompokTaniNama

                // Kecamatan
                tvKecamatan.text = item.kecamatanNama

                // Status
                statusChip.text = item.status
                setupStatusColor(item.status)

                // Date
                dateTv.text = formatDate(item.createdAt)

                // Image
                if (item.image.isNotEmpty()) {
                    Glide.with(itemView.context)
                        .load(item.image)
                        .placeholder(R.drawable.ic_image_placeholder)
                        .error(R.drawable.ic_image_placeholder)
                        .into(pengaduanIv)
                } else {
                    pengaduanIv.setImageResource(R.drawable.ic_image_placeholder)
                }

                // Click listener
                root.setOnClickListener {
                    onItemClick(item)
                }
            }

        }

        private fun setupStatusColor(status: String) {
            val context = binding.root.context
            val (backgroundColor, textColor) = when (status.uppercase()) {
                "DITUGASKAN" -> Pair(
                    ContextCompat.getColor(context, R.color.status_assigned_bg),
                    ContextCompat.getColor(context, R.color.status_assigned_text)
                )

                "DALAM PROSES" -> Pair(
                    ContextCompat.getColor(context, R.color.status_in_progress_bg),
                    ContextCompat.getColor(context, R.color.status_in_progress_text)
                )

                "SELESAI" -> Pair(
                    ContextCompat.getColor(context, R.color.status_completed_bg),
                    ContextCompat.getColor(context, R.color.status_completed_text)
                )

                else -> Pair(
                    ContextCompat.getColor(context, R.color.status_pending_bg),
                    ContextCompat.getColor(context, R.color.status_pending_text)
                )
            }

            binding.statusChip.setChipBackgroundColorResource(android.R.color.transparent)
            binding.statusChip.chipBackgroundColor =
                android.content.res.ColorStateList.valueOf(backgroundColor)
            binding.statusChip.setTextColor(textColor)
        }

        private fun formatDate(dateString: String): String {
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

    }

    companion object {
        private val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<AssignedPengaduanTanamanResponse>() {
                override fun areItemsTheSame(
                    oldItem: AssignedPengaduanTanamanResponse,
                    newItem: AssignedPengaduanTanamanResponse
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: AssignedPengaduanTanamanResponse,
                    newItem: AssignedPengaduanTanamanResponse
                ): Boolean {
                    return oldItem == newItem
                }

            }
    }
}