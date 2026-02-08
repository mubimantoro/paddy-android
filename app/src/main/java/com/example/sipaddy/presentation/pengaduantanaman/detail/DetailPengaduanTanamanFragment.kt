package com.example.sipaddy.presentation.pengaduantanaman.detail

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
import com.example.sipaddy.databinding.FragmentDetailPengaduanTanamanBinding
import com.example.sipaddy.presentation.ViewModelFactory
import com.example.sipaddy.utils.ResultState
import com.example.sipaddy.utils.showToast
import com.example.sipaddy.utils.toStatusColor
import com.example.sipaddy.utils.toStatusText
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone


class DetailPengaduanTanamanFragment : Fragment() {

    private var _binding: FragmentDetailPengaduanTanamanBinding? = null
    private val binding get() = _binding!!

    private val args: DetailPengaduanTanamanFragmentArgs by navArgs()

    private val viewModel: DetailPengaduanTanamanViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDetailPengaduanTanamanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObserver()
        setupListener()
        loadDetailPengaduan()
    }

    private fun loadDetailPengaduan() {
        viewModel.getDetailPengaduanTanaman(args.pengadunId)
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
                    displayDetailPengaduan(result.data)
                }
            }
        }
    }

    private fun setupListener() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.openMapBtn.setOnClickListener {
            openMap()
        }
    }

    private fun displayDetailPengaduan(data: DetailPengaduanTanamanResponse) {
        val pengaduan = data.pengaduanTanaman

        with(binding) {
            // Basic Information
            pelaporTv.text = pengaduan.pelaporNama
            kelompokTaniTv.text = pengaduan.kelompokTaniNama
            kecamatanTv.text = pengaduan.kecamatanNama
            deskripsiTv.text = pengaduan.deskripsi


            statusTv.text = pengaduan.status.toStatusText()
            statusChip.text = pengaduan.status.toStatusText()
            statusChip.setChipBackgroundColorResource(pengaduan.status.toStatusColor())

            // Location
            latitudeTv.text = pengaduan.latitude
            longitudeTv.text = pengaduan.longitude

            // Date
            createdAtTv.text = formatDate(pengaduan.createdAt)
            updatedAtTv.text = formatDate(pengaduan.updatedAt)

            // POPT Assignment
            if (pengaduan.poptNama != null) {
                poptTv.text = pengaduan.poptNama
                poptSectionCard.visibility = View.VISIBLE
            } else {
                poptSectionCard.visibility = View.GONE
            }

            // Image
            if (pengaduan.image.isNotEmpty()) {
                Glide.with(requireContext())
                    .load(pengaduan.image)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_placeholder)
                    .centerCrop()
                    .into(pengaduanImageIv)
                imageCard.visibility = View.VISIBLE
            } else {
                imageCard.visibility = View.GONE
            }

            // Verifikasi Section
            if (data.verifikasiPengaduanTanaman.isNotEmpty()) {
                val verifikasi = data.verifikasiPengaduanTanaman.first()
                verifikasiCatatanTv.text = verifikasi.catatan ?: "Tidak ada catatan"
                verifikasiPoptTv.text = "Oleh: ${verifikasi.poptNama}"
                verifikasiDateTv.text = formatDate(verifikasi.createdAt)
                verifikasiCard.visibility = View.VISIBLE
            } else {
                verifikasiCard.visibility = View.GONE
            }

            // Pemeriksaan Section
            if (data.pemeriksaanPengaduanTanaman.isNotEmpty()) {
                val pemeriksaan = data.pemeriksaanPengaduanTanaman.first()

                // Tampilkan hasil pemeriksaan
                pemeriksaanHasilTv.text = pemeriksaan.hasilPemeriksaan
                pemeriksaanPemeriksaTv.text = "Oleh: ${pemeriksaan.pemeriksaNama}"
                pemeriksaanDateTv.text = formatDate(pemeriksaan.createdAt)

                // Tampilkan file PDF jika ada
                if (!pemeriksaan.file.isNullOrEmpty()) {
                    pemeriksaanFileCard.visibility = View.VISIBLE
                    pemeriksaanFileNameTv.text = "Dokumen Pemeriksaan.pdf"

                    openPemeriksaanFileBtn.setOnClickListener {
                        openPdfFile(pemeriksaan.file)
                    }
                } else {
                    pemeriksaanFileCard.visibility = View.GONE
                }

                pemeriksaanCard.visibility = View.VISIBLE
            } else {
                pemeriksaanCard.visibility = View.GONE
            }
        }
    }

    private fun openMap() {
        viewModel.detailResult.value?.let { state ->
            if (state is ResultState.Success) {
                val pengaduan = state.data.pengaduanTanaman
                val latitude = pengaduan.latitude
                val longitude = pengaduan.longitude

                val gmmIntentUri =
                    "geo:$latitude,$longitude?q=$latitude,$longitude(${pengaduan.deskripsi})".toUri()
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")

                if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivity(mapIntent)
                } else {
                    // Fallback to browser
                    val browserIntent = Intent(
                        Intent.ACTION_VIEW,
                        "https://www.google.com/maps/search/?api=1&query=$latitude,$longitude".toUri()
                    )
                    startActivity(browserIntent)
                }
            }
        }
    }


    private fun openPdfFile(pdfUrl: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(pdfUrl.toUri(), "application/pdf")
            intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY

            val chooser = Intent.createChooser(intent, "Buka PDF dengan")

            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(chooser)
            } else {
                // Fallback: open in browser
                val browserIntent = Intent(Intent.ACTION_VIEW, pdfUrl.toUri())
                startActivity(browserIntent)
            }
        } catch (e: Exception) {
            requireContext().showToast("Tidak dapat membuka file PDF: ${e.message}")
        }
    }

    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(dateString)

            val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
            date?.let { outputFormat.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
        binding.scrollView.isVisible = !isLoading
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}