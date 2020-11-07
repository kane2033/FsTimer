package com.focusstart.fstimer.timer

import java.text.SimpleDateFormat
import java.util.*

/*
* Класс конвертирует секунды в Long
* в формат "HH:mm:ss" и наоборот
* */
object TimerConverter {

    fun toSeconds(timeStr: String): Long {
        val units = timeStr.split(":".toRegex())
        val hours = units[0].toLong()
        val minutes = units[1].toLong()
        val seconds = units[2].toLong()
        return 3600L * hours * 60L * minutes + seconds
    }

    fun toString(time: Long): String {
        val timeInMs = time * 1000L
        val df = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        df.timeZone = TimeZone.getTimeZone("GMT") // Иначе прибавляются часы по таймзоне
        return df.format(timeInMs)
    }
}