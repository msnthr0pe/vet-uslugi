package com.vetuslugi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.vetuslugi.adapters.NurseriesAdapter
import com.vetuslugi.adapters.SheltersAdapter
import com.vetuslugi.databinding.FragmentProfileBinding
import com.vetuslugi.presentation.viewmodel.ProfileViewModel
import com.vetuslugi.presentation.viewmodel.SharedPlaceViewModel
import com.vetuslugi.presentation.viewmodel.SharedPlaceViewModel.PlaceType
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var sheltersAdapter: SheltersAdapter
    private lateinit var nurseriesAdapter: NurseriesAdapter

    private val viewModel: ProfileViewModel by viewModels {
        (requireActivity().application as VetUslugiApp).container.profileViewModelFactory
    }
    private val sharedPlaceViewModel: SharedPlaceViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = viewModel.currentUser
        if (user?.role == "user") {
            findNavController().navigate(R.id.action_profileFragment_to_profileInfoFragment)
            return
        }

        binding.tvNameUser.text = user?.name
        binding.tvSurnameUser.text = user?.surname

        sheltersAdapter = SheltersAdapter(emptyList()) { shelter ->
            sharedPlaceViewModel.select(shelter, PlaceType.SHELTER, editable = true, fromProfile = true)
            findNavController().navigate(R.id.action_profileFragment_to_infoFragment)
        }
        binding.shelterRecycler.layoutManager = LinearLayoutManager(activity)
        binding.shelterRecycler.adapter = sheltersAdapter

        nurseriesAdapter = NurseriesAdapter(emptyList()) { nursery ->
            sharedPlaceViewModel.select(nursery, PlaceType.NURSERY, editable = true, fromProfile = true)
            findNavController().navigate(R.id.action_profileFragment_to_infoFragment)
        }
        binding.nurseryRecycler.layoutManager = LinearLayoutManager(activity)
        binding.nurseryRecycler.adapter = nurseriesAdapter

        binding.tvGetInfo.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_profileInfoFragment)
        }
        binding.btnAddPlace.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_addPlaceFragment)
        }

        binding.customBottomBar4.iconNews.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_newsFragment)
        }
        binding.customBottomBar4.iconPlaces.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_placesFragment)
        }

        viewModel.loadPlaces()

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
