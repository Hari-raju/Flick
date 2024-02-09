package com.raju.flick.activities

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.raju.flick.adapters.PostAdapter
import com.raju.flick.databinding.ActivityPostViewBinding
import com.raju.flick.models.PostModel

class PostViewActivity : AppCompatActivity() {
    private val postView by lazy {
        ActivityPostViewBinding.inflate(layoutInflater)
    }

    private var postArray: ArrayList<PostModel>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(postView.root)
        postArray = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra(
                "PostsArray",
                PostModel::class.java
            ) as ArrayList<PostModel>
        } else {
            intent.getSerializableExtra("PostsArray") as ArrayList<PostModel>
        }
        val position = intent.getStringExtra("position")
        if(position!=null){
            init(position.toInt())
        }
        listeners()
    }

    private fun listeners(){
        postView.postViewBack.setOnClickListener {
            finish()
        }
    }

    private fun init(pos:Int) {
        val adapter = postArray?.let { PostAdapter(this, it) }
        postView.postViewPosts.adapter = adapter
        postView.postViewPosts.smoothScrollToPosition(pos)
    }
}