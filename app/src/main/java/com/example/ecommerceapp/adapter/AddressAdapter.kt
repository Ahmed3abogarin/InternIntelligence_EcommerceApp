package com.example.ecommerceapp.adapter

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerceapp.R
import com.example.ecommerceapp.data.Address
import com.example.ecommerceapp.databinding.AddressRvItemBinding
import com.example.ecommerceapp.databinding.FragmentBillingBinding
import com.example.ecommerceapp.viewmodel.AddressViewModel

class AddressAdapter: RecyclerView.Adapter<AddressAdapter.AddressViewHolder>(){


    inner class AddressViewHolder(val binding: AddressRvItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(address: Address, isSelected:Boolean){
            binding.apply {
                buttonAddress.text = address.addressTitle
                if (isSelected){
                    buttonAddress.background = ColorDrawable(itemView.context.resources.getColor(R.color.my_blue))
                }else{
                    buttonAddress.background =ColorDrawable(itemView.context.resources.getColor(R.color.my_gray))
                }

            }

        }
    }

    private val differCallBack = object : DiffUtil.ItemCallback<Address>(){
        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, differCallBack)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        return AddressViewHolder(
            AddressRvItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var selectedAddress = -1

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = differ.currentList[position]
        holder.bind(address, selectedAddress==position)

        // check if the address is selected
        holder.binding.buttonAddress.setOnClickListener{
            if (selectedAddress >= 0){
                notifyItemChanged(selectedAddress)
            }
            selectedAddress = holder.adapterPosition
            notifyItemChanged(selectedAddress)
            onClick?.invoke(address)
        }

    }

    var onClick:((Address) -> Unit)? = null
}