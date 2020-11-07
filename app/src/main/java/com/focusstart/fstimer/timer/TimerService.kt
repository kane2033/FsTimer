package com.focusstart.fstimer.timer

import android.app.*
import android.content.Intent
import android.os.IBinder
import android.util.Log
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class TimerService: Service() {

    companion object {
        const val timerIntentAction = "timerIntentAction"
        const val timerIntentValue = "timerValue"
        var isWorking = false
    }

    private val scheduleTaskExecutor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private lateinit var task: ScheduledFuture<*>

    private var notificationUtil: NotificationUtil = NotificationUtil(this)

    private var timerValue = 0L // Значение таймера

    override fun onBind(p0: Intent?): IBinder? = null // Приложение не будет связываться с другими, => null

    override fun onCreate() {
        super.onCreate()
        notificationUtil.createNotificationChannel() // Создание канала уведомлений
        // Запуск сервиса в переднем плане, чтобы сервис оставался после закрытия приложения
        startForeground(NotificationUtil.NOTIFICATION_ID,
            notificationUtil.buildFirstNotification(this, "Здесь будет отображаться таймер"))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.w("TIMER_SERVICE", "Service started!")
        isWorking = true

        // Получаем время работы таймера из интента
        timerValue = intent!!.getLongExtra(timerIntentValue, 0L)
        // Планируем уведомление о окончании работы таймера
        AlarmManagerUtil.makeAlarmForNotification(this, timerValue)

        // Отсчет таймера каждую секунду в бэкграунд потоке
        task = scheduleTaskExecutor.scheduleAtFixedRate(
            {
                if (timerValue > 0) {
                    timerValue--
                    // Отправка значения в фрагмент в строковом формате
                    val timerStr = TimerConverter.toString(timerValue)
                    val broadcastIntent = Intent(timerIntentAction)
                    broadcastIntent.putExtra(timerIntentValue, timerStr)
                    sendBroadcast(broadcastIntent)
                    // Обновление значения в уведомлении
                    notificationUtil.updateTimerNotification(this, timerStr)
                }
                else {
                    // Окончание работы таймера, когда отсчет дошел до 0
                    Log.w("TIMER_SERVICE", "Service stopped because timerValue is 0")
                    stopSelf()
                }

            },
            0, 1, TimeUnit.SECONDS
        )

        return START_STICKY // Такой режим используется, если служба явно запускается и останавливается
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.w("TIMER_SERVICE", "Service stopped!")
        isWorking = false
        task.cancel(true) // Отменяем таймер при остановке сервиса
        // Отменяем отправку уведомления о завершении работы таймера
        AlarmManagerUtil.cancelAlarmForNotification(this)
    }

}