package com.raju.flick.models

import java.io.Serializable

data class CommentModel(
    var userId:String?=null,
    var comment:String?=null,
    var commentId:String?=null,
    var dateCommented:String?=null,
    var commentLikes:Long=0
):Serializable