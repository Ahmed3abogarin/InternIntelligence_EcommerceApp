package com.example.ecommerceapp.util

import android.view.View
import androidx.fragment.app.Fragment
import com.example.ecommerceapp.R
import com.example.ecommerceapp.activity.ShoppingActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

fun Fragment.hideBottomNavigationBar(){
    val bottomNavigationBar =
        (activity as ShoppingActivity).findViewById<BottomNavigationView>(R.id.bottomNavigation)

    bottomNavigationBar.visibility = View.GONE
}

fun Fragment.showBottomNavigationBar(){
    val bottomNavigationBar =
        (activity as ShoppingActivity).findViewById<BottomNavigationView>(R.id.bottomNavigation)

    bottomNavigationBar.visibility = View.VISIBLE
}