package us.cyberstar.data.utils

import java.text.SimpleDateFormat
import java.util.*

object DateFormatter {
    private var format = SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.getDefault())

    fun formatDate(milliSec: Long): String {
        return format.format(Date(milliSec))
    }

    fun formatDate(date: Date): String {
        return format.format(date)
    }
}