package com.raju.flick.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.raju.flick.databinding.ActivityProfileBinding
import com.raju.flick.models.PreferenceManager
import com.raju.flick.models.UserModel
import com.raju.flick.utils.EncoderDecoder
import com.raju.flick.utils.KEY_BIO
import com.raju.flick.utils.KEY_COLLECTION_USERS
import com.raju.flick.utils.KEY_EMAIL
import com.raju.flick.utils.KEY_FOLLOWERS
import com.raju.flick.utils.KEY_FOLLOWING
import com.raju.flick.utils.KEY_IMAGE
import com.raju.flick.utils.KEY_IS_USER_SIGNED_IN
import com.raju.flick.utils.KEY_NAME
import com.raju.flick.utils.KEY_POSTS
import com.raju.flick.utils.KEY_USER
import com.raju.flick.utils.KEY_USER_ID
import com.raju.flick.utils.KEY_USER_NAME
import java.io.InputStream

class ProfileActivity : AppCompatActivity() {
    private val profileBinding by lazy {
        ActivityProfileBinding.inflate(layoutInflater)
    }
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var user: UserModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(profileBinding.root)
        user = intent.extras?.getSerializable(KEY_USER) as UserModel
        preferenceManager = PreferenceManager(this)
        listeners()
    }

    private fun listeners() {
        profileBinding.backProfile.setOnClickListener {
            finish()
        }

        profileBinding.doneProfile.setOnClickListener {
            loading()
            if (profileBinding.profileName.editText?.text.toString().isNotEmpty()) {
                user.name = profileBinding.profileName.editText?.text.toString()
            }
            if (profileBinding.profileBio.editText?.text.toString().isNotEmpty()){
                user.bio = profileBinding.profileBio.editText?.text.toString()
            }
            uploadData()
        }

        profileBinding.addProfileProfile.setOnClickListener {
            launcher.launch("image/*")
        }
    }

    private fun uploadData() {
        //Upload it to firebase
        user.email?.let {
            user.password?.let { it1 ->
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    it,
                    it1
                ).addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        Firebase.firestore.collection(KEY_COLLECTION_USERS)
                            .add(user)
                            .addOnSuccessListener {
                                preferenceManager.putBoolean(
                                    KEY_IS_USER_SIGNED_IN, true
                                )

                                preferenceManager.putString(KEY_USER_ID, it.id)
                                preferenceManager.putString(
                                    KEY_USER_NAME, user.username
                                )

                                preferenceManager.putString(
                                    KEY_BIO,user.bio
                                )

                                preferenceManager.putString(
                                    KEY_FOLLOWING, user.following
                                )

                                preferenceManager.putString(
                                    KEY_FOLLOWERS, user.followers
                                )

                                preferenceManager.putString(
                                    KEY_POSTS, user.posts
                                )

                                preferenceManager.putString(
                                    KEY_NAME,user.name
                                )

                                preferenceManager.putString(
                                    KEY_EMAIL, user.email
                                )

                                preferenceManager.putString(
                                    KEY_IMAGE, user.image
                                )
                                unLoading()
                                startActivity(
                                    Intent(
                                        this@ProfileActivity,
                                        HomeActivity::class.java
                                    )
                                )
                                finish()
                            }
                    } else {
                        unLoading()
                        Toast.makeText(
                            this@ProfileActivity,
                            result.exception?.localizedMessage?.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        //if uri isn't null then following code will work
        uri?.let {
            profileBinding.profileProfile.setImageURI(uri)
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
            user.image = EncoderDecoder.encodeImage(bitmap)
        }
    }


    private fun unLoading() {
        profileBinding.progressProfile.visibility = View.GONE
        profileBinding.doneProfile.visibility = View.VISIBLE
    }

    private fun loading() {
        profileBinding.doneProfile.visibility = View.GONE
        profileBinding.progressProfile.visibility = View.VISIBLE
    }
}