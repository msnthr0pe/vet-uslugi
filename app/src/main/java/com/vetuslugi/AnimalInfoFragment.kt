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
import com.vetuslugi.databinding.FragmentAnimalInfoBinding
import com.vetuslugi.domain.model.Animal
import com.vetuslugi.presentation.viewmodel.AnimalInfoViewModel
import com.vetuslugi.presentation.viewmodel.SharedAnimalViewModel
import kotlinx.coroutines.launch

class AnimalInfoFragment : Fragment() {

    private var _binding: FragmentAnimalInfoBinding? = null
    private val binding get() = _binding!!

    private val sharedAnimalViewModel: SharedAnimalViewModel by activityViewModels()
    private val viewModel: AnimalInfoViewModel by viewModels {
        (requireActivity().application as VetUslugiApp).container.animalInfoViewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnimalInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val selection = sharedAnimalViewModel.selection.value ?: return
        val animal = selection.animal

        binding.nicknameCard.tvCardTitle.text = "Кличка"
        binding.nicknameCard.tvDescriptionCard.text = animal.nickname

        binding.speciesCard.tvCardTitle.text = "Вид"
        binding.speciesCard.tvDescriptionCard.text = animal.species

        binding.breedCard.tvCardTitle.text = "Порода"
        binding.breedCard.tvDescriptionCard.text = animal.breed

        binding.ageCard.tvCardTitle.text = "Возраст"
        binding.ageCard.tvDescriptionCard.text = animal.age.toString()

        binding.diseasesCard.tvCardTitle.text = "Болезни"
        binding.diseasesCard.tvDescriptionCard.text = animal.diseases ?: "—"

        if (selection.editable) {
            binding.btnSaveAnimal.visibility = View.VISIBLE
            binding.btnDeleteAnimal.visibility = View.VISIBLE
            binding.nicknameCard.tvEditCard.setOnClickListener { showEditDialog(binding.nicknameCard) }
            binding.speciesCard.tvEditCard.setOnClickListener { showEditDialog(binding.speciesCard) }
            binding.breedCard.tvEditCard.setOnClickListener { showEditDialog(binding.breedCard) }
            binding.ageCard.tvEditCard.setOnClickListener { showEditDialog(binding.ageCard) }
            binding.diseasesCard.tvEditCard.setOnClickListener { showEditDialog(binding.diseasesCard) }
        } else {
            binding.nicknameCard.tvEditCard.visibility = View.GONE
            binding.speciesCard.tvEditCard.visibility = View.GONE
            binding.breedCard.tvEditCard.visibility = View.GONE
            binding.ageCard.tvEditCard.visibility = View.GONE
            binding.diseasesCard.tvEditCard.visibility = View.GONE
        }

        binding.btnSaveAnimal.setOnClickListener {
            val nickname = binding.nicknameCard.tvDescriptionCard.text.toString()
            val species = binding.speciesCard.tvDescriptionCard.text.toString()
            val breed = binding.breedCard.tvDescriptionCard.text.toString()
            val ageText = binding.ageCard.tvDescriptionCard.text.toString()
            val diseasesText = binding.diseasesCard.tvDescriptionCard.text.toString()

            if (nickname.isEmpty() || species.isEmpty() || breed.isEmpty() || ageText.isEmpty()) {
                Toast.makeText(activity, "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val age = ageText.toIntOrNull()
            if (age == null) {
                Toast.makeText(activity, "Возраст должен быть числом", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val diseases = if (diseasesText == "—" || diseasesText.isEmpty()) null else diseasesText
            viewModel.updateAnimal(animal.copy(nickname = nickname, species = species, breed = breed, age = age, diseases = diseases))
        }

        binding.btnDeleteAnimal.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Удалить животное")
                .setMessage("Вы уверены, что хотите удалить это животное?")
                .setPositiveButton("Удалить") { _, _ -> viewModel.deleteAnimal(animal.id) }
                .setNegativeButton("Отмена", null)
                .show()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is AnimalInfoViewModel.UiState.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is AnimalInfoViewModel.UiState.Updated -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), "Изменения сохранены", Toast.LENGTH_SHORT).show()
                        viewModel.resetState()
                        findNavController().popBackStack()
                    }
                    is AnimalInfoViewModel.UiState.Deleted -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), "Животное удалено", Toast.LENGTH_SHORT).show()
                        viewModel.resetState()
                        findNavController().popBackStack()
                    }
                    is AnimalInfoViewModel.UiState.Error -> {
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
            card.tvDescriptionCard.text = input
            dialog.dismiss()
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
