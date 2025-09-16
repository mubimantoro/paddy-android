package com.example.sipaddy.presentation.home.diagnose.result

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        val photoUri = ResultFragmentArgs.fromBundle(arguments as Bundle).photoUri
        val predictResult = ResultFragmentArgs.fromBundle(arguments as Bundle).predictResult
        val history = ResultFragmentArgs.fromBundle(arguments as Bundle).history

        with(binding) {
            backBtn.setOnClickListener {
                view.findNavController().popBackStack()
            }

            if (history != null) {
                diseaseNameTextView.text = history.label
                dateTextView.text = history.createdAt?.let { DateFormatter.formatIsoDate(it) }
                descriptionText.text = history.description
                causesText.text = history.causes
                val solutionText = history.solutions?.let {
                    setSolutionText(it)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    solutionTv.text = Html.fromHtml(solutionText, Html.FROM_HTML_MODE_COMPACT)
                }
                Glide.with(requireContext())
                    .load(history.photoUrl)
                    .error(R.drawable.sample_scan)
                    .into(resultIv)
            } else {
                if (predictResult != null) {
                    diseaseNameTextView.text = predictResult.label
                    dateTextView.text = date
                    descriptionText.text = predictResult.description
                    causesText.text = predictResult.causes
                    val solutionText = predictResult.solutions?.let {
                        setSolutionText(it)
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        solutionTv.text = Html.fromHtml(solutionText, Html.FROM_HTML_MODE_COMPACT)
                    }
                }
            }
            diagnoseViewModel.setPhotoUri(
                photoUri?.toUri()
            )
            diagnoseViewModel.photoUri.observe(viewLifecycleOwner) {
                showPreview(it)
            }
        }
    }

    private fun showPreview(photoUri: Uri?) {
        binding.resultIv.setImageURI(photoUri)

    }

    private fun setSolutionText(text: String): String {
        val solution = text.trimIndent()
        val htmlText =
            solution.split("\n").joinToString("<br>") { "<li>&nbsp;&nbsp;&nbsp;&nbsp;$it</li>" }
        val finalHtmlText = "<ul>$htmlText</ul>"
        return finalHtmlText
    }

}