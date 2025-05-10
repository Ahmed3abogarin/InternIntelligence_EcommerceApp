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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerceapp.R
import com.example.ecommerceapp.adapter.AddressAdapter
import com.example.ecommerceapp.adapter.BillingProductAdapter
import com.example.ecommerceapp.data.Address
import com.example.ecommerceapp.data.CartProduct
import com.example.ecommerceapp.data.order.Order
import com.example.ecommerceapp.data.order.OrderStatus
import com.example.ecommerceapp.databinding.FragmentBillingBinding
import com.example.ecommerceapp.util.Constants.PB_KEY
import com.example.ecommerceapp.util.HorizontalItemDecoration
import com.example.ecommerceapp.util.Resource
import com.example.ecommerceapp.viewmodel.BillingViewModel
import com.example.ecommerceapp.viewmodel.OrderedViewModel
import com.google.android.material.snackbar.Snackbar
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BillingFragment : Fragment() {
    private lateinit var binding: FragmentBillingBinding
    private val addressAdapter by lazy { AddressAdapter() }
    private val billingProductAdapter by lazy { BillingProductAdapter() }

    private val billingViewModel by viewModels<BillingViewModel>()


    // receive the products the total price
    private val args by navArgs<BillingFragmentArgs>()
    private var products = emptyList<CartProduct>()
    private var totalPrice = 0f

    private var selectedAddress: Address? = null
    private val orderViewModel by viewModels<OrderedViewModel>()


    // payment
    private lateinit var paymentSheet: PaymentSheet



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PaymentConfiguration.init(requireActivity(), PB_KEY)
        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)


        //-------------------- Receive the products list and the total price--------------
        products = args.products.toList()
        totalPrice = args.totalPrice


    }





    fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        when (paymentSheetResult) {
            is PaymentSheetResult.Completed -> {
                val order = Order(
                    OrderStatus.Ordered.status,
                    totalPrice,
                    products,
                    selectedAddress!!
                )
                orderViewModel.placeOrder(order)
                Toast.makeText(requireContext(), "Payment Done successfully", Toast.LENGTH_SHORT).show()
            }

            is PaymentSheetResult.Canceled -> {
                Log.v("PAYMENT","payment canleded")
            }

            is PaymentSheetResult.Failed -> {
                val errorMessage = paymentSheetResult.error.message ?: "Unknown payment error"
                Log.e("PAYMENT", errorMessage)
                Toast.makeText(requireContext(), "Payment failed: $errorMessage", Toast.LENGTH_LONG).show()



            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = FragmentBillingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setUpAddressRV()
        setUpBillingProductRV()

        if (!args.payment) {
            binding.apply {
                buttonPlaceOrder.visibility = View.INVISIBLE
                totalBoxContainer.visibility = View.INVISIBLE
                middleLine.visibility = View.INVISIBLE
                bottomLine.visibility = View.INVISIBLE
            }
        }

        // navigate to address fragments to add more address
        binding.imageAddAddress.setOnClickListener {
            findNavController().navigate(R.id.action_billingFragment_to_addressFragment)
        }

        addressAdapter.onClick = {
            selectedAddress = it
            if (!args.payment) {
                val b = Bundle().apply { putParcelable("address", selectedAddress) }
                findNavController().navigate(R.id.action_billingFragment_to_addressFragment, b)
            }

        }
        lifecycleScope.launchWhenStarted {
            billingViewModel.address.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarAddress.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        addressAdapter.differ.submitList(it.data)
                        binding.progressbarAddress.visibility = View.INVISIBLE

                    }

                    is Resource.Error -> {
                        binding.progressbarAddress.visibility = View.GONE
                        Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT)
                            .show()

                    }

                    else -> Unit
                }
            }
        }

        // change the button status
        lifecycleScope.launchWhenStarted {
            orderViewModel.order.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.buttonPlaceOrder.isEnabled = false
                    }

                    is Resource.Success -> {
                        binding.buttonPlaceOrder.isEnabled = true
                        findNavController().navigateUp()
                        Snackbar.make(
                            requireView(),
                            "Your order has been placed",
                            Snackbar.LENGTH_LONG
                        ).show()

                    }

                    is Resource.Error -> {
                        binding.buttonPlaceOrder.isEnabled = true
                        Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT)
                            .show()

                    }

                    else -> Unit
                }
            }
        }

        lifecycleScope.launch {
            billingViewModel.paymentDetails.collectLatest { res ->
                when (res) {
                    is Resource.Loading -> {
                        binding.buttonPlaceOrder.isEnabled = false
                        binding.buttonPlaceOrder.startAnimation()
                    }

                    is Resource.Success -> {
                        res.data?.let { (customerId, ephKey, clientSecret) ->
                            paymentSheet.presentWithPaymentIntent(
                                clientSecret,
                                PaymentSheet.Configuration(
                                    "Ahmed Ecommerce App",
                                    PaymentSheet.CustomerConfiguration(customerId, ephKey)
                                )
                            )
                        } ?: run {
                            Toast.makeText(requireContext(), "Payment data is missing", Toast.LENGTH_SHORT).show()
                        }
                        binding.buttonPlaceOrder.revertAnimation()
                        binding.buttonPlaceOrder.isEnabled = true
                    }
                    is Resource.Error -> {
                        Log.d("StripeDate","error is : ${res.message}")
                        Toast.makeText(requireContext(), res.message ?: "Payment failed", Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }




        billingProductAdapter.differ.submitList(products)

        binding.tvTotalPrice.text = "$ " + String.format("%.2f", totalPrice)

        // -----------------direct the user to place order
        addressAdapter.onClick = {
            selectedAddress = it
        }

        binding.buttonPlaceOrder.setOnClickListener {
            if (selectedAddress == null) {
                Toast.makeText(requireContext(), "Please select address", Toast.LENGTH_SHORT).show()
            } else {
                billingViewModel.preparePaymentFlow(totalPrice)
            }
        }
    }

//    private fun showOrderConfirmationDialog() {
//        val alertDialog = AlertDialog.Builder(requireContext()).apply {
//            setTitle("Order items")
//            setMessage("Do you want to order your cart items?")
//            setNegativeButton("Cancel") { dialog, _ ->
//                dialog.dismiss()
//            }
//            setPositiveButton("Yes") { dialog, _ ->
//                val order = Order(
//                    OrderStatus.Ordered.status,
//                    totalPrice,
//                    products,
//                    selectedAddress!!
//                )
//                orderViewModel.placeOrder(order)
//                dialog.dismiss()
//            }
//        }
//        alertDialog.create()
//        alertDialog.show()
//    }

    private fun setUpBillingProductRV() {
        binding.rvProducts.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            adapter = billingProductAdapter
            addItemDecoration(HorizontalItemDecoration())
        }
    }

    private fun setUpAddressRV() {
        binding.rvAddress.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            adapter = addressAdapter
            addItemDecoration(HorizontalItemDecoration())
        }
    }

}