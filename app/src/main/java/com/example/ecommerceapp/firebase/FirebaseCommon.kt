package com.example.ecommerceapp.firebase

import com.example.ecommerceapp.data.CartProduct
import com.example.ecommerceapp.data.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class FirebaseCommon(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
) {
    //private val cartCollection = firestore.collection("Shop_users").document(auth.uid!!).collection("cart")

    private fun getCartCollection() = auth.uid?.let { uid ->
        firestore.collection("Shop_users").document(uid).collection("cart")
    } ?: throw NullPointerException("User ID is null. User might not be authenticated.")

    private fun getUserFavoriteCollection() = auth.uid?.let { uid ->
        firestore.collection("Shop_users").document(uid).collection("favorites")
    } ?: throw NullPointerException("User ID is null. User might not be authenticated.")

    fun addProductToFavorite(product: Product, onResult: (Product?, Exception?) -> Unit) {
        val collection = getUserFavoriteCollection()
        collection.document().set(product)
            .addOnSuccessListener {
                onResult(product, null)
            }.addOnFailureListener {
                onResult(null, it)
            }
    }


    fun addProductToCart(cartProduct: CartProduct, onResult: (CartProduct?, Exception?) -> Unit) {
        val cartCollection = getCartCollection()
        cartCollection.document().set(cartProduct)
            .addOnSuccessListener {
                onResult(cartProduct, null)
            }.addOnFailureListener {
                onResult(null, it)

            }
    }

    // the product exits so we have just to increase the quantity
    fun increaseQuantity(documentId: String, onResult: (String?, Exception?) -> Unit) {
        val cartCollection = getCartCollection()
        firestore.runTransaction { transition ->
            val documentRef = cartCollection.document(documentId)
            val document = transition.get(documentRef)
            val productObject = document.toObject(CartProduct::class.java)
            productObject?.let { cartProduct ->
                val newQuantity = cartProduct.quantity + 1
                val newProductObject = cartProduct.copy(quantity = newQuantity)
                transition.set(documentRef, newProductObject)
            }
        }.addOnSuccessListener {
            onResult(documentId, null)

        }.addOnFailureListener {
            onResult(null, it)
        }
    }

    fun decreaseQuantity(documentId: String, onResult: (String?, Exception?) -> Unit) {
        val cartCollection = getCartCollection()
        firestore.runTransaction { transition ->
            val documentRef = cartCollection.document(documentId)
            val document = transition.get(documentRef)
            val productObject = document.toObject(CartProduct::class.java)
            productObject?.let { cartProduct ->
                val newQuantity = cartProduct.quantity - 1
                val newProductObject = cartProduct.copy(quantity = newQuantity)
                transition.set(documentRef, newProductObject)
            }
        }.addOnSuccessListener {
            onResult(documentId, null)

        }.addOnFailureListener {
            onResult(null, it)
        }
    }

    enum class QuantityChanging {
        INCREASE, DECREASE
    }
}