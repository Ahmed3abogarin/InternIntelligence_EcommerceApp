package com.example.ecommerceapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.example.ecommerceapp.databinding.ViewpagerItemBinding

class ViewPagerAdapter : RecyclerView.Adapter<ViewPagerAdapter.PagerViewHolder>() {


    inner class PagerViewHolder(val binding: ViewpagerItemBinding) : ViewHolder(binding.root) {
        fun bind(image: String) {
            var requestOptions = RequestOptions()
            requestOptions = requestOptions.transform(CenterCrop())
            Glide.with(itemView).load(image).apply(requestOptions).into(binding.imageProductDetails)
        }

    }

    private val differCallBack = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this,differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        return PagerViewHolder(
            ViewpagerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        val image = differ.currentList[position]
        holder.bind(image)
    }
}