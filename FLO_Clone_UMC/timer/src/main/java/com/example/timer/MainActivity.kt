package com.example.timer

import android.app.Activity
import android.os.Bundle
import android.text.BoringLayout
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.timer.databinding.ActivityMainBinding
import java.util.Timer
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding

    lateinit var myTimer: MyTimer

    var isStart:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /**
         * Start -> Pause
         * Pause -> Start
         */
        binding.timerStartB.setOnClickListener {
            Log.d("start", "타이머 시작")
            // Start이면 true -> Pause로
            setButton(true)
            startTimer() // timer 객체 생성 및 타이머 실행
        }
        binding.timerPauseB.setOnClickListener {
            Log.d("pause", "타이머 정지")
            // Pasue이면 false -> Start로
            setButton(false)
        }

        /**
         * Clear 처리
         */
        binding.timerClearB.setOnClickListener {
            reset()
        }

    }

    /**
     * 버튼 전환 함수
     * @param isStart 현재 Start된 상태인지
     */
    private fun setButton(isStart: Boolean) {
        this.isStart = isStart
        Log.d("isStart", "isStart: $isStart")
        if (isStart) {
            // Start 버튼 누르면
            Log.d("Start/Pause", "start -> pause")
            binding.timerStartB.visibility = View.GONE
            binding.timerPauseB.visibility = View.VISIBLE
        } else {
            // Pause 버튼 누르면
            Log.d("Start/Pause", "pause -> start")
            binding.timerPauseB.visibility = View.GONE
            binding.timerStartB.visibility = View.VISIBLE
        }
    }

    private fun startTimer() {
        myTimer = MyTimer()
        myTimer.start()
    }

    private fun reset() {
        myTimer.interrupt()
    }

    inner class MyTimer() : Thread() {
        var mills: Int = 0 // 밀리세컨드
        var second: Int = 0 // 초

        override fun run() {
            super.run()
            // 무한 루프
            while(true) {
                // 현재 Start 중이면 시간 흐름
                if (isStart) {
//                    Log.d("timer", "timer 작동 중")
                    sleep(50)
                    mills += 50 // 밀리세컨드++
                    if (mills % 1000 == 0) {
                        second++
                        mills = 0
                    }
                    runOnUiThread {
                        binding.timerTimeTv.text = String.format("%02d:%02d.%2d", second / 60, second % 60, mills)
                    }
                    Log.d("Time", "second: $second, mills: $mills")
                }
            }
        }
    }
}

/**
 * 현재 clear의 수행을 위해서 interrupt를 걸어주었는데, clear 시 앱이 아예 튕김
 * clear에 interrupt 걸어준 것으로 인해 start를 눌렀을 시 Thread를 생성하게 하였더니, start와 pause를 누를 때마다 Thread가 하나씩 추가되는 문제 발생
 * --> Thread를 죽이는 방법 OR Thread 정지 방법
 */
/**
 * 방법 2) Data Class로 Timer의 구성을 second, mills로 구성하고
 * clear 시 Timer.second = 0, Timer.mills = 0으로 만드는 방법이 있음.
 */