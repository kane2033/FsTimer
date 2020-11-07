package com.focusstart.fstimer.timer

import android.app.*
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.focusstart.fstimer.ui.main.MainActivity
import com.focusstart.fstimer.R


/*
* Класс, отвечающий за создание и отсылку уведомлений
* */
class NotificationUtil(context: Context) {

    companion object {
        const val DEFAULT_CHANNEL_ID = "0"
        const val NOTIFICATION_ID = 111 // Для уведомлений счетчика (5, 4, 3..)
        const val NOTIFICATION_FINAl_ID = 122 // Последнее уведомление о завершении таймера
    }

    private val notificationManager: NotificationManager
            by lazy { context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager }

    // Канал уведомлений создается перед отправкой
    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "FS Timer"
            val descriptionText = "Таймер, сделанный для FS"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(DEFAULT_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Базовый метод создания уведомления
    private fun buildNotification(context: Context, notificationId: Int, msg: String): Notification {
        val activityIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context,
            notificationId, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        return NotificationCompat.Builder(context, DEFAULT_CHANNEL_ID)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("FsTimer")
            .setContentText(msg)
            .setNotificationSilent()
            .build()
    }

    // Возвращает уведомление без его отправки (необходимо для startForeground)
    fun buildFirstNotification(context: Context, msg: String) = buildNotification(context, NOTIFICATION_ID, msg)

    // Отсылка финального уведомления о окончании работы таймера
    fun sendFinalNotification(context: Context, msg: String) {
        notificationManager.notify(NOTIFICATION_FINAl_ID, buildNotification(context, NOTIFICATION_FINAl_ID, msg))
    }

    // Отсылка уведомления со значением таймера
    fun updateTimerNotification(context: Context, timerStr: String) {
        notificationManager.notify(NOTIFICATION_ID, buildNotification(context, NOTIFICATION_ID, timerStr))
    }

    // Отмена уведомления со значением таймера
    fun cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID)
    }
}