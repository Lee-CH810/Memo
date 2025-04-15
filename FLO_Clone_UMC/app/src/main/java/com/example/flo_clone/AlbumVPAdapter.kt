package com.example.flo_clone

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class AlbumVPAdapter(fragment : Fragment) : FragmentStateAdapter(fragment) {
    // ViewPager로 구현할 부분은 Tab의 수록곡, 상세정보, 영상 부분이므로
    // 3개의 부분을 Fragment로 만들어 ViewPager로 구현해야 하는 상황 --> item의 수는 3으로 정해져 있음
    override fun getItemCount(): Int = 3

    // Banner를 구성할 때와는 달리, 같은 형식의 Fragment를 반복해서 사용하는 것이 아님.
    // 따라서, 수록곡 상세정보, 영상 부분 모두 각각의 디자인이 필요하므로
    // 각각의 Fragment를 만들어서 position에 따라 각 Fragment를 리턴할 수 있도록 해야함
    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> SongFragment() // 수록곡 Fragment
            1 -> DetailFragment() // 상세 정보 Fragment
            else -> VideoFragment() // 영상 Fragment
        }
    }

}