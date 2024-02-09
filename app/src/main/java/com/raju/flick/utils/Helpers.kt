package com.raju.flick.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.raju.flick.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

val Root_Dir: String? = Environment.getExternalStorageDirectory().path

val Pictures = "$Root_Dir/Pictures"

val Dcim = "$Root_Dir/DCIM"

fun loadImage(context: Context, url: String, image: ImageView) {
    Glide.with(context)
        .load(url)
        .placeholder(R.drawable.place_holderimage)
        .error(R.drawable.error_image)
        .into(image)
}

fun getBitmap(url:String):Bitmap?{
    val file = File(url)
    var fileInputStream:FileInputStream?=null
    var bitmap:Bitmap?=null
    try{
        fileInputStream= FileInputStream(file)
        bitmap=BitmapFactory.decodeStream(fileInputStream)
    }
    catch (e:Exception){
        e.localizedMessage?.let { Log.d("Directories", it) }
    }
    finally {
        try{
            fileInputStream?.close()
        }
        catch (e:Exception){
            e.localizedMessage?.let { Log.d("Directories", it) }
        }
    }
    return bitmap
}

fun uploadImageToStorage(uri: String, folder: String, userId:String,callback: (String?) -> Unit) {
    val storage = FirebaseStorage.getInstance()
    val randomUUID = UUID.randomUUID().toString()
    val storageRef = storage.reference.child(folder).child(userId).child(randomUUID)
    val bitmap = getBitmap(uri)
    val byteArray = bitmap?.let { getBitmapBytes(it,100) }
    val uploadTask = storageRef.putBytes(byteArray!!)
    uploadTask.addOnSuccessListener {
        it.storage.downloadUrl.addOnSuccessListener { result->
            callback(result.toString())
        }
    }.addOnFailureListener{
    }
}

fun uploadImageBitmapToStorage(bitmap:Bitmap,folder:String,userId: String,callback: (String?) -> Unit){
    val storage = FirebaseStorage.getInstance()
    val randomUUID = UUID.randomUUID().toString()
    val storageRef = storage.reference.child(folder).child(userId).child(randomUUID)
    val byteArray = getBitmapBytes(bitmap,100)
    val uploadTask = storageRef.putBytes(byteArray)
    uploadTask.addOnSuccessListener {
      it.storage.downloadUrl.addOnSuccessListener { result->
          callback(result.toString())
      }
    }.addOnFailureListener{

    }
}

fun getBitmapBytes(bitmap: Bitmap,quality:Int):ByteArray{
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG,quality,stream)
    return stream.toByteArray()
}

fun getTimeStamp():String{
    val simpleFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",Locale.forLanguageTag("en-IN"))
    simpleFormat.timeZone= TimeZone.getTimeZone("Asia/Kolkata")
    return simpleFormat.format(Date())
}

fun getTags(caption:String):String?{
    if(caption.indexOf("#")>=0){
        val builder = StringBuilder()
        val bArray = caption.toCharArray()
        var tags = false
        for(c in bArray){
            if(c == '#'){
                tags=true
                builder.append(c)
            }
            else{
                if(tags){
                    builder.append(c)
                }
            }

            if(c==' '){
                tags=false
            }
        }
        val result = builder.toString()
        return result.substring(0,result.length)
    }
    return null
}