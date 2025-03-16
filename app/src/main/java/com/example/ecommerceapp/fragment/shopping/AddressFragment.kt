package com.example.ecommerceapp.fragment.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.ecommerceapp.data.Address
import com.example.ecommerceapp.databinding.FragmentAddressBinding
import com.example.ecommerceapp.util.Resource
import com.example.ecommerceapp.viewmodel.AddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class AddressFragment: Fragment() {
    private lateinit var binding: FragmentAddressBinding
    val viewModel by viewModels<AddressViewModel>()
    val args by navArgs<AddressFragmentArgs>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenStarted {
            viewModel.addNewAddress.collectLatest {
                when(it){
                    is Resource.Loading ->{
                        binding.addressProgressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success ->{
                        binding.addressProgressBar.visibility = View.INVISIBLE
                        findNavController().navigateUp()

                    }
                    is Resource.Error ->{
                        Toast.makeText(requireContext(),it.message.toString(),Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.error.collectLatest {
                Toast.makeText(requireContext(),it,Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentAddressBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val address = args.address
        if (address == null){
            binding.deleteAddressBtn.visibility = View.GONE
        }else{
            binding.apply {
                addressTitleET.setText(address.addressTitle)
                fullNameET.setText(address.fullName)
                stateEt.setText(address.state)
                phoneET.setText(address.phone)
                cityET.setText(address.city)
                streetET.setText(address.street)
            }
        }


        binding.apply {
            saveAddressBtn.setOnClickListener{
                val addressTitle = addressTitleET.text.trim().toString()
                val fullName = fullNameET.text.trim().toString()
                val street = streetET.text.trim().toString()
                val phone = phoneET.text.trim().toString()
                val city = cityET.text.trim().toString()
                val state = stateEt.text.trim().toString()
                val address = Address(addressTitle, fullName, street, phone, city, state)

                viewModel.addAddress(address)
            }
        }
    }

}