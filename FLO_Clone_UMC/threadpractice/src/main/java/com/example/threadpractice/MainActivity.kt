package com.example.threadpractice

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.threadpractice.databinding.ActivityMainBinding

// 실스 2: 2초마다 이미지가 바뀌도록 함
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 뷰 렌더링 요청을 위해서 Handler 객체 생성
        // Looper: 메서지 처리를 위해 메세지 루퍼를 실행하여 다른 스레드에서 전달한 메세지들을 꺼내서 반환하는 역할
        val handler = Handler(Looper.getMainLooper())

        // 이미지 뷰 리스트를 만들어서 바뀔 이미지들을 등록
        val imgList = arrayListOf<Int>() // 정수형 리스트 생성
        imgList.add(R.drawable.img_album_exp2)
        imgList.add(R.drawable.img_album_exp3)
        imgList.add(R.drawable.img_album_exp4)
        imgList.add(R.drawable.img_album_exp5)
        imgList.add(R.drawable.img_album_exp6)
        imgList.add(R.drawable.img_album_exp)

        // onCreate 함수에서 ImageView 전환을 시도하면, 해당 작업이 끝날 때까지 다른 작업은 수행이 불가함 -> 새로운 워커 스레드를 통해 작업을 수행해야 할 필요가 있음.
        // 이때, 메인 스레드에서만 뷰 렌더링이 가능하므로 워커 스레드에서 메인 스레드로 뷰 렌더링을 요청해야 함.
        Thread {
            for (image in imgList) {
                // handler에 다음 명령(뷰 렌더링 요청)을 담아서 메인 스레드로 전달. 메인 스레드에서 이를 실행.
                // handler.post 외에 runOnUiThread{} 를 사용해도 같은 결과를 얻을 수 있음.
                handler.post {
                    binding.mainIv.setImageResource(image)
                }
                Thread.sleep(2000) // 해당 명령이 handler 내부로 들어가면 이미지 안 바뀜.
            }
        }.start()
    }
}