package com.example.ecommerceapp.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.R
import com.example.ecommerceapp.util.Constants.INTRODUCTION_KEY
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.core.content.edit


@HiltViewModel
class IntroductionViewModel @Inject constructor(
    private val sharedReference: SharedPreferences,
    firebaseAuth: FirebaseAuth,
): ViewModel() {
    private val _navigation = MutableStateFlow(0)

    val navigation: StateFlow<Int> = _navigation

    companion object{
        const val SHOPPING_ACTIVITY = 14
        val ACCOUNT_OPTIONS_FRAGMENT = R.id.action_introFragment_to_accountOptionsFragment
    }

    init {


        val isButtonCLicked = sharedReference.getBoolean(INTRODUCTION_KEY, false)
        val user = firebaseAuth.currentUser

        if (user != null){
            // to directly navigate to main screens if the user has already sign in
            viewModelScope.launch {
                _navigation.emit(SHOPPING_ACTIVITY)
            }
        }else if (isButtonCLicked){
            // to skip the intro even if the user is not sign in before
            viewModelScope.launch {
                _navigation.emit(ACCOUNT_OPTIONS_FRAGMENT)
            }
        }else{
            Unit
        }
    }

    fun startButtonCLicked(){
        sharedReference.edit { putBoolean(INTRODUCTION_KEY, true) }
    }
}