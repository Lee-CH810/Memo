package com.example.flo_clone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.collection.intListOf
import com.example.flo_clone.databinding.FragmentAlbumBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson

class AlbumFragment : Fragment() {

    lateinit var binding: FragmentAlbumBinding
    private var gson: Gson = Gson()
    private val information = arrayListOf("수록곡", "상세 정보", "영상")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlbumBinding.inflate(inflater, container, false)

        // HomeFragment에서 생성한 argument에 들어있는 Json형태의 데이터를 다시 album 객체로 변환
        val albumJson = arguments?.getString("album")
        val album = gson.fromJson(albumJson, Album::class.java)
        setInit(album)

        /**
         * 뒤로 가기
         */
        binding.albumBackIv.setOnClickListener {
            // context sd : MainActivity 안에 있는 Fragment를 어디서 변경할 건지 명시하는 것.
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, HomeFragment())
                .commitAllowingStateLoss()
        }

        /**
         * TabLayout 하단 영역의 ViewPager 구현
         * SongFragment, DetailFragment, VideoFragment 와 albumContentVP를, albumAdapter를 통해 연결
         */
        val albumAdapter = AlbumVPAdapter(this)
        binding.albumContentVp.adapter = albumAdapter

        /**
         * TabLayout과 ViewPager을 연결하는 중재자
         * Tab이 선택될 때, ViewPager의 위치를 선택된 Tab과 동기화하고,
         * ViewPager가 슬라이드될 때, Tab의 위치를 동기화함.
         * 연결할 TabLayout과 ViewPager를 차례로 인자로 받음
         */
        TabLayoutMediator(binding.albumContentTb, binding.albumContentVp) {
            tab, position ->
            tab.text = information[position] // tab의 위치에 따라 tab 바에 있는 글자를 information list의 요소들로 지정
        }.attach() // TabLayout과 ViewPager를 붙여주는 코드

        return binding.root
    }

    /**
     * 넘겨받은 album 객체의 정보로 AlbumFragement에서의 앨범 제목, 가수명, 앨범 이미지를 재설정
     */
    private fun setInit(album: Album) {
        binding.albumAlbumIv.setImageResource(album.coverImg!!)
        binding.albumMusicTitleTv.text = album.title.toString()
        binding.albumSingerNameTv.text = album.singer.toString()
    }
}