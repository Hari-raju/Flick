package com.raju.flick.models

import java.io.Serializable

data class UserModel(
        var image:String?=null,
        var username:String?=null,
        var email:String?=null,
        var password:String?=null,
        var name:String?=null,
        var bio:String?=null,
        var followers:String="0",
        var following:String="0",
        var posts:String="0"
):Serializable
