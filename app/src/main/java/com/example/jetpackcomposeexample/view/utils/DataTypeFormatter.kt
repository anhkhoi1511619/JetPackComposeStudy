package com.example.jetpackcomposeexample.view.utils

import java.text.SimpleDateFormat
import java.util.Locale

    fun formatValues(money: String, date: String, time: String): Triple<String, String, String> {
        // Định dạng lại Date
        val dateInputFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val dateOutputFormat = SimpleDateFormat("MMMM dd", Locale.getDefault())
        val parsedDate = dateInputFormat.parse(date)
        val formattedDate = parsedDate?.let { dateOutputFormat.format(it) } ?: date

        // Định dạng lại Time
        val timeInputFormat = SimpleDateFormat("HHmmss", Locale.getDefault())
        val timeOutputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val parsedTime = timeInputFormat.parse(time)
        val formattedTime = parsedTime?.let { timeOutputFormat.format(it) } ?: time

        return Triple(money, formattedDate, formattedTime)
    }
