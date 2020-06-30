package com.example.findyourpets.`object`

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.findyourpets.fragment.DetailedInfo
import com.example.findyourpets.fragment.OverallInfo

class PostInfoAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    private var fragments:ArrayList<Fragment> = arrayListOf(
        OverallInfo(),
        DetailedInfo()
    )

    override fun getItemCount(): Int{
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

}