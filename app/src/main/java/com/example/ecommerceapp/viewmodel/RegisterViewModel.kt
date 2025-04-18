package com.example.ecommerceapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.ecommerceapp.data.User
import com.example.ecommerceapp.util.Constants.USER_COLLECTION
import com.example.ecommerceapp.util.RegisterFieldsState
import com.example.ecommerceapp.util.RegisterValidation
import com.example.ecommerceapp.util.Resource
import com.example.ecommerceapp.util.validateEmail
import com.example.ecommerceapp.util.validatePassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking

import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore
): ViewModel() {

    private val _register = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val register:Flow<Resource<User>> = _register

    private val _validation = Channel<RegisterFieldsState>()
    val  validation = _validation.receiveAsFlow()

     fun createNewUserWithEmailAndPass(user:User, password:String){


         if (checkValidation(user, password)) {


             runBlocking {
                 _register.emit(Resource.Loading())
             }
             firebaseAuth.createUserWithEmailAndPassword(user.email, password)
                 .addOnSuccessListener { it ->
                     it.user?.let {
                         saveUserInfo(it.uid,user)
                         Log.v("FUCK","The user is created")
                         _register.value = Resource.Success(user)
                     }
                 }.addOnFailureListener {
                     _register.value = Resource.Error(it.message.toString())

                 }
         }
         else{
             val registerValidState = RegisterFieldsState(
                 validateEmail(user.email),validatePassword(password))

             runBlocking {
                 _validation.send(registerValidState)
             }
         }
    }
    fun saveUserInfo(userUid:String, user: User){
        db.collection(USER_COLLECTION)
            .document(userUid)
            .set(user) // to save document
            .addOnSuccessListener {
                _register.value = Resource.Success(user)
            }.addOnFailureListener {
                _register.value = Resource.Error(it.message.toString())
            }

    }

    private fun checkValidation(user: User, password: String) : Boolean{
        val emailValidation = validateEmail(user.email)
        val passwordValidation = validatePassword(password)

        // this will be ture only if the state of emailValidation and passwordValidation are success
        val shouldRegister = emailValidation is RegisterValidation.Success
                && passwordValidation is RegisterValidation.Success

        return shouldRegister
    }
}