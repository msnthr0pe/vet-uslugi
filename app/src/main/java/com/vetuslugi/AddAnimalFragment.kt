package com.vetuslugi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.vetuslugi.databinding.FragmentAddAnimalBinding
import com.vetuslugi.presentation.viewmodel.AddAnimalViewModel
import com.vetuslugi.presentation.viewmodel.SharedAnimalViewModel
import kotlinx.coroutines.launch

class AddAnimalFragment : Fragment() {

    private var _binding: FragmentAddAnimalBinding? = null
    private val binding get() = _binding!!

    private val sharedAnimalViewModel: SharedAnimalViewModel by activityViewModels()
    private val viewModel: AddAnimalViewModel by viewModels {
        (requireActivity().application as VetUslugiApp).container.addAnimalViewModelFactory
    }

    private var selectedImageBytes: ByteArray? = null
    private var selectedImageName: String = "photo.jpg"

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri ?: return@registerForActivityResult
        val b = _binding ?: return@registerForActivityResult
        val cr = requireContext().contentResolver
        selectedImageBytes = cr.openInputStream(uri)?.readBytes()
        val mimeType = cr.getType(uri) ?: "image/jpeg"
        val ext = android.webkit.MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: "jpg"
        selectedImageName = "photo_${System.currentTimeMillis()}.$ext"
        Glide.with(this).load(uri).centerCrop().into(b.ivAnimalPhoto)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddAnimalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        val ctx = sharedAnimalViewModel.context.value ?: return

        binding.btnSelectPhoto.setOnClickListener { pickImage.launch("image/*") }

        binding.btnAddAnimalSubmit.setOnClickListener {
            val nickname = binding.etAnimalNickname.text.toString().trim()
            val species = binding.etAnimalSpecies.text.toString().trim()
            val breed = binding.etAnimalBreed.text.toString().trim()
            val ageText = binding.etAnimalAge.text.toString().trim()
            val diseasesText = binding.etAnimalDiseases.text.toString().trim()

            if (nickname.isEmpty() || species.isEmpty() || breed.isEmpty() || ageText.isEmpty()) {
                Toast.makeText(activity, "Заполните обязательные поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val age = ageText.toIntOrNull()
            if (age == null) {
                Toast.makeText(activity, "Возраст должен быть числом", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val diseases = diseasesText.ifEmpty { null }
            val shelterAddress = if (!ctx.isNursery) ctx.placeAddress else null
            val nurseryAddress = if (ctx.isNursery) ctx.placeAddress else null
            viewModel.addAnimal(nickname, species, breed, age, diseases, shelterAddress, nurseryAddress, selectedImageBytes, selectedImageName)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is AddAnimalViewModel.UiState.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is AddAnimalViewModel.UiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), "Животное добавлено", Toast.LENGTH_SHORT).show()
                        viewModel.resetState()
                        findNavController().popBackStack()
                    }
                    is AddAnimalViewModel.UiState.Error -> {
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
