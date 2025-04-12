package com.example.ecommerceapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.data.Cats
import com.example.ecommerceapp.data.Product
import com.example.ecommerceapp.data.SliderItem
import com.example.ecommerceapp.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
) : ViewModel() {

    private val _popularProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val popularProducts = _popularProducts.asStateFlow()

    private val _newProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val newProducts = _newProducts.asStateFlow()

    private val _bestProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProducts = _bestProducts.asStateFlow()

    private val _bannerImages = MutableStateFlow<Resource<List<SliderItem>>>(Resource.Unspecified())
    val bannerImages = _bannerImages.asStateFlow()


    private val _catImages = MutableStateFlow<Resource<List<Cats>>>(Resource.Unspecified())
    val catImages = _catImages.asStateFlow()

    private val _searchedProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val searchedProducts = _searchedProducts.asStateFlow()



    init {
        fetchPopularProducts()
        fetchNewProducts()
        fetchBannersImages()
        fetchCatsImage()
        fetchBestSellerProducts()
    }

    private fun fetchBestSellerProducts() {
        viewModelScope.launch {
            _bestProducts.emit(Resource.Loading())
        }
        firestore.collection("products").whereEqualTo("category", "bestSeller")
            .whereEqualTo("offerPercentage", null).get()
            .addOnSuccessListener {
                val products = it.toObjects(Product::class.java)
                viewModelScope.launch {
                    _bestProducts.emit(Resource.Success(products))
                }

            }.addOnFailureListener {
                viewModelScope.launch {
                    _bestProducts.emit(Resource.Error(it.message.toString()))
                }
            }


    }


    private fun fetchPopularProducts() {
        viewModelScope.launch {
            _popularProducts.emit(Resource.Loading())
        }
        firestore.collection("products").whereEqualTo("category", "test1")
            .whereEqualTo("offerPercentage", null).get()
            .addOnSuccessListener {
                val products = it.toObjects(Product::class.java)
                viewModelScope.launch {
                    _popularProducts.emit(Resource.Success(products))
                }

            }.addOnFailureListener {
                viewModelScope.launch {
                    _popularProducts.emit(Resource.Error(it.message.toString()))
                }
            }


    }

    private fun fetchNewProducts() {
        viewModelScope.launch {
            _newProducts.emit(Resource.Loading())
        }
        firestore.collection("products").whereEqualTo("category", "test2")
            .whereEqualTo("offerPercentage", null).get()
            .addOnSuccessListener {
                val products = it.toObjects(Product::class.java)
                viewModelScope.launch {
                    _newProducts.emit(Resource.Success(products))
                }

            }.addOnFailureListener {
                viewModelScope.launch {
                    _newProducts.emit(Resource.Error(it.message.toString()))
                }
            }

    }

    private fun fetchBannersImages(){
        viewModelScope.launch { _bannerImages.emit(Resource.Loading()) }
        firestore.collection("banners").get()
            .addOnSuccessListener {
                val banners = it.toObjects(SliderItem::class.java)
                Log.v("TOOL",banners[0].imageUrl)
                viewModelScope.launch {
                    _bannerImages.emit(Resource.Success(banners))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _bannerImages.emit(Resource.Error(it.message.toString()))
                }

            }
    }

    private fun fetchCatsImage(){
        viewModelScope.launch { _catImages.emit(Resource.Loading()) }
        firestore.collection("cats").get()
            .addOnSuccessListener {
                val cats = it.toObjects(Cats::class.java)
                viewModelScope.launch {
                    _catImages.emit(Resource.Success(cats))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _catImages.emit(Resource.Error(it.message.toString()))
                }

            }
    }


    fun fetchSearchProducts(searchQuery: String){
        viewModelScope.launch { _searchedProducts.emit(Resource.Loading()) }

        firestore.collection("products").get()
            .addOnSuccessListener { documents ->
                val allProducts = documents.toObjects(Product::class.java)

                // Filter products by lowercase search query
                val filteredProducts = allProducts.filter { product ->
                    product.name.lowercase().contains(searchQuery.lowercase()) // Case-insensitive search
                }

                viewModelScope.launch {
                    _searchedProducts.emit(Resource.Success(filteredProducts))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _searchedProducts.emit(Resource.Error(it.message.toString()))
                }
            }


    }



}