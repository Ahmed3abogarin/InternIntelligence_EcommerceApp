package com.example.ecommerceapp.fragment.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerceapp.R
import com.example.ecommerceapp.adapter.CartProductAdapter
import com.example.ecommerceapp.databinding.FragmentCartBinding
import com.example.ecommerceapp.firebase.FirebaseCommon
import com.example.ecommerceapp.util.Resource
import com.example.ecommerceapp.util.VerticalItemDecoration
import com.example.ecommerceapp.viewmodel.CartViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding
    private val cartAdapter by lazy { CartProductAdapter() }

    private val viewModel by activityViewModels<CartViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpCartView()



        var totalPrice = 0f
        lifecycleScope.launchWhenStarted {
            viewModel.productPrice.collectLatest { price ->
                price?.let {
                    totalPrice = it
                    binding.totalTV.text = "$ $price"
                }
            }
        }

        // ----------------------------------go details fragment
        cartAdapter.onProductClick = {
            val bundle = Bundle().apply {
                putParcelable("product",it.product)
            }
            findNavController().navigate(R.id.action_cartFragment_to_productDetailsFragment,bundle)
        }

        // ------------------------ change the quantity --------------
        cartAdapter.onPlusClick = {
            viewModel.changeQuantity(it , FirebaseCommon.QuantityChanging.INCREASE)
        }

        cartAdapter.onMinusClick = {
            viewModel.changeQuantity(it, FirebaseCommon.QuantityChanging.DECREASE)
        }


        // ------------------ -- send the product & total Price to billing fragment -----
        binding.checkOutBtn.setOnClickListener {
            val myAction = CartFragmentDirections.actionCartFragmentToBillingFragment(totalPrice,cartAdapter.differ.currentList.toTypedArray(),true)
            findNavController().navigate(myAction)

        }


        // ---------------- delete dialog -----------------------
        lifecycleScope.launchWhenStarted {
            viewModel.deleteDialog.collectLatest {
                val alertDialog = AlertDialog.Builder(requireContext()).apply {
                    setTitle("Delete item from cart")
                    setMessage("Do you want to delete this item from your cart?")
                    setNegativeButton("Cancel"){ dialog,_ ->
                        dialog.dismiss()
                    }
                    setPositiveButton("Yes"){dialog, _ ->
                        viewModel.deleteCartProduct(it)
                        dialog.dismiss()
                    }
                }
                alertDialog.create()
                alertDialog.show()
            }
        }



        lifecycleScope.launchWhenStarted {
            viewModel.cart_product.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.cartProgressBar.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.cartProgressBar.visibility = View.INVISIBLE
                        if (it.data!!.isEmpty()) {
                            showEmptyCart()
                            hideOthersViews()
                        } else {
                            hideEmptyCart()
                            showOthersViews()
                            cartAdapter.differ.submitList(it.data)
                        }
                    }

                    is Resource.Error -> {
                        binding.cartProgressBar.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), "Fuck you", Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }

    }

    private fun showOthersViews() {
        binding.apply {
            cartRecycler.visibility = View.VISIBLE
            checkOutBtn.visibility = View.VISIBLE
        }
    }

    private fun hideOthersViews() {
        binding.apply {
            cartRecycler.visibility = View.INVISIBLE
        }
    }

    private fun hideEmptyCart() {
        binding.apply {
            layoutLoading.visibility = View.GONE
        }

    }

    private fun showEmptyCart() {
        binding.apply {
            layoutLoading.visibility = View.VISIBLE
        }
    }

    private fun setUpCartView() {
        binding.cartRecycler.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = cartAdapter
            addItemDecoration(VerticalItemDecoration())  // spaces between items
        }
    }
}