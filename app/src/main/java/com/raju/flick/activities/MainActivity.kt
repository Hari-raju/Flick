package com.raju.flick.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.raju.flick.R
import com.raju.flick.models.PreferenceManager
import com.raju.flick.utils.KEY_IS_USER_SIGNED_IN

class MainActivity : AppCompatActivity() {
    private lateinit var preferenceManager: PreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        preferenceManager= PreferenceManager(this)
        Handler(Looper.getMainLooper()).postDelayed(
            {
                if (preferenceManager.getBoolean(KEY_IS_USER_SIGNED_IN))
                   startActivity(Intent(this, HomeActivity::class.java))
                else
                    startActivity(Intent(this, SignUpActivity::class.java))
                finish()
            },
            1000
        )
    }
}