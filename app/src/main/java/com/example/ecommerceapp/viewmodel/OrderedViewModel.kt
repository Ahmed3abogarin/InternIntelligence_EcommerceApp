package com.example.ecommerceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.data.order.Order
import com.example.ecommerceapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderedViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel(){
    private val _order = MutableStateFlow<Resource<Order>>(Resource.Unspecified())
    val order = _order.asStateFlow()

    fun placeOrder(order: Order){
        viewModelScope.launch { _order.emit(Resource.Loading()) }

        //----------------firestore -----------
        firestore.runBatch {
            //1 : Add the order into user-orders collection
            //2 : Add the order into orders collection
            //3 : Delete the products from user-cart collection

            // 1
            firestore.collection("Shop_users")
                .document(auth.uid!!)
                .collection("orders")
                .document()
                .set(order)

            // 2
            firestore.collection("orders").document().set(order)

            // 3 delete the products from user-cart
            firestore.collection("Shop_users").document(auth.uid!!).collection("cart").get()
                .addOnSuccessListener { it ->
                    it.documents.forEach{
                        it.reference.delete()
                    }
                }
        }.addOnSuccessListener {
            viewModelScope.launch { _order.emit(Resource.Success(order)) }
        }.addOnFailureListener {
            viewModelScope.launch { _order.emit(Resource.Error(it.message.toString())) }
        }
    }
}