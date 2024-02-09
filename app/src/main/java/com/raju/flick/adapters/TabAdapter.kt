package com.raju.flick.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.raju.flick.fragments.PostFragment
import com.raju.flick.fragments.ReelsFragment

class TabAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        // Return the fragment for each position
        return if (position == 0) {
            PostFragment()
        } else {
            ReelsFragment()
        }
    }

    override fun getItemCount(): Int {
        // Number of fragments
        return 2
    }
}
