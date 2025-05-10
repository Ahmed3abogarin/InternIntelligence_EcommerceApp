package com.example.ecommerceapp.remote

import com.example.ecommerceapp.data.CustomerModel
import com.example.ecommerceapp.data.EpKeyModel
import com.example.ecommerceapp.data.PaymentIntentModel
import com.example.ecommerceapp.util.Constants.SECRET_KEY
import retrofit2.Response
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiInterface {


    @Headers("Authorization: Bearer $SECRET_KEY")
    @POST("v1/customers")
    suspend fun getCustomer(): Response<CustomerModel>


    @Headers("Authorization: Bearer $SECRET_KEY",
        "Stripe-Version: 2025-03-31.basil")
    @POST("v1/ephemeral_keys")
    suspend fun getEphKey(
        @Query("customer") customer: String
    ): Response<EpKeyModel>


    @Headers("Authorization: Bearer $SECRET_KEY")
    @POST("v1/payment_intents")
    suspend fun getPaymentIntents(
        @Query("customer") customer: String,
        @Query("amount") amount: Int,
        @Query("currency") currency: String = "eur",
        @Query("automatic_payment_methods[enabled]") automatePay: Boolean = true,
    ): Response<PaymentIntentModel>

}