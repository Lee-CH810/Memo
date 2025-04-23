package com.example.flo_clone

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.flo_clone.databinding.FragmentSavedSongBinding

class SavedSongFragment : Fragment() {

    lateinit var binding: FragmentSavedSongBinding
    private var savedSongs = ArrayList<Song>()
    /** SongDB 선언을 위한 부모 Context 받아오기 위한 변수 */
    private lateinit var mContext: Context
    lateinit var songDB: SongDatabase // 원래라면 다른 DB를 쓰거나, SongDao에서 islike인 데이터만 골라와야 할텐데, 번거로우니 일단은 넘김

    /**
     * 부모 Context 받기 위한 onAttach() 상속
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)

        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSavedSongBinding.inflate(layoutInflater)

        /** 보관함 곡 초기화 - RoomDB 이용 가능할 듯 -> 이용함 */
        initSavedSongs()

        /** RecyclerView */
        val savedSongRVAdapter = SavedSongRVAdapter(savedSongs) // adapter와 data list 연결
        binding.lockerContentRv.adapter = savedSongRVAdapter // adapter와 RecyclerView 연결

        /** RecyclerView 버튼 이벤트 처리 */
        savedSongRVAdapter.setMyLockerItemClickListener(object: SavedSongRVAdapter.lockerItemClickListener {
            override fun onRemoveSong(position: Int) {
                Log.d("Flow", "SavedSongFragment ClickListener")
                savedSongRVAdapter.removeItem(position)
            }
        })

        return binding.root
    }

    /**
     * SavedSong 리스트 초기화
     * SongDatabase의 데이터를 리스트에 추가
     */
    private fun initSavedSongs() {
//        savedSongs.apply {
//            add(Song("Butter", "방탄소년단 (BTS)", coverImg = R.drawable.img_album_exp))
//            add(Song("LILAC", "아이유 (IU)", coverImg = R.drawable.img_album_exp2))
//            add(Song("Next Level", "에스파 (AESPA)", coverImg = R.drawable.img_album_exp3))
//            add(Song("Boy with LUV", "방탄소년단 (BTS)", coverImg = R.drawable.img_album_exp4))
//            add(Song("BBoom BBoom", "모모랜드 (MOMOLAND)", coverImg = R.drawable.img_album_exp5))
//            add(Song("Weekend", "태연 (Tae Yeon)", coverImg = R.drawable.img_album_exp6))
//            add(Song("TOO BAD (feat. Anderson .Paak)", "G-DRAGON", coverImg = R.drawable.img_album_exp7))
//            add(Song("like JENNIE", "제니 (JENNIE)", coverImg = R.drawable.img_album_exp8))
//            add(Song("모르시나요(PROD.로코베리)", "조째즈", coverImg = R.drawable.img_album_exp9))
//        }
        songDB = SongDatabase.getInstance(mContext)!!
        savedSongs.addAll(songDB.SongDao().getSongs())
    }
}