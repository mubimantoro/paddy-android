package com.example.sipaddy.presentation.base

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sipaddy.R
import com.example.sipaddy.utils.showErrorDialog
import com.example.sipaddy.utils.showSuccessDialog
import com.example.sipaddy.utils.showTokenExpiredDialog


abstract class BaseFragment : Fragment() {
    protected fun handleTokenExpired(message: String = getString(R.string.session_exp_label)) {
        showTokenExpiredDialog(
            context = requireContext(),
            message = message,
            onLoginClick = {
                navigateToLogin()
            }
        )
    }

    protected fun showError(
        message: String,
        buttonText: String = getString(R.string.okay),
        onRetry: (() -> Unit)? = null
    ) {
        showErrorDialog(
            context = requireContext(),
            message = message,
            buttonText = if (onRetry != null) getString(R.string.retry_label) else buttonText,
            onClick = {
                onRetry?.invoke()
            }
        )
    }

    protected fun showSuccess(
        message: String,
        buttonText: String = getString(R.string.okay),
        onClick: (() -> Unit)? = null
    ) {
        showSuccessDialog(
            context = requireContext(),
            message = message,
            buttonText = buttonText,
            onClick = onClick
        )
    }

    protected fun navigateToLogin() {
        try {
            findNavController().navigate(
                R.id.navigation_login,
                null,
                androidx.navigation.NavOptions.Builder()
                    .setPopUpTo(R.id.main_navigation, true)
                    .build()
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}