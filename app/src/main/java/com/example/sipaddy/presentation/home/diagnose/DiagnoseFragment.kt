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
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


class DiagnoseFragment : Fragment() {

    private var cameraPhotoUri: Uri? = null
    private var tempCropFile: File? = null
    private var isPickPhoto: Boolean = false
    private var currentPhotoUri: Uri? = null
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
                currentPhotoUri?.let { deleteFromUri(it) }
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
                if (!isPickPhoto) {
                    isPickPhoto = true
                    startGallery()
                }
            }

            activity?.onBackPressedDispatcher?.addCallback(
                viewLifecycleOwner,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        currentPhotoUri?.let { deleteFromUri(it) }
                        view.findNavController().popBackStack()
                    }
                }
            )

            viewModel.photoUri.observe(viewLifecycleOwner) {
                currentPhotoUri = it
                showPreview(currentPhotoUri)
            }
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CameraActivity.CAMERA_RESULT) {
            cameraPhotoUri = it.data?.getStringExtra(CameraActivity.EXTRA_CAMERA_IMAGE)?.toUri()
            launchUCrop(cameraPhotoUri!!)
        }

    }

    private val launchGallery =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            uri?.let { launchUCrop(it) } ?: run { isPickPhoto = false }
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
                cameraPhotoUri?.let { deleteFromUri(it) }
                tempCropFile?.delete()
                isPickPhoto = false
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
        if (currentPhotoUri != null) {
            currentPhotoUri?.let { uri ->
                val photoFile = File(uri.path.toString())
                showLoading(true)

                val requestPhotoFile = photoFile.asRequestBody("image/jpeg".toMediaType())
                val multipartBody = MultipartBody.Part.createFormData(
                    "photo",
                    photoFile.name,
                    requestPhotoFile
                )

                viewModel.predict(multipartBody)

                viewModel.resultPredict.observe(viewLifecycleOwner) { result ->
                    if (result != null) {
                        when (result) {
                            is ResultState.Loading -> {
                                showLoading(true)
                            }

                            is ResultState.Error -> {
                                showLoading(false)
                                bottomSheetDialog(
                                    requireContext(),
                                    getString(R.string.failed_to_predict_label),
                                    R.drawable.error_image,
                                    buttonColorResId = R.color.red,
                                    onClick = {}
                                )
                            }

                            is ResultState.Success -> {
                                showLoading(false)
                                val toResultFragment =
                                    DiagnoseFragmentDirections.actionNavigationDiagnoseToResultFragment(
                                        currentPhotoUri.toString(),
                                        result.data.data,
                                        null,
                                    )
                                isPickPhoto = false
                                view.findNavController().navigate(toResultFragment)
                            }


                        }
                    }

                }
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
        currentPhotoUri?.let { deleteFromUri(it) }
        val photoCompress = uriToFile(uri, requireContext()).reduceFileImage()
        viewModel.setPhotoUri(photoCompress.toUri())
        tempCropFile?.let { deleteFromUri(it.toUri()) }
        cameraPhotoUri?.let { deleteFromUri(it) }
        isPickPhoto = false
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