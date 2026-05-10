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
import com.bumptech.glide.Glide
import com.vetuslugi.databinding.FragmentAnimalPublicDetailBinding
import com.vetuslugi.presentation.viewmodel.AnimalPublicDetailViewModel
import com.vetuslugi.presentation.viewmodel.SharedAnimalViewModel
import com.vetuslugi.presentation.viewmodel.SharedPlaceViewModel
import com.vetuslugi.presentation.viewmodel.SharedPlaceViewModel.PlaceType
import kotlinx.coroutines.launch

class AnimalPublicDetailFragment : Fragment() {

    private var _binding: FragmentAnimalPublicDetailBinding? = null
    private val binding get() = _binding!!

    private val sharedAnimalViewModel: SharedAnimalViewModel by activityViewModels()
    private val sharedPlaceViewModel: SharedPlaceViewModel by activityViewModels()

    private val viewModel: AnimalPublicDetailViewModel by viewModels {
        (requireActivity().application as VetUslugiApp).container.animalPublicDetailViewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnimalPublicDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        val animal = sharedAnimalViewModel.selection.value?.animal ?: return

        Glide.with(this)
            .load(animal.imageUrl)
            .centerCrop()
            .placeholder(R.drawable.nursery)
            .into(binding.ivAnimalPhoto)

        binding.nicknameCard.tvCardTitle.text = "Кличка"
        binding.nicknameCard.tvDescriptionCard.text = animal.nickname
        binding.nicknameCard.tvEditCard.visibility = View.GONE

        binding.speciesCard.tvCardTitle.text = "Вид"
        binding.speciesCard.tvDescriptionCard.text = animal.species
        binding.speciesCard.tvEditCard.visibility = View.GONE

        binding.breedCard.tvCardTitle.text = "Порода"
        binding.breedCard.tvDescriptionCard.text = animal.breed
        binding.breedCard.tvEditCard.visibility = View.GONE

        binding.ageCard.tvCardTitle.text = "Возраст"
        binding.ageCard.tvDescriptionCard.text = "${animal.age} лет"
        binding.ageCard.tvEditCard.visibility = View.GONE

        binding.diseasesCard.tvCardTitle.text = "Болезни"
        binding.diseasesCard.tvDescriptionCard.text = animal.diseases ?: "—"
        binding.diseasesCard.tvEditCard.visibility = View.GONE

        val placeAddress = animal.shelterAddress ?: animal.nurseryAddress
        val isNursery = animal.nurseryAddress != null

        if (placeAddress != null) {
            binding.placeCard.visibility = View.GONE
            viewModel.loadPlace(placeAddress, isNursery)
        } else {
            binding.placeCard.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.placeLoading.collect { loading ->
                binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.place.collect { place ->
                if (place != null) {
                    binding.placeCard.visibility = View.VISIBLE
                    val typeLabel = if (isNursery) "Питомник" else "Приют"
                    binding.tvPlaceLabel.text = typeLabel.uppercase()
                    binding.tvPlaceName.text = place.name
                    binding.tvPlaceAddress.text = place.address

                    binding.placeCard.setOnClickListener {
                        val type = if (isNursery) PlaceType.NURSERY else PlaceType.SHELTER
                        sharedPlaceViewModel.select(place, type, editable = false, fromProfile = false)
                        findNavController().navigate(
                            R.id.action_animalPublicDetailFragment_to_infoFragment
                        )
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
