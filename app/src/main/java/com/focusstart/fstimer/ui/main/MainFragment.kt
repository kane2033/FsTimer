package com.focusstart.fstimer.ui.main

import android.app.TimePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.focusstart.fstimer.R
import com.focusstart.fstimer.timer.AlarmManagerUtil
import com.focusstart.fstimer.timer.TimerConverter
import com.focusstart.fstimer.timer.TimerService
import kotlinx.android.synthetic.main.main_fragment.*
import java.lang.Exception


class MainFragment : Fragment() {

    private lateinit var broadcastReceiver: BroadcastReceiver

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.main_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Intent, с которым запускается сервис таймера
        val serviceIntent = Intent(requireActivity(), TimerService::class.java)

        // Кнопка запуска таймера
        startButton.setOnClickListener {
            if (!TimerService.isWorking) { // Можем запустить, только если не работает
                val timerValue = timerView.text.toString()
                try {
                    serviceIntent.putExtra(TimerService.timerIntentValue,
                        TimerConverter.toSeconds(timerValue))
                    requireActivity().startService(serviceIntent)
                }
                catch (e: Exception) {
                    Toast.makeText(requireContext(),
                        "Строка некорректно введена. Формат: hh:mm:ss",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Создание бродкаста, с помощью которого будет получено значение таймера
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent?) {
                val timerValue = intent!!.getStringExtra(TimerService.timerIntentValue)
                timerView.setText(timerValue)
            }
        }

        // Регистрация бродкаста
        val intentFilter = IntentFilter(TimerService.timerIntentAction)
        requireActivity().registerReceiver(broadcastReceiver, intentFilter)

        // Кнопкой паузы останавливаем сервис
        pauseButton.setOnClickListener {
            if (TimerService.isWorking) {
                requireActivity().stopService(serviceIntent)
            }
        }

        // Кнопкой сброса останавливаем сервис И сбрасывает значение таймера
        restartButton.setOnClickListener {
            timerView.text.clear()
            if (TimerService.isWorking) {
                requireActivity().stopService(serviceIntent)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Отписка от получения обновлений с сервиса
        // при выходе из фрагмента
        requireActivity().unregisterReceiver(broadcastReceiver)
    }

}