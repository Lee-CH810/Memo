package com.example.flo_clone

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.flo_clone.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Flo) // 앱이 로드되어 MainActivity가 실행되면 다시 원래 테마로 바꾸어 줌.

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 그냥 Intent만 사용해도 액티비티 전환을 일으킬 수 있음
        // startActivity를 활용해서 Intent를 수행할 수 있고,
        // 해당 Intent에는 어디(this)에서 어느 곳(SongActivity)로 이동해야 하는지를 명시.
//        binding.mainPlayerCl.setOnClickListener {
//            startActivity(Intent(this, SongActivity::class.java))
//        }

        /**
         * MainActicity의 miniplayer 클릭 시 SongActivity로의 전환과 함께, miniplayer에서의 곡 제목과 가수명을 넘겨주는 부분
         * 음악에 대한 정보를 담기 위해 Song data clss 생성
         * + 현재 재생 정도, 총 재생 시간, 재생 여부를 추가로 초기화
         */
        val song = Song(binding.mainMiniplayerTitleTv.text.toString(), binding.mainMiniplayerSingerTv.text.toString(), 0, 60, false)

        binding.mainPlayerCl.setOnClickListener {
            // intent에 곡명과 가수명을 각각 title, singer라는 key값으로 담음
            val intent = Intent(this, SongActivity::class.java)
            intent.putExtra("title", song.title)
            intent.putExtra("singer", song.singer)
            intent.putExtra("second", song.second) // 현재 재생 정도와 총 재생 시간, 재생 여부를 추가로 intent에 담기
            intent.putExtra("playTime", song.playTime)
            intent.putExtra("isPlaying", song.isPlaying)
            startActivity(intent) // SongActivity로 intent를 넘겨주면서 data도 함께 넘겨주게 됨. 이를 받는 부분이 필요
        }

        /**
         * BottomNavigation 사용을 위한 초기화
         */
        initBottomNavigation()
    }

    /**
     * BottomNavigation 활용을 위한 초기화
     */
    private fun initBottomNavigation() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, HomeFragment())
            .commitAllowingStateLoss()

        binding.mainBnv.setOnItemSelectedListener { item ->
            when(item.itemId) {

                R.id.homeFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, HomeFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.lookFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LookFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.searchFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, SearchFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.lockerFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LockerFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }
}