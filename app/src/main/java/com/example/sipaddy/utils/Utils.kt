package com.example.sipaddy.utils

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
private const val DATE_FORMAT = "dd MMM yyyy, HH:mm:ss"
private val timestamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale("en", "ID")).format(Date())

fun createCustomTempFile(context: Context): File {
    val filesDir = context.externalCacheDir
    return File.createTempFile(timestamp, ".jpg", filesDir)
}