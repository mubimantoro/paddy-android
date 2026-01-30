package com.example.sipaddy.presentation.home.diagnose.result

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.sipaddy.R
import com.example.sipaddy.data.model.response.PredictResponse
import com.example.sipaddy.databinding.FragmentResultBinding
import com.example.sipaddy.presentation.ViewModelFactory
import com.example.sipaddy.utils.ResultState
import com.example.sipaddy.utils.showToast
import java.text.SimpleDateFormat
import java.util.Locale

class ResultFragment : Fragment() {

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ResultViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private val args: ResultFragmentArgs by navArgs()
    private var youtubeUrl: String = ""


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

        viewModel.getPredictionDetail(args.predictionId)
    }

    private fun setupObserver() {
        viewModel.predictionResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Loading -> {
                    showLoading(true)
                }

                is ResultState.Error -> {
                    showLoading(false)
                    requireContext().showToast(result.message)
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
            openYoutubeVideo()
        }

        binding.predictAgainBtn.setOnClickListener {
            findNavController().navigateUp()
        }
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

    private fun displayResult(data: PredictResponse) {
        with(binding) {
            // Disease name
            diseaseTv.text = data.disease

            // Confidence score (convert string to float for progress)
            val confidence = data.confidenceScore.toInt()
            confidenceTv.text = "${confidence}%"
            progressConfidence.progress = confidence

            // Description
            descriptionTv.text = data.description

            // Causes
            causesTv.text = data.causes

            // Solutions
            solutionsTv.text = data.solutions

            // Store YouTube URL
            youtubeUrl = data.urlYoutube

            // Hide video button if no URL
            if (data.urlYoutube == "-" || data.urlYoutube.isEmpty()) {
                watchVideoBtn.visibility = View.GONE
            } else {
                watchVideoBtn.visibility = View.VISIBLE
            }

            // Load image if available
            if (data.image.isNotEmpty()) {
                val imageUrl = "YOUR_BASE_URL${data.image}"
                Glide.with(this@ResultFragment)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_placeholder)
                    .centerCrop()
                    .into(diseaseImageIv)
                diseaseImageCard.visibility = View.VISIBLE
            } else {
                diseaseImageCard.visibility = View.GONE
            }
        }
    }

    private fun openYoutubeVideo() {
        if (youtubeUrl.isEmpty() || youtubeUrl == "-") {
            requireContext().showToast("Video tidak tersedia")
            return
        }

        try {
            // Try to open with YouTube app
            val intent = Intent(Intent.ACTION_VIEW, youtubeUrl.toUri())
            intent.setPackage("com.google.android.youtube")

            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            } else {
                // Fallback to browser
                val browserIntent = Intent(Intent.ACTION_VIEW, youtubeUrl.toUri())
                startActivity(browserIntent)
            }
        } catch (e: Exception) {
            requireContext().showToast("Tidak dapat membuka video: ${e.message}")
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
        binding.scrollView.isVisible = !isLoading
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}