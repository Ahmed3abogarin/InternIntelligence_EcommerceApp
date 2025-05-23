package com.example.ecommerceapp.util

import android.util.Patterns

// Make file so we can use it in many fragments

fun validateEmail(email:String): RegisterValidation{
    if(email.isEmpty())
        return RegisterValidation.Failed("Email cannot be empty")
    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        return RegisterValidation.Failed("Wrong email Format")

    return RegisterValidation.Success
}


fun validatePassword(password:String):RegisterValidation{
     if (password.isEmpty())
         return RegisterValidation.Failed("Password cannot be empty")

    if (password.length < 6)
        return RegisterValidation.Failed("Password must be at least 6 characters")

    return RegisterValidation.Success
}