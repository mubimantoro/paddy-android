package com.example.sipaddy.presentation.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.example.sipaddy.R
import com.example.sipaddy.data.ResultState
import com.example.sipaddy.databinding.FragmentLoginBinding
import com.example.sipaddy.presentation.ViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class LoginFragment : Fragment() {

    private var loadingDialog: AlertDialog? = null

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels {
        ViewModelFactory(requireContext())
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


        with(binding) {
            backBtn.setOnClickListener {
                view.findNavController().popBackStack()
            }

            registerHereTv.setOnClickListener {
                view.findNavController()
                    .navigate(R.id.action_navigation_login_to_navigation_register)
            }

            viewModel.loginResult.observe(viewLifecycleOwner) { result ->
                Log.d("LoginFragment", "Login result: $result")
                when (result) {
                    is ResultState.Loading -> {
                        showLoading(true)
                    }

                    is ResultState.Success -> {
                        showLoading(false)
                        val username = result.data.loginResult?.user?.username
                        val token = result.data.loginResult?.token



                        if (token != null && username != null) {
                            viewModel.saveSession(username, token) {
                                toHome(view)
                            }
                        }
                    }

                    is ResultState.Error -> {
                        showLoading(false)
                    }
                }
            }

            loginBtn.setOnClickListener {
                val usernameEdt = usernameEdt.text.toString().trim()
                val passwordEdt = passwordEdt.text.toString().trim()

                viewModel.login(usernameEdt, passwordEdt)


            }
        }

    }


    private fun toHome(view: View?) {

        view?.findNavController()?.navigate(
            R.id.action_loginFragment_to_navigation_home,
            null,
            navOptions = NavOptions.Builder().setPopUpTo(R.id.main_navigation, true).build()
        )
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}