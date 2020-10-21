package com.example.sitam.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.sitam.MainActivity
import com.example.sitam.R
import com.example.sitam.databinding.FragmentRegisterBinding
import com.example.sitam.utils.Constants
import com.example.sitam.utils.Resource
import com.example.sitam.utils.SharedPreferenceProvider

class RegisterFragment : Fragment() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var preferenceProvider: SharedPreferenceProvider

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_register, container, false
        )
        preferenceProvider = SharedPreferenceProvider(requireActivity().applicationContext)

        loginViewModel = (activity as LoginActivity).viewModel

        loginViewModel.regisAuth.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Succes -> {
                    setViewOnLoading(false)
                    response.data?.let {
                        when(it.success){
                            true ->{
                                val data = it.data
                                if (data.identifier.isNotEmpty()) {
                                    preferenceProvider.saveLevelUser(Constants.KEY_LEVEL_USER, data.level)
                                    preferenceProvider.saveTokenUser(Constants.KEY_TOKEN_USER, data.token)
                                    preferenceProvider.saveIdentifierUser(
                                        Constants.KEY_IDENTIFIE_USER,
                                        data.identifier
                                    )

                                    val intent = Intent(context, MainActivity::class.java).apply {
                                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    startActivity(intent)
                                }
                            }
                            false ->{
                                showToast(it.message)
                            }
                        }
                    }
                }
                is Resource.Error -> {
                    setViewOnLoading(false)
                    response.message?.let { message ->
                        showToast(message)
                    }
                }
                is Resource.Loading -> {
                    setViewOnLoading(true)
                }
            }
        })

        binding.btnCreateAccount.setOnClickListener {
            val npm = binding.etNpm.text.toString()
            val nama = binding.etNama.text.toString()
            val noTelp = binding.etTelp.text.toString()
            val alamat = binding.etAlamat.text.toString()
            val password = binding.etPassword.text.toString()
            val rePassword = binding.etRePassword.text.toString()
            inputValidate(npm, nama, noTelp, alamat, password, rePassword)
        }

        binding.tvLogin.setOnClickListener {
            findNavController().navigate(
                R.id.action_registerFragment_to_loginFragment
            )
        }


        return binding.root
    }

    private fun inputValidate(
        npm: String,
        nama: String,
        noTelp: String,
        alamat: String,
        password: String,
        rePassword: String
    ) {
        var isEmpty = false

        if(TextUtils.isEmpty(npm)){
            isEmpty = true
            binding.tinpm.error = "Field tidak boleh kosong"
        }
        binding.tinpm.editText?.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if(TextUtils.isEmpty(npm)){
                    binding.tinpm.error = null
                }
            }

        })

        if(TextUtils.isEmpty(nama)){
            isEmpty = true
            binding.tinama.error = "Field tidak boleh kosong"
        }
        binding.tinama.editText?.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if(TextUtils.isEmpty(nama)){
                    binding.tinama.error = null
                }
            }

        })

        if(TextUtils.isEmpty(noTelp)){
            isEmpty = true
            binding.tinotelp.error = "Field tidak boleh kosong"
        }
        binding.tinotelp.editText?.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if(TextUtils.isEmpty(noTelp)){
                    binding.tinotelp.error = null
                }
            }

        })

        if(TextUtils.isEmpty(alamat)){
            isEmpty = true
            binding.tialamat.error = "Field tidak boleh kosong"
        }
        binding.tialamat.editText?.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if(TextUtils.isEmpty(alamat)){
                    binding.tialamat.error = null
                }
            }

        })

        if(TextUtils.isEmpty(password)){
            isEmpty = true
            binding.tipassword.error = "Field tidak boleh kosong"
        }
        binding.tipassword.editText?.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if(TextUtils.isEmpty(password)){
                    binding.tipassword.error = null
                }
            }

        })

        if(TextUtils.isEmpty(rePassword)){
            isEmpty = true
            binding.tirePassword.error = "Field tidak boleh kosong"
        }
        binding.tirePassword.editText?.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if(TextUtils.isEmpty(rePassword)){
                    binding.tirePassword.error = null
                }
            }

        })

        if(!isEmpty){
            if (password != rePassword){
                Toast.makeText(context, "Password tidak sama", Toast.LENGTH_SHORT).show()
            }else{
                loginViewModel.registerUser(npm, nama, noTelp, alamat, password, rePassword)
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun setViewOnLoading(state: Boolean) {
        if (state) {
            binding.etNpm.isEnabled = false
            binding.etNama.isEnabled = false
            binding.etTelp.isEnabled = false
            binding.etAlamat.isEnabled = false
            binding.etPassword.isEnabled = false
            binding.etRePassword.isEnabled = false
            binding.btnCreateAccount.visibility = View.GONE
            binding.activityRegisProgressBar.visibility = View.VISIBLE
        } else {
            binding.etNpm.isEnabled = true
            binding.etNama.isEnabled = true
            binding.etTelp.isEnabled = true
            binding.etAlamat.isEnabled = true
            binding.etPassword.isEnabled = true
            binding.etRePassword.isEnabled = true
            binding.btnCreateAccount.visibility = View.VISIBLE
            binding.activityRegisProgressBar.visibility = View.GONE
        }
    }
}