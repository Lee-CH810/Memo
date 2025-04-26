
package com.example.flo_clone

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
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
//    private var gson: Gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Flo) // 앱이 로드되어 MainActivity가 실행되면 다시 원래 테마로 바꾸어 줌.

        Log.d("Flow", "MainAct: onCreate")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // DB에 데이터 주입
        inputDummySongs()
        inputDummyAlbums()

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
            Log.d("Flow", "MainAct: setOnClickListener")
            /** MusicDatabase를 사용하기 전 - intent를 활용하여 SongActivity로 넘어가는 데이터를 지정 */
//            // intent에 곡명과 가수명을 각각 title, singer라는 key값으로 담음
//            val intent = Intent(this, SongActivity::class.java)
//            intent.putExtra("title", song.title)
//            intent.putExtra("singer", song.singer)
//            intent.putExtra("second", song.second) // 현재 재생 정도와 총 재생 시간, 재생 여부를 추가로 intent에 담기
//            intent.putExtra("playTime", song.playTime)
//            intent.putExtra("isPlaying", song.isPlaying)
//            intent.putExtra("music", song.music) // 어떤 플레이인지에 대한 미디어 파일
//            startActivity(intent) // SongActivity로 intent를 넘겨주면서 data도 함께 넘겨주게 됨. 이를 받는 부분이 필요
            /** MusicDatabase를 사용한 후 - SharedPreference를 활용하여 Song의 ID를 넘겨 SongActivity에서 그 값을 가지고 DB를 조회하도록 함*/
            val spf = getSharedPreferences("song", MODE_PRIVATE)
            Log.d("song spf", "MainAct.setOnClickListener: " + spf.getInt("songId", -1).toString())
            val editor = spf.edit() // SharedPreference 작업을 위해 editor 선언
            editor.putInt("songId", song.id) // editor에 songId를 넣고
            editor.apply() // 적용
            Log.d("song spf", "MainAct.setOnClickListener: " + spf.getInt("songId", -1).toString())

            val intent = Intent(this, SongActivity::class.java) // SongActivity 시작 intent
            startActivity(intent) // SongActivity 시작
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
// --------------------------------------------------
//        /**
//         * Song Activity에서의 Song 데이터를 MainActivity에서 MiniPlayer에 반영해주는 작업
//         */
//
//        // SharedPrefereces에 있는 Song 데이터를 가져오기
//        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE) // song이라는 이름의 sharedPreferences를 불러옴
//        val songJson = sharedPreferences.getString("songData", null) // sharedPreferences의 songData를 불러와서 songJson에 저장
//        // 가져온 값읗 song객체에 담기
//        song = if(songJson == null) {
//            // 만약 실행이 처음이라서 sharedPreferences에 저장된 값이 없을 경우, Song을 다음과 같이 초기화해줌.
//            Song("라일락", "아이유 (iU)", 0, 60, false, "music_lilac")
//        } else {
//            // SharedPreferences에 데이터가 있어 Json 값을 가져올 수 있다면, fromJson을 통해 Json값을 Song 클래스의 java 객체로 변환. 이를 song = ...을 통해 song에 할당.
//            gson.fromJson(songJson, Song::class.java)
//        }
// ---------------------------- MusicDatabase를 활용하게됨으로써
// ---------------------------- SharedPreference를 통해 DB의 곡을 ID를 통해 다룰 수 있게 됨.

        Log.d("Flow", "MainAct: onStart")
        /**
         * SongActivity에서의 Song 데이터를 SharedPreference와 MusicDatabase를 통해 가져와서 MiniPlayer에 반영
         */
        val spf = getSharedPreferences("song", MODE_PRIVATE) // song이라는 이름의 sharedPreference를 spf에 할당
        val songId = spf.getInt("songId", 0) // spf에서 songId를 불러와서 그 값을 songId에 저장
        Log.d("song spf", "MainAct.onStart: " + spf.getInt("songId", -1).toString())

        // songId에 맞는 Song 데이터를 MusicDatabase에서 불러옴
        val songDB = MusicDatabase.getInstance(this)!!
        song = setSongById(songId, songDB)

        // Log로 가져온 Song의 ID 확인
        Log.d("Song ID", song.id.toString())
        // 가져온 데이터를 MiniPlayer에 반영
        setMiniPlayer(song)
    }

    /**
     * songId에 맞는 song 데이터로 현재 song 변수를 수정
     */
    private fun setSongById(songId: Int, songDB: MusicDatabase): Song {
        val song = if (songId == 0) {
            // songId == 0이면, 저장된 songId가 없다는 의미이므로 가장 처음 인덱스의 노래를 가져옴
            songDB.SongDao().getSong(1)
        } else {
            // songId != 이면, 저장된 songId가 있으므로 해당 Song의 데이터를 가져옴
            songDB.SongDao().getSong(songId)
        }

        return song
    }

    /**
     * MiniPlayer에 Song 데이터를 반영
     * @param song 전역 변수 Song. SongActivity에서 받은 Song 데이터가 들어있음.
     */
    fun setMiniPlayer(song: Song) {
        Log.d("Flow", "MainAct: setMiniPlayer")
        this.song = setSongById(song.id, MusicDatabase.getInstance(this)!!)
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

    /**
     * DB에 데이터가 없다면 Data를 넣어주는 작업이 필요함.
     * 더미 데이터 초기화하는 함수임
     */
    private fun inputDummySongs() {
        val songDB = MusicDatabase.getInstance(this)!! // Song data를 넣을 MusicDatabase 객체

        // SongDB에 데이터가 있는지 확인하기 위해서는 SongDB의 데이터를 다 받아와야 할 것.
        val songs = songDB.SongDao().getSongs()

        // 만약 데이터가 있다면(song이 비어있지 않음) 함수를 종료시키고, 데이터가 없다면(song이 비어있음) 더미 데이터를 넣어주어야 함.
        if (songs.isNotEmpty()) return

        songDB.SongDao().insert(
            Song(
                "Butter",
                "방탄소년단 (BTS)",
                0,
                164,
                false,
                "music_butter",
                R.drawable.img_album_exp,
                false,
                "Butter"
            )
        )
        songDB.SongDao().insert(
            Song(
                "LILAC",
                "아이유 (IU)",
                0,
                215,
                false,
                "music_lilac",
                R.drawable.img_album_exp2,
                false,
                "IU 5th Album 'LILAC'"
            )
        )
        songDB.SongDao().insert(
            Song(
                "Next Level",
                "에스파 (AESPA)",
                0,
                222,
                false,
                "music_next",
                R.drawable.img_album_exp3,
                false,
                "Next Level"
            )
        )
        songDB.SongDao().insert(
            Song(
                "Boy with LUV",
                "방탄소년단 (BTS)",
                0,
                230,
                false,
                "music_boy",
                R.drawable.img_album_exp4,
                false,
                "MAP OF THE SOUL:PERSONA"
            )
        )
        songDB.SongDao().insert(
            Song(
                "BBoom BBoom",
                "모모랜드 (MOMOLAND)",
                0,
                209,
                false,
                "music_bboom",
                R.drawable.img_album_exp5,
                false,
                "GREAT!"
            )
        )
        songDB.SongDao().insert(
            Song(
                "Weekend",
                "태연 (Tae Yeon)",
                0,
                234,
                false,
                "music_weekend",
                R.drawable.img_album_exp6,
                false,
                "Weekend"
            )
        )
        songDB.SongDao().insert(
            Song(
                "TOO BAD (feat. Anderson .Paak)",
                "G-DRAGON",
                0,
                154,
                false,
                "music_too_bad",
                R.drawable.img_album_exp7,
                false,
                "Übermensch"
            )
        )
        songDB.SongDao().insert(
            Song(
                "like JENNIE",
                "제니 (JENNIE)",
                0,
                124,
                false,
                "music_like_jennie",
                R.drawable.img_album_exp8,
                false,
                "Ruby"
            )
        )
        songDB.SongDao().insert(
            Song(
                "모르시나요(PROD.로코베리)",
                "조째즈",
                0,
                302,
                false,
                "music_dont_you_know",
                R.drawable.img_album_exp9,
                false,
                "모르시나요"
            )
        )

        // 데이터가 잘 들어갔는지 log를 통해 확인
        val _songs = songDB.SongDao().getSongs()
        Log.d("DB data", _songs.toString())
    }

    private fun inputDummyAlbums() {
        val albumDB = MusicDatabase.getInstance(this)!!
        /** Album Table이 초기화 되었는지 확인 */
        val albums = albumDB.AlbumDao().getAlbums()
        if (albums.isNotEmpty()) return

        /** 초기화 */
        albumDB.AlbumDao().insert(
            Album(
                "Butter",
                "방탄소년단 (BTS)",
                R.drawable.img_album_exp
            )
        )
        albumDB.AlbumDao().insert(
            Album(
                "IU 5th Album 'LILAC'",
                "아이유 (IU)",
                R.drawable.img_album_exp2
            )
        )
        albumDB.AlbumDao().insert(
            Album(
                "Next Level",
                "에스파 (AESPA)",
                R.drawable.img_album_exp3
            )
        )
        albumDB.AlbumDao().insert(
            Album(
                "MAP OF THE SOUL:PERSONA",
                "방탄소년단 (BTS)",
                R.drawable.img_album_exp4
            )
        )
        albumDB.AlbumDao().insert(
            Album(
                "GREAT!",
                "모모랜드 (MOMOLAND)",
                R.drawable.img_album_exp5
            )
        )
        albumDB.AlbumDao().insert(
            Album(
                "Weekend",
                "태연 (Tae Yeon)",
                R.drawable.img_album_exp6
            )
        )
        albumDB.AlbumDao().insert(
            Album(
                "Übermensch",
                "G-DRAGON",
                R.drawable.img_album_exp7
            )
        )
        albumDB.AlbumDao().insert(
            Album(
                "Ruby",
                "제니 (JENNIE)",
                R.drawable.img_album_exp8
            )
        )
        albumDB.AlbumDao().insert(
            Album(
                "모르시나요",
                "조째즈",
                R.drawable.img_album_exp9
            )
        )
    }
}

