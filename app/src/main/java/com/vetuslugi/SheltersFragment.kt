package com.vetuslugi

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vetuslugi.adapters.SheltersAdapter
import com.vetuslugi.databinding.FragmentSheltersBinding
import com.vetuslugi.ktor.ApiClient
import com.vetuslugi.ktor.AuthModels
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SheltersFragment : Fragment() {

    private var _binding: FragmentSheltersBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var requestAdapter: SheltersAdapter

    private lateinit var searchEditText: EditText
    private val searchQuery = MutableStateFlow("")

    private var originalShelters: List<AuthModels.PlaceDTO> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @OptIn(FlowPreview::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSheltersBinding.inflate(layoutInflater, container, false)

        recyclerView = binding.sheltersRecycler
        recyclerView.layoutManager = LinearLayoutManager(activity)

        val fragmentPrefs = requireActivity().getSharedPreferences("fragment_prefs",
            Context.MODE_PRIVATE)
        fragmentPrefs.edit().apply {
            putBoolean("fromProfile", false)
            apply()
        }

        lifecycleScope.launch {
            try {
                val shelters = withContext(Dispatchers.IO) {
                    ApiClient.authApi.getShelters()
                }
                originalShelters = shelters
                requestAdapter = SheltersAdapter(shelters) {shelter ->
                    val prefs = requireActivity().getSharedPreferences("place_prefs",
                        Context.MODE_PRIVATE)
                    prefs.edit().apply {
                        putString("address", shelter.address)
                        putString("name", shelter.name)
                        putString("phone", shelter.phone)
                        putString("description", shelter.description)
                        putString("owner", shelter.owner)
                        putString("place", "приюте")
                        putBoolean("editable", false)
                        apply()
                    }
                    findNavController().navigate(R.id.action_sheltersFragment_to_infoFragment)
                }
                recyclerView.adapter = requestAdapter

            } catch (e: Exception) {
                Log.e("MYCLIENT", "Ошибка: ${e.message}")
            }
        }

        searchEditText = binding.etSearch

        searchEditText.addTextChangedListener {
            searchQuery.value = it.toString()
        }

        lifecycleScope.launch {
            searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .flowOn(Dispatchers.Default)
                .collectLatest { query ->
                    filterCards(query)
                }
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

        return binding.root
    }

    private fun filterCards(query: String) {
        val filtered = if (query.isEmpty()) {
            originalShelters
        } else {
            originalShelters.filter { it.name.contains(query, ignoreCase = true) }
        }
        try {
            requestAdapter.updateList(filtered)
        } catch(_: Exception) {
            Log.e("VETUSLUGI", "Exception occurred")
        }
    }

    companion object {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}