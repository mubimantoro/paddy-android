package com.example.sipaddy.presentation.home.diagnose.result

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.sipaddy.R
import com.example.sipaddy.databinding.FragmentResultBinding
import com.example.sipaddy.presentation.ViewModelFactory
import com.example.sipaddy.presentation.home.diagnose.DiagnoseViewModel
import com.example.sipaddy.utils.DateFormatter
import com.example.sipaddy.utils.date

class ResultFragment : Fragment() {

    private val binding: FragmentResultBinding by lazy {
        FragmentResultBinding.inflate(layoutInflater)
    }

    private val diagnoseViewModel: DiagnoseViewModel by viewModels {
        ViewModelFactory(requireContext())
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageUri = ResultFragmentArgs.fromBundle(arguments as Bundle).imageUri
        val predictResult = ResultFragmentArgs.fromBundle(arguments as Bundle).predictResult
        val history = ResultFragmentArgs.fromBundle(arguments as Bundle).disease

        with(binding) {
            backBtn.setOnClickListener {
                view.findNavController().popBackStack()
            }

            if (history != null) {
                diseaseLabelTv.text = history.disease
                dateTv.text = history.createdAt?.let { DateFormatter.formatIsoDate(it) }
                descTv.text = history.description
                causesTv.text = history.causes

                history.confidenceScore.let {
                    confidenceTv.text = String.format("%.1f%%", it)
                }

                val solutionText = history.solutions?.let {
                    setSolutionText(it)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    solutionTv.text = Html.fromHtml(solutionText, Html.FROM_HTML_MODE_COMPACT)
                }

                setupUrlSolution(history.urlSolution)


                Glide.with(requireContext())
                    .load(history.imageUrl)
                    .error(R.drawable.sample_scan)
                    .into(resultIv)

            } else {
                if (predictResult != null) {
                    diseaseLabelTv.text = predictResult.disease
                    dateTv.text = date
                    descTv.text = predictResult.description
                    causesTv.text = predictResult.causes

                    confidenceTv.text = String.format("%.1f%%", predictResult.confidenceScore)

                    val solutionText = predictResult.solutions?.let {
                        setSolutionText(it)
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        solutionTv.text = Html.fromHtml(solutionText, Html.FROM_HTML_MODE_COMPACT)
                    }

                    setupUrlSolution(predictResult.urlSolution)

                }
                imageUri?.let {
                    resultIv.setImageURI(it.toUri())
                }
            }
        }
    }

    private fun setupUrlSolution(urlSolution: String?) {
        with(binding) {
            if (urlSolution.isNullOrEmpty()) {
                videoContainer.visibility = View.GONE
            } else {
                videoContainer.visibility = View.VISIBLE
                youtubeCard.setOnClickListener {
                    openYoutubeVideo(urlSolution)
                }
            }
        }
    }

    private fun openYoutubeVideo(url: String) {
        try {
            val videoId = extractYoutubeVideoId(url)

            if (videoId != null) {
                val appIntent = Intent(Intent.ACTION_VIEW, "vnd.youtube:$videoId".toUri())
                appIntent.putExtra("force_fullscreen", true)
                startActivity(appIntent)
            } else {
                openInBrowser(url)
            }
        } catch (e: ActivityNotFoundException) {
            openInBrowser(url)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Gagal membuka video", Toast.LENGTH_SHORT).show()
        }
    }

    private fun extractYoutubeVideoId(url: String): String? {
        val patterns = listOf(
            "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*",
            "(?<=\\?si=)[^#\\&\\?\\n]*"
        )

        patterns.forEach { pattern ->
            val regex = pattern.toRegex()
            val match = regex.find(url)
            if (match != null) {
                return match.value.split("?").first()
            }
        }
        return null
    }

    private fun setSolutionText(text: String): String {
        val solution = text.trimIndent()
        val htmlText =
            solution.split("\n").joinToString("<br>") { "<li>&nbsp;&nbsp;&nbsp;&nbsp;$it</li>" }
        val finalHtmlText = "<ul>$htmlText</ul>"
        return finalHtmlText
    }

    private fun openInBrowser(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, url.toUri())
        startActivity(browserIntent)
    }

}