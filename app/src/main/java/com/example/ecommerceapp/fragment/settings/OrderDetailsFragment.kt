package com.example.ecommerceapp.fragment.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerceapp.adapter.BillingProductAdapter
import com.example.ecommerceapp.data.order.OrderStatus
import com.example.ecommerceapp.data.order.getOrderStatus
import com.example.ecommerceapp.databinding.FragmentOrderDetailsBinding
import com.example.ecommerceapp.util.VerticalItemDecoration


class OrderDetailsFragment : Fragment() {
    private lateinit var binding: FragmentOrderDetailsBinding
    private val orderDetailsAdapter by lazy { BillingProductAdapter() }
    private val args by navArgs<OrderDetailsFragmentArgs>()




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentOrderDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpOrdersRV()

        // retrieve the order from all orders fragment
        val order = args.order

        // assign the widgets
        binding.apply {
            tvOrderId.text = "Order #${order.orderId}"
            stepView.setSteps(
                mutableListOf(
                    OrderStatus.Ordered.status,
                    OrderStatus.Confirmed.status,
                    OrderStatus.Shipped.status,
                    OrderStatus.Delivered.status,
                )
            )

            Log.v("TAGY",order.orderStatus)

            val currentOrderState = when (getOrderStatus(order.orderStatus)) {
                is OrderStatus.Ordered -> 0
                is OrderStatus.Confirmed -> 1
                is OrderStatus.Shipped -> 2
                is OrderStatus.Delivered -> 3
                else -> 0
            }

            stepView.go(currentOrderState, false)
            if (currentOrderState == 3) {
                stepView.done(true)
            }

            tvFullName.text = order.address.fullName
            tvAddress.text = "${order.address.street} ${order.address.city}"
            tvPhoneNumber.text = order.address.phone

            tvTotalPrice.text = "$ ${order.totalPrice}"
        }

        orderDetailsAdapter.differ.submitList(order.products)

        binding.imageCloseOrder.setOnClickListener {
            findNavController().navigateUp()
        }


    }

    private fun setUpOrdersRV() {
        binding.rvProducts.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = orderDetailsAdapter
            addItemDecoration(VerticalItemDecoration())
        }
    }
}