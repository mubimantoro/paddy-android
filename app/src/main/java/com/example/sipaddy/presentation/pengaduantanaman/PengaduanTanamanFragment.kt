package com.example.sipaddy.presentation.pengaduantanaman

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.sipaddy.R
import com.example.sipaddy.data.ResultState
import com.example.sipaddy.databinding.FragmentPengaduanTanamanBinding
import com.example.sipaddy.presentation.ViewModelFactory
import com.example.sipaddy.utils.createCustomTempFile
import com.example.sipaddy.utils.getImageUri
import com.example.sipaddy.utils.reduceFileImage
import com.example.sipaddy.utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.yalantis.ucrop.UCrop
import java.io.File


class PengaduanTanamanFragment : Fragment() {

    private var currentImageUri: Uri? = null
    private var tempCropFile: File? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient


    private val binding: FragmentPengaduanTanamanBinding by lazy {
        FragmentPengaduanTanamanBinding.inflate(layoutInflater)
    }

    private val viewModel: PengaduanTanamanViewModel by viewModels {
        ViewModelFactory(requireContext())
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions[REQUIRED_PERMISSION] == true -> {
                    startCamera()
                }

                permissions[REQUIRED_PERMISSION_FINE_LOCATION] == true -> {
                    getMyLocation()
                }

                permissions[REQUIRED_PERMISSION_COARSE_LOCATION] == true -> {
                    getMyLocation()
                }

                else -> {
                    showToast("Permission ditolak")
                }
            }
        }


    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this.requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun cameraPermissionGranted() = ContextCompat.checkSelfPermission(
        requireContext(),
        REQUIRED_PERMISSION
    ) == PackageManager.PERMISSION_GRANTED

    private fun locationPermissionGranted() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            REQUIRED_PERMISSION_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            requireContext(),
            REQUIRED_PERMISSION_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        with(binding) {
            backBtn.setOnClickListener {
                view.findNavController().popBackStack()
            }

            cameraBtn.setOnClickListener {
                startCamera()
            }

            galleryBtn.setOnClickListener {
                startGallery()
            }

            submitBtn.setOnClickListener {
                submitPengaduanTanaman()
            }

            locationBtn.setOnClickListener {
                getMyLocation()
            }
        }
    }

    private fun startCamera() {
        if (cameraPermissionGranted()) {
            currentImageUri = getImageUri(requireContext())
            launcherCamera.launch(currentImageUri)
        } else {
            requestPermissionLauncher.launch(arrayOf(REQUIRED_PERMISSION))
        }
    }

    private val launcherCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess: Boolean ->
        if (isSuccess) {
            launchUCrop(currentImageUri!!)
        } else {
            showToast("Gagal mengambil gambar")
        }
    }


    private fun submitPengaduanTanaman() {
        binding.apply {
            val kelompokTani = kelompokTaniEdt.text.toString().trim()
            val alamat = alamatEdt.text.toString().trim()
            val kecamatan = kecamatanEdt.text.toString().trim()
            val kabupaten = kabupatenEdt.text.toString().trim()
            val deskripsi = deskripsiEdt.text.toString().trim()
            val latitude = latitudeEdt.text.toString().trim()
            val longitude = longitudeEdt.text.toString().trim()
            val imageFile = uriToFile(currentImageUri!!, requireContext())
            imageFile.reduceFileImage()

            viewModel.createPengaduanTanaman(
                kelompokTani,
                alamat,
                kecamatan,
                kabupaten,
                deskripsi,
                latitude,
                longitude,
                imageFile
            )

            viewModel.result.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is ResultState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is ResultState.Error -> {
                        binding.progressBar.visibility = View.GONE
                    }

                    is ResultState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        showToast(result.data.message)
                    }
                }

            }
        }
    }

    private fun startGallery() {
        binding.galleryBtn.isEnabled = false
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                launchUCrop(uri)
            } else {
                showToast("Gagal mengambil gambar dari galeri")
                binding.galleryBtn.isEnabled = true
            }
        }

    private fun launchUCrop(sourceUri: Uri) {
        tempCropFile = createCustomTempFile(requireContext())
        val destinationUri = Uri.fromFile(tempCropFile)
        val uCrop = UCrop.of(sourceUri, destinationUri).withAspectRatio(1f, 1f)
        uCrop.getIntent(requireContext()).let {
            uCropLauncher.launch(it)
        }
    }

    private val uCropLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val resultUri = UCrop.getOutput(result.data!!)
                if (resultUri != null) {
                    currentImageUri = resultUri
                    showImage()
                    binding.galleryBtn.isEnabled = true
                }
            } else if (result.resultCode == UCrop.RESULT_ERROR) {
                binding.galleryBtn.isEnabled = true
            } else {
                binding.galleryBtn.isEnabled = true
            }
        }

    private fun showImage() {
        currentImageUri?.let {
            binding.previewIv.setImageURI(it)
            binding.previewIv.visibility = View.VISIBLE
        }
    }


    private fun getMyLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    binding.latitudeEdt.setText(it.latitude.toString())
                    binding.longitudeEdt.setText(it.longitude.toString())
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.location_not_found_label), Toast.LENGTH_SHORT
                    ).show()
                }
            }.addOnFailureListener {
                binding.latitudeEdt.setText("")
                binding.longitudeEdt.setText("")
                Toast.makeText(
                    requireContext(),
                    "Gagal mengambil lokasi: ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
        private const val REQUIRED_PERMISSION_FINE_LOCATION =
            Manifest.permission.ACCESS_FINE_LOCATION
        private const val REQUIRED_PERMISSION_COARSE_LOCATION =
            Manifest.permission.ACCESS_COARSE_LOCATION
    }
}