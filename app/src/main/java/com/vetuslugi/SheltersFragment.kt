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
import com.vetuslugi.adapters.SheltersAdapter
import com.vetuslugi.databinding.FragmentSheltersBinding
import com.vetuslugi.presentation.viewmodel.SharedPlaceViewModel
import com.vetuslugi.presentation.viewmodel.SharedPlaceViewModel.PlaceType
import com.vetuslugi.presentation.viewmodel.SheltersViewModel
import kotlinx.coroutines.launch

class SheltersFragment : Fragment() {

    private var _binding: FragmentSheltersBinding? = null
    private val binding get() = _binding!!

    private lateinit var sheltersAdapter: SheltersAdapter

    private val viewModel: SheltersViewModel by viewModels {
        (requireActivity().application as VetUslugiApp).container.sheltersViewModelFactory
    }
    private val sharedPlaceViewModel: SharedPlaceViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSheltersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sheltersAdapter = SheltersAdapter(emptyList()) { shelter ->
            sharedPlaceViewModel.select(shelter, PlaceType.SHELTER, editable = false, fromProfile = false)
            findNavController().navigate(R.id.action_sheltersFragment_to_infoFragment)
        }
        binding.sheltersRecycler.layoutManager = LinearLayoutManager(activity)
        binding.sheltersRecycler.adapter = sheltersAdapter

        binding.etSearch.addTextChangedListener {
            viewModel.searchQuery.value = it.toString()
        }

        binding.customBottomBar.iconNews.setOnClickListener {
            findNavController().navigate(R.id.action_sheltersFragment_to_newsFragment)
        }
        binding.customBottomBar.iconNursery.setOnClickListener {
            findNavController().navigate(R.id.action_sheltersFragment_to_nurseriesFragment)
        }
        binding.customBottomBar.iconProfile.setOnClickListener {
            findNavController().navigate(R.id.action_sheltersFragment_to_profileFragment)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.shelters.collect { shelters ->
                sheltersAdapter.updateList(shelters)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
