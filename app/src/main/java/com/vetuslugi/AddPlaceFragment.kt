package com.vetuslugi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.vetuslugi.databinding.FragmentAddPlaceBinding
import com.vetuslugi.domain.model.Place
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
    private var clubsList: List<Place> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPlaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        binding.cbShelter.setOnCheckedChangeListener { _, isChecked ->
            shelterSelected = isChecked
            if (isChecked) binding.cbNursery.isChecked = false
        }
        binding.cbNursery.setOnCheckedChangeListener { _, isChecked ->
            nurserySelected = isChecked
            if (isChecked) binding.cbShelter.isChecked = false
        }

        binding.btnAddNewPlace.setOnClickListener {
            val name = binding.etNameNewPlace.text.toString().trim()
            val address = binding.etAddressNewPlace.text.toString().trim()
            val phone = binding.etPhoneNewPlace.text.toString().trim()
            val description = binding.etDescNewPlace.text.toString().trim()

            if (name.isEmpty() || address.isEmpty() || phone.isEmpty() || description.isEmpty()) {
                Toast.makeText(activity, "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!shelterSelected && !nurserySelected) {
                Toast.makeText(activity, "Выберите тип места", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedPos = binding.spinnerClub.selectedItemPosition
            val clubAddress = if (selectedPos == 0 || clubsList.isEmpty()) null
                              else clubsList[selectedPos - 1].address

            viewModel.addPlace(name, address, phone, description, shelterSelected, clubAddress)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.clubs.collect { clubs ->
                clubsList = clubs
                val items = listOf("Без клуба") + clubs.map { it.name }
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    items
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerClub.adapter = adapter
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
