package com.raju.flick.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.raju.flick.R
import com.raju.flick.databinding.PostViewPostHolderBinding
import com.raju.flick.fragments.CommentDialogFragment
import com.raju.flick.models.LikeModel
import com.raju.flick.models.PostModel
import com.raju.flick.models.PreferenceManager
import com.raju.flick.utils.DoubleClickListener
import com.raju.flick.utils.EncoderDecoder
import com.raju.flick.utils.FireBaseHelper
import com.raju.flick.utils.KEY_COLLECTION_USERS
import com.raju.flick.utils.KEY_IMAGE
import com.raju.flick.utils.KEY_USER_ID
import com.raju.flick.utils.KEY_USER_NAME
import com.raju.flick.utils.getTimeStamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class PostAdapter(
    val context: Context,
    private var posts: ArrayList<PostModel>
) : RecyclerView.Adapter<PostAdapter.PostHolder>() {

    val preferenceManager = PreferenceManager(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val binding = PostViewPostHolderBinding.inflate(LayoutInflater.from(context), parent, false)
        return PostHolder(binding)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        try {
            holder.setPosts(posts[position])
        } catch (e: Exception) {
            Log.d("PostAdapter", e.message.toString())
        }
    }


    inner class PostHolder(
        binding: PostViewPostHolderBinding
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {
        private var binding: PostViewPostHolderBinding

        init {
            this@PostHolder.binding = binding
        }

        fun setPosts(post: PostModel) {
            val helper = FireBaseHelper()

            //Checking whether user already liked or not
            helper.isCurrentUserLiked(preferenceManager.getString(KEY_USER_ID)!!, post) {
                val isLiked = it
                if (isLiked) {
                    binding.postHolderLike.visibility = View.GONE
                    binding.postHolderLikeBlue.visibility = View.VISIBLE
                }
            }

            //Getting Likes Count
            helper.getLikesCount(post) {res->
                if (res == 0L) {
                    binding.likedBy.visibility = View.GONE
                } else if (res == 1L) {
                    binding.likedBy.visibility = View.VISIBLE
                    val likesCount = res.toString()
                    val likes = "like"
                    val formattedText =
                        context.getString(R.string.user_post_format, likesCount, likes)
                    binding.likedBy.text = formattedText
                } else if (res > 1L) {
                    binding.likedBy.visibility = View.VISIBLE
                    val likesCount = res.toString()
                    val likes = "likes"
                    val formattedText =
                        context.getString(R.string.user_post_format, likesCount, likes)
                    binding.likedBy.text = formattedText
                }
            }

            Firebase.firestore
                .collection(KEY_COLLECTION_USERS)
                .document(post.userId!!)
                .get()
                .addOnCompleteListener { result ->
                    if (result.isSuccessful && result.result != null) {
                        binding.postHolderUsername.text =
                            result.result.getString(KEY_USER_NAME)
                        binding.postHolderProfile.setImageBitmap(
                            EncoderDecoder.decodeImage(result.result.getString(KEY_IMAGE)!!)
                        )
                        if (post.caption == null || post.caption!!.isEmpty()) {
                            binding.postHolderCaption.visibility = View.GONE
                        } else {
                            val userName: String = result.result.getString(KEY_USER_NAME)!!
                            val postCaption: String? = post.caption
                            val formattedString =
                                context.getString(
                                    R.string.user_post_format,
                                    userName,
                                    postCaption
                                )
                            binding.postHolderCaption.text = formattedString
                        }
                        val simpleFormat = SimpleDateFormat(
                            "yyyy-MM-dd'T'HH:mm:ss'Z'",
                            Locale.forLanguageTag("en-IN")
                        )
                        val date: Date = simpleFormat.parse(getTimeStamp())!!
                        val postDate = simpleFormat.parse(post.dateCreated!!)!!
                        val timeDifference = date.time - postDate.time
                        val days: Long = TimeUnit.MILLISECONDS.toDays(timeDifference)
                        val hours: Long = TimeUnit.MILLISECONDS.toHours(timeDifference) % 24
                        val minutes: Long =
                            TimeUnit.MILLISECONDS.toMinutes(timeDifference) % 60
                        val seconds: Long =
                            TimeUnit.MILLISECONDS.toSeconds(timeDifference) % 60
                        Log.d(
                            "PostAdapter",
                            "$days day $hours hr  $minutes mins $seconds sec"
                        )
                        if (days == 0L) {
                            if (hours == 0L) {
                                if (minutes == 0L) {
                                    if (seconds == 0L) {
                                        binding.postHolderDatePosted.text =
                                            context.getString(R.string.just_now)
                                    } else {
                                        val second: String = seconds.toString()
                                        val text: String = if (seconds == 1L) {
                                            "second ago"
                                        } else {
                                            "seconds ago"
                                        }
                                        binding.postHolderDatePosted.text =
                                            context.getString(
                                                R.string.user_post_format,
                                                second,
                                                text
                                            )
                                    }
                                } else {
                                    val minute: String = minutes.toString()
                                    val text: String = if (minutes == 1L) {
                                        "minute ago"
                                    } else {
                                        "minutes ago"
                                    }
                                    binding.postHolderDatePosted.text =
                                        context.getString(
                                            R.string.user_post_format,
                                            minute,
                                            text
                                        )
                                }
                            } else {
                                val hour: String = hours.toString()
                                val text: String = if (hours == 1L) {
                                    "hour ago"
                                } else {
                                    "hours ago"
                                }
                                binding.postHolderDatePosted.text =
                                    context.getString(R.string.user_post_format, hour, text)
                            }
                        } else {
                            val day: String = days.toString()
                            val text: String = if (days == 1L) {
                                "day ago"
                            } else {
                                "days ago"
                            }
                            binding.postHolderDatePosted.text =
                                context.getString(R.string.user_post_format, day, text)
                        }
                        Glide.with(context)
                            .load(post.imagePath)
                            .placeholder(R.drawable.place_holderimage)
                            .error(R.drawable.error_image)
                            .into(binding.postHolderPosts)
                    }
                }

            val zoomInAnim = AnimationUtils.loadAnimation(context, R.anim.zoom_in)
            val zoomOutAnim = AnimationUtils.loadAnimation(context, R.anim.zoom_out)
            binding.postHolderPosts.setOnClickListener(object : DoubleClickListener() {
                override fun onDoubleClick(v: View?) {
                    binding.postHolderLikeBlue.startAnimation(zoomInAnim)
                    binding.postHolderInsideLike.startAnimation(zoomInAnim)
                    binding.postHolderInsideLike.startAnimation(zoomOutAnim)
                    if (binding.postHolderLike.visibility==View.VISIBLE) {
                        binding.postHolderLike.visibility = View.GONE
                        binding.postHolderLikeBlue.visibility = View.VISIBLE
                        binding.postHolderLikeBlue.startAnimation(zoomInAnim)
                        helper.addLike(post, LikeModel(preferenceManager.getString(KEY_USER_ID)!!)){
                            if(it==-1L){
                                Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show()
                            }
                            else{
                                if(it==0L){
                                    binding.likedBy.visibility=View.GONE
                                }
                                else if (it == 1L) {
                                    binding.likedBy.visibility = View.VISIBLE
                                    val likesCount = it.toString()
                                    val likes = "like"
                                    val formattedText =
                                        context.getString(R.string.user_post_format, likesCount, likes)
                                    binding.likedBy.text = formattedText
                                } else if (it > 1L) {
                                    binding.likedBy.visibility = View.VISIBLE
                                    val likesCount = it.toString()
                                    val likes = "likes"
                                    val formattedText =
                                        context.getString(R.string.user_post_format, likesCount, likes)
                                    binding.likedBy.text = formattedText
                                }
                            }
                        }
                    }
                    else{
                        binding.postHolderLikeBlue.startAnimation(zoomInAnim)
                    }
                }
            })

            binding.postHolderLikeBlue.setOnClickListener {
                binding.postHolderLike.visibility = View.VISIBLE
                binding.postHolderLikeBlue.visibility = View.GONE
                helper.removeLike(post, LikeModel(preferenceManager.getString(KEY_USER_ID)!!)) {
                    if(it==-1L){
                        Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        if(it==0L){
                            binding.likedBy.visibility=View.GONE
                        }
                        else if (it == 1L) {
                            binding.likedBy.visibility = View.VISIBLE
                            val likesCount = it.toString()
                            val likes = "like"
                            val formattedText =
                                context.getString(R.string.user_post_format, likesCount, likes)
                            binding.likedBy.text = formattedText
                        } else if (it > 1L) {
                            binding.likedBy.visibility = View.VISIBLE
                            val likesCount = it.toString()
                            val likes = "likes"
                            val formattedText =
                                context.getString(R.string.user_post_format, likesCount, likes)
                            binding.likedBy.text = formattedText
                        }
                    }
                }
            }
            binding.postHolderLike.setOnClickListener {
                binding.postHolderLike.visibility = View.GONE
                binding.postHolderLikeBlue.visibility = View.VISIBLE
                binding.postHolderLikeBlue.startAnimation(zoomInAnim)
                helper.addLike(post, LikeModel(preferenceManager.getString(KEY_USER_ID)!!)) {
                    if(it==-1L){
                        Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        if(it==0L){
                            binding.likedBy.visibility=View.GONE
                        }
                        else if (it == 1L) {
                            binding.likedBy.visibility = View.VISIBLE
                            val likesCount = it.toString()
                            val likes = "like"
                            val formattedText =
                                context.getString(R.string.user_post_format, likesCount, likes)
                            binding.likedBy.text = formattedText
                        } else if (it > 1L) {
                            binding.likedBy.visibility = View.VISIBLE
                            val likesCount = it.toString()
                            val likes = "likes"
                            val formattedText =
                                context.getString(R.string.user_post_format, likesCount, likes)
                            binding.likedBy.text = formattedText
                        }
                    }
                }
            }

            binding.postHolderComment.setOnClickListener {
                //Open Comment Dialog Box
                val bottomSheetFragment = CommentDialogFragment()
                bottomSheetFragment.show((context as AppCompatActivity).supportFragmentManager,bottomSheetFragment.tag)
            }
        }
    }
}
