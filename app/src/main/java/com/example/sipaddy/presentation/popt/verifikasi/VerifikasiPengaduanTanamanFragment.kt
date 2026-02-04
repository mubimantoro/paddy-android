package com.example.sipaddy.presentation.popt.verifikasi

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.sipaddy.data.model.response.PengaduanTanamanResponse
import com.example.sipaddy.databinding.FragmentVerifikasiPengaduanTanamanBinding
import com.example.sipaddy.presentation.ViewModelFactory
import com.example.sipaddy.utils.ImageUtils
import com.example.sipaddy.utils.ImageUtils.reduceFileImage
import com.example.sipaddy.utils.ResultState
import com.example.sipaddy.utils.showToast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yalantis.ucrop.UCrop
import java.io.File


class VerifikasiPengaduanTanamanFragment : Fragment() {

    private var _binding: FragmentVerifikasiPengaduanTanamanBinding? = null
    private val binding get() = _binding!!

    private val args: VerifikasiPengaduanTanamanFragmentArgs by navArgs()

    private val viewModel: VerifikasiPengaduanTanamanViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private var currentImageFile: File? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

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

                    // Show preview
                    binding.fotoPreviewIv.setImageURI(uri)
                    showPhotoPreview()
                }
            }
        }
    }

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getCurrentLocation()
        } else {
            requireContext().showToast("Permission lokasi ditolak")
        }
    }

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            requireContext().showToast("Permission kamera ditolak")
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentVerifikasiPengaduanTanamanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListener()
        setupObserver()
        loadPengaduanDetail()

    }

    private fun loadPengaduanDetail() {
        viewModel.fetchPengaduanDetail(args.pengaduanId)
    }

    private fun setupObserver() {
        viewModel.detailResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Loading -> {
                }

                is ResultState.Error -> {
                    requireContext().showToast("Gagal memuat detail: ${result.message}")
                }

                is ResultState.Success -> {
                    populateHeader(result.data)
                }
            }
        }

        viewModel.verifikasiresult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Loading -> {
                    showLoading(true)
                }

                is ResultState.Error -> {
                    showLoading(false)
                    requireContext().showToast("Gagal mengirim verifikasi: ${result.message}")
                }

                is ResultState.Success -> {
                    showLoading(false)
                    requireContext().showToast("Verifikasi berhasil dikirim")
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun populateHeader(item: PengaduanTanamanResponse) {
        binding.pelaporTv.text = item.pelaporNama
        binding.statusChip.text = item.status
    }

    private fun setupListener() {
        binding.ambilFotoBtn.setOnClickListener {
            showImageSourceDialog()
        }

        binding.pilihGaleriBtn.setOnClickListener {
            openGallery()
        }

        binding.hapusFotoBtn.setOnClickListener {
            removeImage()
        }

        binding.refreshLokasiBtn.setOnClickListener {
            checkLocationPermission()
        }

        binding.kirimVerifikasiBtn.setOnClickListener {
            submitVerifikasi()
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
                openCamera()
            }

            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun openCamera() {
        currentImageFile = ImageUtils.createImageFile(requireContext())
        val uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            currentImageFile!!
        )
        cameraLauncher.launch(uri)
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun startUCrop(sourceUri: Uri) {
        val destinationUri = Uri.fromFile(ImageUtils.createCustomTempFile(requireContext()))
        val options = UCrop.Options().apply {
            setCompressionQuality(80)
            setFreeStyleCropEnabled(true)
        }

        val uCropIntent = UCrop.of(sourceUri, destinationUri)
            .withOptions(options)
            .getIntent(requireContext())

        uCropLauncher.launch(uCropIntent)
    }

    private fun removeImage() {
        currentImageFile = null
        showPhotoPlaceholder()
    }

    private fun showPhotoPreview() {
        binding.placeholderContent.visibility = View.GONE
        binding.fotoPreviewIv.visibility = View.VISIBLE
        binding.hapusFotoBtn.visibility = View.VISIBLE
    }


    private fun showPhotoPlaceholder() {
        binding.placeholderContent.visibility = View.VISIBLE
        binding.fotoPreviewIv.visibility = View.GONE
        binding.hapusFotoBtn.visibility = View.GONE
        binding.fotoPreviewIv.setImageDrawable(null)
    }

    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getCurrentLocation()
            }

            else -> {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun getCurrentLocation() {
        binding.gpsStatusTv.visibility = View.GONE

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    binding.latitudeTv.text = it.latitude.toString()
                    binding.longitudeTv.text = it.longitude.toString()
                } ?: run {
                    binding.gpsStatusTv.visibility = View.VISIBLE
                    requireContext().showToast("Gagal mendapatkan lokasi. Pastikan GPS aktif.")
                }
            }.addOnFailureListener {
                binding.gpsStatusTv.visibility = View.VISIBLE
                requireContext().showToast("Gagal mendapatkan lokasi: ${it.message}")
            }
        }
    }

    private fun submitVerifikasi() {
        val latitude = binding.latitudeTv.text.toString().trim()
        val longitude = binding.longitudeTv.text.toString().trim()
        val catatan = binding.catatanEt.text.toString().trim()

        if (latitude.isEmpty() || longitude.isEmpty()) {
            requireContext().showToast("Tunggu hingga lokasi GPS berhasil didapat")
            return
        }

        if (catatan.isEmpty()) {
            binding.catatanInputLayout.error = "Catatan pemeriksaan tidak boleh kosong"
            return
        } else {
            binding.catatanInputLayout.error = null
        }

        // Submit
        viewModel.submitVerikasi(
            pengaduanId = args.pengaduanId,
            fotoFile = currentImageFile,
            latitude = latitude,
            longitude = longitude,
            catatan = catatan
        )
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.kirimVerifikasiBtn.isEnabled = !isLoading
        binding.scrollView.isEnabled = !isLoading
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}