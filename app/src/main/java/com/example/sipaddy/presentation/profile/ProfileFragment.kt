package com.example.sipaddy.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.sipaddy.R
import com.example.sipaddy.databinding.FragmentProfileBinding
import com.example.sipaddy.presentation.ViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfilViewModel by viewModels {
        ViewModelFactory(requireContext())
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

        with(binding) {

            viewModel.getNamaLengkap().observe(viewLifecycleOwner) { namaLengkap ->
                if (namaLengkap != null) {
                    namaLengkapTv.text = namaLengkap
                }
            }

            viewModel.getUsername().observe(viewLifecycleOwner) { username ->
                if (username != null) {
                    usernameTv.text = username
                }
            }

            btnLogout.setOnClickListener {
                showLogoutDialog()
            }

            btnSettings.setOnClickListener {
                view.findNavController().navigate(R.id.action_navigation_profile_to_settingAkunFragment)
            }
        }
    }

    private fun showLogoutDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Keluar")
            .setMessage("Apakah Anda yakin ingin keluar?")
            .setPositiveButton("Ya") { _, _ ->
                viewModel.logout() {
                    findNavController().navigate(R.id.action_to_navigation_login)
                }
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}