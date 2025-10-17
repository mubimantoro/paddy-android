package com.example.sipaddy.utils

import android.content.Context
import android.view.LayoutInflater
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.example.sipaddy.R
import com.example.sipaddy.databinding.BottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

fun bottomSheetDialog(
    context: Context,
    text: String,
    @DrawableRes imageResId: Int,
    @ColorRes buttonColorResId: Int? = null,
    buttonText: String? = context.getString(R.string.okay),
    isCancelable: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val bottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
    val bottomSheetBinding = BottomSheetBinding.inflate(LayoutInflater.from(context))

    bottomSheetBinding.apply {
        descTv.text = text
        sheetIv.setImageResource(imageResId)
        sheetBtn.text = buttonText
        buttonColorResId?.let {
            sheetBtn.setBackgroundColor(ContextCompat.getColor(context, it))
        }

        sheetBtn.setOnClickListener {
            onClick?.invoke()
            bottomSheetDialog.dismiss()
        }

    }

    bottomSheetDialog.apply {
        setContentView(bottomSheetBinding.root)
        setCancelable(false)
        show()
    }
}

fun showSuccessDialog(
    context: Context,
    message: String,
    buttonText: String = context.getString(R.string.okay),
    onClick: (() -> Unit)? = null
) {
    bottomSheetDialog(
        context = context,
        text = message,
        imageResId = R.drawable.ic_success_check,
        buttonText = buttonText,
        onClick = onClick
    )
}

fun showErrorDialog(
    context: Context,
    message: String,
    buttonText: String = context.getString(R.string.okay),
    onClick: (() -> Unit)? = null
) {
    bottomSheetDialog(
        context = context,
        text = message,
        imageResId = R.drawable.error_image,
        buttonColorResId = R.color.red,
        buttonText = buttonText,
        onClick = onClick
    )
}

fun showTokenExpiredDialog(
    context: Context,
    message: String = context.getString(R.string.session_exp_label),
    onLoginClick: () -> Unit
) {
    bottomSheetDialog(
        context = context,
        text = message,
        imageResId = R.drawable.error_image,
        buttonColorResId = R.color.red,
        buttonText = context.getString(R.string.login_label),
        isCancelable = false,
        onClick = onLoginClick
    )
}