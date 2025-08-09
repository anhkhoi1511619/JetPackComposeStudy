package com.example.jetpackcomposeexample.view.utils

import java.text.SimpleDateFormat
import java.util.Locale

    fun formatValues(money: String, date: String, time: String): Triple<String, String, String> {
        try {
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
        } catch (e: Exception) {
            return Triple(money, "", "")
        }
    }

    fun validateValues(money: String, date: String, time: String): Boolean {
        // 1. Kiểm tra Tiền
        if (money.toIntOrNull() == null || money.toInt() <= 0) return false

        // 2. Kiểm tra Date (MMMM dd)
        val dateFormat = SimpleDateFormat("MMMM dd", Locale.getDefault())
        dateFormat.isLenient = false
        try {
            dateFormat.parse(date) ?: return false
        } catch (e: Exception) {
            return false
        }

        // 3. Kiểm tra Time (HH:mm:ss)
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        timeFormat.isLenient = false
        try {
            timeFormat.parse(time) ?: return false
        } catch (e: Exception) {
            return false
        }

        return true
    }
