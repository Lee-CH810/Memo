package com.example.flo_clone

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * ViewPager 구현 원리
 * ViewPager에 이미지를 그냥 넣는 것이 아니라
 * fragment를 만들어 각 fragment 하나당 이미지 하나를 넣고 여러 fragment가 좌우로 슬라이드 되도록 하는 것임
 * 따라서, 인자값으로 fragment를 받아야 할 필요가 있음.
 * Adapter는 FragmentStateAdapter라는 class를 상속받을 필요가 있음.
 */
class BannerVPAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    // 여러 가지 fragment를 ViewPager에서 보여주어야 하므로, 여러 개의 fragment를 담아둘 공간이 필요함.
    // private : 이 클래스 안에서만 사용하겠다는 의미. 이를 사용하지 않으면, HomeFragment 등의 BannerVPAdapter 외부에서도 사용이 가능함.
    private val fragmentlist : ArrayList<Fragment> = ArrayList()

    /**
     * 이 Adapter Class와 연결된 ViewPager에게 데이터를 전달할 때, 데이터를 몇 개를 전달할 것인지를 알려주는 함수
     * 전달할 데이터: fragmentlist
     * 함수에 =을 사용함으로써 fragmentlist.size를 반환하도록 하는 코드를 다음과 같이 return 없이 작성할 수 있음.
     */
    override fun getItemCount(): Int = fragmentlist.size

    /**
     * fragmentlist의 item인 fragment들을 생성해주는 함수
     * -> fragment의 list에서 fragment를 리턴함.
     */
    override fun createFragment(position: Int): Fragment = fragmentlist[position]

    /**
     * fragmentlist에 fragment를 추가하기 위한 함수
     */
    fun addFragment(fragment: Fragment) {
        fragmentlist.add(fragment)
        // list 안에 새로운 item이 추가되었을 때, ViewPager에게 새로운 item이 추가되었다고 알려주어 출력되게 만드는 역할
        notifyItemInserted(fragmentlist.size-1) // 새로운 값이 list에 추가되면, size-1의 위치에 item이 위치하기 때문.
    }
}