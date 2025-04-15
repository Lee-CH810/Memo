package com.example.flo_clone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.flo_clone.databinding.FragmentSongBinding

class SongFragment : Fragment() {

    lateinit var binding: FragmentSongBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSongBinding.inflate(layoutInflater)

        /**
         * 앨범의 수록곡을 눌렀을 때 수록곡의 제목이 나오도록 함
         */
        binding.songLalacLayout.setOnClickListener {
            // Toast.makeText(...).show()를 통해 토스트 메세지를 띄울 수 있음.
            // 토스트 메시지를 어디서 띄울 것인지(activity--> albumFragment가 있는 곳은 activity이기 때문)와
            // 띄울 메세지, 얼마나 띄울 것인지(짧게 띄울 것이므로 LENGTH_SHORT)를 인자로 넘겨줌
            Toast.makeText(activity, "LILAC", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }
}