package com.vetuslugi

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.vetuslugi.databinding.CardInfoBinding
import com.vetuslugi.databinding.FragmentInfoBinding
import com.vetuslugi.domain.model.Place
import com.vetuslugi.presentation.viewmodel.InfoViewModel
import com.vetuslugi.presentation.viewmodel.SharedAnimalViewModel
import com.vetuslugi.presentation.viewmodel.SharedPlaceViewModel
import kotlinx.coroutines.launch

class InfoFragment : Fragment() {

    private var _binding: FragmentInfoBinding? = null
    private val binding get() = _binding!!

    private val sharedPlaceViewModel: SharedPlaceViewModel by activityViewModels()
    private val sharedAnimalViewModel: SharedAnimalViewModel by activityViewModels()
    private val viewModel: InfoViewModel by viewModels {
        (requireActivity().application as VetUslugiApp).container.infoViewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val selection = sharedPlaceViewModel.selection.value ?: return
        val place = selection.place

        binding.tvPlaceTitle.text = selection.type.label

        binding.addressCard.tvCardTitle.text = "Адрес"
        binding.addressCard.tvDescriptionCard.text = place.address
        binding.addressCard.tvEditCard.visibility = View.GONE

        binding.nameCard.tvCardTitle.text = "Название"
        binding.nameCard.tvDescriptionCard.text = place.name

        binding.phoneCard.tvCardTitle.text = "Контактный телефон"
        binding.phoneCard.tvDescriptionCard.text = place.phone

        binding.descriptionCard.tvCardTitle.text = "Описание"
        binding.descriptionCard.tvDescriptionCard.text = place.description

        // Club card — hidden until clubs are loaded
        binding.clubCard.root.visibility = View.GONE
        binding.clubCard.tvCardTitle.text = "Клуб"
        binding.clubCard.tvEditCard.visibility = View.GONE

        if (!selection.editable) {
            binding.nameCard.tvEditCard.visibility = View.GONE
            binding.phoneCard.tvEditCard.visibility = View.GONE
            binding.descriptionCard.tvEditCard.visibility = View.GONE
            binding.btnSaveChanges.visibility = View.GONE
        }

        if (selection.type == SharedPlaceViewModel.PlaceType.SHELTER ||
            selection.type == SharedPlaceViewModel.PlaceType.NURSERY) {
            binding.btnViewAnimals.visibility = View.VISIBLE
            binding.btnViewAnimals.setOnClickListener {
                sharedAnimalViewModel.setContext(
                    placeAddress = place.address,
                    isNursery = selection.type == SharedPlaceViewModel.PlaceType.NURSERY,
                    editable = selection.editable
                )
                findNavController().navigate(R.id.action_infoFragment_to_animalsFragment)
            }
        }

        binding.nameCard.tvEditCard.setOnClickListener { showEditDialog(binding.nameCard) }
        binding.phoneCard.tvEditCard.setOnClickListener { showEditDialog(binding.phoneCard) }
        binding.descriptionCard.tvEditCard.setOnClickListener { showEditDialog(binding.descriptionCard) }

        binding.btnSaveChanges.setOnClickListener {
            val name = binding.nameCard.tvDescriptionCard.text.toString()
            val phone = binding.phoneCard.tvDescriptionCard.text.toString()
            val description = binding.descriptionCard.tvDescriptionCard.text.toString()
            if (name.isNotEmpty() && phone.isNotEmpty() && description.isNotEmpty()) {
                val updated = Place(place.address, name, phone, description, place.owner, place.clubAddress)
                viewModel.updatePlace(updated, selection.type, selection.fromProfile)
            } else {
                Toast.makeText(activity, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }

        // Resolve and show club name when clubs are loaded
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.clubs.collect { clubs ->
                if (place.clubAddress != null) {
                    val clubName = clubs.find { it.address == place.clubAddress }?.name
                    if (clubName != null) {
                        binding.clubCard.tvDescriptionCard.text = clubName
                        binding.clubCard.root.visibility = View.VISIBLE
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is InfoViewModel.UiState.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is InfoViewModel.UiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        viewModel.resetState()
                        if (state.fromProfile) {
                            findNavController().navigate(R.id.action_infoFragment_to_profileFragment)
                        } else {
                            findNavController().navigate(R.id.action_infoFragment_to_placesFragment)
                        }
                    }
                    is InfoViewModel.UiState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        viewModel.resetState()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun showEditDialog(card: CardInfoBinding) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_text, null)
        val etInput = dialogView.findViewById<EditText>(R.id.etDialogInput)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSave)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)

        etInput.setText(card.tvDescriptionCard.text)
        etInput.setSelection(etInput.text.length)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Редактировать ${card.tvCardTitle.text.toString().lowercase()}")
            .setView(dialogView)
            .create()

        btnSave.setOnClickListener {
            val input = etInput.text.toString()
            if (input.isNotEmpty()) {
                card.tvDescriptionCard.text = input
                dialog.dismiss()
            } else {
                Toast.makeText(activity, "Заполните поле", Toast.LENGTH_SHORT).show()
            }
        }
        btnCancel.setOnClickListener { dialog.dismiss() }

        dialog.window?.setBackgroundDrawable(
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_edittext_dialog)
        )
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
