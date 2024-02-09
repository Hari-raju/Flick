package com.raju.flick.utils

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.raju.flick.models.LikeModel
import com.raju.flick.models.PostModel

class FireBaseHelper {
    private val firebase = Firebase.firestore

    fun addLike(post: PostModel, likeModel: LikeModel, callback: (Long) -> Unit) {
        val postRefPhotos = firebase.collection(KEY_PHOTOS).document(post.photoId!!)

        // Reference to the post document in the 'user_posts' collection
        val postRefUserPosts = firebase.collection(KEY_USER_POSTS)
            .document(post.userId!!)
            .collection(KEY_USER_POST_HOLDER)
            .document(post.photoId!!)

        // Create a batch
        val batch = firebase.batch()

        // Update the 'likes' array in the 'photos' collection
        batch.update(postRefPhotos, KEY_LIKES, FieldValue.arrayUnion(likeModel))

        // Update the 'likes' array in the 'user_posts' collection
        batch.update(postRefUserPosts, KEY_LIKES, FieldValue.arrayUnion(likeModel))

        // Update the 'likesCount' field in the 'photos' collection
        batch.update(postRefPhotos, KEY_LIKES_COUNT, FieldValue.increment(1))

        // Update the 'likesCount' field in the 'user_posts' collection
        batch.update(postRefUserPosts, KEY_LIKES_COUNT, FieldValue.increment(1))

        // Commit the batch
        batch.commit()
            .addOnSuccessListener {
                // Directly return the updated likes count in the callback
                getLikesCount(post){
                    callback(it)
                }
            }
            .addOnFailureListener {
                callback(-1)
            }
    }

    fun removeLike(post: PostModel, likeModel: LikeModel, callback: (Long) -> Unit) {
        val batch = firebase.batch()

        val photoRef = firebase.collection(KEY_PHOTOS).document(post.photoId!!)
        batch.update(photoRef, KEY_LIKES, FieldValue.arrayRemove(likeModel))

        val userPostRef = firebase.collection(KEY_USER_POSTS)
            .document(post.userId!!)
            .collection(KEY_USER_POST_HOLDER)
            .document(post.photoId!!)
        batch.update(userPostRef, KEY_LIKES, FieldValue.arrayRemove(likeModel))
        batch.update(photoRef, KEY_LIKES_COUNT,FieldValue.increment(-1))
        batch.update(userPostRef, KEY_LIKES_COUNT,FieldValue.increment(-1))
        batch.commit()
            .addOnSuccessListener {
                getLikesCount(post){
                    callback(it)
                }
            }
            .addOnFailureListener {
                callback(-1)
            }
    }


    fun getLikesCount(post: PostModel, onResult: (Long) -> Unit) {
        firebase.collection(KEY_PHOTOS)
            .document(post.photoId!!)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val totalLikes = documentSnapshot.getLong(KEY_LIKES_COUNT) ?: 0
                    onResult(totalLikes)
                } else {
                    // Document doesn't exist
                    onResult(0)
                }
            }
            .addOnFailureListener {
                // Handle the failure
                onResult(0)
            }
    }

    fun isCurrentUserLiked(userId: String, post: PostModel, isLiked: (Boolean) -> Unit) {
        firebase.collection(KEY_PHOTOS)
            .document(post.photoId!!)
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val likesList = it.get("likes") as? List<Map<String, String>> ?: emptyList()
                    isLiked(likesList.any { res ->
                        res["userId"] == userId
                    })
                } else {
                    isLiked(false)
                }
            }
            .addOnFailureListener {
                isLiked(false)
            }
    }
}