package com.example.sipaddy.presentation.auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.sipaddy.R
import com.example.sipaddy.databinding.FragmentLoginBinding
import com.example.sipaddy.presentation.ViewModelFactory
import com.example.sipaddy.utils.ResultState
import com.example.sipaddy.utils.showToast


class LoginFragment : Fragment() {


    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels {
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

        setupObserver()
        setupListener()

    }


    private fun setupObserver() {
        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
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
                    findNavController().navigate(R.id.action_navigation_login_to_navigation_home)
                }
            }
        }
    }

    private fun setupListener() {
        binding.loginBtn.setOnClickListener {
            login()
        }

        binding.registerLinkTv.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_login_to_navigation_register)
        }
    }

    private fun login() {
        val username = binding.usernameEt.text.toString().trim()
        val password = binding.usernameEt.text.toString().trim()

        viewModel.login(username, password)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
        binding.loginBtn.isEnabled = !isLoading
        binding.usernameEt.isEnabled = !isLoading
        binding.passwordEt.isEnabled = !isLoading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}