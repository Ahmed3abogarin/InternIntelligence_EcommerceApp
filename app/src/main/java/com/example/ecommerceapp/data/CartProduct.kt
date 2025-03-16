package com.example.ecommerceapp.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartProduct(
     val product: Product,
     val quantity: Int,
     val selectedColors: Int? = null,
     val selectedSizes: String?
):Parcelable{
    constructor(): this(Product(),1,null,null)
}
