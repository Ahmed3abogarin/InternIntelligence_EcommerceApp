package com.example.ecommerceapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerceapp.data.Cats
import com.example.ecommerceapp.databinding.CatsItemBinding

class CatsAdapter: RecyclerView.Adapter<CatsAdapter.CatsViewHolder>() {
    inner class CatsViewHolder(val binding:CatsItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(cat: Cats){
            Glide.with(itemView).load(cat.imageUrl).into(binding.cateImg)
            binding.cateName.text = cat.catName
        }
    }

    private val differCallBack = object : DiffUtil.ItemCallback<Cats>(){
        override fun areItemsTheSame(oldItem: Cats, newItem: Cats): Boolean {
            return oldItem.imageUrl == newItem.imageUrl
        }

        override fun areContentsTheSame(oldItem: Cats, newItem: Cats): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this,differCallBack)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatsViewHolder {
        return CatsViewHolder(
            CatsItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: CatsViewHolder, position: Int) {
        val cat = differ.currentList[position]
        holder.bind(cat)
    }


}