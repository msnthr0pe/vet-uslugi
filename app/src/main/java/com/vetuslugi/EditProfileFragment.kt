package com.vetuslugi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.vetuslugi.databinding.FragmentEditProfileBinding
import com.vetuslugi.presentation.viewmodel.EditProfileViewModel
import kotlinx.coroutines.launch

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EditProfileViewModel by viewModels {
        (requireActivity().application as VetUslugiApp).container.editProfileViewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        val user = viewModel.getCurrentUser()
        binding.etNameEdit.setText(user?.name)
        binding.etSurnameEdit.setText(user?.surname)
        binding.etPhoneEdit.setText(user?.phone)

        binding.btnChangeData.setOnClickListener {
            val name = binding.etNameEdit.text.toString()
            val surname = binding.etSurnameEdit.text.toString()
            val phone = binding.etPhoneEdit.text.toString()
            val password = binding.etConfirmationPassword.text.toString()

            if (name.isNotEmpty() && surname.isNotEmpty() && phone.isNotEmpty() && password.isNotEmpty()) {
                viewModel.updateUser(name, surname, phone, password)
            } else {
                Toast.makeText(activity, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is EditProfileViewModel.UiState.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is EditProfileViewModel.UiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), "Информация обновлена", Toast.LENGTH_SHORT).show()
                        viewModel.resetState()
                        findNavController().navigate(R.id.action_editProfileFragment_to_profileInfoFragment)
                    }
                    is EditProfileViewModel.UiState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        viewModel.resetState()
                    }
                    else -> binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
