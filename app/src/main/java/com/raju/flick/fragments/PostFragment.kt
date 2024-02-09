package com.raju.flick.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.raju.flick.R
import com.raju.flick.activities.PostViewActivity
import com.raju.flick.adapters.GridImageAdapter
import com.raju.flick.databinding.FragmentPostBinding
import com.raju.flick.models.PostModel
import com.raju.flick.models.PreferenceManager
import com.raju.flick.utils.KEY_USER_ID
import com.raju.flick.utils.KEY_USER_POSTS
import com.raju.flick.utils.KEY_USER_POST_HOLDER
import java.text.SimpleDateFormat
import java.util.Locale


class PostFragment : Fragment() {
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var postBinding:FragmentPostBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        preferenceManager = context?.let { PreferenceManager(it) }!!
        postBinding= FragmentPostBinding.inflate(inflater,container,false)
        setUpGridView()
        return postBinding.root
    }

    private fun setUpGridView(){
        val postArray:ArrayList<PostModel> = ArrayList()
        Firebase.firestore.collection(KEY_USER_POSTS)
            .document(preferenceManager.getString(KEY_USER_ID)!!)
            .collection(KEY_USER_POST_HOLDER)
            .get()
            .addOnCompleteListener {
                if(it.isSuccessful){
                    val querySnapshot = it.result
                    for(documents in querySnapshot.documents){
                        try{
                            Log.d("PostFragment",documents.toObject(PostModel::class.java).toString())
                            val data = documents.toObject(PostModel::class.java)
                            postArray.add(data!!)
                        }
                        catch (e:Exception){
                            Log.d("PostFragment",e.message.toString())
                        }
                    }
                    val gridWidth = resources.displayMetrics.widthPixels/3
                    postBinding.postsGrid.columnWidth=gridWidth
                    postArray.sortByDescending {res->
                        res.dateCreated?.let { it1 ->
                            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.forLanguageTag("en-IN")).parse(
                                it1
                            )
                        }
                    }
                    val postLinks:ArrayList<String> = ArrayList()
                    for(i in postArray){
                        i.imagePath?.let { it1 -> postLinks.add(it1) }
                    }
                    val adapter = GridImageAdapter(requireContext(),R.layout.layout_grid_view,postLinks)
                    postBinding.postsGrid.adapter=adapter
                    postBinding.postsGrid.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                        val intent = Intent(activity, PostViewActivity::class.java)
                        intent.putExtra("position",position.toString())
                        intent.putExtra("PostsArray",postArray)
                        startActivity(intent)
                    }
                }
            }
    }
}