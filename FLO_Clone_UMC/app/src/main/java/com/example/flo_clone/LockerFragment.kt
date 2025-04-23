package com.example.flo_clone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.flo_clone.databinding.FragmentLockerBinding
import com.google.android.material.tabs.TabLayoutMediator

class LockerFragment : Fragment() {

    lateinit var binding: FragmentLockerBinding
    private val tabInfo = arrayListOf("저장된 곡", "음악 파일")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLockerBinding.inflate(layoutInflater, container, false)

        val lockerAdapter = LockerVPAdapter(this)
        binding.lockerTabVp.adapter = lockerAdapter

        TabLayoutMediator(binding.lockerTb, binding.lockerTabVp){
            tab, position ->
            tab.text = tabInfo[position]
        }.attach()

        return binding.root
    }
}