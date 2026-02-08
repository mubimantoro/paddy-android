package com.example.sipaddy.presentation.popt

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sipaddy.R
import com.example.sipaddy.data.model.response.AssignedPengaduanTanamanResponse
import com.example.sipaddy.databinding.ItemAssignedPengaduanTanamanBinding
import com.example.sipaddy.utils.Constants
import com.example.sipaddy.utils.formatDateTime
import com.example.sipaddy.utils.toStatusChipColors
import com.example.sipaddy.utils.toStatusText

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
                pelaporTv.text = "Pelapor: ${item.pelaporNama ?: "-"}"

                // Kelompok Tani
                kelompokTaniTv.text = item.kelompokTaniNama

                // Kecamatan
                tvKecamatan.text = item.kecamatanNama

                // Status
                statusChip.text = item.status.toStatusText()
                val (bgColor, textColor) = item.status.toStatusChipColors(itemView.context)
                statusChip.chipBackgroundColor = android.content.res.ColorStateList.valueOf(bgColor)
                statusChip.setTextColor(textColor)

                // Date
                dateTv.text = item.createdAt.formatDateTime()

                // Image
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

                // Click listener
                root.setOnClickListener {
                    onItemClick(item)
                }
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