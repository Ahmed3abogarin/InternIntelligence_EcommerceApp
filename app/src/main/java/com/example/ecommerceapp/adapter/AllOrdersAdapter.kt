package com.example.ecommerceapp.adapter

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerceapp.R
import com.example.ecommerceapp.data.order.Order
import com.example.ecommerceapp.data.order.OrderStatus
import com.example.ecommerceapp.data.order.getOrderStatus
import com.example.ecommerceapp.databinding.AllOrdersItemBinding

class AllOrdersAdapter : RecyclerView.Adapter<AllOrdersAdapter.AllOrdersViewHolder>() {

    inner class AllOrdersViewHolder(val binding: AllOrdersItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            binding.apply {
                tvOrderId.text = order.orderId.toString()
                tvOrderDate.text = order.date

                // change the color of dots
                val resources = itemView.resources
                val colorDrawable = when (getOrderStatus(order.orderStatus)){
                    is OrderStatus.Ordered ->{
                        ColorDrawable(resources.getColor(R.color.my_orange))
                    }
                    is OrderStatus.Confirmed ->{
                        ColorDrawable(resources.getColor(R.color.my_green))
                    }
                    is OrderStatus.Delivered ->{
                        ColorDrawable(resources.getColor(R.color.my_green))
                    }
                    is OrderStatus.Shipped ->{
                        ColorDrawable(resources.getColor(R.color.my_green))
                    }
                    is OrderStatus.Canceled ->{
                        ColorDrawable(resources.getColor(R.color.my_red))
                    }
                    is OrderStatus.Returned ->{
                        ColorDrawable(resources.getColor(R.color.my_red))
                    }
                }

                imageOrderState.setImageDrawable(colorDrawable)
            }

        }

    }

    private val differCallBack = object : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.products == newItem.products
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallBack)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllOrdersViewHolder {
        return AllOrdersViewHolder(
            AllOrdersItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: AllOrdersViewHolder, position: Int) {
        val order = differ.currentList[position]
        holder.bind(order)

        // listen to clicks event
        holder.itemView.setOnClickListener{
            onClick?.invoke(order)
        }
    }

    // handle clicks
    var onClick :((Order) -> Unit)? = null


}