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
import com.vetuslugi.databinding.FragmentRegisterBinding
import com.vetuslugi.presentation.viewmodel.RegisterViewModel
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegisterViewModel by viewModels {
        (requireActivity().application as VetUslugiApp).container.registerViewModelFactory
    }

    private var role = "user"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        binding.cbIsBreeder.setOnCheckedChangeListener { _, isBreeder ->
            role = if (isBreeder) "breeder" else "user"
        }

        binding.btnContinueRegister.setOnClickListener {
            val name = binding.etNameRegister.text.toString()
            val surname = binding.etSurnameRegister.text.toString()
            val phone = binding.etPhoneRegister.text.toString()
            val login = binding.etLoginRegister.text.toString()
            val password = binding.etPasswordRegister.text.toString()

            if (name.isNotEmpty() && surname.isNotEmpty() && phone.isNotEmpty()
                && login.isNotEmpty() && password.isNotEmpty()
            ) {
                viewModel.register(name, surname, phone, login, password, role)
            } else {
                Toast.makeText(activity, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is RegisterViewModel.UiState.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is RegisterViewModel.UiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), "Учётная запись создана", Toast.LENGTH_SHORT).show()
                        viewModel.resetState()
                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                    }
                    is RegisterViewModel.UiState.Error -> {
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
