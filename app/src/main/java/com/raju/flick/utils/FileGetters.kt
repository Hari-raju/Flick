package com.raju.flick.utils

import java.io.File

fun getDirectoryPath(directory:String):ArrayList<String>{
    val pathArray = ArrayList<String>()
    val file = File(directory)
    val listFiles = file.listFiles()
    if (listFiles != null) {
        for ( i in listFiles.indices){
            if(listFiles[i].isDirectory){
                pathArray.add(listFiles[i].absolutePath)
            }
        }
    }
    return pathArray
}

fun getFilePath(directory:String):ArrayList<String>{
    val pathArray = ArrayList<String>()
    val file = File(directory)
    val listFiles = file.listFiles()
    if (listFiles != null) {
        for ( i in listFiles.indices){
            if(listFiles[i].isFile){
                pathArray.add(listFiles[i].absolutePath)
            }
        }
    }
    return pathArray
}