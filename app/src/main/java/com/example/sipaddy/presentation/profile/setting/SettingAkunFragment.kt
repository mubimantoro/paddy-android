package com.example.sipaddy.presentation.profile.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.sipaddy.utils.ResultState
import com.example.sipaddy.data.network.response.UserProfile
import com.example.sipaddy.databinding.FragmentSettingAkunBinding
import com.example.sipaddy.presentation.ViewModelFactory


class SettingAkunFragment : Fragment() {

    private var _binding: FragmentSettingAkunBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingAkunViewModel by viewModels {
        ViewModelFactory(requireContext())
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSettingAkunBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupListeners()

        viewModel.getUserProfile()
    }

    private fun setupObservers() {
        viewModel.profileState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Loading -> {
                    showLoading(true)
                }

                is ResultState.Success -> {
                    showLoading(false)
                    populateUserData(result.data)
                }

                is ResultState.Error -> {
                    showLoading(false)
                    showError(result.error)
                }
            }
        }


        viewModel.updateState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Loading -> {
                    showLoading(true)
                    binding.saveBtn.isEnabled = false
                }

                is ResultState.Success -> {
                    showLoading(false)
                    binding.saveBtn.isEnabled = true
                    showSuccess("Profil berhasil diperbarui")

                    viewModel.getUserProfile()
                }

                is ResultState.Error -> {
                    showLoading(false)
                    binding.saveBtn.isEnabled = true
                    showError(result.error)

                }
            }
        }
    }

    private fun setupListeners() {
        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.saveBtn.setOnClickListener {
            saveProfileChanges()
        }
    }

    private fun populateUserData(user: UserProfile) {
        binding.apply {
            usernameEt.setText(user.username)
            namaLengkapEt.setText(user.namaLengkap)
            nomorHpEt.setText(user.nomorHp ?: "")
            // Password tetap kosong (tidak ditampilkan)
        }
    }

    private fun saveProfileChanges() {
        val username = binding.usernameEt.text.toString().trim()
        val namaLengkap = binding.namaLengkapEt.text.toString().trim()
        val nomorHp = binding.nomorHpEt.text.toString().trim()
        val password = binding.passwordEt.text.toString()

        viewModel.updateUserProfile(
            username = username,
            namaLengkap = namaLengkap,
            nomorHp = nomorHp.takeIf { it.isNotEmpty() },
            password = password.takeIf { it.isNotEmpty() }
        )
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            saveBtn.isEnabled = !isLoading
            if (isLoading) {
                // Bisa tambahkan progress bar jika ada
            }
        }
    }

    private fun showSuccess(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        binding.passwordEt.setText("")
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}