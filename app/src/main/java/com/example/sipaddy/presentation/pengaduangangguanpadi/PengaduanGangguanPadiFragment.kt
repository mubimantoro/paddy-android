package com.example.sipaddy.presentation.pengaduangangguanpadi

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.sipaddy.databinding.FragmentPengaduanGangguanPadiBinding
import com.example.sipaddy.presentation.ViewModelFactory
import com.example.sipaddy.presentation.home.diagnose.camera.CameraActivity
import com.example.sipaddy.utils.deleteFromUri


class PengaduanGangguanPadiFragment : Fragment() {

    private var isPickImage: Boolean = false
    private var currentPhotoUri: Uri? = null
    private var cameraUri: Uri? = null

    private val binding: FragmentPengaduanGangguanPadiBinding by lazy {
        FragmentPengaduanGangguanPadiBinding.inflate(layoutInflater)
    }

    private val viewModel: PengaduanGangguanPadiViewModel by viewModels {
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
            galleryBtn.setOnClickListener {
                if (!isPickImage) {
                    isPickImage = true
                    startGallery()
                }

            }

            cameraBtn.setOnClickListener {
                val intent = Intent(requireActivity(), CameraActivity::class.java)
                launchIntentCamera.launch(intent)
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
    }

    private val launchIntentCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == CameraActivity.CAMERA_RESULT) {
                cameraUri = it.data?.getStringExtra(CameraActivity.EXTRA_CAMERA_IMAGE)?.toUri()
            }
        }

    private val launchGallery =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            uri?.let { isPickImage = true }
        }

    private fun startGallery() {
        launchGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

}