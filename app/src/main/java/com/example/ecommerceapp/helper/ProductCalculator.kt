package com.example.ecommerceapp.helper

fun Float?.getProductPrice(price: Float): Float{

    if (this == null)
        return price
    val remainingPricePercentage = 1f - this
    val priceAfterOffer = remainingPricePercentage * price

    return priceAfterOffer
}