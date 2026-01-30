package com.example.sipaddy.presentation.auth.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.sipaddy.data.model.response.KelompokTaniResponse
import com.example.sipaddy.databinding.FragmentRegisterBinding
import com.example.sipaddy.presentation.ViewModelFactory
import com.example.sipaddy.utils.ResultState
import com.example.sipaddy.utils.showToast
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class RegisterFragment : Fragment() {

    private var loadingDialog: AlertDialog? = null

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegisterViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private var kelompokTaniList = listOf<KelompokTaniResponse>()
    private var selectedKelompokTaniId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListener()
        observerViewModel()

        viewModel.loadKelompokTani()
    }

    private fun observerViewModel() {
        viewModel.registerResult.observe(viewLifecycleOwner) { result ->
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
                    showSuccessDialog(result.data.username)
                }
            }
        }

        viewModel.kelompokTaniResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Error -> {
                    requireContext().showToast("Gagal memuat kelompok tani")

                }

                is ResultState.Success -> {
                    kelompokTaniList = result.data
                    setupKelompokTaniDropdown(result.data)
                }

                else -> {}
            }
        }
    }

    private fun setupListener() {
        binding.registerBtn.setOnClickListener {
            register()
        }

        binding.loginLinkTv.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupKelompokTaniDropdown(data: List<KelompokTaniResponse>) {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            data.map { it.nama }
        )
        binding.kelompokTaniActv.setAdapter(adapter)

        binding.kelompokTaniActv.setOnItemClickListener { _, _, position, _ ->
            selectedKelompokTaniId = data[position].id
        }
    }

    private fun register() {
        val username = binding.usernameEt.text.toString().trim()
        val password = binding.passwordEt.text.toString().trim()
        val namaLengkap = binding.namaLengkapEt.text.toString().trim()
        val nomorHp = binding.nomorHpEt.text.toString().trim()


        viewModel.register(
            username = username,
            password = password,
            namaLengkap = namaLengkap,
            nomorHp = nomorHp,
            kelompokTaniId = selectedKelompokTaniId
        )
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
        binding.registerBtn.isEnabled = !isLoading
        binding.usernameEt.isEnabled = !isLoading
        binding.passwordEt.isEnabled = !isLoading
        binding.namaLengkapEt.isEnabled = !isLoading
        binding.nomorHpEt.isEnabled = !isLoading
        binding.kelompokTaniActv.isEnabled = !isLoading
    }

    private fun showSuccessDialog(username: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Registrasi Berhasil")
            .setMessage("Selamat datang, $username!\n\nAkun Anda telah berhasil dibuat. Silakan login untuk melanjutkan.")
            .setPositiveButton("Login") { _, _ ->
                // Navigate to login
                findNavController().navigateUp()
            }
            .setCancelable(false)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}