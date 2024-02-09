package com.raju.flick.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.raju.flick.databinding.FragmentCommentDialogBinding


class CommentDialogFragment : BottomSheetDialogFragment() {
    private lateinit var binding:FragmentCommentDialogBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCommentDialogBinding.inflate(inflater, container, false)
        return binding.root
    }


}