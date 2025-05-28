package com.vetuslugi

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.vetuslugi.databinding.FragmentAddPlaceBinding
import com.vetuslugi.databinding.FragmentEditProfileBinding
import com.vetuslugi.databinding.FragmentProfileInfoBinding
import com.vetuslugi.databinding.FragmentTitleBinding
import kotlinx.coroutines.launch

class ProfileInfoFragment : Fragment() {

    private var _binding: FragmentProfileInfoBinding? = null
    private val binding get() = _binding!!

    lateinit var etName: EditText
    lateinit var etSurname: EditText
    lateinit var etPhone: EditText
    lateinit var etLogin: EditText
    lateinit var tvRole: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileInfoBinding.inflate(layoutInflater, container, false)

        etName = binding.etName
        etSurname = binding.etSurname
        etPhone = binding.etPhone
        etLogin = binding.etLogin
        tvRole = binding.tvRole

        val prefs = requireContext().getSharedPreferences("credentials", Context.MODE_PRIVATE)
        val name = prefs.getString("name", "-")
        val surname = prefs.getString("surname", "-")
        val phone = prefs.getString("phone", "-")
        val login = prefs.getString("login", "-")
        val role = prefs.getString("role", "-")

        etName.setText(name)
        etSurname.setText(surname)
        etPhone.setText(phone)
        etLogin.setText(login)
        tvRole.text = role

        binding.btnEditInformation.setOnClickListener {
            findNavController().navigate(R.id.action_profileInfoFragment_to_editProfileFragment)
        }

        binding.btnLogOut.setOnClickListener {
            findNavController().navigate(R.id.action_profileInfoFragment_to_titleFragment)
        }

        binding.customBottomBar.iconNews.setOnClickListener {
            findNavController().navigate(R.id.action_profileInfoFragment_to_newsFragment)
        }

        binding.customBottomBar.iconShelter.setOnClickListener {
            findNavController().navigate(R.id.action_profileInfoFragment_to_sheltersFragment)
        }

        binding.customBottomBar.iconNursery.setOnClickListener {
            findNavController().navigate(R.id.action_profileInfoFragment_to_nurseriesFragment)
        }

        return binding.root
    }

    companion object {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}