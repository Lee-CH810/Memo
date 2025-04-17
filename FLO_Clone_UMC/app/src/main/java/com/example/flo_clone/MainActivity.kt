package com.example.flo_clone

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.flo_clone.databinding.ActivityMainBinding
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    /**
     * 전역 변수 선언
     */
    lateinit var binding: ActivityMainBinding
    private var song: Song = Song()
    private var gson: Gson = Gson()

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

//        SharedPreferences에 저장된 Song의 값을 가져올 것이므로, 지워도 됨.
//        val song = Song(binding.mainMiniplayerTitleTv.text.toString(), binding.mainMiniplayerSingerTv.text.toString(), 0, 60, false, "music_lilac")

        binding.mainPlayerCl.setOnClickListener {
            // intent에 곡명과 가수명을 각각 title, singer라는 key값으로 담음
            val intent = Intent(this, SongActivity::class.java)
            intent.putExtra("title", song.title)
            intent.putExtra("singer", song.singer)
            intent.putExtra("second", song.second) // 현재 재생 정도와 총 재생 시간, 재생 여부를 추가로 intent에 담기
            intent.putExtra("playTime", song.playTime)
            intent.putExtra("isPlaying", song.isPlaying)
            intent.putExtra("music", song.music) // 어떤 플레이인지에 대한 미디어 파일
            startActivity(intent) // SongActivity로 intent를 넘겨주면서 data도 함께 넘겨주게 됨. 이를 받는 부분이 필요
        }

        /**
         * BottomNavigation 사용을 위한 초기화
         */
        initBottomNavigation()
    }

    /**
     * Activity 전환이 될 때 onStart부터 실행됨.
     * Ex. MainActivity에서 SongActivity로 갔다가, SongActivity에서 창을 닫고 다시 MainActivity로 돌아갈 때는 onStart부터 수행됨. 따라서 SongActivity에서 저장한 SharedPreference의 값을 적용하기 위해서는 onStart에서의 작업이 필요함
     */
    override fun onStart() {
        super.onStart()

        /**
         * Song Activity에서의 Song 데이터를 MainActivity에서 MiniPlayer에 반영해주는 작업
         */

        // SharedPrefereces에 있는 Song 데이터를 가져오기
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE) // song이라는 이름의 sharedPreferences를 불러옴
        val songJson = sharedPreferences.getString("songData", null) // sharedPreferences의 songData를 불러와서 songJson에 저장
        // 가져온 값읗 song객체에 담기
        song = if(songJson == null) {
            // 만약 실행이 처음이라서 sharedPreferences에 저장된 값이 없을 경우, Song을 다음과 같이 초기화해줌.
            Song("라일락", "아이유 (iU)", 0, 60, false, "music_lilac")
        } else {
            // SharedPreferences에 데이터가 있어 Json 값을 가져올 수 있다면, fromJson을 통해 Json값을 Song 클래스의 java 객체로 변환. 이를 song = ...을 통해 song에 할당.
            gson.fromJson(songJson, Song::class.java)
        }

        // 가져온 데이터를 MiniPlayer에 반영
        setMiniPlayer(song)
    }

    /**
     * MiniPlayer에 Song 데이터를 반영
     * @param song 전역 변수 Song. SongActivity에서 받은 Song 데이터가 들어있음.
     */
    private fun setMiniPlayer(song: Song) {
        binding.mainMiniplayerTitleTv.text = song.title
        binding.mainMiniplayerSingerTv.text = song.singer
        binding.mainProgressSb.progress = (song.second*100000) / song.playTime // song.second * 100,000인 이유는 seekbar의 max가 100,000이기 때문
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