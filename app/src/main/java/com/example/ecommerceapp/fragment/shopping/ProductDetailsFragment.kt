package com.example.ecommerceapp.fragment.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerceapp.R
import com.example.ecommerceapp.adapter.ColorsAdapter
import com.example.ecommerceapp.adapter.SizesAdapter
import com.example.ecommerceapp.adapter.ViewPagerAdapter
import com.example.ecommerceapp.data.CartProduct
import com.example.ecommerceapp.data.Product
import com.example.ecommerceapp.databinding.FragmentProductDetailsBinding
import com.example.ecommerceapp.util.HorizontalItemDecoration
import com.example.ecommerceapp.util.Resource
import com.example.ecommerceapp.util.hideBottomNavigationBar
import com.example.ecommerceapp.viewmodel.DetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProductDetailsFragment : Fragment() {
    private val args by navArgs<ProductDetailsFragmentArgs>()
    private lateinit var binding: FragmentProductDetailsBinding
    private val colorsAdapter by lazy { ColorsAdapter() }
    private val sizesAdapter by lazy { SizesAdapter() }
    private val viewModel by viewModels<DetailsViewModel>()
    private var selectedColors: Int? = null
    private var selectedSizes: String? = null
    private lateinit var product: Product
    private val viewPagerAdapter by lazy { ViewPagerAdapter() }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        hideBottomNavigationBar()
        binding = FragmentProductDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageView6.setOnClickListener {
            findNavController().navigateUp()
        }

        product = args.product
        setUpViewPager()
        setUpSizesRV()
        // ----------------- Colors ------------------------
        setUpColorsRV()

        colorsAdapter.onItemClicked = {
            selectedColors = it
        }
        product.colors?.let { colorsAdapter.differ.submitList(it) }

        // ----------------- Sizes ------------------------
        binding.apply {
            sizesRecycler.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            sizesRecycler.adapter = sizesAdapter
        }

        sizesAdapter.onItemClicked = {
            selectedSizes = it
        }
        product.sizes?.let { sizesAdapter.differ.submitList(it) }

        initiViews()
        // --------------------- View Pager ---------------------

        viewPagerAdapter.differ.submitList(product.images)


        // -------------------- Add To Cart Process -----------------
        binding.addToCartBtn.setOnClickListener {

            if (!product.colors.isNullOrEmpty() && selectedColors == null){
                Toast.makeText(requireContext(),"Choose a color",Toast.LENGTH_SHORT).show()
            }else if (!product.sizes.isNullOrEmpty() && selectedSizes == null){
                Toast.makeText(requireContext(),"Choose a Size",Toast.LENGTH_SHORT).show()
            }else {
                viewModel.addOrUpdateProductToCart(
                    CartProduct(
                        product,
                        1,
                        selectedColors,
                        selectedSizes
                    )
                )
            }

        }

        lifecycleScope.launchWhenStarted {
            // 'add to cart' process
            viewModel.addToCart.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.addToCartBtn.startAnimation()
                    }

                    is Resource.Success -> {
                        val cartImg = ContextCompat.getDrawable(requireContext(),R.drawable.ic_done)
                        val ss = cartImg?.toBitmap()


                        binding.addToCartBtn.doneLoadingAnimation(R.color.my_green,ss!!)
                        delay(1000)
                        binding.addToCartBtn.revertAnimation()
                        binding.addToCartBtn.setBackgroundColor(resources.getColor(R.color.grey))
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }

                    else -> Unit
                }
            }

        }


    }

    private fun setUpViewPager() {
        binding.apply {
            detailsViewPager.adapter = viewPagerAdapter
        }
    }

    private fun initiViews() {
        binding.apply {
//            var requestOptions = RequestOptions()
//            requestOptions = requestOptions.transform(CenterCrop())
            productName.text = product.name
//            productDescription.text = product.description
            productPrice.text = "$"+product.price.toString()

            if (product.colors.isNullOrEmpty()) {
                binding.colorsTV.visibility = View.INVISIBLE
            }
            if (product.sizes.isNullOrEmpty()) {
                binding.sizesTV.visibility = View.INVISIBLE
            }
        }
    }

    private fun setUpSizesRV() {
        binding.apply {
            sizesRecycler.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            sizesRecycler.adapter = sizesAdapter
            sizesRecycler.addItemDecoration(HorizontalItemDecoration())
        }

    }

    private fun setUpColorsRV() {
        binding.apply {
            colorsRecycler.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            colorsRecycler.adapter = colorsAdapter
            colorsRecycler.addItemDecoration(HorizontalItemDecoration())
        }

    }

}