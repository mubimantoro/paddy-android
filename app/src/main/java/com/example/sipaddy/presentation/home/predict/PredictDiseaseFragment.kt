package com.example.sipaddy.presentation.home.predict

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.sipaddy.R
import com.example.sipaddy.data.model.response.PredictResponse
import com.example.sipaddy.databinding.FragmentPredictDiseaseBinding
import com.example.sipaddy.presentation.ViewModelFactory
import com.example.sipaddy.utils.ImageUtils
import com.example.sipaddy.utils.ResultState
import com.example.sipaddy.utils.gone
import com.example.sipaddy.utils.showToast
import com.example.sipaddy.utils.visible
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class PredictDiseaseFragment : Fragment() {
    private var _binding: FragmentPredictDiseaseBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PredictDiseaseViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private var currentPhotoUri: Uri? = null
    private var croppedImageFile: File? = null

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            startCrop(it)
        }
    }

    private val launcherUCrop = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val resultUri = UCrop.getOutput(result.data ?: return@registerForActivityResult)
        resultUri?.let { uri ->
            handleCroppedImage(uri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPredictDiseaseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObserver()
        setupListener()
        observerCameraResult()
    }

    private fun observerCameraResult() {
        TODO("Not yet implemented")
    }

    private fun setupListener() {
        binding.cameraBtn.setOnClickListener {
            openCamera()
        }
        binding.galleryBtn.setOnClickListener {
            openGallery()
        }

        binding.changeImageBtn.setOnClickListener {
            showImageSourceDialog()
        }

        binding.predictBtn.setOnClickListener {
            postPrediction()
        }
    }

    private fun postPrediction() {
        val imageFile = croppedImageFile

        if (imageFile == null) {
            requireContext().showToast("Pilih gambar terlebih dahulu")
            return
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Mulai Diagnosa?")
            .setMessage("Pastikan gambar sudah jelas dan fokus pada bagian penyakit tanaman padi.")
            .setPositiveButton("Ya, Diagnosa") { dialog, _ ->
                dialog.dismiss()

                lifecycleScope.launch {
                    val compressedFile = withContext(Dispatchers.IO) {
                        ImageUtils.compressImage(requireContext(), Uri.fromFile(imageFile))
                    }

                    if (compressedFile != null) {
                        viewModel.predictDisease(compressedFile)
                    } else {
                        requireContext().showToast("Gagal memproses gambar")
                    }
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun setupObserver() {
        viewModel.selectedImage.observe(viewLifecycleOwner) { imageFile ->
            if (imageFile != null) {
                showPreview(imageFile)
            } else {
                hidePreview()
            }
        }

        viewModel.predictResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Loading -> {
                    showLoading(true)
                }

                is ResultState.Error -> {
                    showLoading(false)
                    showErrorDialog(result.message)
                }

                is ResultState.Success -> {
                    showLoading(false)
                    navigateToResult(result.data)
                }
            }
        }
    }

    private fun navigateToResult(data: PredictResponse) {
        try {
            val action =
                PredictDiseaseFragmentDirections.actionNavigationPredictDiseaseToNavigationResult(
                    data.id
                )
            findNavController().navigate(action)


        } catch (e: Exception) {
            requireContext().showToast("Prediksi berhasil: ${data.disease}")
        }
    }

    private fun showImageSourceDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Pilih Sumber Gambar")
            .setMessage("Dari mana Anda ingin mengambil gambar?")
            .setPositiveButton("Kamera") { dialog, _ ->
                dialog.dismiss()
                openCamera()
            }
            .setNegativeButton("Galeri") { dialog, _ ->
                dialog.dismiss()
                openGallery()
            }
            .setNeutralButton("Batal", null)
            .show()
    }


    private fun openCamera() {
        try {
            findNavController().navigate(R.id.action_predictDiseaseFragment_to_navigation_camerax)
        } catch (e: Exception) {
            requireContext().showToast("Fitur kamera belum tersedia")
        }
    }

    private fun openGallery() {
        launcherGallery.launch("image/*")
    }

    private fun showErrorDialog(message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Diagnosa Gagal")
            .setMessage(message)
            .setIcon(R.drawable.ic_error)
            .setPositiveButton("Coba Lagi") { dialog, _ ->
                dialog.dismiss()
            }
            .setNegativeButton("Ganti Gambar") { dialog, _ ->
                dialog.dismiss()
                showImageSourceDialog()
            }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visible()
            binding.scrollView.gone()
        } else {
            binding.progressBar.gone()
            binding.scrollView.visible()
        }
    }


    private fun showPreview(imageFile: File) {
        binding.imagePickerLayout.gone()

        binding.imagePreviewLayout.visible()

        Glide.with(this)
            .load(imageFile)
            .centerCrop()
            .placeholder(R.drawable.ic_image_placeholder)
            .error(R.drawable.ic_error)
            .into(binding.previewIv)

    }

    private fun hidePreview() {
        binding.imagePickerLayout.visible()

        binding.imagePreviewLayout.gone()

        binding.previewIv.setImageDrawable(null)
    }


    private fun handleCroppedImage(uri: Uri) {
        val imageFile = ImageUtils.createTempFileFromUri(
            requireContext(),
            uri,
            "final_${System.currentTimeMillis()}.jpg"
        )

        croppedImageFile = imageFile
        viewModel.setSelectedImage(imageFile)
    }

    private fun startCrop(sourceUri: Uri) {
        val destinationUri = Uri.fromFile(
            File(requireContext().cacheDir, "cropped_${System.currentTimeMillis()}.jpg")
        )

        val uCrop = UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(1024, 1024)
            .withOptions(UCrop.Options().apply {
                setCompressionQuality(80)
                setHideBottomControls(false)
                setFreeStyleCropEnabled(false)
                setToolbarTitle("Crop Gambar")
            })

        launcherUCrop.launch(uCrop.getIntent(requireContext()))

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}