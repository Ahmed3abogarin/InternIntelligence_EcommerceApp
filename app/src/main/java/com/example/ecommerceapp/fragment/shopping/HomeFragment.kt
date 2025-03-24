package com.example.ecommerceapp.fragment.shopping

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerceapp.R
import com.example.ecommerceapp.adapter.BannersViewPagerAdapter
import com.example.ecommerceapp.adapter.CatsAdapter
import com.example.ecommerceapp.adapter.ProductAdapter
import com.example.ecommerceapp.databinding.FragmentHomeBinding
import com.example.ecommerceapp.util.Resource
import com.example.ecommerceapp.util.VerticalItemDecoration
import com.example.ecommerceapp.util.showBottomNavigationBar
import com.example.ecommerceapp.viewmodel.ProductsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class HomeFragment: Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private var adapter = ProductAdapter()
    private var adapter2 = ProductAdapter()
    private var adapter3 = ProductAdapter()
    private val viewModel by viewModels<ProductsViewModel>()
    private val pagerAdapter by lazy { BannersViewPagerAdapter() }
    private val catAdapter by lazy { CatsAdapter() }

    // TODO : TO Forget to add the method for download product as long as scroll not all at once
    //  to prevent memory leak and waste the download data from the database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter.onClick = {
            val b = Bundle().apply { putParcelable("product",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)
        }

        adapter2.onClick = {
            val b = Bundle().apply { putParcelable("product",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)
        }
        adapter3.onClick = {
            val b = Bundle().apply { putParcelable("product",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)
        }



        lifecycleScope.launchWhenStarted {
            viewModel.searchedProducts.collectLatest {
                when (it) {
                    is Resource.Loading ->{

                    }
                    is Resource.Success -> {
                        adapter3.differ.submitList(it.data)
                    }
                    is Resource.Error -> {

                    }
                    else -> Unit
                }
            }
        }


        lifecycleScope.launchWhenStarted {
            viewModel.bannerImages.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        //binding.officialProgress.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {

                        pagerAdapter.differ.submitList(it.data)

                        //binding.officialProgress.visibility = View.GONE
                    }

                    is Resource.Error -> {
                        Log.e("TAG", it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }

        }


        lifecycleScope.launchWhenStarted {
            viewModel.catImages.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.officialProgress.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {

                        catAdapter.differ.submitList(it.data)

                        binding.officialProgress.visibility = View.GONE
                    }

                    is Resource.Error -> {
                        Log.e("TAG", it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.newProducts.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.newProductsProgress.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        adapter2.differ.submitList(it.data)
                        binding.newProductsProgress.visibility = View.GONE
                    }

                    is Resource.Error -> {
                        hideLoading()
                        Log.e("TAG", it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }

        }

        lifecycleScope.launchWhenStarted {
            viewModel.popularProducts.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        showLoading()
                    }

                    is Resource.Success -> {
                        adapter.differ.submitList(it.data)
                        hideLoading()
                    }

                    is Resource.Error -> {
                        hideLoading()
                        Log.e("TAG", it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }






    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpCatRV()
        setUpPopularRV()
        setUpNewRV()
        setUpViewPager()

        binding.mySearchView.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }



    private fun setUpCatRV() {
        binding.officalRecycler.apply {
            layoutManager =
                LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)

            adapter = catAdapter
            addItemDecoration(VerticalItemDecoration())
        }
    }

    private fun setUpViewPager() {
        binding.viewPager.adapter = pagerAdapter
    }

    private fun setUpNewRV() {
        binding.apply {
            newRecycler.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            newRecycler.adapter = adapter2
            newRecycler.addItemDecoration(VerticalItemDecoration())
        }


    }

    private fun setUpPopularRV() {
        binding.apply {
            popularRecycler.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            popularRecycler.adapter = adapter
            popularRecycler.addItemDecoration(VerticalItemDecoration())
        }
    }
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            binding = FragmentHomeBinding.inflate(inflater)
            return binding.root
        }





    private fun showLoading() {
        binding.popularProgress.visibility = View.VISIBLE
    }
    private fun hideLoading(){
        binding.popularProgress.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()

        showBottomNavigationBar()
    }
}
