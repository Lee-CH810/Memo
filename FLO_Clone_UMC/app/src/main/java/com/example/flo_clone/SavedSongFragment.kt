package com.example.flo_clone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.flo_clone.databinding.FragmentSavedSongBinding

class SavedSongFragment : Fragment() {

    lateinit var binding: FragmentSavedSongBinding
    private var savedSongs = ArrayList<Song>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSavedSongBinding.inflate(layoutInflater)

        /** 보관함 곡 초기화 - RoomDB 이용 가능할 듯 */
        initSavedSongs()

        /** RecyclerView */
        val savedSongRVAdapter = SavedSongRVAdapter(savedSongs) // adapter와 data list 연결
        binding.lockerContentRv.adapter = savedSongRVAdapter // adapter와 RecyclerView 연결

        return binding.root
    }

    private fun initSavedSongs() {
        savedSongs.apply {
            add(Song("Butter", "방탄소년단 (BTS)", coverImg = R.drawable.img_album_exp))
            add(Song("LILAC", "아이유 (IU)", coverImg = R.drawable.img_album_exp2))
            add(Song("Next Level", "에스파 (AESPA)", coverImg = R.drawable.img_album_exp3))
            add(Song("Boy with LUV", "방탄소년단 (BTS)", coverImg = R.drawable.img_album_exp4))
            add(Song("BBoom BBoom", "모모랜드 (MOMOLAND)", coverImg = R.drawable.img_album_exp5))
            add(Song("Weekend", "태연 (Tae Yeon)", coverImg = R.drawable.img_album_exp6))
            add(Song("TOO BAD (feat. Anderson .Paak)", "G-DRAGON", coverImg = R.drawable.img_album_exp7))
            add(Song("like JENNIE", "제니 (JENNIE)", coverImg = R.drawable.img_album_exp8))
            add(Song("모르시나요(PROD.로코베리)", "조째즈", coverImg = R.drawable.img_album_exp9))
        }
    }
}