package com.example.ecommerceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.data.Product
import com.example.ecommerceapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
): ViewModel() {


    private val _favoriteProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val favoriteProducts = _favoriteProducts.asStateFlow()


    init {
        getFavoriteProducts()
    }


    fun getFavoriteProducts(){
        val auth = firebaseAuth.uid

        if (auth == null){
            viewModelScope.launch { _favoriteProducts.emit(Resource.Error("User not authenticated")) }
            return
        }

        viewModelScope.launch { _favoriteProducts.emit(Resource.Loading()) }

        firestore.collection("Shop_users").document(auth).collection("favorites")
            .addSnapshotListener { value, error ->
                if (error != null || value == null){
                    viewModelScope.launch {
                        _favoriteProducts.emit(Resource.Error(error?.message.toString()))
                    }
                }else{
                    val favoriteProducts = value.toObjects(Product::class.java)
                    viewModelScope.launch { _favoriteProducts.emit(Resource.Success(favoriteProducts)) }
                }

            }

    }
}