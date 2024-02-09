package com.raju.flick.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.raju.flick.R
import com.raju.flick.databinding.ActivityHomeBinding
import com.raju.flick.fragments.AddFragment
import com.raju.flick.fragments.HomeFragment
import com.raju.flick.fragments.ProfileFragment
import com.raju.flick.fragments.ReelFragment
import com.raju.flick.fragments.SearchFragment
import com.raju.flick.utils.MyViewModel

class HomeActivity : AppCompatActivity() {

    private lateinit var homeBinding: ActivityHomeBinding
    private val viewModel:MyViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(homeBinding.root)
        homeBinding.bottomNav.background=null
        if(savedInstanceState==null){
            replaceFragment(HomeFragment())
        }
        else{
            restoreActiveFragment()
        }
        navigationFragments()
        listeners()
    }

    private fun replaceFragment(fragment:Fragment){
        val fragmentTransaction:FragmentTransaction = supportFragmentManager.beginTransaction()
        val fragmentTag = fragment.javaClass.simpleName
        fragmentTransaction.setCustomAnimations(
            R.anim.fade_in,
            R.anim.fade_out
        )
        fragmentTransaction.replace(homeBinding.frame.id,fragment)
        viewModel.fragmentTag=fragmentTag
        fragmentTransaction.commit()
    }


    private fun navigationFragments(){
        homeBinding.bottomNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home ->{
                    replaceFragment(HomeFragment())
                    true
                }

                R.id.search ->{
                    replaceFragment(SearchFragment())
                    true
                }

                R.id.reels ->{
                    replaceFragment(ReelFragment())
                    true
                }

                R.id.profile->{
                    replaceFragment(ProfileFragment())
                    true
                }

                else -> {
                    Toast.makeText(
                        this@HomeActivity,
                        "Problem",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("Error","Some Problem")
                    true
                }
            }
        }
    }

    private fun listeners(){
        homeBinding.addFab.setOnClickListener {
            val addFragment = AddFragment()
            addFragment.show(supportFragmentManager,addFragment.tag)
        }
    }

    private fun restoreActiveFragment(){
        val tag = viewModel.fragmentTag
        if(tag!=null){
            val fragment = supportFragmentManager.findFragmentByTag(tag)
            if (fragment != null) {
                // The fragment is still in the FragmentManager, no need to re-add
            } else {
                when(tag){
                    "HomeFragment"->{
                        replaceFragment(HomeFragment())
                    }
                    "SearchFragment"->{
                        replaceFragment(SearchFragment())
                    }
                    "ReelFragment"->{
                        replaceFragment(ReelFragment())
                    }
                    "ProfileFragment"->{
                        replaceFragment(ProfileFragment())
                    }

                    else -> {
                        Toast.makeText(
                            this@HomeActivity,
                            "Problem",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("Error","Some Problem")
                    }
                }
            }
        }
    }
}