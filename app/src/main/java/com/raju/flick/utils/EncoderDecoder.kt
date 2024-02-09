package com.raju.flick.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

class EncoderDecoder {
    companion object{
        fun encodeImage(bitmap: Bitmap):String{
            val byteArray= getBitmapBytes(bitmap,100)
            return Base64.encodeToString(byteArray,Base64.DEFAULT)
        }

        fun decodeImage(image:String):Bitmap{
            val byte:ByteArray = Base64.decode(image,Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(byte,0,byte.size)
        }
    }
}