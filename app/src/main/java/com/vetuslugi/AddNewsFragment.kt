package com.vetuslugi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.vetuslugi.databinding.FragmentAddNewsBinding
import com.vetuslugi.presentation.viewmodel.AddNewsViewModel
import kotlinx.coroutines.launch

class AddNewsFragment : Fragment() {

    private var _binding: FragmentAddNewsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddNewsViewModel by viewModels {
        (requireActivity().application as VetUslugiApp).container.addNewsViewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnPublishNews.setOnClickListener {
            val title = binding.etNewsTitle.text.toString()
            val description = binding.etNewsDescription.text.toString()
            if (title.isNotEmpty() && description.isNotEmpty()) {
                viewModel.addNews(title, description)
            } else {
                Toast.makeText(activity, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is AddNewsViewModel.UiState.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is AddNewsViewModel.UiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), "Запись добавлена", Toast.LENGTH_SHORT).show()
                        viewModel.resetState()
                        findNavController().navigate(R.id.action_addNewsFragment_to_newsFragment)
                    }
                    is AddNewsViewModel.UiState.Error -> {
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
