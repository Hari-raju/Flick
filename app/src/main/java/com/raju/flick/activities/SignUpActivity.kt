package com.raju.flick.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.raju.flick.databinding.ActivitySignUpBinding
import com.raju.flick.models.UserModel
import com.raju.flick.utils.KEY_COLLECTION_USERS
import com.raju.flick.utils.KEY_EMAIL
import com.raju.flick.utils.KEY_USER
import com.raju.flick.utils.KEY_USER_NAME
import com.raju.flick.utils.validateConfirmPass
import com.raju.flick.utils.validateEmail
import com.raju.flick.utils.validatePass
import com.raju.flick.utils.validateUsername


class SignUpActivity : AppCompatActivity() {
    private val signupBinding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(signupBinding.root)
        listeners()

    }

    private fun listeners() {
        signupBinding.loginSignup.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        signupBinding.signup.setOnClickListener {
            signupBinding.signup.visibility = View.GONE
            signupBinding.progressSignup.visibility = View.VISIBLE
            validate()
            //If all fields are valid
            if (signupBinding.usernameSignUp.helperText == null && signupBinding.emailSignup.helperText == null && signupBinding.passwordSignUp.helperText == null && signupBinding.confirmPasswordSignUp.helperText == null) {
                try{
                    val database = FirebaseFirestore.getInstance()
                    //Checking already account exists or not
                    database.collection(KEY_COLLECTION_USERS)
                        .whereEqualTo(
                            KEY_USER_NAME,
                            signupBinding.usernameSignUp.editText?.text.toString()
                        )
                        .whereEqualTo(KEY_EMAIL, signupBinding.emailSignup.editText?.text.toString())
                        .get()
                        .addOnCompleteListener { task ->
                            if (task.result != null && task.isSuccessful && task.result.documents.size > 0) {
                                Toast.makeText(
                                    this@SignUpActivity,
                                    "Account already exists",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                //If not we're checking username exits are not
                                database.collection(KEY_COLLECTION_USERS)
                                    .whereEqualTo(
                                        KEY_USER_NAME,
                                        signupBinding.usernameSignUp.editText?.text.toString()
                                    )
                                    .get()
                                    .addOnCompleteListener { task2 ->
                                        if (task2.result != null && task2.isSuccessful && task2.result.documents.size > 0) {
                                            Toast.makeText(
                                                this@SignUpActivity,
                                                "Username already taken",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            //If not then we're checking with email existence
                                            database.collection(KEY_COLLECTION_USERS)
                                                .whereEqualTo(
                                                    KEY_USER_NAME,
                                                    signupBinding.emailSignup.editText?.text.toString()
                                                )
                                                .get()
                                                .addOnCompleteListener { task3 ->
                                                    if (task3.result != null && task3.isSuccessful && task3.result.documents.size > 0) {
                                                        Toast.makeText(
                                                            this@SignUpActivity,
                                                            "Email already taken",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    } else {
                                                        //Else we're proceeding for account creation
                                                        val intent = Intent(
                                                            this@SignUpActivity,
                                                            ProfileActivity::class.java
                                                        )
                                                        val user = UserModel(
                                                            image = null,
                                                            username = signupBinding.usernameSignUp.editText?.text.toString(),
                                                            email = signupBinding.emailSignup.editText?.text.toString(),
                                                            password = signupBinding.passwordSignUp.editText?.text.toString()
                                                        )
                                                        intent.putExtra(KEY_USER, user)
                                                        startActivity(intent)
                                                        finish()
                                                    }
                                                }
                                        }
                                        signupBinding.signup.visibility = View.VISIBLE
                                        signupBinding.progressSignup.visibility = View.GONE
                                    }
                            }
                        }
                }
                catch (e:Exception){
                    Log.d("SignUpError",e.message.toString())
                    Toast.makeText(this@SignUpActivity,e.message.toString(),Toast.LENGTH_LONG).show()
                }
            } else {
                signupBinding.signup.visibility = View.VISIBLE
                signupBinding.progressSignup.visibility = View.GONE
            }
        }
    }


    private fun validate() {
        signupBinding.usernameSignUp.helperText =
            validateUsername(signupBinding.usernameSignUp.editText?.text.toString())
        signupBinding.emailSignup.helperText =
            validateEmail(signupBinding.emailSignup.editText?.text.toString())
        signupBinding.passwordSignUp.helperText =
            validatePass(signupBinding.passwordSignUp.editText?.text.toString())
        signupBinding.confirmPasswordSignUp.helperText = validateConfirmPass(
            signupBinding.passwordSignUp.editText?.text.toString(),
            signupBinding.confirmPasswordSignUp.editText?.text.toString()
        )
    }
}