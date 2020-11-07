package com.focusstart.fstimer.timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        if (intent.action == AlarmManagerUtil.intentActionString) { // Если получили intent именно от AlarmManager
            // Отображаем уведомление о завершении таймера
            Log.w("TIMER_SERVICE", "Notification received")
            val timerValue = intent.getStringExtra(AlarmManagerUtil.timerValueString)
            context?.let {
                NotificationUtil(it).sendFinalNotification(it, "Указанное время прошло = $timerValue")
                Log.w("TIMER_SERVICE", "Notification sent")
            }
        }
    }

}