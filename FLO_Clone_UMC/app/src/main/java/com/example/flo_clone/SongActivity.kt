package com.example.flo_clone

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.flo_clone.databinding.ActivitySongBinding

// 상속 시에는 상속클래스()의 형태로 작성해야함.
class SongActivity : AppCompatActivity() {
    // 바인딩 객체는 전역 변수로 설정해주어야 함.
    lateinit var binding : ActivitySongBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // inflate: XML에 표기된 레이아웃들을 메모리에 객체화 시키는 행동
        // layoutInflater: XML의 정보를 메모리에 로드 후 View를 객체화 시켜서 반환
        binding = ActivitySongBinding.inflate(layoutInflater)
        // onCreate함수 안에서 레이아웃을 inflate함
        setContentView(binding.root)

        // SongActivity의 song_down_ib를 눌렀을 때, 어떠한 작업을 수행할 수 있도록 함
        // 클릭 시 수행할 작업: SongActivity 종료 --> finish()
        // MainActivity에서 미니 플레이어를 누르면, SongActivity가 실행되어 화면에 덮어져 출력
        // 이때, finish()를 호툴하면, SongActivity가 종료되어 다시 MainActivity의 수핼으로 돌아가고
        // 이전에 출력 중이던 Fragment가 조합된 activity_main.xml의 화면을 볼 수 있음.
        binding.songDownIb.setOnClickListener {
            finish()
        }
    }
}