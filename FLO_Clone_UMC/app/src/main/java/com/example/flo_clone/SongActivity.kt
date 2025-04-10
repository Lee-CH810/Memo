package com.example.flo_clone

import android.os.Bundle
import android.view.View
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

        /**
         * 뒤로 가기
         */
        // SongActivity의 song_down_ib를 눌렀을 때, 어떠한 작업을 수행할 수 있도록 함
        // 클릭 시 수행할 작업: SongActivity 종료 --> finish()
        // MainActivity에서 미니 플레이어를 누르면, SongActivity가 실행되어 화면에 덮어져 출력
        // 이때, finish()를 호툴하면, SongActivity가 종료되어 다시 MainActivity의 수핼으로 돌아가고
        // 이전에 출력 중이던 Fragment가 조합된 activity_main.xml의 화면을 볼 수 있음.
        binding.songDownIb.setOnClickListener {
            finish()
        }

        /**
         * 음악이 재생 및 정지 중일때, 재생 버튼의 변화 처리
         */
        binding.songMiniplayerIv.setOnClickListener {
            // 음악이 재생 중일 때 정지 버튼 띄움
            setPlayerStatus(true)
            }
        binding.songPauseIv.setOnClickListener {
            // 음악이 정지 중일 때, 재생 버튼 띄움
            setPlayerStatus(false)
        }

        /**
         * MainActivity의 miniplayer에서 보내주는 intent의 song 정보 받기
         */
        if (intent.hasExtra("title") && intent.hasExtra("singer")){ // "intent에 title과 singer라는 태그가 있을 경우에만 반영하겠다"
            binding.songMusicTitleTv.text = intent.getStringExtra("title") // intent에 담긴 title이라는 태그의 String을 SongActivity의 곡명에 반영
            binding.songSingerNameTv.text = intent.getStringExtra("singer")
        }
    }


    /**
     * 재생 중이면, 정지 버튼.
     * 정지 중이면, 재생 버튼을 띄우는 함수
     * @param isPlaying 현재 재생 중인지의 여부
     */
    fun setPlayerStatus (isPlaying: Boolean) {
        if (isPlaying) {
            // 재생 중
            binding.songMiniplayerIv.visibility = View.GONE
            binding.songPauseIv.visibility = View.VISIBLE
        } else {
            // 정지 중
            binding.songMiniplayerIv.visibility = View.VISIBLE
            binding.songPauseIv.visibility = View.GONE
        }
    }
}