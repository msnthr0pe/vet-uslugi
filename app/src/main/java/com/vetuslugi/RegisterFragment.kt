package com.vetuslugi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.vetuslugi.databinding.FragmentRegisterBinding
import com.vetuslugi.ktor.ApiClient
import com.vetuslugi.ktor.AuthModels
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    lateinit var etName: EditText
    lateinit var etSurname: EditText
    lateinit var etPhone: EditText
    lateinit var etLogin: EditText
    lateinit var etPassword: EditText
    lateinit var btnContinue: Button
    lateinit var cbIsAdministrator: CheckBox
    lateinit var role: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)

        etName = binding.etNameRegister
        etSurname = binding.etSurnameRegister
        etPhone = binding.etPhoneRegister
        etLogin = binding.etLoginRegister
        etPassword = binding.etPasswordRegister
        btnContinue = binding.btnContinueRegister
        role = "admin"

        btnContinue.setOnClickListener {
            val name = etName.text.toString()
            val surname = etSurname.text.toString()
            val phone = etPhone.text.toString()
            val login = etLogin.text.toString()
            val password = etPassword.text.toString()

            if (
                name.isNotEmpty() &&
                surname.isNotEmpty() &&
                phone.isNotEmpty() &&
                login.isNotEmpty() &&
                password.isNotEmpty()
            ) {
                registerNewUser(name, surname, phone, login, password)
            } else {
                Toast.makeText(activity, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun registerNewUser(
        name: String,
        surname: String,
        phone: String,
        login: String,
        password: String
    ) {
        val call = ApiClient.authApi.register(AuthModels.RegisterRequest(
            login, password, name, surname, phone, role)
        )
        call.enqueue(object : Callback<AuthModels.AuthResponse> {
            override fun onResponse(
                call: Call<AuthModels.AuthResponse>,
                response: Response<AuthModels.AuthResponse>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Учётная запись создана", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                } else {
                    Toast.makeText(requireContext(), "Ошибка создания учётной записи", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AuthModels.AuthResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT).show()
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