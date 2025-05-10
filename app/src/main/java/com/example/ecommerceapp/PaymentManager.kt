package com.example.ecommerceapp

import android.util.Log
import com.example.ecommerceapp.remote.ApiInterface
import javax.inject.Inject

class PaymentManager @Inject constructor(
    private val api: ApiInterface
) {
    suspend fun preparePayment(amount: Float): Result<Triple<String, String, String>> {
        return try {
            val customerRes = api.getCustomer()
            val customerId = customerRes.body()?.id ?: return Result.failure(Exception("Customer creation failed"))

            val ephKeyRes = api.getEphKey(customerId)
            val ephKey = ephKeyRes.body()?.secret ?: return Result.failure(Exception("Ephemeral key fetch failed"))

            val paymentIntentRes = api.getPaymentIntents(customerId, 100f)
            val clientSecret = paymentIntentRes.body()?.client_secret ?: return Result.failure(Exception("PaymentIntent failed"))

            Log.v("STRIPE","cId: $customerId, ephKey: $ephKey, clientSecret: $clientSecret")
            Result.success(Triple(customerId, ephKey, clientSecret))
        } catch (e: Exception) {
            Log.v("STRIPE","error is occur: ${e.message}")
            Result.failure(e)
        }
    }
}
