package com.raju.flick.activities

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.raju.flick.databinding.ActivityNextBinding
import com.raju.flick.models.PostModel
import com.raju.flick.models.PreferenceManager
import com.raju.flick.utils.KEY_COLLECTION_USERS
import com.raju.flick.utils.KEY_PHOTOS
import com.raju.flick.utils.KEY_POSTS
import com.raju.flick.utils.KEY_POST_FOLDER
import com.raju.flick.utils.KEY_USER_ID
import com.raju.flick.utils.KEY_USER_POSTS
import com.raju.flick.utils.KEY_USER_POST_HOLDER
import com.raju.flick.utils.getTags
import com.raju.flick.utils.getTimeStamp
import com.raju.flick.utils.loadImage
import com.raju.flick.utils.uploadImageBitmapToStorage
import com.raju.flick.utils.uploadImageToStorage

class NextActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityNextBinding.inflate(layoutInflater)
    }
    private val preferenceManager by lazy {
        PreferenceManager(this@NextActivity)
    }
    private var url: String? = null
    private var bitmap: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if (intent.getStringExtra("from") == "camera") {
            bitmap = intent.getParcelableExtra("camera_bitmap")
        } else {
            url = intent.getStringExtra("image")
        }
        init()
        listeners()
    }

    private fun listeners() {
        binding.postBack.setOnClickListener {
            finish()
        }

        binding.post.setOnClickListener {
            if (url != null) {
                binding.postCap.visibility = View.GONE
                binding.progressPost.visibility = View.VISIBLE
                binding.post.visibility = View.GONE
                uploadImageToStorage(
                    url!!,
                    KEY_POST_FOLDER,
                    preferenceManager.getString(KEY_USER_ID)!!
                ) { downloadedUrl ->
                    Log.d("NextActivity", downloadedUrl.toString())
                    val countPost = preferenceManager.getString(KEY_POSTS)!!.toInt() + 1
                    var caption = ""
                    if (binding.postCap.text.toString().isNotEmpty()) {
                        caption = binding.postCap.text.toString()
                    }
                    val post = PostModel(
                        caption = caption,
                        dateCreated = getTimeStamp(),
                        imagePath = downloadedUrl,
                        userId = preferenceManager.getString(KEY_USER_ID),
                        tags = getTags(caption)
                    )
                    Firebase.firestore
                        .collection(KEY_USER_POSTS)
                        .document(preferenceManager.getString(KEY_USER_ID)!!)
                        .collection(KEY_USER_POST_HOLDER)
                        .add(post)
                        .addOnSuccessListener { id ->
                            Firebase.firestore.collection(KEY_USER_POSTS)
                                .document(preferenceManager.getString(KEY_USER_ID)!!)
                                .collection(KEY_USER_POST_HOLDER)
                                .document(id.id)
                                .update("photoId", id.id)
                                .addOnSuccessListener {
                                    post.photoId = id.id
                                    Firebase.firestore
                                        .collection(KEY_PHOTOS)
                                        .document(id.id)
                                        .set(post)
                                        .addOnSuccessListener {
                                            Firebase.firestore.collection(KEY_COLLECTION_USERS)
                                                .document(preferenceManager.getString(KEY_USER_ID)!!)
                                                .update(KEY_POSTS, countPost.toString())
                                                .addOnSuccessListener { _ ->
                                                    preferenceManager.putString(
                                                        KEY_POSTS,
                                                        countPost.toString()
                                                    )
                                                    startActivity(
                                                        Intent(
                                                            this@NextActivity,
                                                            HomeActivity::class.java
                                                        )
                                                    )
                                                    finish()
                                                }
                                        }
                                }
                        }

                }
            } else {
                if (bitmap != null) {
                    binding.postCap.visibility = View.GONE
                    binding.progressPost.visibility = View.VISIBLE
                    binding.post.visibility = View.GONE
                    uploadImageBitmapToStorage(
                        bitmap!!,
                        KEY_POST_FOLDER,
                        preferenceManager.getString(KEY_USER_ID)!!
                    ) { downloadedUrl ->
                        Log.d("NextActivity", downloadedUrl.toString())
                        val countPost = preferenceManager.getString(KEY_POSTS)!!.toInt() + 1
                        var caption = ""
                        if (binding.postCap.text.toString().isNotEmpty()) {
                            caption = binding.postCap.text.toString()
                        }
                        val post = PostModel(
                            caption = caption,
                            dateCreated = getTimeStamp(),
                            imagePath = downloadedUrl,
                            userId = preferenceManager.getString(KEY_USER_ID),
                            tags = getTags(caption)
                        )
                        Firebase.firestore
                            .collection(KEY_USER_POSTS)
                            .document(preferenceManager.getString(KEY_USER_ID)!!)
                            .collection(KEY_USER_POST_HOLDER)
                            .add(post)
                            .addOnSuccessListener { id ->
                                Firebase.firestore.collection(KEY_USER_POSTS)
                                    .document(preferenceManager.getString(KEY_USER_ID)!!)
                                    .collection(KEY_USER_POST_HOLDER)
                                    .document(id.id)
                                    .update("photoId", id.id)
                                    .addOnSuccessListener {
                                        post.photoId = id.id
                                        Firebase.firestore
                                            .collection(KEY_PHOTOS)
                                            .document(id.id)
                                            .set(post)
                                            .addOnSuccessListener {
                                                Firebase.firestore.collection(KEY_COLLECTION_USERS)
                                                    .document(
                                                        preferenceManager.getString(
                                                            KEY_USER_ID
                                                        )!!
                                                    )
                                                    .update(KEY_POSTS, countPost.toString())
                                                    .addOnSuccessListener { _ ->
                                                        preferenceManager.putString(
                                                            KEY_POSTS,
                                                            countPost.toString()
                                                        )
                                                        startActivity(
                                                            Intent(
                                                                this@NextActivity,
                                                                HomeActivity::class.java
                                                            )
                                                        )
                                                        finish()
                                                    }
                                            }
                                    }
                            }
                    }
                }
            }
        }
    }


    private fun init() {
        if(url!=null){
            loadImage(this@NextActivity, url!!, binding.postPic)
        }
        else{
            binding.postPic.setImageBitmap(bitmap)
        }
    }
}
