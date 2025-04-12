package com.example.ecommerceapp.fragment.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ecommerceapp.R
import com.example.ecommerceapp.adapter.ProductAdapter
import com.example.ecommerceapp.databinding.FragmentFavoriteBinding
import com.example.ecommerceapp.util.Resource
import com.example.ecommerceapp.viewmodel.FavoriteViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class FavoriteFragment : Fragment() {
    private lateinit var binding: FragmentFavoriteBinding
    private val favoriteAdapter = ProductAdapter()
    private val viewmodel by viewModels<FavoriteViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        favoriteAdapter.onClick = {
            val product = Bundle().apply { putParcelable("product",it) }
            findNavController().navigate(R.id.action_favoriteFragment_to_productDetailsFragment, product)
        }

        lifecycleScope.launchWhenStarted {
            viewmodel.favoriteProducts.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.noFavoritesTV.visibility = View.GONE
                        binding.emptyFavorite.visibility = View.GONE
                        binding.favoriteProgress.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        if (it.data!!.isEmpty()){
                            binding.favoriteProgress.visibility = View.GONE
                            binding.noFavoritesTV.visibility = View.VISIBLE
                            binding.emptyFavorite.visibility = View.VISIBLE
                        }else{
                            binding.emptyFavorite.visibility = View.GONE
                            binding.favoriteProgress.visibility = View.GONE
                            binding.noFavoritesTV.visibility = View.GONE
                            favoriteAdapter.differ.submitList(it.data)
                        }
                    }

                    is Resource.Error -> {
                        binding.noFavoritesTV.visibility = View.VISIBLE
                        binding.emptyFavorite.visibility = View.VISIBLE
                        binding.favoriteProgress.visibility = View.GONE
                    }
                    else -> Unit
                }
            }


        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpFavoriteRv()


    }

    private fun setUpFavoriteRv() {
        binding.favoriteRV.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = favoriteAdapter
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater)
        return binding.root
    }

}