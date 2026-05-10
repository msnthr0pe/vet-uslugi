package com.vetuslugi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.vetuslugi.adapters.AnimalsAdapter
import com.vetuslugi.adapters.SurveyResultAdapter
import com.vetuslugi.databinding.FragmentSurveyHomeBinding
import com.vetuslugi.presentation.viewmodel.SharedAnimalViewModel
import com.vetuslugi.presentation.viewmodel.SurveyViewModel
import kotlinx.coroutines.launch

class SurveyHomeFragment : Fragment() {

    private var _binding: FragmentSurveyHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var allAnimalsAdapter: AnimalsAdapter
    private lateinit var surveyResultAdapter: SurveyResultAdapter

    private val surveyViewModel: SurveyViewModel by activityViewModels {
        (requireActivity().application as VetUslugiApp).container.surveyViewModelFactory
    }
    private val sharedAnimalViewModel: SharedAnimalViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSurveyHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        allAnimalsAdapter = AnimalsAdapter(emptyList()) { animal ->
            sharedAnimalViewModel.selectAnimal(animal, editable = false)
            findNavController().navigate(R.id.action_surveyHomeFragment_to_animalPublicDetailFragment)
        }

        surveyResultAdapter = SurveyResultAdapter(emptyList()) { result ->
            sharedAnimalViewModel.selectAnimal(result.animal, editable = false)
            findNavController().navigate(R.id.action_surveyHomeFragment_to_animalPublicDetailFragment)
        }

        binding.surveyResultsRecycler.layoutManager = LinearLayoutManager(requireContext())

        binding.btnStartSurvey.setOnClickListener {
            findNavController().navigate(R.id.action_surveyHomeFragment_to_surveyFragment)
        }

        binding.customBottomBar5.iconNews.setOnClickListener {
            findNavController().navigate(R.id.action_surveyHomeFragment_to_newsFragment)
        }
        binding.customBottomBar5.iconPlaces.setOnClickListener {
            findNavController().navigate(R.id.action_surveyHomeFragment_to_placesFragment)
        }
        binding.customBottomBar5.iconProfile.setOnClickListener {
            findNavController().navigate(R.id.action_surveyHomeFragment_to_profileFragment)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            surveyViewModel.uiState.collect { state ->
                when (state) {
                    is SurveyViewModel.UiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.tvSurveyEmpty.visibility = View.GONE
                    }
                    else -> binding.progressBar.visibility = View.GONE
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            surveyViewModel.results.collect { results ->
                if (results.isNotEmpty()) {
                    surveyResultAdapter.updateList(results)
                    binding.surveyResultsRecycler.adapter = surveyResultAdapter
                    binding.surveyResultsRecycler.visibility = View.VISIBLE
                    binding.tvSurveyEmpty.visibility = View.GONE
                } else {
                    binding.surveyResultsRecycler.adapter = allAnimalsAdapter
                    val animals = surveyViewModel.allAnimals.value
                    allAnimalsAdapter.updateList(animals)
                    if (animals.isEmpty()) {
                        binding.surveyResultsRecycler.visibility = View.GONE
                        binding.tvSurveyEmpty.visibility = View.VISIBLE
                    } else {
                        binding.surveyResultsRecycler.visibility = View.VISIBLE
                        binding.tvSurveyEmpty.visibility = View.GONE
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            surveyViewModel.allAnimals.collect { animals ->
                if (surveyViewModel.results.value.isEmpty()) {
                    allAnimalsAdapter.updateList(animals)
                    binding.surveyResultsRecycler.adapter = allAnimalsAdapter
                    if (animals.isEmpty()) {
                        binding.surveyResultsRecycler.visibility = View.GONE
                        binding.tvSurveyEmpty.visibility = View.VISIBLE
                    } else {
                        binding.surveyResultsRecycler.visibility = View.VISIBLE
                        binding.tvSurveyEmpty.visibility = View.GONE
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
