package com.example.ecommerceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.data.CartProduct
import com.example.ecommerceapp.data.Product
import com.example.ecommerceapp.firebase.FirebaseCommon
import com.example.ecommerceapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon

): ViewModel() {

    // state
    private val _addToCart = MutableStateFlow<Resource<CartProduct>>(Resource.Unspecified())
    val addToCart = _addToCart.asStateFlow()

    private val _addToFavorite = MutableStateFlow<Resource<Product>>(Resource.Unspecified())
    val addToFavorite = _addToFavorite.asStateFlow()

    // We have two scenarios: 1) the item doesn't exists in cart 2) item exists so we have just to increase the quantity
    fun addOrUpdateProductToCart(cartProduct: CartProduct){
        viewModelScope.launch { _addToCart.emit(Resource.Loading()) }
        firestore.collection("Shop_users").document(auth.uid!!).collection("cart")
            .whereEqualTo("product.id",cartProduct.product.id).get()
            .addOnSuccessListener {
                it.documents.let {
                    if (it.isEmpty()){ // add new product (so it doesn't exists in cart)
                        addNewProduct(cartProduct)

                    }else{
                        val product = it.first().toObject(CartProduct::class.java)
                         // after that we gonna compare this product with the product the user trying to add
                        // if they are the same then we are gonna increase the quantity if they are not we gonna add a new product

                        if (product != null) {
                            if (product.product == cartProduct.product && product.selectedColors == cartProduct.selectedColors && product.selectedSizes == cartProduct.selectedSizes){ // increase the quantity
                                val documentId = it.first().id
                                increaseQuantity(documentId,cartProduct)

                            }else{ // Add new product
                                addNewProduct(cartProduct)

                            }
                        }
                    }
                }

            }.addOnFailureListener{
                viewModelScope.launch { _addToCart.emit(Resource.Error(it.message.toString())) }

            }
    }

    fun addProductToFavorite(product: Product){
        firebaseCommon.addProductToFavorite(product){ addedProduct, e ->
            viewModelScope.launch {
                if (e == null){
                    _addToFavorite.emit(Resource.Success(addedProduct!!))
                }else{
                    _addToFavorite.emit(Resource.Error(e.message.toString()))
                }
            }

        }
    }

    private fun addNewProduct(cartProduct: CartProduct){
        firebaseCommon.addProductToCart(cartProduct){ addedProduct, e ->
            viewModelScope.launch {
                if (e == null){
                    _addToCart.emit(Resource.Success(addedProduct!!))
                }else{
                    _addToCart.emit(Resource.Error(e.message.toString()))
                }
            }

        }
    }

    private fun increaseQuantity(documentId: String, cartProduct: CartProduct){
        firebaseCommon.increaseQuantity(documentId){_,e ->
            viewModelScope.launch {
                if (e == null)
                    _addToCart.emit(Resource.Success(cartProduct))
                else
                    _addToCart.emit(Resource.Error(e.message.toString()))
            }

        }

    }
}