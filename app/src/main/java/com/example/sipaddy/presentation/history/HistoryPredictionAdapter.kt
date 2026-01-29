package com.example.sipaddy.presentation.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sipaddy.R
import com.example.sipaddy.data.model.response.PredictResponse
import com.example.sipaddy.databinding.ItemHistoryPredictionBinding
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
            ItemHistoryPredictionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemHistoryPredictionBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PredictResponse) {
            with(binding) {
                diseaseNameTv.text = item.disease.replace("-", " ")
                descriptionTv.text = item.description
                confidenceTv.text = "${item.confidenceScore}%"
                dateTv.text = formatDate(item.createdAt)

                val confidence = item.confidenceScore
                val color = when {
                    confidence >= 80 -> ContextCompat.getColor(
                        root.context,
                        R.color.confidence_high
                    )

                    confidence >= 60 -> ContextCompat.getColor(
                        root.context,
                        R.color.confidence_medium
                    )

                    else -> ContextCompat.getColor(root.context, R.color.confidence_low)
                }
                confidenceTv.setTextColor(color)

                val badgeColor = when (item.disease) {
                    "Normal" -> ContextCompat.getColor(root.context, R.color.disease_normal)
                    "Blast" -> ContextCompat.getColor(root.context, R.color.disease_blast)
                    "Bacterial-Leaf-Blight" -> ContextCompat.getColor(
                        root.context,
                        R.color.disease_blight
                    )

                    "Stem-Borer" -> ContextCompat.getColor(root.context, R.color.disease_borer)
                    else -> ContextCompat.getColor(root.context, R.color.disease_default)
                }
                diseaseCard.setCardBackgroundColor(badgeColor)

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