package com.example.sipaddy.presentation.popt.detail

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.sipaddy.R
import com.example.sipaddy.utils.ResultState
import com.example.sipaddy.databinding.FragmentPoptDetailPengaduanTanamanBinding
import com.example.sipaddy.presentation.ViewModelFactory
import com.example.sipaddy.utils.DateFormatter
import com.example.sipaddy.utils.PengaduanTanamanStatus
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Locale

class PoptDetailPengaduanTanamanFragment : Fragment() {

    private var _binding: FragmentPoptDetailPengaduanTanamanBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PoptDetailPengaduanTanamanViewModel by viewModels {
        ViewModelFactory(requireContext())
    }

    private val args: PoptDetailPengaduanTanamanFragmentArgs by navArgs()

    private var currentPengaduanId: String? = null
    private var fileUrl: String? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            fileUrl?.let { downloadFile(it) }
        } else {
            Toast.makeText(
                requireContext(),
                "Permission diperlukan untuk mengunduh file",
                Toast.LENGTH_SHORT
            ).show()
        }
        fileUrl = null
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPoptDetailPengaduanTanamanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            toolbar.setOnClickListener {
                findNavController().navigateUp()
            }
        }

        val pengaduanTanamanId = args.id
        currentPengaduanId = pengaduanTanamanId
        viewModel.getDetailPengaduanTanaman(pengaduanTanamanId)

        observerDetail()
        observerVerifikasi()
    }



    private fun observerDetail() {
        viewModel.detailResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Loading -> {}
                is ResultState.Error -> {}
                is ResultState.Success -> {
                    showLoading(false)
                    result.data.data?.let { populateData(it.pengaduan) }
                }
            }
        }
    }

    private fun populateData(item: PengaduanTanamanDetailItem) {
        with(binding) {
            Glide.with(requireContext())
                .load(item.image)
                .centerCrop()
                .placeholder(R.drawable.sample_scan)
                .into(pengaduanTanamanIv)

            statusTv.text = item.status
            setStatusBadge(item.status)

            createdDateTv.text = item.createdAt?.let { DateFormatter.formatIsoDate(it) }

            setupVerificationSection(item.status)

            val isCompleted =
                item.status.equals(PengaduanTanamanStatus.COMPLETED, ignoreCase = true)

            if (isCompleted && !item.file.isNullOrEmpty()) {
                downloadReportCard.isVisible = true

                val verificationDate = if (!item.tanggalVerifikasi.isNullOrEmpty()) {
                    formatDate(item.tanggalVerifikasi)
                } else {
                    formatDate(item.updatedAt)
                }

                verificationDateTv.text = getString(
                    R.string.tanggal_verifikasi_label,
                    verificationDate
                )
                verifierNameTv.text = getString(
                    R.string.nama_popt_label,
                    item.popt?.namaLengkap ?: "-"
                )


                if (!item.catatanPopt.isNullOrEmpty()) {
                    binding.catatanPoptContainer.isVisible = true
                    binding.catatanPoptTv.text = item.catatanPopt
                } else {
                    binding.catatanPoptContainer.isVisible = false
                }

                downloadReportBtn.setOnClickListener {
                    checkPermissionAndDownload(item.file)
                }
            } else {
                downloadReportCard.isVisible = false
            }

            userPelaporContainer.isVisible = true
            userNameTv.text = item.user?.namaLengkap

            kelompokTaniTv.text = item.kelompokTani
            alamatTv.text = item.alamat
            kecamatanKabupatenTv.text =
                getString(R.string.kecamatan_kabupaten_label, item.kecamatan, item.kabupaten)

            descriptionTv.text = item.deskripsi


            latitudeTv.text = item.latitude
            longitudeTv.text = item.longitude


            openMapBtn.setOnClickListener {
                openInMaps(item.latitude?.toDouble(), item.longitude?.toDouble())
            }

        }
    }

    private fun observerVerifikasi() {
        viewModel.verifikasiResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Loading -> {
                    showLoading(true)
                }

                is ResultState.Error -> {
                    showLoading(false)
                }

                is ResultState.Success -> {
                    Toast.makeText(
                        requireContext(),
                        "Pengaduan berhasil diverifikasi",
                        Toast.LENGTH_SHORT
                    ).show()
                    currentPengaduanId?.let {
                        viewModel.getDetailPengaduanTanaman(it)
                    }

                }
            }
        }
    }

    private fun openInMaps(latitude: Double?, longitude: Double?) {
        val uri = "geo:$latitude,$longitude?q=$latitude,$longitude".toUri()
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")

        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        } else {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                "https://www.google.com/maps/search/?api=1&query=$latitude,$longitude".toUri()
            )
            startActivity(browserIntent)
        }
    }

    private fun checkPermissionAndDownload(file: String?) {
        if (file.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "File tidak tersedia", Toast.LENGTH_SHORT).show()
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            downloadFile(file)
        } else {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    downloadFile(file)
                }

                else -> {
                    fileUrl = file
                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
        }
    }

    private fun downloadFile(fileUrl: String) {
        try {
            val fileName = "hasil_pemeriksaan_${System.currentTimeMillis()}.pdf"

            val request = DownloadManager.Request(fileUrl.toUri())
                .setTitle("Hasil Pemeriksaan Tanaman")
                .setDescription("Downloading...")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
                .setMimeType("application/pdf")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                request.setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    fileName
                )
            } else {
                request.setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    fileName
                )
            }

            val downloadManager =
                requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)

            Toast.makeText(requireContext(), "Mengunduh file...", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Gagal mengunduh file: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun setupVerificationSection(status: String?) {
        val canVerify = status.equals(PengaduanTanamanStatus.ASSIGNED, ignoreCase = true)


        if (canVerify) {
            showVerificationButton()
        }
    }

    private fun showVerificationButton() {
        binding.toolbar.inflateMenu(R.menu.popt_detail_menu)
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_verify -> {
                    showVerificationDialog()
                    true
                }

                else -> false
            }
        }

    }

    private fun showVerificationDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_verifikasi_pengaduan_tanaman, null)
        val catatanEditText = dialogView.findViewById<TextInputEditText>(R.id.catatanEt)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Verifikasi Pengaduan")
            .setMessage("Apakah Anda yakin ingin memverifikasi pengaduan ini?")
            .setView(dialogView)
            .setPositiveButton("Verifikasi") { dialog, _ ->
                val catatan = catatanEditText.text.toString()
                currentPengaduanId?.let {
                    viewModel.verifikasiPengaduanTanaman(it, catatan)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun setStatusBadge(status: String?) {
        val (backgroundColor, textColor) = when (status) {
            PengaduanTanamanStatus.PENDING -> R.drawable.status_pending_bg to R.color.white

            PengaduanTanamanStatus.ASSIGNED -> R.drawable.status_assigned_label to R.color.white

            PengaduanTanamanStatus.VERIFIED -> R.drawable.status_verified_bg to R.color.white

            PengaduanTanamanStatus.HANDLED -> R.drawable.status_handled_bg to R.color.white

            PengaduanTanamanStatus.COMPLETED -> R.drawable.status_completed_bg to R.color.white

            else -> R.drawable.status_pending_bg to R.color.white
        }

        binding.statusTv.setBackgroundResource(backgroundColor)
        binding.statusTv.setTextColor(ContextCompat.getColor(requireContext(), textColor))
    }

    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
            val date = inputFormat.parse(dateString)
            date?.let { outputFormat.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }


    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}