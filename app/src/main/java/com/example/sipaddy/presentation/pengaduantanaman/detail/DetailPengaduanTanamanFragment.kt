package com.example.sipaddy.presentation.pengaduantanaman.detail

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.sipaddy.R
import com.example.sipaddy.data.network.response.PengaduanTanamanItem
import com.example.sipaddy.databinding.FragmentDetailPengaduanTanamanBinding
import com.example.sipaddy.presentation.ViewModelFactory
import com.example.sipaddy.utils.DateFormatter
import com.example.sipaddy.utils.showErrorDialog


class DetailPengaduanTanamanFragment : Fragment() {

    private var _binding: FragmentDetailPengaduanTanamanBinding? = null
    private val binding get() = _binding!!

    private val args: DetailPengaduanTanamanFragmentArgs by navArgs()

    private val viewModel: DetailPengaduanTanamanViewModel by viewModels {
        ViewModelFactory(requireContext())
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

        args.pengaduanTanaman?.let { viewModel.setPengaduanTanamanDetail(it) }

        binding.openMapBtn.setOnClickListener {
            openInMaps()
        }

        setupObserver()

    }

    private fun setupObserver() {
        viewModel.detail.observe(viewLifecycleOwner) { result ->
            displayDetailPengaduanTanaman(result)
        }
    }

    private fun displayDetailPengaduanTanaman(item: PengaduanTanamanItem) {
        with(binding) {
            Glide.with(requireContext())
                .load(item.image)
                .placeholder(R.drawable.sample_scan)
                .error(R.drawable.sample_scan)
                .centerCrop()
                .into(pengaduanTanamanIv)

            setStatusBadge(item.status ?: "-")

            createdDateTv.text = item.createdAt?.let {
                DateFormatter.formatIsoDate(it)
            }

            kelompokTaniTv.text = item.kelompokTani ?: "-"
            alamatTv.text = item.alamat ?: "-"

            val location = buildString {
                item.kecamatan?.let {
                    append("Kec. $it")
                }
                if (item.kecamatan != null && item.kabupaten != null) {
                    append(", ")
                }
                item.kabupaten?.let { append("Kab. $it") }
            }
            kecamatanKabupatenTv.text = location.ifEmpty { "-" }

            descriptionTv.text = item.deskripsi ?: "-"

            latitudeTv.text = item.latitude.toString() ?: "-"
            longitudeTv.text = item.longitude.toString() ?: "-"
        }
    }

    private fun setStatusBadge(status: String?) {
        with(binding.statusTv) {
            when (status) {
                "Pending" -> {
                    text = context.getString(R.string.pending_label)
                    setBackgroundResource(R.drawable.badge_pending_bg)
                }

                "in_progress", "diproses" -> {
                    text = context.getString(R.string.in_progress_label)
                    setBackgroundResource(R.drawable.badge_in_progress_bg)
                }

                "resolved", "selesai" -> {
                    text = context.getString(R.string.resolved_label)
                    setBackgroundResource(R.drawable.badge_resolved_bg)
                }

                else -> {
                    text = context.getString(R.string.pending_label)
                    setBackgroundResource(R.drawable.badge_pending_bg)
                }
            }
        }
    }

    private fun openInMaps() {
        val pengaduanTanaman = viewModel.detail.value ?: return
        val latitude = pengaduanTanaman.latitude
        val longitude = pengaduanTanaman.longitude

        if (latitude != null && longitude != null) {
            val uri =
                "geo:$latitude,$longitude?q=$latitude,$longitude(${pengaduanTanaman.kelompokTani})".toUri()
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                setPackage("com.google.android.apps.maps")
            }

            if (intent.resolveActivity(requireContext().packageManager) != null) {
                startActivity(intent)
            } else {
                val browserUri =
                    "https://www.google.com/maps/search/?api=1&query=$latitude,$longitude".toUri()
                val browserIntent = Intent(Intent.ACTION_VIEW, browserUri)
                startActivity(browserIntent)
            }

        } else {
            showErrorDialog(
                requireContext(),
                "Koordinat lokasi tidak tersedia",
                onClick = {}
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}