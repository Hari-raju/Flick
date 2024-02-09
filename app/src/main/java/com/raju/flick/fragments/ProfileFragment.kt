package com.raju.flick.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.raju.flick.R
import com.raju.flick.activities.EditActivity
import com.raju.flick.activities.MainActivity
import com.raju.flick.adapters.TabAdapter
import com.raju.flick.databinding.FragmentProfileBinding
import com.raju.flick.models.PreferenceManager
import com.raju.flick.utils.EncoderDecoder.Companion.decodeImage
import com.raju.flick.utils.KEY_BIO
import com.raju.flick.utils.KEY_FOLLOWERS
import com.raju.flick.utils.KEY_FOLLOWING
import com.raju.flick.utils.KEY_IMAGE
import com.raju.flick.utils.KEY_NAME
import com.raju.flick.utils.KEY_POSTS
import com.raju.flick.utils.KEY_USER_NAME


class ProfileFragment : Fragment() {

    private lateinit var preferenceManager: PreferenceManager
    private lateinit var profileBinding: FragmentProfileBinding
    private val icons= intArrayOf(R.drawable.post_icon,R.drawable.play_button)

    override fun onResume() {
        super.onResume()
        init()
    }

    override fun onStart() {
        super.onStart()
        init()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        profileBinding = FragmentProfileBinding.inflate(inflater, container, false)
        preferenceManager = context?.let { PreferenceManager(it) }!!
        listeners()
        val tabAdapter = TabAdapter(requireActivity())
        profileBinding.viewPager.adapter=tabAdapter
        TabLayoutMediator(profileBinding.homeProfileTabLayout, profileBinding.viewPager) { tab, position ->
            tab.setIcon(icons[position])

        }.attach()
        return profileBinding.root
    }


    private fun listeners(){
        profileBinding.homeEditProfile.setOnClickListener {
            startActivity(Intent(activity,EditActivity::class.java))
        }

        profileBinding.logout.setOnClickListener {
            preferenceManager.clear()
            startActivity(Intent(activity,MainActivity::class.java))
            activity?.finish()
        }
    }

    private fun init() {
        if (preferenceManager.getString(KEY_IMAGE) != null) {
            profileBinding.homeProfileProfile.setImageBitmap(
                decodeImage(
                    preferenceManager.getString(
                        KEY_IMAGE
                    )!!
                )
            )
        }

        profileBinding.homeProfileUsername.text=preferenceManager.getString(KEY_USER_NAME)

        //Setting up name
        preferenceManager.getString(KEY_NAME)?.let {
            profileBinding.homeProfileName.text = it
        }

        //Setting up bio
        preferenceManager.getString(KEY_BIO)?.let {
            profileBinding.homeProfileBio.text = it
        }

        //Setting up followers and following counts
        profileBinding.profileFollowersCount.text=preferenceManager.getString(KEY_FOLLOWERS)
        profileBinding.profileFollowingCount.text=preferenceManager.getString(KEY_FOLLOWING)
        profileBinding.profilePostCount.text=preferenceManager.getString(KEY_POSTS)
    }

}