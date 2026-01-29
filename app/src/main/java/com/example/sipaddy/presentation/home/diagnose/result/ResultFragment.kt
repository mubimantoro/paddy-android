package com.example.sipaddy.presentation.home.diagnose.result

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.sipaddy.R
import com.example.sipaddy.data.model.response.PredictResponse
import com.example.sipaddy.databinding.FragmentResultBinding
import com.example.sipaddy.presentation.ViewModelFactory
import com.example.sipaddy.utils.Constants
import com.example.sipaddy.utils.ResultState
import com.example.sipaddy.utils.gone
import com.example.sipaddy.utils.showToast
import com.example.sipaddy.utils.visible
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.Locale

class ResultFragment : Fragment() {

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ResultViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private val args: ResultFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObserver()
        setupListener()

        viewModel.loadPredictionResult(args.predictionId)
    }

    private fun setupObserver() {
        viewModel.predictionResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Loading -> {
                    showLoading(true)
                }

                is ResultState.Error -> {
                    showLoading(false)
                    showError(result.message)
                }

                is ResultState.Success -> {
                    showLoading(false)
                    displayResult(result.data)
                }
            }
        }
    }

    private fun setupListener() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.watchVideoBtn.setOnClickListener {
            val url = viewModel.predictionResult.value
                ?.let { (it as? ResultState.Success)?.data?.urlYoutube }

            if (url != null && url != "-") {
                openYoutubeVideo(url)
            } else {
                requireContext().showToast("Video tidak tersedia")
            }

        }

        binding.diagnoseAgainBtn.setOnClickListener {
            showDiagnoseAgainDialog()
        }

        binding.shareResultBtn.setOnClickListener {
            shareResult()
        }
    }

    private fun displayResult(data: PredictResponse) {
        with(binding) {
            contentLayout.visible()

            if (data.image.isNotEmpty()) {
                val imageUrl = "${Constants.BASE_URL}${data.image}"
                Glide.with(this@ResultFragment)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_error)
                    .centerCrop()
                    .into(diseaseImageIv)
            }

            val diseaseNameFormatted = data.disease.replace("-", " ")
            diseaseNameTv.text = diseaseNameFormatted

            val badgeColor = getDiseaseColor(data.disease)
            diseaseBadgeCard.setCardBackgroundColor(badgeColor)

            val confidenceFormatted = String.format("%.2f%%", data.confidenceScore)
            confidenceTv.text = confidenceFormatted

            setConfidenceIndicator(data.confidenceScore)

            descriptionTv.text = data.description

            causesTv.text = data.causes

            tvSolutions.text = data.solutions

            dateTv.text = formatDate(data.createdAt)

            watchVideoBtn.isEnabled = data.urlYoutube != "-"
            watchVideoBtn.alpha = if (data.urlYoutube != "-") 1f else 0.5f
        }
    }

    private fun getDiseaseColor(disease: String): Int {
        return when (disease) {
            "Normal" -> ContextCompat.getColor(requireContext(), R.color.disease_normal)
            "Blast" -> ContextCompat.getColor(requireContext(), R.color.disease_blast)
            "Bacterial-Leaf-Blight" -> ContextCompat.getColor(
                requireContext(),
                R.color.disease_blight
            )

            "Stem-Borer" -> ContextCompat.getColor(requireContext(), R.color.disease_borer)
            else -> ContextCompat.getColor(requireContext(), R.color.disease_default)
        }
    }

    private fun setConfidenceIndicator(confidence: Double) {
        val color: Int
        val icon: Int

        when {
            confidence >= 80 -> {
                color = ContextCompat.getColor(requireContext(), R.color.confidence_high)
                icon = R.drawable.ic_check_circle
            }

            confidence >= 60 -> {
                color = ContextCompat.getColor(requireContext(), R.color.confidence_medium)
                icon = R.drawable.ic_warning
            }

            else -> {
                color = ContextCompat.getColor(requireContext(), R.color.confidence_low)
                icon = R.drawable.ic_error
            }
        }

        binding.confidenceTv.setTextColor(color)
        binding.ivConfidenceIcon.setImageResource(icon)
        binding.ivConfidenceIcon.setColorFilter(color)
    }

    fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID"))
            val date = inputFormat.parse(dateString)
            date?.let { outputFormat.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visible()
            binding.contentLayout.gone()
            binding.errorLayout.gone()
        } else {
            binding.progressBar.gone()
        }
    }

    private fun showError(message: String) {
        binding.contentLayout.gone()
        binding.errorLayout.visible()
        binding.errorMessageTv.text = message

        binding.retryBtn.setOnClickListener {
            viewModel.loadPredictionResult(args.predictionId)
        }
    }

    private fun openYoutubeVideo(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            startActivity(intent)
        } catch (e: Exception) {
            requireContext().showToast("Tidak dapat membuka video")
        }
    }

    private fun showDiagnoseAgainDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Diagnosa Lagi?")
            .setMessage("Anda akan kembali ke halaman Diagnosa dan data saat ini akan hilang.")
            .setPositiveButton("Ya") { dialog, _ ->
                dialog.dismiss()
                navigateToDiagnoseScreen()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun navigateToDiagnoseScreen() {
        try {
            findNavController().navigate(R.id.action_navigation_result_to_navigation_predict_disease)
        } catch (e: Exception) {
            // Just pop back stack
            findNavController().navigateUp()
        }
    }

    private fun shareResult() {
        val result = (viewModel.predictionResult.value as? ResultState.Success)?.data ?: return

        val diseaseNameFormatted = result.disease.replace("-", " ")
        val confidenceFormatted = String.format("%.2f%%", result.confidenceScore)

        val shareText = """
            🌾 Hasil Diagnosa Penyakit Padi
            
            Penyakit: $diseaseNameFormatted
            Tingkat Keyakinan: $confidenceFormatted
            
            Deskripsi:
            ${result.description}
            
            Penyebab:
            ${result.causes}
            
            Solusi:
            ${result.solutions}
            
            Tanggal: ${formatDate(result.createdAt)}
            
            #SIPaddy #DiagnosaPenyakitPadi
        """.trimIndent()

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
            putExtra(Intent.EXTRA_SUBJECT, "Hasil Diagnosa Penyakit Padi")
        }

        try {
            startActivity(Intent.createChooser(shareIntent, "Bagikan hasil via"))
        } catch (e: Exception) {
            requireContext().showToast("Tidak dapat membagikan hasil")
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}