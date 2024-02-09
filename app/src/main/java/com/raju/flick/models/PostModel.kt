package com.raju.flick.models

import java.io.Serializable

data class PostModel(
    var caption:String?=null,
    var dateCreated:String?=null,
    var imagePath:String?=null,
    var photoId:String?=null,
    var userId:String?=null,
    var tags:String?=null,
    val likes:List<LikeModel>?=null,
    var likesCount:Long=0L
):Serializable