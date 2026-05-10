package com.vetuslugi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.vetuslugi.adapters.NurseriesAdapter
import com.vetuslugi.databinding.FragmentNurseriesBinding
import com.vetuslugi.presentation.viewmodel.NurseriesViewModel
import com.vetuslugi.presentation.viewmodel.SharedPlaceViewModel
import com.vetuslugi.presentation.viewmodel.SharedPlaceViewModel.PlaceType
import kotlinx.coroutines.launch

class NurseriesFragment : Fragment() {

    private var _binding: FragmentNurseriesBinding? = null
    private val binding get() = _binding!!

    private lateinit var nurseriesAdapter: NurseriesAdapter

    private val viewModel: NurseriesViewModel by viewModels {
        (requireActivity().application as VetUslugiApp).container.nurseriesViewModelFactory
    }
    private val sharedPlaceViewModel: SharedPlaceViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNurseriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nurseriesAdapter = NurseriesAdapter(emptyList()) { nursery ->
            sharedPlaceViewModel.select(nursery, PlaceType.NURSERY, editable = false, fromProfile = false)
            findNavController().navigate(R.id.action_nurseriesFragment_to_infoFragment)
        }
        binding.nurseriesRecycler.layoutManager = LinearLayoutManager(activity)
        binding.nurseriesRecycler.adapter = nurseriesAdapter

        binding.etSearch.addTextChangedListener {
            viewModel.searchQuery.value = it.toString()
        }

        binding.customBottomBar.iconNews.setOnClickListener {
            findNavController().navigate(R.id.action_nurseriesFragment_to_newsFragment)
        }
        binding.customBottomBar.iconPlaces.setOnClickListener {
            findNavController().navigate(R.id.action_nurseriesFragment_to_placesFragment)
        }
        binding.customBottomBar.iconProfile.setOnClickListener {
            findNavController().navigate(R.id.action_nurseriesFragment_to_profileFragment)
        }
        binding.customBottomBar.iconSurvey.setOnClickListener {
            findNavController().navigate(R.id.action_nurseriesFragment_to_surveyHomeFragment)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.nurseries.collect { nurseries ->
                nurseriesAdapter.updateList(nurseries)
                val isEmpty = nurseries.isEmpty()
                val isSearching = viewModel.searchQuery.value.isNotEmpty()
                binding.tvEmptyState.text = if (isSearching) "Ничего не найдено" else "Питомников пока нет"
                binding.tvEmptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
