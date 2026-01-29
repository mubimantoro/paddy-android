package com.example.sipaddy.presentation.auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.sipaddy.R
import com.example.sipaddy.databinding.FragmentLoginBinding
import com.example.sipaddy.presentation.ViewModelFactory
import com.example.sipaddy.presentation.auth.AuthViewModel
import com.example.sipaddy.utils.ResultState
import com.example.sipaddy.utils.gone
import com.example.sipaddy.utils.showToast
import com.example.sipaddy.utils.visible
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class LoginFragment : Fragment() {


    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListener()
        setupObserver()

    }


    private fun setupObserver() {
        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
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
                    navigateToHome()
                }
            }
        }
    }

    private fun setupListener() {
        binding.loginBtn.setOnClickListener {
            performLogin()
        }

        binding.registerTv.setOnClickListener {
            navigateToRegister()
        }
    }

    private fun performLogin() {
        val username = binding.usernameEt.text.toString().trim()
        val password = binding.usernameEt.text.toString().trim()

        if (!validateInput(username, password)) {
            return
        }

        viewModel.login(username, password)
    }

    private fun validateInput(username: String, password: String): Boolean {
        var isValid = true

        if (username.isEmpty()) {
            binding.usernameTil.error = "Username tidak boleh kosong"
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

        return isValid
    }

    private fun navigateToHome() {
        try {
            findNavController().navigate(R.id.action_navigation_login_to_navigation_home)
        } catch (e: Exception) {
            requireContext().showToast("Login berhasil!")
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visible()
            binding.loginBtn.isEnabled = false
            binding.usernameEt.isEnabled = false
            binding.passwordEt.isEnabled = false
        } else {
            binding.progressBar.gone()
            binding.loginBtn.isEnabled = true
            binding.usernameEt.isEnabled = true
            binding.passwordEt.isEnabled = true
        }
    }


    private fun showErrorDialog(message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Login Gagal")
            .setMessage(message)
            .setIcon(R.drawable.ic_error)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun navigateToRegister() {
        try {
            findNavController().navigate(R.id.action_navigation_login_to_navigation_register)
        } catch (e: Exception) {
            requireContext().showToast("Navigasi gagal")
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}