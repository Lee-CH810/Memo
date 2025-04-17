package com.example.flo_clone

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.flo_clone.databinding.ActivitySongBinding
import java.util.Timer

// 상속 시에는 상속클래스()의 형태로 작성해야함.
class SongActivity : AppCompatActivity() {
    /**
     * 전역변수 설정
     */
    // 바인딩 객체는 전역 변수로 설정해주어야 함.
    lateinit var binding: ActivitySongBinding
    // MainActivity에서 넘어오는 intent를 효율적으로 이용하기 위해 Song 전역 변수를 선언하고 이를 초기화해주는 함수를 작성
    lateinit var song: Song
    // 전역변수로 스레드 생성
    lateinit var timer: Timer
    // 미디어 파일의 재생을 위한 클래스
    // ?: nullable. 액티비티가 소멸될 때 미디어 플레이어를 소멸시켜주어야 하기 때문.
    private var mediaPlayer : MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // inflate: XML에 표기된 레이아웃들을 메모리에 객체화 시키는 행동
        // layoutInflater: XML의 정보를 메모리에 로드 후 View를 객체화 시켜서 반환
        binding = ActivitySongBinding.inflate(layoutInflater)
        // onCreate함수 안에서 레이아웃을 inflate함
        setContentView(binding.root)

        initSong()
        setPlayer(song)

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
    }

    /**
     * 사용자가 포커스를 잃었을 때
     */
    override fun onPause() {
        super.onPause()
        setPlayerStatus(false) // 음악이 중지됨. 다시 돌아와도 중지되어 있는 상태 유지.
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

    /**
     * 재생 중이면, 정지 버튼.
     * 정지 중이면, 재생 버튼을 띄우는 함수
     * @param isPlaying 현재 재생 중인지의 여부
     */
    fun setPlayerStatus(isPlaying: Boolean) {
        // 현재 버튼에 따라 재생하고 정지하는 작업은 setPlayerStatus에서 이루어지고 있으므로
        // 음악 리소스의 재생 및 정지도 여기서 다루도록 함.
        song.isPlaying = isPlaying // 음악 재생 및 정지시 상태 반영
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
     * MainActivity의 miniplayer에서 넘긴 intent를 받아 곡의 정보(song)를 SongActivity의 전역변수 song에 초기화하는 함수
     */
    private fun initSong() {
        if (intent.hasExtra("title") && intent.hasExtra("singer")) { // intent의 tag가 if문에서 걸리면 이를 활용한 setPlay에 영향이 가서 Activity 전환 자체가 일어나지 않음. -- song이 초기화되지 않았다는 에러 메세지
            song = Song(
                intent.getStringExtra("title")!!,
                intent.getStringExtra("singer")!!,
                intent.getIntExtra("second", 0),
                intent.getIntExtra("playTime", 0),
                intent.getBooleanExtra("isPlaying", false),
                intent.getStringExtra("music")!!
            )
        }

        // SongActivity으로 넘어온 정보를 바탕으로 song 객체를 초기화해줌과 동시에 Timer 객체를 생성 및 실핼
        startTimer()
    }

    /**
     * 초기화한 song의 정보를 바탕으로 뷰 렌더링
     * @param song MainActivity에서 intent를 통해 넘긴 song 정보를 SongActivity 화면에 반영
     */
    private fun setPlayer(song: Song) {
        binding.songMusicTitleTv.text =
            intent.getStringExtra("title")!! // intent에 담긴 title이라는 태그의 String을 SongActivity의 곡명에 반영
        binding.songSingerNameTv.text = intent.getStringExtra("singer")!!
        binding.songStartTimeTv.text =
            String.format("%02d:%02d", song.second / 60, song.second % 60)
        binding.songEndTimeTv.text =
            String.format("%02d:%02d", song.playTime / 60, song.playTime % 60)
        // seekbar 진행 정도 반영
        binding.songProgressSb.progress = (song.second * 1000 / song.playTime) // progressbar 설정
        // 음악 파일을 리소스 폴더에서 찾아서 mediaPlayer에게 반환
        // 삭선이 나오는 경우, 더이상 지원하지 않으니 유의하라는 뜻. R.raw.music을 권장하고 있음
        val music = resources.getIdentifier(song.music, "raw", this.packageName)
        mediaPlayer = MediaPlayer.create(this, music) // 음악 리소스를 mediaPlayer에 반환. 어떤 음악을 다룰 지를 정한다고 보면 됨.

        setPlayerStatus(song.isPlaying)
    }

    /**
     * Timer를 시작하는 함수
     * timer 객체가 전역변수로 선언되었으므로 매개변수는 전달받지 않아도 됨.
     */
    private fun startTimer() {
        timer = Timer(song.playTime, song.isPlaying)
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