package com.example.ecommerceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.data.CartProduct
import com.example.ecommerceapp.firebase.FirebaseCommon
import com.example.ecommerceapp.helper.getProductPrice
import com.example.ecommerceapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon


) :ViewModel(){



    private val _cart_product = MutableStateFlow<Resource<List<CartProduct>>>(Resource.Unspecified())
    val cart_product = _cart_product.asStateFlow()

    val productPrice = cart_product.map {
        when(it){
            is Resource.Success -> {
                calculatePrice(it.data!!)
            }
            else -> null
        }
    }

    private val _deleteDialog = MutableSharedFlow<CartProduct>()
    val deleteDialog = _deleteDialog.asSharedFlow()

    private var cartProductDocuments = emptyList<DocumentSnapshot>()


    fun deleteCartProduct(cartProduct: CartProduct){
        val index = cart_product.value.data?.indexOf(cartProduct)
        if (index != null && index != -1){
            val documentId = cartProductDocuments[index].id
            firestore.collection("Shop_users").document(firebaseAuth.uid!!).collection("cart")
                .document(documentId).delete()
        }
    }

    private fun calculatePrice(data: List<CartProduct>): Float {
        return data.sumByDouble { cartProduct ->
            (cartProduct.product.offerPercentage.getProductPrice(cartProduct.product.price) * cartProduct.quantity).toDouble()
        }.toFloat()

    }

    init {
        getCartProducts()
    }


    private fun getCartProducts(){
        val userId = firebaseAuth.uid
        if (userId == null) {
            viewModelScope.launch { _cart_product.emit(Resource.Error("User not authenticated")) }
            return
        }

        viewModelScope.launch { _cart_product.emit(Resource.Loading()) }
        firestore.collection("Shop_users").document(userId).collection("cart")
            .addSnapshotListener{ value, error ->
                if (error != null || value == null){
                    viewModelScope.launch { _cart_product.emit(Resource.Error(error?.message.toString())) }
                }else{
                    cartProductDocuments = value.documents
                    val cartProducts =  value.toObjects(CartProduct::class.java)
                    viewModelScope.launch { _cart_product.emit(Resource.Success(cartProducts)) }
                }

        }
    }

     fun changeQuantity(
        cartProduct: CartProduct,
        quantityChanging: FirebaseCommon.QuantityChanging
    ){
        val index = cart_product.value.data?.indexOf(cartProduct)
         /**
          * index could be equal to -1 if the function [getCartProducts] deals which will also delay the result we expect to be inside the [_cart_product]
          * and to prevent the app from crashing we make a check
          */

         if (index != null && index != -1){
             val documentId = cartProductDocuments[index].id
             when(quantityChanging){
                 FirebaseCommon.QuantityChanging.INCREASE ->{
                     viewModelScope.launch { _cart_product.emit(Resource.Loading()) }
                     increaseQuantity(documentId)
                 }
                 FirebaseCommon.QuantityChanging.DECREASE ->{
                     if (cartProduct.quantity==1){
                         viewModelScope.launch { _deleteDialog.emit(cartProduct) }
                         return
                     }
                     viewModelScope.launch { _cart_product.emit(Resource.Loading()) }
                     decreaseQuantity(documentId)

                 }
             }
         }


    }

    private fun decreaseQuantity(documentId: String) {
        firebaseCommon.decreaseQuantity(documentId){ result, exception ->
            if (exception != null){
                viewModelScope.launch { _cart_product.emit(Resource.Error(exception.message.toString())) }

            }
        }
    }

    private fun increaseQuantity(documentId: String) {
        firebaseCommon.increaseQuantity(documentId){ result, exception ->
            if (exception != null){
                viewModelScope.launch { _cart_product.emit(Resource.Error(exception.message.toString())) }

            }

        }
    }


}