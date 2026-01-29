package com.example.sipaddy.presentation.auth.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.sipaddy.R
import com.example.sipaddy.data.model.response.KelompokTaniResponse
import com.example.sipaddy.databinding.FragmentRegisterBinding
import com.example.sipaddy.presentation.ViewModelFactory
import com.example.sipaddy.presentation.auth.AuthViewModel
import com.example.sipaddy.utils.ResultState
import com.example.sipaddy.utils.gone
import com.example.sipaddy.utils.showToast
import com.example.sipaddy.utils.visible
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class RegisterFragment : Fragment() {

    private var loadingDialog: AlertDialog? = null

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private var kelompokTaniList = listOf<KelompokTaniResponse>()
    private var selectedKelompokTaniId: Int? = null

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
                    showErrorDialog(result.message)
                }

                is ResultState.Success -> {
                    showLoading(false)
                    showSuccessDialog()
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
            performRegister()
        }

        binding.loginTv.setOnClickListener {
            navigateToLogin()
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

    private fun performRegister() {
        val username = binding.usernameEt.text.toString().trim()
        val password = binding.passwordEt.text.toString().trim()
        val namaLengkap = binding.namaLengkapEt.text.toString().trim()
        val nomorHp = binding.nomorHpEt.text.toString().trim()

        if (!validateInput(username, password, namaLengkap, nomorHp)) {
            return
        }

        if (selectedKelompokTaniId == null) {
            binding.kelompokTaniTil.error = "Pilih kelompok tani"
            return
        } else {
            binding.kelompokTaniTil.error = null
        }

        viewModel.register(
            username = username,
            password = password,
            namaLengkap = namaLengkap,
            nomorHp = nomorHp,
            kelompokTaniId = selectedKelompokTaniId!!
        )
    }


    fun validateInput(
        username: String,
        password: String,
        namaLengkap: String,
        nomorHp: String
    ): Boolean {
        var isValid = true

        if (username.isEmpty()) {
            binding.usernameTil.error = "Username tidak boleh kosong"
            isValid = false
        } else if (username.length < 4) {
            binding.usernameTil.error = "Username minimal 4 karakter"
            isValid = false
        } else {
            binding.usernameTil.error = null
        }

        if (password.isEmpty()) {
            binding.passwordTil.error = "Password tidak boleh kosong"
            isValid = false
        } else if (password.length < 6) {
            binding.passwordTil.error = "Password minimal 6 karakter"
            isValid = false
        } else {
            binding.passwordTil.error = null
        }


        if (namaLengkap.isEmpty()) {
            binding.usernameTil.error = "Nama lengkap tidak boleh kosong"
            isValid = false
        } else {
            binding.usernameTil.error = null
        }

        if (nomorHp.isEmpty()) {
            binding.nomorHpTil.error = "Nomor HP tidak boleh kosong"
            isValid = false
        } else if (nomorHp.length < 10) {
            binding.nomorHpTil.error = "Nomor HP minimal 10 digit"
            isValid = false
        } else {
            binding.nomorHpTil.error = null
        }

        return isValid
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visible()
            binding.registerBtn.isEnabled = false
            setInputsEnabled(false)
        } else {
            binding.progressBar.gone()
            binding.registerBtn.isEnabled = true
            setInputsEnabled(true)
        }
    }

    private fun setInputsEnabled(enabled: Boolean) {
        binding.usernameEt.isEnabled = enabled
        binding.passwordEt.isEnabled = enabled
        binding.namaLengkapEt.isEnabled = enabled
        binding.nomorHpEt.isEnabled = enabled
        binding.kelompokTaniActv.isEnabled = enabled
    }

    private fun showSuccessDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Registrasi Berhasil")
            .setMessage("Akun Anda berhasil dibuat. Silakan login dengan username dan password yang telah dibuat.")
            .setIcon(R.drawable.ic_check_circle)
            .setPositiveButton("Login") { dialog, _ ->
                dialog.dismiss()
                navigateToLogin()
            }
            .setCancelable(false)
            .show()
    }

    private fun showErrorDialog(message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Registrasi Gagal")
            .setMessage(message)
            .setIcon(R.drawable.ic_error)
            .setPositiveButton("OK", null)
            .show()
    }


    private fun navigateToLogin() {
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}