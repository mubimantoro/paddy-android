package com.example.sipaddy.presentation.home.diagnose

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import com.example.sipaddy.R
import com.example.sipaddy.data.ResultState
import com.example.sipaddy.databinding.FragmentDiagnoseBinding
import com.example.sipaddy.presentation.ViewModelFactory
import com.example.sipaddy.presentation.home.diagnose.camera.CameraActivity
import com.example.sipaddy.utils.bottomSheetDialog
import com.example.sipaddy.utils.createCustomTempFile
import com.example.sipaddy.utils.deleteFromUri
import com.example.sipaddy.utils.gone
import com.example.sipaddy.utils.reduceFileImage
import com.example.sipaddy.utils.show
import com.example.sipaddy.utils.uriToFile
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


class DiagnoseFragment : Fragment() {

    private var cameraImageUri: Uri? = null
    private var tempCropFile: File? = null
    private var isPickImage: Boolean = false
    private var currentImageUri: Uri? = null
    private var loadingDialog: AlertDialog? = null

    private val binding: FragmentDiagnoseBinding by lazy {
        FragmentDiagnoseBinding.inflate(layoutInflater)
    }

    private val viewModel: DiagnoseViewModel by viewModels {
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

        with(binding) {
            backBtn.setOnClickListener {
                currentImageUri?.let { deleteFromUri(it) }
                view.findNavController().popBackStack()
            }

            diagnoseBtn.setOnClickListener { view ->
                predict(view)
            }


            cameraCardBtn.setOnClickListener {
                val intent = Intent(requireActivity(), CameraActivity::class.java)
                launcherIntentCamera.launch(intent)
            }

            galleryCardBtn.setOnClickListener {
                if (!isPickImage) {
                    isPickImage = true
                    startGallery()
                }
            }

            activity?.onBackPressedDispatcher?.addCallback(
                viewLifecycleOwner,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        currentImageUri?.let { deleteFromUri(it) }
                        view.findNavController().popBackStack()
                    }
                }
            )

            viewModel.imageUri.observe(viewLifecycleOwner) {
                currentImageUri = it
                showPreview(currentImageUri)
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.resultPredict.collect { result ->
                        when (result) {
                            is ResultState.Loading -> {
                                showLoading(true)
                            }

                            is ResultState.Error -> {
                                showLoading(false)
                                bottomSheetDialog(
                                    requireContext(),
                                    result.error,
                                    R.drawable.error_image,
                                    buttonColorResId = R.color.red,
                                    onClick = {}
                                )
                            }

                            is ResultState.Success -> {
                                showLoading(false)
                                val toResultFragment =
                                    DiagnoseFragmentDirections.actionNavigationDiagnoseToResultFragment(
                                        currentImageUri.toString(),
                                        result.data.data,
                                        null,
                                    )
                                isPickImage = false
                                view.findNavController().navigate(toResultFragment)
                            }
                        }
                    }
                }
            }
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CameraActivity.CAMERA_RESULT) {
            cameraImageUri = it.data?.getStringExtra(CameraActivity.EXTRA_CAMERA_IMAGE)?.toUri()
            launchUCrop(cameraImageUri!!)
        }

    }

    private val launchGallery =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            uri?.let { launchUCrop(it) } ?: run { isPickImage = false }
        }

    private fun startGallery() {
        launchGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val uCropLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val resultUri = UCrop.getOutput(result.data!!)
                resultUri?.let { uri ->
                    handlePhotoUri(uri)
                }
            } else {
                cameraImageUri?.let { deleteFromUri(it) }
                tempCropFile?.delete()
                isPickImage = false
            }
        }

    private fun launchUCrop(sourceUri: Uri) {
        tempCropFile = createCustomTempFile(requireContext())
        val destinationUri = Uri.fromFile(tempCropFile)
        val uCrop = UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(1f, 1f)
        uCrop.getIntent(requireContext()).let {
            uCropLauncher.launch(it)
        }
    }

    private fun predict(view: View) {
        if (currentImageUri != null) {
            currentImageUri?.let { uri ->
                val imageFile = File(uri.path.toString())
                showLoading(true)

                val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
                val multipartBody = MultipartBody.Part.createFormData(
                    "image",
                    imageFile.name,
                    requestImageFile
                )

                showLoading(true)

                viewModel.predict(multipartBody)


            }
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.please_select_image_label),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun handlePhotoUri(uri: Uri) {
        currentImageUri?.let { deleteFromUri(it) }
        val imageCompress = uriToFile(uri, requireContext()).reduceFileImage()
        viewModel.setImageUri(imageCompress.toUri())
        tempCropFile?.let { deleteFromUri(it.toUri()) }
        cameraImageUri?.let { deleteFromUri(it) }
        isPickImage = false
    }

    private fun showPreview(uri: Uri?, isShow: Boolean = true) {
        if (uri != null && isShow) {
            binding.previewIv.setImageURI(uri)
            binding.previewIv.show()
            binding.previewTv.show()
        } else {
            binding.previewIv.gone()
            binding.previewTv.gone()
        }

    }

    private fun showLoading(isLoading: Boolean) {
        showLoadingDialog(isLoading)
    }


    private fun showLoadingDialog(state: Boolean) {
        if (state) {
            if (loadingDialog == null) {
                loadingDialog = MaterialAlertDialogBuilder(requireContext())
                    .setMessage("Mohon Menunggu...")
                    .setCancelable(false)
                    .create()
            }
            loadingDialog?.show()
        } else {
            loadingDialog?.dismiss()
            loadingDialog = null
        }
    }

}