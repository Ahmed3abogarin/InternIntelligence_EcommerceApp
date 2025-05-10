package com.example.ecommerceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.PaymentManager
import com.example.ecommerceapp.data.Address
import com.example.ecommerceapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt


@HiltViewModel
class BillingViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val paymentManager: PaymentManager
) : ViewModel() {

    private val _address = MutableStateFlow<Resource<List<Address>>>(Resource.Unspecified())
    val address = _address.asStateFlow()

    private val _paymentDetails = MutableStateFlow<Resource<Triple<String, String, String>>>(Resource.Unspecified())
    val paymentDetails = _paymentDetails.asStateFlow()

    init {
        getUserAddress()
    }

    private fun getUserAddress() {
        viewModelScope.launch { _address.emit(Resource.Loading()) }
        firestore.collection("Shop_users").document(auth.uid!!).collection("address")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    viewModelScope.launch { _address.emit(Resource.Error(error.message.toString())) }
                    return@addSnapshotListener
                }
                val address = value?.toObjects(Address::class.java)
                viewModelScope.launch { _address.emit(Resource.Success(address!!)) }

            }
    }

    fun preparePaymentFlow(price: Float) {
        val totalPrice = (price * 100).roundToInt()
        viewModelScope.launch {
            _paymentDetails.emit(Resource.Loading())
            val result = paymentManager.preparePayment(totalPrice)
            _paymentDetails.emit(
                result.fold(
                    onSuccess = { Resource.Success(it) },
                    onFailure = { Resource.Error(it.message ?: "Payment error") }
                )
            )
        }
    }



}