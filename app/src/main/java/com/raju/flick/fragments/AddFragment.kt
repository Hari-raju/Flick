package com.raju.flick.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.raju.flick.R
import com.raju.flick.activities.SharePostActivity
import com.raju.flick.databinding.FragmentAddBinding


class AddFragment : BottomSheetDialogFragment() {

    private lateinit var addBinding: FragmentAddBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        addBinding = FragmentAddBinding.inflate(inflater, container, false)
        listeners()
        return addBinding.root
    }

    private fun listeners() {

        addBinding.addPost.setOnClickListener {
            startActivity(Intent(activity, SharePostActivity::class.java))
            dismiss()
        }
        addBinding.addReel.setOnClickListener {

        }
    }
}