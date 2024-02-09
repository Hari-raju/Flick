package com.raju.flick.activities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.raju.flick.databinding.ActivityEditBinding
import com.raju.flick.models.PreferenceManager
import com.raju.flick.models.UserModel
import com.raju.flick.utils.EncoderDecoder.Companion.decodeImage
import com.raju.flick.utils.EncoderDecoder.Companion.encodeImage
import com.raju.flick.utils.KEY_BIO
import com.raju.flick.utils.KEY_COLLECTION_USERS
import com.raju.flick.utils.KEY_EMAIL
import com.raju.flick.utils.KEY_IMAGE
import com.raju.flick.utils.KEY_NAME
import com.raju.flick.utils.KEY_USER_ID
import com.raju.flick.utils.KEY_USER_NAME
import com.raju.flick.utils.validateEmail
import com.raju.flick.utils.validateUsername
import java.io.InputStream

class EditActivity : AppCompatActivity() {
    private val editBinding by lazy {
        ActivityEditBinding.inflate(layoutInflater)
    }

    private val user by lazy {
        UserModel(
            image = preferenceManager.getString(KEY_IMAGE)
        )
    }

    private val preferenceManager by lazy {
        PreferenceManager(this@EditActivity)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(editBinding.root)
        listeners()
    }

    override fun onStart() {
        super.onStart()
        init()
    }

    private fun init(){
        user.name=preferenceManager.getString(KEY_USER_NAME)
        editBinding.editUsername.editText?.setText(preferenceManager.getString(KEY_USER_NAME))
        preferenceManager.getString(KEY_NAME)?.let {
            editBinding.editName.editText?.setText(it)
            user.name=it
        }
        preferenceManager.getString(KEY_IMAGE)?.let {
            editBinding.editProfile.setImageBitmap(
                decodeImage(it)
            )
            user.image=it
        }
        preferenceManager.getString(KEY_BIO)?.let {
            editBinding.editBio.editText?.setText(it)
            user.bio=it
        }
        user.email=preferenceManager.getString(KEY_EMAIL)
        editBinding.editEmail.editText?.setText(preferenceManager.getString(KEY_EMAIL))
    }

    private fun listeners(){
        editBinding.editBack.setOnClickListener {
            finish()
        }

        editBinding.editProfile.setOnClickListener{
            loading()
            launcher.launch("image/*")
        }

        editBinding.doneEdit.setOnClickListener {
            loading()
            validate()
            if (editBinding.editUsername.helperText == null && editBinding.editEmail.helperText == null && editBinding.editEmail.helperText==null){
                user.name = editBinding.editName.editText?.text.toString()
                user.username=editBinding.editUsername.editText?.text.toString()
                user.email=editBinding.editEmail.editText?.text.toString()
                val userModel=UserModel(
                    image = preferenceManager.getString(KEY_IMAGE),
                    username = preferenceManager.getString(KEY_USER_NAME),
                    email = preferenceManager.getString(KEY_EMAIL),
                    name = preferenceManager.getString(KEY_NAME),
                    bio = preferenceManager.getString(KEY_BIO)
                )

                if(userModel.name==user.name && userModel.email==user.email && userModel.image == user.image && userModel.username==user.username && userModel.bio==user.bio){
                    Toast.makeText(
                        this@EditActivity,
                        "No changes were made...",
                        Toast.LENGTH_SHORT
                    ).show()
                    unLoading()
                }

                else{
                    Toast.makeText(
                        this@EditActivity,
                        "Changes Detected...",
                        Toast.LENGTH_SHORT
                    ).show()
                    preferenceManager.getString(KEY_USER_ID)?.let { id ->
                        Firebase.firestore.collection(KEY_COLLECTION_USERS)
                            .document(id)
                            .set(user)
                            .addOnSuccessListener {
                                preferenceManager.putString(KEY_IMAGE,user.image)
                                preferenceManager.putString(KEY_USER_NAME,user.username)
                                preferenceManager.putString(KEY_EMAIL,user.email)
                                preferenceManager.putString(KEY_NAME,user.name)
                                Toast.makeText(
                                    this@EditActivity,
                                    "Updated...",
                                    Toast.LENGTH_SHORT
                                ).show()
                                unLoading()
                                finish()
                            }
                    }
                }
            }
            else{
                unLoading()
            }
        }
    }


    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()){
        uri->
        uri?.let{
            editBinding.editProfile.setImageURI(uri)
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val bitmap:Bitmap = BitmapFactory.decodeStream(inputStream)
            user.image = encodeImage(bitmap)
        }
        unLoading()
    }

    private fun validate(){
        editBinding.editUsername.helperText= validateUsername(editBinding.editUsername.editText?.text.toString())
        editBinding.editEmail.helperText=validateEmail(editBinding.editEmail.editText?.text.toString())
    }


    private fun unLoading() {
        editBinding.doneEdit.visibility = View.VISIBLE
        editBinding.progressEdit.visibility = View.GONE
    }

    private fun loading() {
        editBinding.doneEdit.visibility = View.GONE
        editBinding.progressEdit.visibility = View.VISIBLE
    }
}