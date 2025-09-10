package com.example.sipaddy.utils

import android.content.Context
import com.example.sipaddy.R
import com.google.android.material.bottomsheet.BottomSheetDialog

fun BottomSheetDialog(
    context: Context,
    text: String,
    imageResId: Int,
    buttonColorResId: Int? = null,
    onClick: (() -> Unit)? = null
) {
    val bottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
    val bottomSheetBinding = BottomSheetBinding.inflate
}