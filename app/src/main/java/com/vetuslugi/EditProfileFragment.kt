package com.vetuslugi

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.vetuslugi.databinding.FragmentEditProfileBinding
import com.vetuslugi.ktor.ApiClient
import com.vetuslugi.ktor.AuthModels
import com.vetuslugi.ktor.AuthModels.UserDTO
import kotlinx.coroutines.launch

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var etNameEdit: EditText
    private lateinit var etSurnameEdit: EditText
    private lateinit var etPhoneEdit: EditText
    private lateinit var etPasswordConfirmation: EditText
    private lateinit var btnChangeData: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(layoutInflater, container, false)

        etNameEdit = binding.etNameEdit
        etSurnameEdit = binding.etSurnameEdit
        etPhoneEdit = binding.etPhoneEdit

        etPasswordConfirmation = binding.etConfirmationPassword
        btnChangeData = binding.btnChangeData

        val prefs = requireContext().getSharedPreferences("credentials", Context.MODE_PRIVATE)
        etNameEdit.setText(prefs.getString("name", "-"))
        etSurnameEdit.setText(prefs.getString("surname", "-"))
        etPhoneEdit.setText(prefs.getString("phone", "-"))

        btnChangeData.setOnClickListener {
            val name = etNameEdit.text
            val surname = etSurnameEdit.text
            val phone = etPhoneEdit.text
            val password = etPasswordConfirmation.text
            val login = prefs.getString("login", "-")
            val role = prefs.getString("role", "-")

            val correctPassword = prefs.getString("password", "-")
            if (
                name.isNotEmpty() &&
                surname.isNotEmpty() &&
                phone.isNotEmpty() &&
                password.isNotEmpty()
                ) {

                if (password.toString() == correctPassword) {
                    binding.progressBar.visibility = View.VISIBLE
                    lifecycleScope.launch {
                        editUserInfo(UserDTO(
                            login = login.toString(),
                            name = name.toString(),
                            surname = surname.toString(),
                            phone = phone.toString(),
                            password = password.toString(),
                            role = role.toString(),
                        ))
                    }
                } else {
                    Toast.makeText(activity, "Неверный пароль", Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(activity, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }

    private suspend fun editUserInfo(userDTO: UserDTO) {
        ApiClient.authApi.updateUser(
            userDTO
        )
        Toast.makeText(activity, "Информация обновлена", Toast.LENGTH_SHORT).show()
        val prefs = requireContext().getSharedPreferences("credentials", Context.MODE_PRIVATE)
        prefs.edit {
            putString("login", userDTO.login).commit()
            putString("name", userDTO.name).commit()
            putString("surname", userDTO.surname).commit()
            putString("phone", userDTO.phone).commit()
            putString("password", userDTO.password).commit()
            putString("role", userDTO.role).commit()
        }
        binding.progressBar.visibility = View.GONE
        findNavController().navigate(R.id.action_editProfileFragment_to_profileInfoFragment)
    }

    companion object {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}