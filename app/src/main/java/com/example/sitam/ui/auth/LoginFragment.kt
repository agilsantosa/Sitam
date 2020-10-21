package com.example.sitam.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
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
import com.example.sitam.databinding.FragmentLoginBinding
import com.example.sitam.models.auth.DataAuth
import com.example.sitam.utils.Constants
import com.example.sitam.utils.Resource
import com.example.sitam.utils.SharedPreferenceProvider
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment() {

    private lateinit var loginViewModel: LoginViewModel

    private lateinit var binding: FragmentLoginBinding

    private lateinit var preferenceProvider: SharedPreferenceProvider

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_login,
            container,
            false
        )

        preferenceProvider = SharedPreferenceProvider(requireActivity().applicationContext)
        val sessionUser = preferenceProvider.getLevelUser(Constants.KEY_LEVEL_USER)
        if (sessionUser != null) checkSession(sessionUser)

        loginViewModel = (activity as LoginActivity).viewModel

        loginViewModel.loginAuth.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Succes -> {
                    setViewOnLoading(false)
                    response.data?.let {
                        when(it.success){
                            true ->{
                                val data: DataAuth = it.data
                                preferenceProvider.saveLevelUser(Constants.KEY_LEVEL_USER, data.level)
                                preferenceProvider.saveTokenUser(Constants.KEY_TOKEN_USER, data.token)
                                preferenceProvider.saveIdentifierUser(
                                    Constants.KEY_IDENTIFIE_USER,
                                    data.identifier
                                )

                                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                                val intent = Intent(context, MainActivity::class.java).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                startActivity(intent)
                            }
                            false -> {
                                showToast(it.message)
                            }
                        }
                    }
                }
                is Resource.Error -> {
                    setViewOnLoading(false)
                    Log.i("login", response.data.toString())
                    response.message?.let { message ->
                        showToast(message)
                    }
                }
                is Resource.Loading -> {
                    setViewOnLoading(true)
                }
            }
        })

        binding.btnSignIn.setOnClickListener {
            val npm = etNpm.text.toString()
            val pass = etPassword.text.toString()
            inputValidate(npm, pass)
        }

        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(
                R.id.action_loginFragment_to_registerFragment
            )
        }

        return binding.root
    }

    private fun checkSession(sessionUser: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }

    private fun inputValidate(npm: String, pass: String) {
        var isEmpty = false

        if(TextUtils.isEmpty(npm)){
            isEmpty = true
            binding.tinpm.error = "Field tidak boleh kosong"
        }
        binding.tinpm.editText?.addTextChangedListener(object : TextWatcher {
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

        if(TextUtils.isEmpty(pass)){
            isEmpty = true
            binding.tipassword.error = "Field tidak boleh kosong"
        }
        binding.tipassword.editText?.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if(TextUtils.isEmpty(pass)){
                    binding.tipassword.error = null
                }
            }

        })

        if (!isEmpty){
            loginViewModel.loginUser(npm, pass)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun setViewOnLoading(state: Boolean) {
        if (state) {
            binding.etNpm.isEnabled = false
            binding.etPassword.isEnabled = false
            binding.btnSignIn.visibility = View.GONE
            binding.activityLoginProgressBar.visibility = View.VISIBLE
        } else {
            binding.etNpm.isEnabled = true
            binding.etPassword.isEnabled = true
            binding.btnSignIn.visibility = View.VISIBLE
            binding.activityLoginProgressBar.visibility = View.GONE
        }
    }
}