package com.example.ecommerceapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.request.RequestOptions
import com.example.ecommerceapp.data.SliderItem
import com.example.ecommerceapp.databinding.ViewpagerItemBinding

class BannersViewPagerAdapter : RecyclerView.Adapter<BannersViewPagerAdapter.BannerViewHolder>() {


    inner class BannerViewHolder(val binding: ViewpagerItemBinding) : ViewHolder(binding.root) {
        fun bind(image: SliderItem) {
            var requestOptions = RequestOptions()
            requestOptions = requestOptions.transform(FitCenter())
            Glide.with(itemView).load(image.imageUrl).apply(requestOptions).into(binding.imageProductDetails)
        }

    }

    private val differCallBack = object : DiffUtil.ItemCallback<SliderItem>() {
        override fun areItemsTheSame(oldItem: SliderItem, newItem: SliderItem): Boolean {
            return oldItem.imageUrl == newItem.imageUrl
        }

        override fun areContentsTheSame(oldItem: SliderItem, newItem: SliderItem): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this,differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        return BannerViewHolder(
            ViewpagerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val image = differ.currentList[position]
        holder.bind(image)
    }
}