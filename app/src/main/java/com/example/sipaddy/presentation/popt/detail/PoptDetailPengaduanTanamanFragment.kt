package com.example.sipaddy.presentation.popt.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.sipaddy.R
import com.example.sipaddy.data.ResultState
import com.example.sipaddy.data.network.response.PengaduanTanamanItem
import com.example.sipaddy.databinding.FragmentPoptDetailPengaduanTanamanBinding
import com.example.sipaddy.presentation.ViewModelFactory
import com.example.sipaddy.utils.DateFormatter
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PoptDetailPengaduanTanamanFragment : Fragment() {

    private var _binding: FragmentPoptDetailPengaduanTanamanBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PoptDetailPengaduanTanamanViewModel by viewModels {
        ViewModelFactory(requireContext())
    }

    private val args: PoptDetailPengaduanTanamanFragmentArgs by navArgs()


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
            backBtn.setOnClickListener {
                findNavController().navigateUp()
            }

            verifikasiBtn.setOnClickListener {
                showVerifikasiDialog()
            }
        }

        setupObserver()

        args.pengaduanTanaman?.id?.let { viewModel.getDetailPengaduanTanaman(it) }
    }

    private fun setupObserver() {
        viewModel.resultDetail.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Loading -> {}
                is ResultState.Error -> {}
                is ResultState.Success -> {
                    showLoading()
                    val data = result.data.data?.pengaduanTanaman
                    data?.let { setupUIDetail(it) }
                }
            }
        }

        viewModel.resultVerifikasi.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Loading -> {}
                is ResultState.Error -> {}
                is ResultState.Success -> {
                    Toast.makeText(
                        requireContext(),
                        "Pengaduan berhasil diverifikasi",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setupUIDetail(item: PengaduanTanamanItem) {
        with(binding) {
            Glide.with(requireContext())
                .load(item.image)
                .placeholder(R.drawable.sample_scan)
                .error(R.drawable.sample_scan)
                .into(fotoTanamanIv)

            statusTv.text = getStatusText(item.status ?: "-")
            statusCard.setCardBackgroundColor(
                ContextCompat.getColor(requireContext(), getStatusColor(item.status ?: "-"))

            )

            namaPelaporTv.text = item.userNama ?: "-"
            teleponTv.text = item.userUsername ?: "-"
            lokasiTv.text = "${item.alamat}, ${item.kecamatan}, ${item.kabupaten}"
            tanggalTv.text = item.createdAt?.let {
                DateFormatter.formatIsoDate(it)
            } ?: "-"
            deskripsiTv.text = item.deskripsi

            verifikasiBtn.visibility = if (item.status == "Ditugaskan") {
                View.VISIBLE
            } else {
                View.GONE
            }

        }
    }

    private fun showVerifikasiDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Verifikasi Pengaduan")
            .setMessage("Apakah Anda yakin ingin memverifikasi pengaduan ini?")
            .setPositiveButton("Ya") { _, _ ->
                args.pengaduanTanaman?.id?.let { viewModel.verifikasiPengaduanTanaman(it) }
            }
            .setNegativeButton("Batal", null)
            .show()
    }


    private fun getStatusText(status: String): String {
        return when (status) {
            "pending" -> "Pending"
            "assigned" -> "Ditugaskan"
            "verified" -> "Diverifikasi"
            "completed" -> "Selesai"
            else -> status
        }
    }

    private fun getStatusColor(status: String): Int {
        return when (status) {
            "pending" -> R.color.yellow
            "assigned" -> R.color.blue
            "verified" -> R.color.green_500
            "completed" -> R.color.green_700
            else -> R.color.neutral_500
        }
    }


    private fun showLoading() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}