package com.example.sipaddy.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.sipaddy.R
import com.example.sipaddy.databinding.FragmentProfileBinding
import com.example.sipaddy.presentation.ViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfilViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObserver()
        setupListener()
    }

    private fun setupObserver() {
        viewModel.userData.observe(viewLifecycleOwner) { result ->
            result?.let {
                // Personal Info
                binding.nameHeaderTv.text = it.namaLengkap ?: it.username
                binding.usernameHeaderTv.text = "@${it.username}"

                // Role Badge
                val roleText = when (it.role) {
                    "user" -> "Petani"
                    "popt" -> "Petugas POPT"
                    else -> "Pengguna"
                }
                binding.roleChip.text = roleText

                // Profile Details
                binding.usernameTv.text = it.username
                binding.namaLengkapTv.text = it.namaLengkap ?: "-"
                binding.nomorHpTv.text = it.nomorHp ?: "-"
                binding.kelompokTaniTv.text = it.kelompokTani ?: "-"
                binding.roleTv.text = roleText

                // Get initials for avatar
                val initials = getInitials(it.namaLengkap ?: it.username)
                binding.tvAvatarInitials.text = initials
            }
        }

        viewModel.logoutResult.observe(viewLifecycleOwner) { isLoggedOut ->
            if (isLoggedOut) {
                navigateToLogin()
            }
        }
    }

    private fun setupListener() {
        binding.logoutBtn.setOnClickListener {
            confirmLogout()
        }

        binding.editProfileCard.setOnClickListener {
            showComingSoonDialog("Edit Profil")
        }

        binding.changePasswordCard.setOnClickListener {
            showComingSoonDialog("Ubah Password")
        }

        binding.aboutCard.setOnClickListener {
            showAboutDialog()
        }

    }

    private fun confirmLogout() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Logout")
            .setMessage("Apakah Anda yakin ingin keluar?")
            .setPositiveButton("Ya") { _, _ ->
                viewModel.logout()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun navigateToLogin() {
        // Navigate to login
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.main_navigation, true)
            .setLaunchSingleTop(true)
            .build()

        findNavController().navigate(
            R.id.navigation_login,
            null,
            navOptions
        )
    }

    private fun showAboutDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Tentang Aplikasi")
            .setMessage(
                """
                Balintan Smart Mobile
                Version 1.0.0
                
                Aplikasi monitoring dan deteksi penyakit pada tanaman padi menggunakan teknologi AI.
                
                © 2026 Balai Perlindungan Tanaman Pertanian Provinsi Gorontalo
            """.trimIndent()
            )
            .setPositiveButton("Tutup", null)
            .show()
    }

    private fun showComingSoonDialog(feature: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Segera Hadir")
            .setMessage("Fitur $feature akan segera tersedia!")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun getInitials(name: String): String {
        val parts = name.trim().split(" ")
        return when {
            parts.size >= 2 -> {
                "${parts[0].first().uppercase()}${parts[1].first().uppercase()}"
            }

            parts.size == 1 && parts[0].length >= 2 -> {
                parts[0].substring(0, 2).uppercase()
            }

            else -> {
                parts[0].first().uppercase()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}