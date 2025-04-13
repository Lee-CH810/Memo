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
    /**
     * 1. 변수 설정
     */

    // 바인딩 객체
    lateinit var binding : ActivityMainBinding
    // 타이머를 수행할 스레드 객체
    var myTimer: MyTimer = MyTimer()

    // 상태
    // isStart: 현재 start 상태인지
    // isClear: Clear 버튼이 눌린 상태인지
    var isStart: Boolean = false
    var isClear: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        myTimer.start() // 타이머 스레드 생성

        /**
         * 버튼 전환
         * Start -> Pause
         * Pause -> Start
         */
        binding.timerStartB.setOnClickListener {
            // 타이머 실행
            Log.d("Status", "Start: 타이머 시작")

            // Start이면 true -> Pause로
            setButton(true)
        }
        binding.timerPauseB.setOnClickListener {
            // 타이머 정지
            Log.d("Status", "Pause: 타이머 정지")

            // Pasue이면 false -> Start로
            setButton(false)
        }
        binding.timerClearB.setOnClickListener {
            // 타이머 초기화
            Log.d("Status", "Clear: 타이머 초기화")

            setButton(false) // Button을 Pause로 바꿈
            isClear = true // Clear 누른 상태
        }

    }

    /**
     * 버튼 전환 함수
     * @param isStart 현재 Start된 상태인지
     */
    private fun setButton(isStart: Boolean) {
        // setButton을 호출한 곳에서 준 isStart 값을 전역변수에 반영
        this.isStart = isStart
        Log.d("isStart", "isStart: $isStart")

        if (isStart) {
            // Start 버튼 누르면
            Log.d("Button", "start -> pause")
            binding.timerStartB.visibility = View.GONE
            binding.timerPauseB.visibility = View.VISIBLE // Pause가 보이도록
        } else {
            // Pause 버튼 누르면
            Log.d("Button", "pause -> start")
            binding.timerPauseB.visibility = View.GONE
            binding.timerStartB.visibility = View.VISIBLE // Start가 보이도록
        }
    }

    /**
     * 타이머를 실행할 Thread 객체
     * 주의할 점: Thread가 실행 중인 것과 시간이 카운트되는 것은 다른 개념이라는 것을 유의
     */
    inner class MyTimer() : Thread() {
        var mills: Int = 0
        var second: Int = 0

        override fun run() {
            super.run()
            // 무한 루프
            while(true) {
                // 현재 Start 중이면 시간 흐름
                if (isStart) {
                    sleep(10)
                    mills += 10 // 밀리세컨드++
                    if (mills % 1000 == 0) {
                        second++
                        mills = 0
                    }
                    runOnUiThread {
                        binding.timerTimeTv.text = String.format("%02d:%02d.%2d", second / 60, second % 60, mills / 10)
                    }
                    Log.d("Time", "second: $second, mills: $mills")
                }
                // Clear가 눌렸는지 확인
                // Clear 세팅 함수를 따로 빼주려 했지만, 해당 작업을 class 외부에서 수행하면
                // Thread 내의 while이 한 번 더 실행되는 이슈가 발생해서 run() 내부에 작성했음
                if (isClear) {
                    mills = 0
                    second = 0
                    runOnUiThread {
                        binding.timerTimeTv.text = "00:00:00"
                    }
                    isClear = false // 다시 Clear가 눌리지 않은 것처럼 설정
                }
            }
        }
    }
}

/**
 * 현재 clear의 수행을 위해서 interrupt를 걸어주었는데, clear 시 앱이 아예 튕김
 * clear에 interrupt 걸어준 것으로 인해 start를 눌렀을 시 Thread를 생성하게 하였더니, start와 pause를 누를 때마다 Thread가 하나씩 추가되는 문제 발생
 * --> Thread를 죽이는 방법 OR Thread 정지 방법
 * --> Thread가 실행 중이라고 시간도 카운트되는 것이 아니고, Thread는 그냥 또다른 작업 라인이고 그곳에서 시간 카운트라는 작업을 하는 것임을 이해하면
 * 굳이 Thread 죽이고 할 필요 없음.
 */

/**
 * 방법 2) Data Class로 Timer의 구성을 second, mills로 구성하고
 * clear 시 Timer.second = 0, Timer.mills = 0으로 만드는 방법이 있음.
 * --> 오류가 더 잦음. 컴파일 에러가 아닌, 런타임 에러로 의도한 대로, 의도한 값으로 흘러가지 않는 부분이 많았음
 */