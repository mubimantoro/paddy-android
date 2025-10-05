package com.example.sipaddy.utils

import android.content.Context
import android.widget.TextView
import com.example.sipaddy.R

object ValidationAuth {
    fun isFieldEmpty(field: String, errorView: TextView, errorMsg: String): Boolean {
        return if (field.isEmpty()) {
            showText(errorView, true, errorMsg)
            true
        } else {
            showText(errorView, false)
            false
        }
    }

    fun validateUsername(context: Context, username: String, errorView: TextView): Boolean {
        return when {
            username.isEmpty() -> {
                showText(errorView, true, context.getString(R.string.username_empty_label))
                false
            }

            else -> {
                showText(errorView, false)
                true
            }
        }
    }

    fun validatePassword(context: Context, password: String, errorView: TextView): Boolean {
        return when {
            password.isEmpty() -> {
                showText(errorView, true, context.getString(R.string.password_empty_label))
                false
            }

            else -> {
                showText(errorView, false)
                true
            }
        }
    }

    fun validatenamaLengkap(context: Context, namaLengkap: String, errorView: TextView): Boolean {
        return when {
            namaLengkap.isEmpty() -> {
                showText(errorView, true, context.getString(R.string.namalengkap_empty_label))
                false
            }

            else -> {
                showText(errorView, false)
                true
            }
        }
    }

    private fun showText(binding: TextView?, state: Boolean, msg: String? = null) {
        if (state) {
            if (msg != null) binding?.text = msg
            binding?.show()
        } else {
            binding?.gone()
        }
    }
}