package com.example.ecommerceapp.fragment.loginregister

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ecommerceapp.R
import com.example.ecommerceapp.activity.ShoppingActivity
import com.example.ecommerceapp.data.User
import com.example.ecommerceapp.databinding.FragmentRegisterBinding
import com.example.ecommerceapp.util.RegisterValidation
import com.example.ecommerceapp.util.Resource
import com.example.ecommerceapp.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class RegisterFragment: Fragment() {
    private lateinit var  binding : FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()


    // #056B59

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentRegisterBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.alreadyHaveTV.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.apply {
            RegisterNewBtn.setOnClickListener{
                val user = User(
                    firstName.text.toString().trim(),
                    lastName.text.toString().trim(),
                    EmailNewET.text.toString().trim()

                )
                // password can has an empty spaces so don't use trim
                val password = passNewET.text.toString()
                viewModel.createNewUserWithEmailAndPass(user,password)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.register.collect{
                when(it){
                    is Resource.Loading -> {
                        binding.RegisterNewBtn.isEnabled = false

                    }
                    is Resource.Success -> {
                        Log.v("TEST",it.message.toString())
                        binding.RegisterNewBtn.isEnabled = true
                        Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                    is Resource.Error -> {
                        Log.v("TEST",it.message.toString())

                        binding.RegisterNewBtn.isEnabled = true
                    }
                    else -> Unit
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.validation.collect{ validation ->
                if (validation.email is RegisterValidation.Failed){
                    // when detect something wrong we need to show it in UI
                    withContext(Dispatchers.Main){
                        binding.EmailNewET.apply {
                            requestFocus()
                            error = validation.email.message
                        }
                    }
                }
                if (validation.password is RegisterValidation.Failed){
                    withContext(Dispatchers.Main){
                        binding.passNewET.apply {
                            requestFocus()
                            error = validation.password.message
                        }
                    }
                }
            }
        }


    }

}