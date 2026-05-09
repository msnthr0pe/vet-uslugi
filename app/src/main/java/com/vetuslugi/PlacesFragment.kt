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
import com.vetuslugi.adapters.ClubsAdapter
import com.vetuslugi.adapters.NurseriesAdapter
import com.vetuslugi.adapters.SheltersAdapter
import com.vetuslugi.databinding.FragmentPlacesBinding
import com.vetuslugi.presentation.viewmodel.PlacesViewModel
import com.vetuslugi.presentation.viewmodel.PlacesViewModel.Tab
import com.vetuslugi.presentation.viewmodel.SharedPlaceViewModel
import com.vetuslugi.presentation.viewmodel.SharedPlaceViewModel.PlaceType
import kotlinx.coroutines.launch

class PlacesFragment : Fragment() {

    private var _binding: FragmentPlacesBinding? = null
    private val binding get() = _binding!!

    private lateinit var sheltersAdapter: SheltersAdapter
    private lateinit var nurseriesAdapter: NurseriesAdapter
    private lateinit var clubsAdapter: ClubsAdapter

    private val viewModel: PlacesViewModel by viewModels {
        (requireActivity().application as VetUslugiApp).container.placesViewModelFactory
    }
    private val sharedPlaceViewModel: SharedPlaceViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlacesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sheltersAdapter = SheltersAdapter(emptyList()) { shelter ->
            sharedPlaceViewModel.select(shelter, PlaceType.SHELTER, editable = false, fromProfile = false)
            findNavController().navigate(R.id.action_placesFragment_to_infoFragment)
        }
        binding.sheltersRecycler.layoutManager = LinearLayoutManager(activity)
        binding.sheltersRecycler.adapter = sheltersAdapter

        nurseriesAdapter = NurseriesAdapter(emptyList()) { nursery ->
            sharedPlaceViewModel.select(nursery, PlaceType.NURSERY, editable = false, fromProfile = false)
            findNavController().navigate(R.id.action_placesFragment_to_infoFragment)
        }
        binding.nurseriesRecycler.layoutManager = LinearLayoutManager(activity)
        binding.nurseriesRecycler.adapter = nurseriesAdapter

        clubsAdapter = ClubsAdapter(emptyList()) { club ->
            sharedPlaceViewModel.select(club, PlaceType.CLUB, editable = false, fromProfile = false)
            findNavController().navigate(R.id.action_placesFragment_to_infoFragment)
        }
        binding.clubsRecycler.layoutManager = LinearLayoutManager(activity)
        binding.clubsRecycler.adapter = clubsAdapter

        binding.chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            when {
                checkedIds.contains(R.id.chipShelters) -> switchTab(Tab.SHELTERS)
                checkedIds.contains(R.id.chipNurseries) -> switchTab(Tab.NURSERIES)
                checkedIds.contains(R.id.chipClubs) -> switchTab(Tab.CLUBS)
            }
        }

        binding.etSearch.addTextChangedListener {
            viewModel.searchQuery.value = it.toString()
        }

        binding.customBottomBar.iconNews.setOnClickListener {
            findNavController().navigate(R.id.action_placesFragment_to_newsFragment)
        }
        binding.customBottomBar.iconProfile.setOnClickListener {
            findNavController().navigate(R.id.action_placesFragment_to_profileFragment)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.shelters.collect { shelters ->
                sheltersAdapter.updateList(shelters)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.nurseries.collect { nurseries ->
                nurseriesAdapter.updateList(nurseries)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.clubs.collect { clubs ->
                clubsAdapter.updateList(clubs)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loading.collect { loading ->
                binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            }
        }
    }

    private fun switchTab(tab: Tab) {
        viewModel.activeTab.value = tab
        binding.sheltersRecycler.visibility = if (tab == Tab.SHELTERS) View.VISIBLE else View.GONE
        binding.nurseriesRecycler.visibility = if (tab == Tab.NURSERIES) View.VISIBLE else View.GONE
        binding.clubsRecycler.visibility = if (tab == Tab.CLUBS) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
