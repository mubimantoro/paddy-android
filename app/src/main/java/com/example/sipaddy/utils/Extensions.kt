package com.example.sipaddy.utils

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.sipaddy.R
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

fun String.toStatusText(): String {
    return when (this.lowercase()) {
        PengaduanTanamanStatus.PENDING -> "pending"
        PengaduanTanamanStatus.ASSIGNED -> "ditugaskan"
        PengaduanTanamanStatus.IN_PROGRESS -> "dalam proses"
        PengaduanTanamanStatus.VERIFIED -> "diverifikasi"
        PengaduanTanamanStatus.COMPLETED -> "selesai"
        PengaduanTanamanStatus.REJECTED -> "ditolak"
        else -> this.replaceFirstChar { it.uppercase() }
    }
}

fun String.toStatusColor(): Int {
    return when (this.lowercase()) {
        PengaduanTanamanStatus.PENDING -> R.color.status_pending
        PengaduanTanamanStatus.ASSIGNED -> R.color.status_assigned
        PengaduanTanamanStatus.IN_PROGRESS -> R.color.status_in_progress
        PengaduanTanamanStatus.VERIFIED -> R.color.status_verified
        PengaduanTanamanStatus.COMPLETED -> R.color.status_completed
        PengaduanTanamanStatus.REJECTED -> R.color.status_rejected
        else -> R.color.status_pending
    }
}

fun String.toStatusChipColors(context: Context): Pair<Int, Int> {
    return when (this.lowercase()) {
        PengaduanTanamanStatus.PENDING -> Pair(
            ContextCompat.getColor(context, R.color.status_pending_bg),
            ContextCompat.getColor(context, R.color.status_pending_text)
        )

        PengaduanTanamanStatus.ASSIGNED -> Pair(
            ContextCompat.getColor(context, R.color.status_assigned_bg),
            ContextCompat.getColor(context, R.color.status_assigned_text)
        )

        PengaduanTanamanStatus.IN_PROGRESS -> Pair(
            ContextCompat.getColor(context, R.color.status_in_progress_bg),
            ContextCompat.getColor(context, R.color.status_in_progress_text)
        )

        PengaduanTanamanStatus.VERIFIED -> Pair(
            ContextCompat.getColor(context, R.color.status_verified_bg),
            ContextCompat.getColor(context, R.color.status_verified_text)
        )

        PengaduanTanamanStatus.COMPLETED -> Pair(
            ContextCompat.getColor(context, R.color.status_completed_bg),
            ContextCompat.getColor(context, R.color.status_completed_text)
        )

        PengaduanTanamanStatus.REJECTED -> Pair(
            ContextCompat.getColor(context, R.color.status_rejected_bg),
            ContextCompat.getColor(context, R.color.status_rejected_text)
        )

        else -> Pair(
            ContextCompat.getColor(context, R.color.status_pending_bg),
            ContextCompat.getColor(context, R.color.status_pending_text)
        )
    }
}

fun String.formatDateTime(): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(this)

        val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
        date?.let { outputFormat.format(it) } ?: this
    } catch (e: Exception) {
        this
    }
}




fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}