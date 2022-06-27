package com.danywizzy.timer.ui.main

import android.content.res.AssetFileDescriptor
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import com.danywizzy.timer.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel(){

    private val _timerState = MutableStateFlow<TimerState>(TimerState.Default(""))
    val timerState = _timerState.asStateFlow()

    private var timer:CountDownTimer = createTimer(EggState.Soft)

    private var soundPool: SoundPool? = null
    private var alarm: Int = 0

    fun initSound(fd: AssetFileDescriptor) {
       val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()
            .apply {
                alarm = load(fd, 1)
            }
    }

    private fun createTimer(state: EggState): CountDownTimer {
        _timerState.value = TimerState.Default(getTime(state.time))
        return object :CountDownTimer(state.time, 1_000){
            override fun onTick(millishUntilFinished: Long) {
                _timerState.value = TimerState.Running(getTime(millishUntilFinished))
            }

            override fun onFinish() {
                _timerState.value = TimerState.Done
                if (alarm > 0){
                    soundPool?.play(alarm, 1f, 1f, 1, 0, 1f)
                }
            }

        }
    }

    private fun getTime(millis: Long): String {
        val seconds = millis / 1_000
        val min = seconds / 60
        val sec = seconds % 60
        return String.format("%02d:%02d", min, sec)
    }

    fun onStartBtnClick() {
        if(_timerState.value is TimerState.Running){
            timer.cancel()
            _timerState.value = TimerState.Done
        } else {
            timer.start()
        }
        soundPool?.stop(alarm)
    }

    fun onItemSelected(itemId: Int) {
        when(itemId){
            R.id.action_soft -> installTimer(EggState.Soft)
            R.id.action_medium -> installTimer(EggState.Medium)
            R.id.action_hard -> installTimer(EggState.Hard)
        }
    }

    private fun installTimer(state: EggState) {
        timer.cancel()
        timer = createTimer(state)
    }
}