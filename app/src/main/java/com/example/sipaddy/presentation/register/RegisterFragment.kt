package com.example.sipaddy.presentation.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.sipaddy.R
import com.example.sipaddy.data.ResultState
import com.example.sipaddy.databinding.FragmentRegisterBinding
import com.example.sipaddy.presentation.ViewModelFactory
import com.example.sipaddy.utils.TextListener
import com.example.sipaddy.utils.ValidationAuth
import com.example.sipaddy.utils.bottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class RegisterFragment : Fragment() {

    private var loadingDialog: AlertDialog? = null

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegisterViewModel by viewModels {
        ViewModelFactory(requireContext())
    }

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

        with(binding) {
            backBtn.setOnClickListener {
                view.findNavController().popBackStack()
            }

            namaLengkapEdt.addTextChangedListener(object : TextListener {
                override fun onTextListener(s: CharSequence) {
                    ValidationAuth.isFieldEmpty(
                        s.toString().trim(),
                        namaLengkapErrorTv,
                        requireContext().getString(R.string.namalengkap_empty_label)
                    )

                    ValidationAuth.validatenamaLengkap(
                        requireContext(),
                        s.toString().trim(),
                        namaLengkapErrorTv
                    )
                }
            })

            usernameEdt.addTextChangedListener(object : TextListener {
                override fun onTextListener(s: CharSequence) {
                    ValidationAuth.isFieldEmpty(
                        s.toString().trim(),
                        usernameErrorTv,
                        requireContext().getString(R.string.username_empty_label)
                    )

                    ValidationAuth.validateUsername(
                        requireContext(),
                        s.toString().trim(),
                        usernameErrorTv
                    )
                }
            })

            passwordEdt.addTextChangedListener(object : TextListener {
                override fun onTextListener(s: CharSequence) {
                    ValidationAuth.isFieldEmpty(
                        s.toString().trim(),
                        passwordErrorTv,
                        requireContext().getString(R.string.password_empty_label)
                    )

                    ValidationAuth.validatePassword(
                        requireContext(),
                        s.toString().trim(),
                        passwordErrorTv
                    )
                }
            })
        }

        setupAction()
    }

    private fun setupAction() {
        with(binding) {
            registerBtn.setOnClickListener {
                val namaLengkapEdt = namaLengkapEdt.text.toString().trim()
                val nomorHpEdt = nomorHpEdt.text.toString().trim()
                val usernameEdt = usernameEdt.text.toString().trim()
                val passwordEdt = passwordEdt.text.toString().trim()

                viewModel.register(usernameEdt, passwordEdt, namaLengkapEdt, nomorHpEdt)

                viewModel.registerResult.observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is ResultState.Loading -> {
                            showLoading(true)

                        }

                        is ResultState.Error -> {
                            showLoading(false)
                            bottomSheetDialog(
                                requireContext(),
                                result.error,
                                R.drawable.error_image,
                                buttonColorResId = R.color.red
                            )
                        }

                        is ResultState.Success -> {
                            showLoading(false)
                            bottomSheetDialog(
                                requireContext(),
                                getString(R.string.success_register_label),
                                R.drawable.ic_success_check,
                                onClick = {
                                    view?.findNavController()?.popBackStack()
                                }
                            )
                        }
                    }

                }

            }
        }
    }

    private fun showLoading(state: Boolean) {
        with(binding) {
            if (state) {
                registerBtn.isEnabled = false
                backBtn.isEnabled = false
                showLoadingDialog(true)
            } else {
                registerBtn.isEnabled = true
                backBtn.isEnabled = true
                showLoadingDialog(false)
            }
        }
    }

    private fun showLoadingDialog(state: Boolean) {
        if (state) {
            if (loadingDialog == null) {
                loadingDialog = MaterialAlertDialogBuilder(requireContext())
                    .setMessage(getString(R.string.please_wait_label))
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