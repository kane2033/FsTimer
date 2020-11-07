package com.focusstart.fstimer.timer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log


object AlarmManagerUtil {

    const val intentActionString = "send_notification"
    const val timerValueString = "timerValue"

    // Всегда возвращает одинковый PendingIntent
    private fun getPendingIntent(context: Context, timerValue: String = "00:00:00"): PendingIntent? {
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.action = intentActionString
        intent.putExtra(timerValueString, timerValue)
        return PendingIntent.getBroadcast(context, 0, intent, 0)
    }

    // Создание сигнала, который прийдет через время таймера
    fun makeAlarmForNotification(context: Context, timerValue: Long) {
        Log.w("TIMER_SERVICE", "Notification scheduled")
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + timerValue * 1000L, // ms to sc
             getPendingIntent(context, TimerConverter.toString(timerValue))
        )
    }

    // Отмена отправки уведомления о завершении работы
    fun cancelAlarmForNotification(context: Context) {
        val pendingIntent = getPendingIntent(context)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (pendingIntent != null) {
            pendingIntent.cancel()
            alarmManager.cancel(pendingIntent)
        }
    }
}