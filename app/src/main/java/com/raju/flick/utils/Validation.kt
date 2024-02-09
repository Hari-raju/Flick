package com.raju.flick.utils

import android.text.TextUtils
import android.util.Patterns

public fun validateUsername(username:String): String? {
    if (TextUtils.isEmpty(username) || username.length < 4) {
        return "Enter valid username"
    }
    return null
}

public fun validateEmail(email:String): String? {
    if (!Patterns.EMAIL_ADDRESS.matcher(email)
            .matches()
    ) {
        return "Invalid Email"
    }
    return null
}

public fun validatePass(pass:String): String? {
    val pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$".toRegex()
    if (pass.isEmpty()||!pass.matches(pattern)) {
        return "Weak Password"
    }
    return null
}

public fun validateConfirmPass(pass: String,confirm:String): String? {
    val pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{6,}$".toRegex()
    if (!(confirm.matches(pattern) && confirm == pass)) {
        return "Password Mismatches"
    }
    return null
}