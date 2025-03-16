package com.example.ecommerceapp.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.request.RequestOptions
import com.example.ecommerceapp.data.Product
import com.example.ecommerceapp.databinding.ProductItemBinding
import com.example.ecommerceapp.helper.getProductPrice

class ProductAdapter : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {


    inner class ProductViewHolder(val binding:ProductItemBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind( product: Product){
            var requestOptions = RequestOptions()
            requestOptions = requestOptions.transform(FitCenter())
            binding.apply {
                Glide.with(itemView)
                    .load(product.images[0])
                    .apply(requestOptions)
                    .into(productImage)

                productTitle.text = product.name

                val priceAfterOffer = product.offerPercentage.getProductPrice(product.price)

                productOldPrice.paintFlags = productOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                if (product.offerPercentage == null)
                    productOldPrice.visibility = View.INVISIBLE

                productNewPrice.text = "$ ${String.format("%.2f", priceAfterOffer)}"
                productOldPrice.text ="$ ${product.price}"

            }

        }

    }

    private val diffCallback = object : DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            ProductItemBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)

        holder.itemView.setOnClickListener {
            onClick?.invoke(product)
        }
    }

    var onClick: ((Product) -> Unit)? = null
}