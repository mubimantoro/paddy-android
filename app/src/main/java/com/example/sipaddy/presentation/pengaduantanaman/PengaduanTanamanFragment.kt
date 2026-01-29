package com.example.sipaddy.presentation.pengaduantanaman

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.sipaddy.data.model.response.KecamatanResponse
import com.example.sipaddy.data.model.response.KelompokTaniResponse
import com.example.sipaddy.databinding.FragmentPengaduanTanamanBinding
import com.example.sipaddy.presentation.ViewModelFactory
import com.example.sipaddy.utils.ImageUtils
import com.example.sipaddy.utils.ResultState
import com.example.sipaddy.utils.reduceFileImage
import com.example.sipaddy.utils.showToast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yalantis.ucrop.UCrop
import java.io.File


class PengaduanTanamanFragment : Fragment() {

    private var _binding: FragmentPengaduanTanamanBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PengaduanTanamanViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private var currentImageFile: File? = null
    private var selectedKecamatanId: Int? = null
    private var selectedKelompokTaniId: Int? = null
    private var kelompokTaniList = mutableListOf<KelompokTaniResponse>()
    private var kecamatanList = mutableListOf<KecamatanResponse>()

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
                    binding.ivPreview.setImageURI(uri)
                    binding.imageNameTv.text = file.name
                    binding.imagePreviewCard.visibility = View.VISIBLE
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
            binding.useLocationCb.isChecked = false
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
        _binding = FragmentPengaduanTanamanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        setupListener()
        loadInitialData()
        setupObserver()
    }

    private fun loadInitialData() {
        viewModel.loadKelompokTani()
        viewModel.getKecamatan()
    }

    private fun setupObserver() {
        viewModel.kelompokTaniResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Loading -> {}
                is ResultState.Error -> {
                    requireContext().showToast("Gagal memuat kelompok tani: ${result.message}")
                }

                is ResultState.Success -> {
                    kelompokTaniList.clear()
                    kelompokTaniList.addAll(result.data)
                    setupKelompokTaniSpinner()
                }
            }
        }

        viewModel.kecamatanResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Loading -> {}
                is ResultState.Error -> {
                    requireContext().showToast("Gagal memuat kecamatan: ${result.message}")
                }

                is ResultState.Success -> {
                    kecamatanList.clear()
                    kecamatanList.addAll(result.data)
                    setupKecamatanSpinner()
                }
            }
        }

        viewModel.result.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Loading -> {
                    showLoading(true)
                }

                is ResultState.Error -> {
                    showLoading(false)
                    requireContext().showToast("Gagal mengirim pengaduan: ${result.message}")
                }

                is ResultState.Success -> {
                    showLoading(false)
                    requireContext().showToast("Pengaduan berhasil dikirim")
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun setupKelompokTaniSpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            kelompokTaniList.map { it.nama }
        )
        binding.kelompokTaniSpinner.setAdapter(adapter)
        binding.kelompokTaniSpinner.setOnItemClickListener { _, _, position, _ ->
            selectedKelompokTaniId = kelompokTaniList[position].id
        }
    }

    private fun setupKecamatanSpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            kecamatanList.map { it.nama }
        )
        binding.kecamatanSpinner.setAdapter(adapter)
        binding.kecamatanSpinner.setOnItemClickListener { _, _, position, _ ->
            selectedKecamatanId = kecamatanList[position].id
        }

    }

    private fun setupListener() {
        binding.chooseImageBtn.setOnClickListener {
            showImageSourceDialog()
        }


        binding.removeImageBtn.setOnClickListener {
            removeImage()
        }

        binding.useLocationCb.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkLocationPermission()
            } else {
                clearLocation()
            }
        }

        binding.btnSubmit.setOnClickListener {
            submitPengaduan()
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
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
        binding.imagePreviewCard.visibility = View.GONE
        binding.ivPreview.setImageDrawable(null)
        binding.imageNameTv.text = ""
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
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    binding.latitudeEt.setText(it.latitude.toString())
                    binding.longitudeEt.setText(it.longitude.toString())
                    binding.latitudeEt.isEnabled = false
                    binding.longitudeEt.isEnabled = false
                } ?: run {
                    binding.useLocationCb.isChecked = false
                    requireContext().showToast("Gagal mendapatkan lokasi. Pastikan GPS aktif.")
                }
            }.addOnFailureListener {
                binding.useLocationCb.isChecked = false
                requireContext().showToast("Gagal mendapatkan lokasi: ${it.message}")
            }
        }
    }

    private fun clearLocation() {
        binding.latitudeEt.setText("")
        binding.longitudeEt.setText("")
        binding.latitudeEt.isEnabled = true
        binding.longitudeEt.isEnabled = true
    }

    private fun submitPengaduan() {
        val deskripsi = binding.deskripsiEt.text.toString().trim()
        val latitude = binding.latitudeEt.text.toString().trim()
        val longitude = binding.longitudeEt.text.toString().trim()

        if (selectedKelompokTaniId == null) {
            requireContext().showToast("Pilih kelompok tani")
            return
        }

        if (selectedKecamatanId == null) {
            requireContext().showToast("Pilih kecamatan")
            return
        }

        if (deskripsi.isEmpty()) {
            binding.deskripsiEt.error = "Deskripsi tidak boleh kosong"
            return
        }

        if (latitude.isEmpty()) {
            binding.latitudeEt.error = "Latitude tidak boleh kosong"
            return
        }

        if (longitude.isEmpty()) {
            binding.longitudeEt.error = "Longitude tidak boleh kosong"
            return
        }

        viewModel.submitPengaduan(
            kelompokTaniId = selectedKelompokTaniId!!,
            kecamatanId = selectedKecamatanId!!,
            deskripsi = deskripsi,
            latitude = latitude,
            longitude = longitude,
            imageFile = currentImageFile
        )
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSubmit.isEnabled = !isLoading
        binding.chooseImageBtn.isEnabled = !isLoading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}