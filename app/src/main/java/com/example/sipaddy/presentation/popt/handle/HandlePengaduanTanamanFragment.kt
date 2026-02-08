package com.example.sipaddy.presentation.popt.handle

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.sipaddy.R
import com.example.sipaddy.data.model.response.DetailPengaduanTanamanResponse
import com.example.sipaddy.data.model.response.VerifikasiPengaduanTanaman
import com.example.sipaddy.databinding.FragmentHandlePengaduanTanamanBinding
import com.example.sipaddy.presentation.ViewModelFactory
import com.example.sipaddy.utils.PengaduanTanamanStatus
import com.example.sipaddy.utils.ResultState
import com.example.sipaddy.utils.formatDateTime
import com.example.sipaddy.utils.showToast
import com.example.sipaddy.utils.toStatusText
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone


class HandlePengaduanTanamanFragment : Fragment() {

    private var _binding: FragmentHandlePengaduanTanamanBinding? = null
    private val binding get() = _binding!!

    private val args: HandlePengaduanTanamanFragmentArgs by navArgs()

    private val viewModel: HandlePengaduanTanamanViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHandlePengaduanTanamanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObserver()
        setupListener()

        viewModel.fetchPengaduanDetail(args.pengaduanId)
    }

    private fun setupObserver() {
        viewModel.detailResult.observe(viewLifecycleOwner) { result ->
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
                    populateDetail(result.data)
                    updateUIBasedOnStatus(result.data)
                }
            }
        }

        viewModel.handleResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Loading -> {
                    showLoadingButton(true)
                }

                is ResultState.Error -> {
                    showLoadingButton(false)
                    requireContext().showToast(result.message)
                }

                is ResultState.Success -> {
                    showLoadingButton(false)
                    onHandleSuccess()
                }
            }
        }
    }

    private fun updateUIBasedOnStatus(detail: DetailPengaduanTanamanResponse) {
        val pengaduan = detail.pengaduanTanaman
        val verifikasiList = detail.verifikasiPengaduanTanaman

        when (pengaduan.status.lowercase()) {
            PengaduanTanamanStatus.ASSIGNED -> {
                showMulaiPenangananButton()
                hideVerifikasiCard()
            }

            PengaduanTanamanStatus.IN_PROGRESS -> {
                if (verifikasiList.isEmpty()) {
                    showLanjutVerifikasiButton()
                    hideVerifikasiCard()
                } else {
                    hideBottomActionBar()
                    showVerifikasiCard(verifikasiList.first())
                }
            }

            PengaduanTanamanStatus.VERIFIED,
            PengaduanTanamanStatus.COMPLETED -> {
                if (verifikasiList.isNotEmpty()) {
                    hideBottomActionBar()
                    showVerifikasiCard(verifikasiList.first())
                } else {
                    hideBottomActionBar()
                    hideVerifikasiCard()
                }
            }

            else -> {
                hideBottomActionBar()
                hideVerifikasiCard()
            }
        }
    }

    private fun showMulaiPenangananButton() {
        binding.bottomActionBar.isVisible = true
        binding.mulaiPenangananBtn.text = "Mulai Penanganan"
        binding.mulaiPenangananBtn.setOnClickListener {
            showConfirmationDialog()
        }
    }

    private fun showLanjutVerifikasiButton() {
        binding.bottomActionBar.isVisible = true
        binding.mulaiPenangananBtn.text = "Lanjut Verifikasi"
        binding.mulaiPenangananBtn.setOnClickListener {
            navigateToVerifikasi()
        }
    }

    private fun navigateToVerifikasi() {
        val action = HandlePengaduanTanamanFragmentDirections
            .actionNavigationHandlePengaduanTanamanToNavigationVerifikasiPengaduanTanaman(args.pengaduanId)
        findNavController().navigate(action)
    }

    private fun hideVerifikasiCard() {
        binding.verifikasiCard.isVisible = false
    }

    private fun hideBottomActionBar() {
        binding.bottomActionBar.isVisible = false
    }

    private fun populateDetail(detail: DetailPengaduanTanamanResponse) {
        val pengaduan = detail.pengaduanTanaman


        binding.apply {
            statusChip.text = pengaduan.status.toStatusText()


            if (pengaduan.image.isNotEmpty()) {
                imageCard.isVisible = true
                Glide.with(this@HandlePengaduanTanamanFragment)
                    .load(pengaduan.image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_placeholder)
                    .into(pengaduanImageIv)
            } else {
                imageCard.isVisible = false
            }

            pelaporTv.text = pengaduan.pelaporNama
            kelompokTaniTv.text = pengaduan.kelompokTaniNama
            kecamatanTv.text = pengaduan.kecamatanNama

            // Deskripsi
            deskripsiTv.text = pengaduan.deskripsi

            // Lokasi
            latitudeTv.text = pengaduan.latitude
            longitudeTv.text = pengaduan.longitude

            // Tanggal
            createdAtTv.text = pengaduan.createdAt.formatDateTime()


        }
    }


    private fun setupListener() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.mulaiPenangananBtn.setOnClickListener {
            showConfirmationDialog()
        }

        binding.openMapBtn.setOnClickListener {
            openInMaps()
        }

        binding.openMapFloatBtn.setOnClickListener {
            openInMaps()

        }
    }

    private fun openInMaps() {
        val latitude = binding.latitudeTv.text.toString()
        val longitude = binding.longitudeTv.text.toString()

        if (latitude.isNotEmpty() && longitude.isNotEmpty()) {
            try {
                val geoUri = "geo:$latitude,$longitude?q=$latitude,$longitude".toUri()
                val intent = Intent(Intent.ACTION_VIEW, geoUri)

                if (intent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivity(intent)
                } else {
                    requireContext().showToast("Tidak ada aplikasi Maps yang terinstall")
                }
            } catch (e: Exception) {
                requireContext().showToast("Gagal membuka Maps: ${e.message}")
            }
        } else {
            requireContext().showToast("Koordinat lokasi tidak tersedia")
        }
    }

    private fun onHandleSuccess() {
        requireContext().showToast("Penanganan dimulai. Status berubah menjadi \\\"Dalam Proses\\\"")

        binding.statusChip.text = "Dalam Proses"
        binding.statusTv.text = "Dalam Proses"


        val action =
            HandlePengaduanTanamanFragmentDirections.actionNavigationHandlePengaduanTanamanToNavigationVerifikasiPengaduanTanaman(
                args.pengaduanId
            )
        findNavController().navigate(action)
    }

    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Konfirmasi")
            .setMessage(
                "Mulai menangani pengaduan ini?\n\n" +
                        "Status akan berubah menjadi \"Dalam Proses\""
            )
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Ya, Mulai") { dialog, _ ->
                viewModel.handlePengaduan(args.pengaduanId)
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()
    }

    private fun showVerifikasiCard(verifikasi: VerifikasiPengaduanTanaman) {
        binding.verifikasiCard.isVisible = true

        binding.verifikasiCatatanTv.text = verifikasi.catatan ?: "-"
        binding.verifikasiLatitudeTv.text = verifikasi.latitude
        binding.verifikasiLongitudeTv.text = verifikasi.longitude
        binding.verifikasiPoptTv.text = "Oleh: ${verifikasi.poptNama ?: "Unknown"}"
        binding.verifikasiDateTv.text = verifikasi.createdAt.formatDateTime()

        if (verifikasi.fotoVisit.isNotEmpty()) {
            binding.fotoVisitCard.isVisible = true
            Glide.with(this@HandlePengaduanTanamanFragment)
                .load(verifikasi.fotoVisit)
                .centerCrop()
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_placeholder)
                .into(binding.fotoVisitIv)
        } else {
            binding.fotoVisitCard.isVisible = false
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
        binding.scrollView.isVisible = !isLoading
        binding.bottomActionBar.isVisible = !isLoading
    }

    private fun showLoadingButton(isLoading: Boolean) {
        binding.mulaiPenangananBtn.isEnabled = !isLoading
        binding.mulaiPenangananBtn.text = if (isLoading) "Memproses..." else "Mulai Penanganan"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}