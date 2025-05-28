package com.vetuslugi

import android.content.Context
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
import com.vetuslugi.databinding.FragmentAddPlaceBinding
import com.vetuslugi.ktor.ApiClient
import com.vetuslugi.ktor.AuthModels
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddPlaceFragment : Fragment() {

    private var _binding: FragmentAddPlaceBinding? = null
    private val binding get() = _binding!!

    private lateinit var etNameNewPlace: EditText
    private lateinit var etAddressNewPlace: EditText
    private lateinit var etPhoneNewPlace: EditText
    private lateinit var etDescriptionNewPlace: EditText
    private lateinit var cbShelter: CheckBox
    private lateinit var cbNursery: CheckBox
    private lateinit var btnAddPlace: Button




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPlaceBinding.inflate(layoutInflater, container, false)

        etNameNewPlace = binding.etNameNewPlace
        etAddressNewPlace = binding.etAddressNewPlace
        etPhoneNewPlace = binding.etPhoneNewPlace
        etDescriptionNewPlace = binding.etDescNewPlace
        cbShelter = binding.cbShelter
        cbNursery = binding.cbNursery
        btnAddPlace = binding.btnAddNewPlace
        var shelterPressed = false
        var nurseryPressed = false

        cbShelter.setOnCheckedChangeListener { _, isPressed ->
            shelterPressed = isPressed
            if (isPressed) {
                cbNursery.isChecked = false
            }
        }
        cbNursery.setOnCheckedChangeListener { _, isPressed ->
            nurseryPressed = isPressed
            if (isPressed) {
                cbShelter.isChecked = false
            }
        }

        val prefs = requireActivity().getSharedPreferences("credentials",
            Context.MODE_PRIVATE)
        val owner = prefs.getString("login", "не указано").toString()

        btnAddPlace.setOnClickListener {
            val name = etNameNewPlace.text.toString()
            val address = etAddressNewPlace.text.toString()
            val phone = etPhoneNewPlace.text.toString()
            val description = etDescriptionNewPlace.text.toString()

            if (
                name.isNotEmpty() &&
                address.isNotEmpty() &&
                phone.isNotEmpty() &&
                description.isNotEmpty()
            ) {
                if (shelterPressed || nurseryPressed) {
                    binding.progressBar.visibility = View.VISIBLE
                    if (shelterPressed) {
                        addPlace(
                            AuthModels.PlaceDTO(
                                address = address,
                                name = name,
                                phone = phone,
                                description = description,
                                owner = owner
                            ), true
                        )
                    } else {
                        addPlace(
                            AuthModels.PlaceDTO(
                                address = address,
                                name = name,
                                phone = phone,
                                description = description,
                                owner = owner
                            ), false
                        )
                    }
                    binding.progressBar.visibility = View.GONE
                } else {
                    Toast.makeText(activity, "Выберите тип места", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(activity, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun addPlace(placeDTO: AuthModels.PlaceDTO, putInShelter: Boolean) {
        if (putInShelter) {
            val call = ApiClient.authApi.addShelter(
                placeDTO
            )
            insertPlaceData(call)
        } else {
            val call = ApiClient.authApi.addNursery(
                placeDTO
            )
            insertPlaceData(call)
        }
    }

    private fun insertPlaceData(call: Call<AuthModels.AuthResponse>) {
        call.enqueue(object : Callback<AuthModels.AuthResponse> {
            override fun onResponse(
                call: Call<AuthModels.AuthResponse>,
                response: Response<AuthModels.AuthResponse>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Место добавлено", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_addPlaceFragment_to_profileFragment)
                } else {
                    Toast.makeText(requireContext(), "Ошибка добавления места", Toast.LENGTH_SHORT).show()
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