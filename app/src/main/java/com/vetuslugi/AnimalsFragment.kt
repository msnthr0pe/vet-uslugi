package com.vetuslugi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.vetuslugi.adapters.AnimalsAdapter
import com.vetuslugi.databinding.FragmentAnimalsBinding
import com.vetuslugi.presentation.viewmodel.AnimalsViewModel
import com.vetuslugi.presentation.viewmodel.SharedAnimalViewModel
import kotlinx.coroutines.launch

class AnimalsFragment : Fragment() {

    private var _binding: FragmentAnimalsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: AnimalsAdapter

    private val sharedAnimalViewModel: SharedAnimalViewModel by activityViewModels()
    private val viewModel: AnimalsViewModel by viewModels {
        (requireActivity().application as VetUslugiApp).container.animalsViewModelFactory
    }

    private var animalCtx: SharedAnimalViewModel.AnimalContext? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnimalsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        adapter = AnimalsAdapter(emptyList()) { animal ->
            val ctx = sharedAnimalViewModel.context.value ?: return@AnimalsAdapter
            sharedAnimalViewModel.selectAnimal(animal, editable = ctx.editable)
            findNavController().navigate(R.id.action_animalsFragment_to_animalInfoFragment)
        }
        binding.animalsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.animalsRecycler.adapter = adapter

        val ctx = sharedAnimalViewModel.context.value ?: return
        animalCtx = ctx

        viewModel.startObserving(ctx.placeAddress, ctx.isNursery)

        if (ctx.editable) binding.btnAddAnimal.visibility = View.VISIBLE

        binding.btnAddAnimal.setOnClickListener {
            findNavController().navigate(R.id.action_animalsFragment_to_addAnimalFragment)
        }

        binding.swipeRefresh.setColorSchemeResources(R.color.blue)
        binding.swipeRefresh.setOnRefreshListener {
            val c = animalCtx ?: run { binding.swipeRefresh.isRefreshing = false; return@setOnRefreshListener }
            viewModel.syncFromApi(c.placeAddress, c.isNursery)
        }

        viewModel.searchQuery.value = ""
        binding.etAnimalSearch.addTextChangedListener {
            viewModel.searchQuery.value = it.toString()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.animals.collect { animals ->
                adapter.updateList(animals)
                updateEmptyState(animals.isEmpty())
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loading.collect { loading ->
                binding.swipeRefresh.isRefreshing = loading
                if (!loading) updateEmptyState(viewModel.animals.value.isEmpty())
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.error.collect { error ->
                if (error != null) {
                    Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
                    viewModel.clearError()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val ctx = animalCtx ?: sharedAnimalViewModel.context.value ?: return
        if (animalCtx == null) {
            animalCtx = ctx
            viewModel.startObserving(ctx.placeAddress, ctx.isNursery)
        }
        viewModel.syncFromApi(ctx.placeAddress, ctx.isNursery)
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        if (viewModel.loading.value) return
        val query = viewModel.searchQuery.value
        binding.tvEmptyAnimals.text =
            if (query.isNotEmpty()) "Ничего не найдено" else "Животных пока нет"
        binding.tvEmptyAnimals.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
