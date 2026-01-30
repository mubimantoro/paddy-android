package com.example.sipaddy.presentation.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sipaddy.R
import com.example.sipaddy.data.model.response.PredictResponse
import com.example.sipaddy.databinding.ItemPredictionHistoryBinding
import com.example.sipaddy.utils.Constants
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryPredictionAdapter(
    private val onItemClick: (PredictResponse) -> Unit
) : ListAdapter<PredictResponse, HistoryPredictionAdapter.ViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ItemPredictionHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemPredictionHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PredictResponse) {
            with(binding) {
                // Disease name
                diseaseNameTv.text = item.disease

                // Confidence
                val confidence = item.confidenceScore.toInt()
                confidenceTv.text = "${confidence}%"
                progressConfidence.progress = confidence

                // Date
                dateTv.text = formatDate(item.createdAt)

                // Description
                descriptionTv.text = item.description

                // Image
                if (item.image.isNotEmpty()) {
                    val imageUrl = "${Constants.BASE_URL} ${item.image}"
                    Glide.with(itemView.context)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_image_placeholder)
                        .error(R.drawable.ic_image_placeholder)
                        .into(diseaseIv)
                } else {
                    diseaseIv.setImageResource(R.drawable.ic_image_placeholder)
                }

                root.setOnClickListener {
                    onItemClick(item)
                }
            }
        }
    }

    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            date?.let { outputFormat.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PredictResponse>() {
            override fun areItemsTheSame(
                oldItem: PredictResponse,
                newItem: PredictResponse
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: PredictResponse,
                newItem: PredictResponse
            ): Boolean = oldItem == newItem

        }
    }

}