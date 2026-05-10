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
import com.vetuslugi.databinding.FragmentAddClubBinding
import com.vetuslugi.presentation.viewmodel.AddClubViewModel
import kotlinx.coroutines.launch

class AddClubFragment : Fragment() {

    private var _binding: FragmentAddClubBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddClubViewModel by viewModels {
        (requireActivity().application as VetUslugiApp).container.addClubViewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddClubBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        binding.btnCreateClub.setOnClickListener {
            val name = binding.etClubName.text.toString().trim()
            val address = binding.etClubAddress.text.toString().trim()
            val phone = binding.etClubPhone.text.toString().trim()
            val description = binding.etClubDescription.text.toString().trim()

            if (name.isEmpty() || address.isEmpty() || phone.isEmpty() || description.isEmpty()) {
                Toast.makeText(activity, "Заполните все поля", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.addClub(name, address, phone, description)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is AddClubViewModel.UiState.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is AddClubViewModel.UiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), "Клуб создан", Toast.LENGTH_SHORT).show()
                        viewModel.resetState()
                        findNavController().navigate(R.id.action_addClubFragment_to_placesFragment)
                    }
                    is AddClubViewModel.UiState.Error -> {
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
