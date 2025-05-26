package com.vetuslugi

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.vetuslugi.databinding.FragmentLoginBinding
import com.vetuslugi.ktor.ApiClient
import com.vetuslugi.ktor.AuthModels
import com.vetuslugi.ktor.AuthModels.LoginDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    lateinit var etLogin: EditText
    lateinit var etPassword: EditText
    lateinit var btnLogin: Button
    lateinit var tvRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)

        etLogin = binding.etLoginLogin
        etPassword = binding.etLoginPassword
        btnLogin = binding.loginBtn
        tvRegister = binding.tvRegister

        btnLogin.setOnClickListener {
            val login = etLogin.text.toString()
            val password = etPassword.text.toString()
            if (login.isNotEmpty() && password.isNotEmpty()) {
                loginUser(login, password)
            } else {
                Toast.makeText(activity, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }

        tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        return binding.root
    }

    private fun loginUser(login: String, password: String) {
        val call = ApiClient.authApi.login(AuthModels.LoginRequest(login, password))
        call.enqueue(object : Callback<AuthModels.AuthResponse> {
            override fun onResponse(
                call: Call<AuthModels.AuthResponse>,
                response: Response<AuthModels.AuthResponse>
            ) {
                if (response.isSuccessful) {

                    var userDTO = AuthModels.RegisterRequest(
                        "-",
                        "-",
                        "-",
                        "-",
                        "-",
                        "-"
                    )

                    lifecycleScope.launch {
                        try {
                            userDTO = withContext(Dispatchers.IO) {
                                ApiClient.authApi.getUser(LoginDTO(etLogin.text.toString()))
                            }

                            val prefs = requireContext().getSharedPreferences(
                                "credentials",
                                Context.MODE_PRIVATE
                            )

                            prefs.edit {
                                putString("email", userDTO.email).commit()
                                putString("name", userDTO.name).commit()
                                putString("surname", userDTO.surname).commit()
                                putString("phone", userDTO.phone).commit()
                                putString("password", userDTO.password).commit()
                                putString("status", userDTO.status).commit()
                            }
                            val status = prefs.getString("status", "--")

                            Log.e("PREFS", status!!)

                            findNavController().navigate(R.id.action_loginFragment_to_newsFragment)
                        } catch (e: Exception) {
                            Log.e("MYCLIENT", "Ошибка: ${e.message}")
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Ошибка входа", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AuthModels.AuthResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    companion object {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}