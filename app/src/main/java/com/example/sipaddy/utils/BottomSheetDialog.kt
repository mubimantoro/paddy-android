package com.example.sipaddy.utils

import android.content.Context
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import com.example.sipaddy.R
import com.example.sipaddy.databinding.BottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

fun bottomSheetDialog(
    context: Context,
    text: String,
    imageResId: Int,
    buttonColorResId: Int? = null,
    onClick: (() -> Unit)? = null
) {
    val bottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
    val bottomSheetBinding = BottomSheetBinding.inflate(LayoutInflater.from(context))

    bottomSheetBinding.apply {
        descTv.text = text
        sheetIv.setImageResource(imageResId)
        sheetBtn.setOnClickListener {
            onClick?.invoke()
            bottomSheetDialog.dismiss()
        }
        buttonColorResId?.let {
            sheetBtn.setBackgroundColor(ContextCompat.getColor(context, it))
        }
    }

    bottomSheetDialog.apply {
        setContentView(bottomSheetBinding.root)
        setCancelable(false)
        show()
    }
}