package com.raju.flick.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.raju.flick.R
import com.raju.flick.databinding.ActivitySignInBinding
import com.raju.flick.models.PreferenceManager
import com.raju.flick.utils.KEY_BIO
import com.raju.flick.utils.KEY_COLLECTION_USERS
import com.raju.flick.utils.KEY_EMAIL
import com.raju.flick.utils.KEY_FOLLOWERS
import com.raju.flick.utils.KEY_FOLLOWING
import com.raju.flick.utils.KEY_IMAGE
import com.raju.flick.utils.KEY_IS_USER_SIGNED_IN
import com.raju.flick.utils.KEY_NAME
import com.raju.flick.utils.KEY_POSTS
import com.raju.flick.utils.KEY_USER_ID
import com.raju.flick.utils.KEY_USER_NAME
import com.raju.flick.utils.validateEmail
import com.raju.flick.utils.validatePass

class SignInActivity : AppCompatActivity() {
    private lateinit var preferenceManager: PreferenceManager
    private val signinBinding by lazy {
        ActivitySignInBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(signinBinding.root)
        listeners()
        preferenceManager = PreferenceManager(this)
    }

    private fun listeners() {
        signinBinding.backSignin.setOnClickListener {
            finish()
        }

        signinBinding.login.setOnClickListener {
            signinBinding.login.visibility = View.GONE
            signinBinding.progressLogin.visibility = View.VISIBLE
            validate()
            if (signinBinding.emailSignin.helperText == null && signinBinding.passwordSignin.helperText == null) {
                Firebase.auth.signInWithEmailAndPassword(
                    signinBinding.emailSignin.editText?.text.toString(),
                    signinBinding.passwordSignin.editText?.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Firebase.firestore.collection(KEY_COLLECTION_USERS)
                            .whereEqualTo(
                                KEY_EMAIL,
                                signinBinding.emailSignin.editText?.text.toString()
                            )
                            .get()
                            .addOnCompleteListener { result ->
                                if (result.isSuccessful && result.result != null && result.result.documents.size > 0) {
                                    try {
                                        val documents: DocumentSnapshot = result.result.documents[0]
                                        preferenceManager.putBoolean(KEY_IS_USER_SIGNED_IN, true)

                                        preferenceManager.putString(
                                            KEY_NAME, documents.getString(
                                                KEY_NAME
                                            )
                                        )

                                        documents.getString(KEY_BIO)?.let { it2 ->
                                            preferenceManager.putString(KEY_BIO, it2)
                                        }

                                        documents.getString(KEY_FOLLOWERS)?.let { it2 ->
                                            preferenceManager.putString(KEY_FOLLOWERS, it2)
                                        }

                                        documents.getString(KEY_FOLLOWING)?.let { it2 ->
                                            preferenceManager.putString(KEY_FOLLOWING, it2)
                                        }

                                        documents.getString(KEY_POSTS)?.let { it2 ->
                                            preferenceManager.putString(KEY_POSTS, it2)
                                        }

                                        preferenceManager.putString(KEY_USER_ID, documents.id)

                                        documents.getString(KEY_USER_NAME)?.let { it2 ->
                                            preferenceManager.putString(KEY_USER_NAME, it2)
                                        }
                                        documents.getString(KEY_EMAIL)?.let { it2 ->
                                            preferenceManager.putString(KEY_EMAIL, it2)
                                        }
                                        preferenceManager.putString(KEY_IMAGE, documents.getString(KEY_IMAGE))
                                        signinBinding.login.visibility = View.VISIBLE
                                        signinBinding.progressLogin.visibility = View.GONE
                                        val intent =
                                            Intent(this@SignInActivity, HomeActivity::class.java)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                        startActivity(intent)
                                        finish()
                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            this@SignInActivity,
                                            e.message.toString(),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        signinBinding.login.visibility = View.VISIBLE
                                        signinBinding.progressLogin.visibility = View.GONE
                                    }
                                }
                            }
                    } else {
                        Toast.makeText(
                            this@SignInActivity,
                            it.exception?.localizedMessage?.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                        signinBinding.login.visibility = View.VISIBLE
                        signinBinding.progressLogin.visibility = View.GONE
                    }
                }
            } else {
                signinBinding.login.visibility = View.VISIBLE
                signinBinding.progressLogin.visibility = View.GONE
            }
        }

        signinBinding.createAccSignin.setOnClickListener {
            finish()
        }
    }

    private fun validate() {
        signinBinding.emailSignin.helperText = validateEmail(signinBinding.emailSignin.editText?.text.toString())
        signinBinding.passwordSignin.helperText = validatePass(signinBinding.passwordSignin.editText?.text.toString())
    }
}