package com.vetuslugi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.vetuslugi.databinding.FragmentSurveyBinding
import com.vetuslugi.presentation.viewmodel.SurveyViewModel
import kotlinx.coroutines.launch

class SurveyFragment : Fragment() {

    private var _binding: FragmentSurveyBinding? = null
    private val binding get() = _binding!!

    private val surveyViewModel: SurveyViewModel by activityViewModels {
        (requireActivity().application as VetUslugiApp).container.surveyViewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSurveyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        binding.btnSubmitSurvey.setOnClickListener {
            val species = binding.etSpecies.text.toString().trim()
            val breed = binding.etBreed.text.toString().trim()

            if (species.isBlank() || breed.isBlank()) {
                Toast.makeText(requireContext(), "Введите вид и породу", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val willingToAdoptSick = binding.rbSickYes.isChecked

            val mostImportant = when {
                binding.rbImportantBreed.isChecked -> "breed"
                binding.rbImportantHealth.isChecked -> "health"
                else -> "species"
            }

            surveyViewModel.submitSurvey(species, breed, willingToAdoptSick, mostImportant)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            surveyViewModel.uiState.collect { state ->
                when (state) {
                    is SurveyViewModel.UiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.btnSubmitSurvey.isEnabled = false
                    }
                    is SurveyViewModel.UiState.Submitted -> {
                        binding.progressBar.visibility = View.GONE
                        binding.btnSubmitSurvey.isEnabled = true
                        surveyViewModel.resetState()
                        findNavController().popBackStack()
                    }
                    is SurveyViewModel.UiState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.btnSubmitSurvey.isEnabled = true
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                        surveyViewModel.resetState()
                    }
                    else -> {
                        binding.progressBar.visibility = View.GONE
                        binding.btnSubmitSurvey.isEnabled = true
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
