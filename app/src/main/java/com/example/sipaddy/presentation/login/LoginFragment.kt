package com.example.sipaddy.presentation.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.sipaddy.data.ResultState
import com.example.sipaddy.databinding.FragmentLoginBinding
import com.example.sipaddy.presentation.ViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class LoginFragment : Fragment() {

    private val binding: FragmentLoginBinding by lazy {
        FragmentLoginBinding.inflate(layoutInflater)
    }

    private val viewModel: LoginViewModel by viewModels {
        ViewModelFactory(requireContext())
    }

    private var loadingDialog: AlertDialog? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupObserver()
    }

    private fun setupObserver() {
        with(binding) {
            loginBtn.setOnClickListener {
                val username = usernameEdt.text.toString().trim()
                val password = passwordEdt.text.toString().trim()

                viewModel.login(username, password)
                viewModel.loginResult.observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is ResultState.Loading -> {}
                        is ResultState.Success -> {
                            showLoading(false)
                            val username = result.data.loginResult?.username
                            val token = result.data.loginResult?.token

                            if (token != null && username != null) {
                                viewModel.saveSession(username, token)
                            }
                        }

                        is ResultState.Error -> {}
                    }
                }
            }
        }

    }

    private fun showLoading(isLoading: Boolean) {
        showLoadingDialog(isLoading)
    }

    private fun showLoadingDialog(state: Boolean) {
        if (state) {
            if (loadingDialog == null) {
                loadingDialog = MaterialAlertDialogBuilder(requireContext())
                    .setMessage("Mohon menunggu...")
                    .setCancelable(false)
                    .create()
            }
            loadingDialog?.show()
        } else {
            loadingDialog?.dismiss()
            loadingDialog = null
        }

    }
}