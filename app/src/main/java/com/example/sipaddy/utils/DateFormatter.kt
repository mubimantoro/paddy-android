package com.example.sipaddy.utils

import android.os.Build
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone

object DateFormatter {
    fun formatIsoDate(isoDate: String): String {
        val indonesianLocale = Locale("en", "ID")
        val indonesiaTimeZone = "Asia/Makassar"

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val isoFormatter = DateTimeFormatter.ISO_DATE_TIME
            val localDateTime = LocalDateTime.parse(isoDate, isoFormatter)
            val zonedDateTime = localDateTime.atZone(ZoneId.of("UTC"))
                .withZoneSameInstant(ZoneId.of(indonesiaTimeZone))
            val readableFormatter =
                DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm", indonesianLocale)
            zonedDateTime.format(readableFormatter)
        } else {
            val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            isoFormatter.timeZone = TimeZone.getTimeZone("UTC")
            val date = isoFormatter.parse(isoDate)!!
            val readableFormatter =
                SimpleDateFormat("dd MMMM yyyy, HH:mm", indonesianLocale)
            readableFormatter.timeZone = TimeZone.getTimeZone(indonesiaTimeZone)
            readableFormatter.format(date)
        }
    }
}