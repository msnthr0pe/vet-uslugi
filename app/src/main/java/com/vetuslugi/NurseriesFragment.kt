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
import com.vetuslugi.adapters.NurseriesAdapter
import com.vetuslugi.databinding.FragmentNurseriesBinding
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

class NurseriesFragment : Fragment() {

    private var _binding: FragmentNurseriesBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var requestAdapter: NurseriesAdapter

    private lateinit var searchEditText: EditText
    private val searchQuery = MutableStateFlow("")

    private var originalNurseries: List<AuthModels.PlaceDTO> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @OptIn(FlowPreview::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNurseriesBinding.inflate(layoutInflater, container, false)

        recyclerView = binding.nurseriesRecycler
        recyclerView.layoutManager = LinearLayoutManager(activity)

        val fragmentPrefs = requireActivity().getSharedPreferences("fragment_prefs",
            Context.MODE_PRIVATE)
        fragmentPrefs.edit().apply {
            putBoolean("fromProfile", false)
            apply()
        }

        lifecycleScope.launch {
            try {
                val nurseries = withContext(Dispatchers.IO) {
                    ApiClient.authApi.getNurseries()
                }
                originalNurseries = nurseries
                requestAdapter = NurseriesAdapter(nurseries) {nursery ->
                    val prefs = requireActivity().getSharedPreferences("place_prefs",
                        Context.MODE_PRIVATE)
                    prefs.edit().apply {
                        putString("address", nursery.address)
                        putString("name", nursery.name)
                        putString("phone", nursery.phone)
                        putString("description", nursery.description)
                        putString("owner", nursery.owner)
                        putString("place", "питомнике")
                        putBoolean("editable", false)
                        apply()
                    }
                    findNavController().navigate(R.id.action_nurseriesFragment_to_infoFragment)
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
            findNavController().navigate(R.id.action_nurseriesFragment_to_newsFragment)
        }

        binding.customBottomBar.iconShelter.setOnClickListener {
            findNavController().navigate(R.id.action_nurseriesFragment_to_sheltersFragment)
        }

        binding.customBottomBar.iconProfile.setOnClickListener {
            findNavController().navigate(R.id.action_nurseriesFragment_to_profileFragment)
        }

        return binding.root
    }

    private fun filterCards(query: String) {
        val filtered = if (query.isEmpty()) {
            originalNurseries
        } else {
            originalNurseries.filter { it.name.contains(query, ignoreCase = true) }
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