package com.example.flo_clone

import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flo_clone.databinding.ActivitySongBinding
import com.google.gson.Gson
import java.util.Timer
import androidx.core.graphics.toColorInt

// 상속 시에는 상속클래스()의 형태로 작성해야함.
class SongActivity : AppCompatActivity() {
    /**
     * 전역변수 설정
     */
    // 바인딩 객체는 전역 변수로 설정해주어야 함.
    lateinit var binding: ActivitySongBinding
    // MainActivity에서 넘어오는 intent를 효율적으로 이용하기 위해 Song 전역 변수를 선언하고 이를 초기화해주는 함수를 작성
//    lateinit var song: Song ---> SongActivity에서 다루는 song을 songs[nowPos]로 대체할 수 있게 됨
    // 전역변수로 스레드 생성
    lateinit var timer: Timer
    // 미디어 파일의 재생을 위한 클래스
    // ?: nullable. 액티비티가 소멸될 때 미디어 플레이어를 소멸시켜주어야 하기 때문.
    private var mediaPlayer : MediaPlayer? = null
    // Json 파일 변환을 위한 Gson 객체
    private var gson : Gson = Gson()

    /** DB에 저장된 Song 데이터를 편리하게 관리하기 위해 ArrayList의 형태로 저장한 전역 변수 */
    val songs = arrayListOf<Song>()
    // DB를 전역변수에 추가 --> 해당 DB에 대한 Song 데이터를 꺼내 Songs에 저장해주어야함.
    lateinit var songDB: MusicDatabase
    /** SongActivity에서 다루는 Song 데이터의 songs에서의 위치 */
    // songs[nowPos]를 통해 기존의 song 변수를 대체할 수 있음
    var nowPos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // inflate: XML에 표기된 레이아웃들을 메모리에 객체화 시키는 행동
        // layoutInflater: XML의 정보를 메모리에 로드 후 View를 객체화 시켜서 반환
        binding = ActivitySongBinding.inflate(layoutInflater)
        // onCreate함수 안에서 레이아웃을 inflate함
        setContentView(binding.root)

        /**
         * DB의 데이터를 모두 받아 songs에 저장 MainActivity에서 SharedPreference에 저장한 Song ID를 바탕으로 플레이리스트 및
         */
        initPlayList()
        initSong()

        /**
         * ClickListener 설정
         */
        initClickListener()

    }

    /**
     * 사용자가 포커스를 잃었을 때
     */
    override fun onPause() {
        super.onPause()
        setPlayerStatus(false) // 음악이 중지됨. 다시 돌아와도 중지되어 있는 상태 유지.
//        songs[nowPos].isPlaying = false --> setPlayerStatus에 false를 넘겼으므로 굳이 추가하지 않았음
        Log.d("Flow","SongAct: onPause")
        /** 현재 재생 중이던 곡의 정보를 저장 */
        // 현재 얼마나 재생되었는지 second 값을 저장. 다음 공식은 Timer의 run함수에서 Seekbar의 진행정도를 구현하기 위해서 썼을 때의 식을 second 기준으로 풀어낸 것. 1000으로 나눈 이유는 해당 식이 밀리세컨드 단위이기 때문.
        songs[nowPos].second = ((binding.songProgressSb.progress * songs[nowPos].playTime) / 100) / 1000

        songDB = MusicDatabase.getInstance(this)!!
        songDB.SongDao().update(songs[nowPos])

        val sharedPreferences = getSharedPreferences("song",MODE_PRIVATE) // SharedPreferences 객체 선언. "song": sharedPreferences의 이름 / MODE_PRIVATE: 해당 SharedPreferences를 이 앱에서만 사용가능하도록 설정하는 것
        Log.d("song spf", "SongAct.onPause: " + sharedPreferences.getInt("songId", -1))
        val editor = sharedPreferences.edit() // sharedPreferences에서의 데이터 조작을 위한 editor 선언
//        val songJson = gson.toJson(songs[nowPos]) // song 객체를 Json로 변환(song의 멤버가 많아서 각각 put해주기에는 번거로움이 있음. 이에 Json으로 Song 객체를 통째로 넘김)
//        editor.putString("songData", songJson) // 에디터에 song이라는 태그로 songJson 삽입
        editor.putInt("songId", songs[nowPos].id) // DB에서 song의 id를 통해 song 데이터를 가져올 수 있으므로, sharedPreference에 id를 넣어줌
        editor.apply() // 변경사항을 실제 저장공간에 저장

        Log.d("song spf", "SongAct.onPause: " + sharedPreferences.getInt("songId", -1))
        // 저장된 data는 어디서 사용해야 하는가? --> 다시 MainActivity가 실행될 때 사용되어야 함.
    }

    /**
     * Activity가 소멸될 때
     */
    override fun onDestroy() {
        super.onDestroy()
        timer.interrupt() // Acitivity가 종료되면 스레드에 에러를 일으켜서 종료. 원래는 SongActivity를 한 번 실행하면 timer 스레드 내부의 while문이 계속 실행되었었음.
        mediaPlayer?.release() // Activity가 소멸될 때, 불필요한 리소스 낭비 방지를 위해 mediaPlayer가 갖고 있던 리소스를 해제
        mediaPlayer = null // mediaPlayer도 해제
    }

    private fun initClickListener() {
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
         * 반복 재생 조작
         * 반복 재생의 옵션: 반복 X / 전체 반복 / 한 곡 반복
         * 현재 플레이리스트를 구현하지 않았으므로 전체 반복은 불가. 따라서 한 곡 반복만 구현할 것임
         */
        binding.songRepeatIv.setOnClickListener {
            setRepeat(true)
        }
        binding.songRepeatOneIv.setOnClickListener {
            setRepeat(false)
        }

        /**
         * 이전, 다음 버튼을 눌러서 재생 곡을 바꿔주는 처리
         */
        binding.songNextIv.setOnClickListener{
            moveSong(1)
        }
        binding.songPreviousIv.setOnClickListener{
            moveSong(-1)
        }
    }

    private fun moveSong(direct: Int) {
        /** songs의 크기를 벗어나서 nowPos가 변동될 때 넘어가지 않도록 처리 */
        if (nowPos + direct < 0) {
            Toast.makeText(this, "first song", Toast.LENGTH_SHORT).show()
            return
        }
        if (nowPos + direct > songs.size-1) {
            Toast.makeText(this, "last song", Toast.LENGTH_SHORT).show()
            return
        }

        /** 이전 버튼, 이후 버튼을 누름에 따라 현재 곡을 나타내는 nowPos에 변동 */
        nowPos += direct

        /** 재설정한 Song 데이터(현재 실행 중인 곡)를 바탕으로 Thread를 종료 후 재시작 */
        timer.interrupt()
        startTimer()

        /** 재설정한 Song 데이터에 맞춰서 MediaPlayer를 초기화 */
        mediaPlayer?.release()
        mediaPlayer = null // 리소스 해제

        setPlayer(songs[nowPos]) // Song 데이터에 맞추어서 mediaPlayer 재설정

    }

    /**
     * 현재 플레이리스트 초기화: DB의 모든 Song 데이터를 가져와서 ArrayList의 형태로 저장
     */
    private fun initPlayList() {
        Log.d("Flow", "SongAct: initPlayList")
        songDB = MusicDatabase.getInstance(this)!!
        songs.addAll(songDB.SongDao().getSongs())
    }

    /**
     * 재생 중이면, 정지 버튼.
     * 정지 중이면, 재생 버튼을 띄우는 함수
     * @param isPlaying 현재 재생 중인지의 여부
     */
    fun setPlayerStatus(isPlaying: Boolean) {
        // 현재 버튼에 따라 재생하고 정지하는 작업은 setPlayerStatus에서 이루어지고 있으므로
        // 음악 리소스의 재생 및 정지도 여기서 다루도록 함.
        songs[nowPos].isPlaying = isPlaying // 음악 재생 및 정지시 상태 반영
        timer.isPlaying =
            isPlaying // 음악 재생 및 정지시 timer에 상태를 반영하여 progressbar와 진행 시간 textView에 영향을 줌

        if (isPlaying) {
            // 재생 중
            binding.songMiniplayerIv.visibility = View.GONE
            binding.songPauseIv.visibility = View.VISIBLE
            mediaPlayer?.start() // 음악 재생
        } else {
            // 정지 중
            binding.songMiniplayerIv.visibility = View.VISIBLE
            binding.songPauseIv.visibility = View.GONE
            if (mediaPlayer?.isPlaying == true) { // mediaPlayer 재생 중이 아닐 때 mediaPlayer를 정지시키면, 문제가 생길 수 있으므로 해당 조건을 추가
                mediaPlayer?.pause() // 음악 정지
            }
        }
    }

    /**
     * MiniPlayer에서 재생한 곡에 대한 정보를 바탕으로 SongActivity에서의 Song 초기화
     */
    private fun initSong() {
        /** SongActivity으로 넘어온 정보를 바탕으로 song 객체를 초기화 */
        /** Database를 사용하기 전
         * - MainActivity의 miniplayer에서 넘긴 intent를 받아 곡의 정보(song)를 SongActivity의 전역변수 song에 초기화했었음 */
//        if (intent.hasExtra("title") && intent.hasExtra("singer")) { // intent의 tag가 if문에서 걸리면 이를 활용한 setPlay에 영향이 가서 Activity 전환 자체가 일어나지 않음. -- song이 초기화되지 않았다는 에러 메세지
//            song = Song(
//                intent.getStringExtra("title")!!,
//                intent.getStringExtra("singer")!!,
//                intent.getIntExtra("second", 0),
//                intent.getIntExtra("playTime", 0),
//                intent.getBooleanExtra("isPlaying", false),
//                intent.getStringExtra("music")!!
//            )
//        }
        /** Database 사용 후 - SharedPreference에서 id를 받아와서 DB에서 데이터 조회 */
        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId", 0)
        Log.d("song spf", spf.getInt("songId", -1).toString())
        Log.d("Flow", "SongActivity initSong")
        Log.d("Song ID", "SongActivity: $songId")

        nowPos = getPlayingSongPosition(songId)

        /** Timer 객체를 생성 및 실핼 */
        startTimer()
        /** 해당 곡에 대해 시작 */
        setPlayer(songs[nowPos])
    }

    /**
     * SharedPreference에서 받아온 id를 통해 songs(리스트)에서 일치하는 song(데이터)의 위치를 반환하는 함수 작성
     */
    private fun getPlayingSongPosition(songId: Int): Int {
        for (i in 0 until songs.size) {
            if (songs[i].id == songId) {
                return i
            }
        }
        return 0
    }

    /**
     * 초기화한 song의 정보를 바탕으로 뷰 렌더링
     * @param song MainActivity에서 intent를 통해 넘긴 song 정보를 SongActivity 화면에 반영
     */
    private fun setPlayer(song: Song) {
//        binding.songMusicTitleTv.text =
//            intent.getStringExtra("title")!! // intent에 담긴 title이라는 태그의 String을 SongActivity의 곡명에 반영
        binding.songMusicTitleTv.text = song.title // 제목
//        binding.songSingerNameTv.text = intent.getStringExtra("singer")!!
        binding.songSingerNameTv.text = song.singer // 가수
        binding.songStartTimeTv.text =
            String.format("%02d:%02d", song.second / 60, song.second % 60) // 시작점
        binding.songEndTimeTv.text =
            String.format("%02d:%02d", song.playTime / 60, song.playTime % 60) // 끝점
        // seekbar 진행 정도 반영
        binding.songProgressSb.progress = (song.second * 1000 / song.playTime) // progressbar 설정
        // 음악 파일을 리소스 폴더에서 찾아서 mediaPlayer에게 반환
        // 삭선이 나오는 경우, 더이상 지원하지 않으니 유의하라는 뜻. R.raw.music을 권장하고 있음
        val music = resources.getIdentifier(song.music, "raw", this.packageName) // 음악 파일
        mediaPlayer = MediaPlayer.create(this, music) // 음악 리소스를 mediaPlayer에 반환. 어떤 음악을 다룰 지를 정한다고 보면 됨.
        // 이미지
        binding.songAlbumIv.setImageResource(song.coverImg!!)

        setPlayerStatus(song.isPlaying)
    }

    /**
     * 반복 재생 조작
     * @param isRepeat 현재 반복 재생 중인지
     */
    private fun setRepeat(isRepeat : Boolean) {
        if (isRepeat) {
            // 반복 X -> 반복
            binding.songRepeatOneIv.visibility = View.VISIBLE
            binding.songRepeatIv.visibility = View.GONE

            /** Timer 객체 재시작 */
            timer.interrupt()
            startTimer()

            /** 재설정한 Song 데이터에 맞춰서 MediaPlayer를 초기화 */
            // 곡의 재시작이므로 현재 진행정도 알려줄 필요 없음
            mediaPlayer?.release()
            mediaPlayer = null // 리소스 해제

            setPlayer(songs[nowPos]) // Song 데이터에 맞추어서 mediaPlayer 재설정
        } else {
            // 반복 -> 반복 X
            binding.songRepeatOneIv.visibility = View.GONE
            binding.songRepeatIv.visibility = View.VISIBLE
        }
    }

    /**
     * Timer를 시작하는 함수
     * timer 객체가 전역변수로 선언되었으므로 매개변수는 전달받지 않아도 됨.
     */
    private fun startTimer() {
        timer = Timer(songs[nowPos].playTime, songs[nowPos].isPlaying)
        timer.start() // timer 스레드 시작
    }

    /**
     * Thread를 확장한 Timer 클래스(내부 클래스)
     * 코틀린에서는 inner 키워드가 없으면 정적인 클래스가 되어서 외부의 변수에 접근할 수 없음
     * 시간이 흐름에 따라 SeekBar와 Timer의 TextView를 바꿔주기 위해서는 binding 변수에 접근해야 하므로 외부 변수에 접근할 수 있는 inner class로 선언해야함.
     * @param playTime 보여주고자 하는 음악이 총 몇 초인지
     * @param isPlaying 현재 진행 중인지
     */
    // 구현해야할 부분 - 시간이 지나는 것에 따라 progressbar와 분 초 TextView의 뷰 렌더링
    inner class Timer(private val playTime: Int, var isPlaying: Boolean = true) : Thread() {
        private var second: Int = 0 // 진행 시간(초)
        private var mills: Float = 0f // 진행 시간(밀리세컨드). progressbar에서 100을 1000으로 나누어 세밀하게 표현해서.

        // Timer class가 호출되면 실행할 함수
        override fun run() {
            super.run()
            try {
                // progressbar 뷰 렌더링
                while (true) { // 무한루프가 아니라, isPlaying으로 해야 하는 거 아님? --> 아님. isPlaying으로 하게 되면, 중간에 음악을 정지한 경우에 스레드가 종료되기 때문에 다시 시작해주어야 하는 문제가 생김
                    if (second >= playTime) { // 진행 시간이 음악 길이와 같아지거나 커지면 종료
                        break
                    }
                    if (isPlaying) { // 음악 재생을 중지하면, isPlaying=false로 인해 해당 작업은 중지됨. 하지만, 스레드는 계속 실행 중이기에 효율적이지는 않음. 이를 멈추는 것에는 Interupt를 이용하는 방법이 있음.
                        sleep(50) // 50 밀리세컨드 동안 정지
                        mills += 50 // 정지된 50밀리 세컨드 반영

                        runOnUiThread { // 메인 스레드에 뷰 렌더링 요청
                            binding.songProgressSb.progress =
                                ((mills / playTime) * 100).toInt() // mills를 playTime으로 나누고 퍼센테이지율로 환산하기 위해 100 곱함
                        }

                        // 아래 진행되는 시간을 표시할 TextView(타이머) 구현
                        if (mills % 1000 == 0f) { // 1000밀리세컨드 == 1초
                            second++ // 1초 추가 후 TextView 수정을 위해 메인 스레드에 뷰 렌더링 요청
                            runOnUiThread {
                                binding.songStartTimeTv.text =
                                    String.format("%02d:%02d", second / 60, second % 60)
                            }
                        }
                    }
                }

            } catch (e: InterruptedException){
                Log.d("Song", "스레드가 죽었습니다. ${e.message}")
            }
        }
    }
}