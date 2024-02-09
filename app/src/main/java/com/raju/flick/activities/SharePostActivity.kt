package com.raju.flick.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.raju.flick.R
import com.raju.flick.adapters.GridImageAdapter
import com.raju.flick.databinding.ActivitySharePostBinding
import com.raju.flick.utils.Dcim
import com.raju.flick.utils.Pictures
import com.raju.flick.utils.getDirectoryPath
import com.raju.flick.utils.getFilePath
import com.raju.flick.utils.loadImage

class SharePostActivity : AppCompatActivity() {
   // private val tag="ShareActivity"
    private val readRequestCode = 100
    private val cameraRequestCode = 500
    private lateinit var imageUrl: String
    private lateinit var cameraLauncher:ActivityResultLauncher<Intent>
    private val sharePost by lazy {
        ActivitySharePostBinding.inflate(layoutInflater)
    }

    private lateinit var directories: ArrayList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(sharePost.root)
        cameraLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val data: Intent? = it.data
                val bitmap = data?.getParcelableExtra<Bitmap>("data")
                val intent=Intent(this@SharePostActivity,NextActivity::class.java)
                intent.putExtra("camera_bitmap",bitmap)
                intent.putExtra("from","camera")
                startActivity(intent)
            }
        }
        listeners()
        directories = ArrayList()
        checkPermissions()
    }

    private fun listeners() {
        sharePost.shareClose.setOnClickListener {
            finish()
        }

        sharePost.shareNext.setOnClickListener {
            val intent = Intent(this@SharePostActivity, NextActivity::class.java)
            intent.putExtra("from","gallery")
            intent.putExtra("image", imageUrl)
            try{
                startActivity(intent)
            }
            catch (e:Exception){
                Toast.makeText(this@SharePostActivity,e.localizedMessage,Toast.LENGTH_SHORT).show()
            }
        }

        sharePost.shareCamera.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this@SharePostActivity,Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED){
                //open camera activity
                openCamera()
            }
            else{
                ActivityCompat.requestPermissions(this@SharePostActivity,
                    arrayOf(Manifest.permission.CAMERA),cameraRequestCode)

            }
        }
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                setUpAdapter()
            } else {

                val builder = AlertDialog.Builder(this)
                builder.setMessage("Do you want to allow permissions ?")

                // Set Alert Title
                builder.setTitle("Permissions Required !")

                // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                builder.setCancelable(false)

                // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                builder.setPositiveButton("Settings") {
                    // When the user click yes button then app will close
                        _, _ ->
                    try {
                        val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                        intent.addCategory("android.intent.category.DEFAULT")
                        intent.setData(
                            Uri.parse(
                                String.format(
                                    "package:%s", *arrayOf<Any>(
                                        applicationContext.packageName
                                    )
                                )
                            )
                        )
                        reqLauncher.launch(intent)
                    } catch (e: Exception) {
                        val intent = Intent()
                        intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                        reqLauncher.launch(intent)
                    }
                }

                // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
                builder.setNegativeButton("No") {
                    // If user click no then dialog box is canceled.
                        dialog, _ ->
                    dialog.cancel()
                    finish()
                }

                // Create the Alert dialog
                val alertDialog = builder.create()
                // Show the Alert Dialog box
                alertDialog.show()
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                setUpAdapter()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    readRequestCode
                )
            }
        }
    }

    private var reqLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    setUpAdapter()
                } else {
                    finish()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == readRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                setUpAdapter()
            } else {
                finish()
            }
        }

        if (requestCode==cameraRequestCode){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //open camera activity
                openCamera()
            }
            else{
                Toast.makeText(
                    this@SharePostActivity,
                    "Allow Camera permission",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun openCamera(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(intent)
    }


    private fun setUpAdapter() {
        directories.addAll(getDirectoryPath(Pictures))
        directories.addAll(getDirectoryPath(Dcim))
        val folders = directories.map {
            it.substringAfterLast("/")
        }
        val adapter = ArrayAdapter(
            this@SharePostActivity,
            android.R.layout.simple_spinner_dropdown_item,
            folders
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sharePost.shareSpinner.adapter = adapter
        sharePost.shareSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                try{
                    setGridView(directories[position])
                }
                catch (e:Exception){
                    Log.d("Errors",e.message.toString())
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }


    private fun setGridView(directory: String) {
        val files = getFilePath(directory)
        if (files.isEmpty()) {
            Toast.makeText(this@SharePostActivity, "Empty Folder", Toast.LENGTH_LONG).show()
        }
        val adapter = GridImageAdapter(this, R.layout.layout_grid_view, files)
        sharePost.shareGrid.columnWidth = resources.displayMetrics.widthPixels / 4
        sharePost.shareGrid.adapter = adapter
        setSelectedPost(files[0])
        imageUrl=files[0]
        sharePost.shareGrid.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                try{
                    setSelectedPost(files[position])
                }
                catch (e:Exception){
                    Log.d("Errors",e.message.toString())
                }
            }
    }

    private fun setSelectedPost(url: String) {
        loadImage(this@SharePostActivity, url, sharePost.shareSelectedPost)
        imageUrl = url
    }
}