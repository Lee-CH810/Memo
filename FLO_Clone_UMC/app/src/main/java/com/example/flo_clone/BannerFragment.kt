package com.example.flo_clone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.flo_clone.databinding.FragmentBannerBinding

/**
 * HomeFragment의 BannerAdapter에서 지원하는 fragmentlist에 추가될 fragment
 * @param imgRes ViewPager에 여러 개의 이미지를 넣기 위해 새로운 BannerFragment가 생성되어 list에 추가될 때마다 받을 이미지 리소스들
 */
class BannerFragment(val imgRes : Int) : Fragment() {

    lateinit var binding: FragmentBannerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBannerBinding.inflate(layoutInflater)

        // 인자 값으로 받은 이미지로 imageView의 이미지가 바뀌게 됨.
        binding.bannerImageIv.setImageResource(imgRes)
        return binding.root
    }
}