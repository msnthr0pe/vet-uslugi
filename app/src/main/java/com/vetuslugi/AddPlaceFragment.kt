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
import com.vetuslugi.databinding.FragmentAddPlaceBinding
import com.vetuslugi.presentation.viewmodel.AddPlaceViewModel
import kotlinx.coroutines.launch

class AddPlaceFragment : Fragment() {

    private var _binding: FragmentAddPlaceBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddPlaceViewModel by viewModels {
        (requireActivity().application as VetUslugiApp).container.addPlaceViewModelFactory
    }

    private var shelterSelected = false
    private var nurserySelected = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPlaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cbShelter.setOnCheckedChangeListener { _, isPressed ->
            shelterSelected = isPressed
            if (isPressed) binding.cbNursery.isChecked = false
        }
        binding.cbNursery.setOnCheckedChangeListener { _, isPressed ->
            nurserySelected = isPressed
            if (isPressed) binding.cbShelter.isChecked = false
        }

        binding.btnAddNewPlace.setOnClickListener {
            val name = binding.etNameNewPlace.text.toString()
            val address = binding.etAddressNewPlace.text.toString()
            val phone = binding.etPhoneNewPlace.text.toString()
            val description = binding.etDescNewPlace.text.toString()

            if (name.isNotEmpty() && address.isNotEmpty() && phone.isNotEmpty() && description.isNotEmpty()) {
                if (shelterSelected || nurserySelected) {
                    viewModel.addPlace(name, address, phone, description, shelterSelected)
                } else {
                    Toast.makeText(activity, "Выберите тип места", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(activity, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is AddPlaceViewModel.UiState.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is AddPlaceViewModel.UiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), "Место добавлено", Toast.LENGTH_SHORT).show()
                        viewModel.resetState()
                        findNavController().navigate(R.id.action_addPlaceFragment_to_profileFragment)
                    }
                    is AddPlaceViewModel.UiState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        viewModel.resetState()
                    }
                    else -> {}
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
