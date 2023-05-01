package com.fajar.mystorydicodingapps.utils

import android.widget.TextView
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


private const val FILENAME_FORMAT = "dd-MMM-yyyy"

fun TextView.withDateFormat(createdAt: String) {
    try {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val date = format.parse(createdAt) as Date
        val dateFormat = DateFormat.getDateInstance(DateFormat.FULL).format(date)
        this.text = dateFormat
    } catch (e: ParseException) {
        // Handle the exception, e.g., display a default value
        this.text = "Invalid Date"
    }
}

val timeStamp: String = SimpleDateFormat(
    FILENAME_FORMAT,
    Locale.US
).format(System.currentTimeMillis())