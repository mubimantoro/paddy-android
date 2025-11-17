package com.example.sipaddy.presentation.pengaduantanaman.detail

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
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
import com.example.sipaddy.data.ResultState
import com.example.sipaddy.data.network.response.PengaduanTanamanDetailItem
import com.example.sipaddy.databinding.FragmentDetailPengaduanTanamanBinding
import com.example.sipaddy.presentation.ViewModelFactory
import com.example.sipaddy.utils.DateFormatter
import java.text.SimpleDateFormat
import java.util.Locale


class DetailPengaduanTanamanFragment : Fragment() {

    private var _binding: FragmentDetailPengaduanTanamanBinding? = null
    private val binding get() = _binding!!

    private val args: DetailPengaduanTanamanFragmentArgs by navArgs()

    private val viewModel: DetailPengaduanTanamanViewModel by viewModels {
        ViewModelFactory(requireContext())
    }

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
        _binding = FragmentDetailPengaduanTanamanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        val pengaduanTanamanId = args.id
        viewModel.getDetailPengaduanTanaman(pengaduanTanamanId)


        setupObserver()

    }

    private fun setupObserver() {
        viewModel.detailResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Loading -> {
                    showLoading(true)
                }

                is ResultState.Error -> {
                    showLoading(false)
                }

                is ResultState.Success -> {
                    showLoading(false)
                    result.data.data?.let { displayDetailPengaduanTanaman(it.pengaduan) }
                }
            }
        }
    }

    private fun displayDetailPengaduanTanaman(item: PengaduanTanamanDetailItem) {
        with(binding) {
            Glide.with(requireContext())
                .load(item.image)
                .placeholder(R.drawable.sample_scan)
                .error(R.drawable.sample_scan)
                .centerCrop()
                .into(pengaduanTanamanIv)

            statusTv.text = item.status
            setStatusBadge(item.status)

            createdDateTv.text = item.createdAt?.let { DateFormatter.formatIsoDate(it) }
            kelompokTaniTv.text = item.kelompokTani
            alamatTv.text = item.alamat
            kecamatanKabupatenTv.text = getString(
                R.string.kecamatan_kabupaten_label,
                item.kecamatan,
                item.kabupaten
            )
            descriptionTv.text = item.deskripsi
            latitudeTv.text = item.latitude
            longitudeTv.text = item.longitude

            openMapBtn.setOnClickListener {
                openInMaps(item.latitude?.toDouble(), item.longitude?.toDouble())
            }


            if (item.status.equals("Selesai", ignoreCase = true)) {
                downloadHasilPemeriksaanCard.isVisible = true

                tanggalVerifikasiTv.text = getString(
                    R.string.tanggal_verifikasi_label,
                    formatDate(item.tanggalVerifikasi ?: "")
                )

                namaPoptTv.text = getString(
                    R.string.nama_popt_label,
                    item.popt?.namaLengkap ?: "-"
                )

                binding.downloaHasilPemeriksaanBtn.setOnClickListener {
                    checkPermissionAndDownload(item.file)
                }
            } else {
                downloadHasilPemeriksaanCard.isVisible = false
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

    private fun downloadFile(fileUrl: String?) {
        try {
            val fileName = "hasil_pemeriksaan_${System.currentTimeMillis()}.pdf"
            val request = DownloadManager.Request(fileUrl?.toUri())
                .setTitle("Hasil Pemeriksaan Tanaman")
                .setDescription("Downloading...")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

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

            Toast.makeText(requireContext(), "Mengunduh file...", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Gagal mengunduh file: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun setStatusBadge(status: String?) {
        val (backgroundColor, textColor) = when (status) {
            "Pending" -> R.drawable.status_pending_bg to R.color.white
            "Diverifikasi" -> R.drawable.status_verified_bg to R.color.white
            "Selesai" -> R.drawable.status_completed_bg to R.color.white
            else -> R.drawable.status_pending_bg to R.color.white
        }

        binding.statusTv.setBackgroundResource(backgroundColor)
        binding.statusTv.setTextColor(ContextCompat.getColor(requireContext(), textColor))
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}