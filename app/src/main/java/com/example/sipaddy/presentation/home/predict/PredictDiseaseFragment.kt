package com.example.sipaddy.presentation.home.predict

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.sipaddy.databinding.FragmentPredictDiseaseBinding
import com.example.sipaddy.presentation.ViewModelFactory
import com.example.sipaddy.utils.ImageUtils
import com.example.sipaddy.utils.ResultState
import com.example.sipaddy.utils.reduceFileImage
import com.example.sipaddy.utils.showToast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yalantis.ucrop.UCrop
import java.io.File

class PredictDiseaseFragment : Fragment() {
    private var _binding: FragmentPredictDiseaseBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PredictDiseaseViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private var currentImageFile: File? = null

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            currentImageFile?.let { file ->
                startUCrop(Uri.fromFile(file))
            }
        }
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val file = ImageUtils.uriToFile(it, requireContext())
            startUCrop(Uri.fromFile(file))
        }
    }


    private val uCropLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            result.data?.let { intent ->
                val resultUri = UCrop.getOutput(intent)
                resultUri?.let { uri ->
                    val file = ImageUtils.uriToFile(uri, requireContext())
                    currentImageFile = file.reduceFileImage()
                    binding.previewIv.setImageURI(uri)
                    binding.imagePreviewCard.visibility = View.VISIBLE
                    binding.predictBtn.isEnabled = true
                }
            }
        }
    }

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            navigateToCameraX()
        } else {
            requireContext().showToast("Permission kamera ditolak")

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
        handleCameraResult()
    }

    private fun handleCameraResult() {
        // Get image URI from CameraX if available
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<String>("imageUri")?.observe(viewLifecycleOwner) { uriString ->
                val uri = uriString.toUri()
                startUCrop(uri)
                // Clear the result
                findNavController().currentBackStackEntry?.savedStateHandle?.remove<String>("imageUri")
            }
    }


    private fun setupListener() {
        binding.chooseImageBtn.setOnClickListener {
            showImageSourceDialog()
        }

        binding.removeImageBtn.setOnClickListener {
            removeImage()
        }

        binding.predictBtn.setOnClickListener {
            predictDisease()
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupObserver() {
        viewModel.predictResult.observe(viewLifecycleOwner) { result ->
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
                    navigateToResult(result.data.id)
                }
            }
        }
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("Kamera", "Galeri")
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Pilih Sumber Gambar")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> checkCameraPermission()
                    1 -> openGallery()
                }
            }
            .show()
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                navigateToCameraX()
            }

            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun navigateToCameraX() {
        val action = PredictDiseaseFragmentDirections
            .actionPredictDiseaseFragmentToNavigationCamerax()
        findNavController().navigate(action)
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun startUCrop(sourceUri: Uri) {
        val destinationUri = Uri.fromFile(ImageUtils.createCustomTempFile(requireContext()))
        val options = UCrop.Options().apply {
            setCompressionQuality(80)
            setFreeStyleCropEnabled(true)
            setToolbarTitle("Crop Image")
        }

        val uCropIntent = UCrop.of(sourceUri, destinationUri)
            .withOptions(options)
            .getIntent(requireContext())

        uCropLauncher.launch(uCropIntent)

    }


    private fun removeImage() {
        currentImageFile = null
        binding.imagePreviewCard.visibility = View.GONE
        binding.previewIv.setImageDrawable(null)
        binding.predictBtn.isEnabled = false
    }

    private fun predictDisease() {
        currentImageFile?.let { file ->
            viewModel.predictDisease(file)
        } ?: run {
            requireContext().showToast("Silakan pilih gambar terlebih dahulu")
        }
    }

    private fun navigateToResult(predictionId: String) {
        val action = PredictDiseaseFragmentDirections
            .actionNavigationPredictDiseaseToNavigationResult(predictionId)
        findNavController().navigate(action)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.predictBtn.isEnabled = !isLoading && currentImageFile != null
        binding.chooseImageBtn.isEnabled = !isLoading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}