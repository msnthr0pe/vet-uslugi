package com.vetuslugi

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vetuslugi.adapters.SheltersAdapter
import com.vetuslugi.databinding.FragmentSheltersBinding
import com.vetuslugi.ktor.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SheltersFragment : Fragment() {

    private var _binding: FragmentSheltersBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var requestAdapter: SheltersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSheltersBinding.inflate(layoutInflater, container, false)

        recyclerView = binding.sheltersRecycler
        recyclerView.layoutManager = LinearLayoutManager(activity)

        lifecycleScope.launch {
            try {
                val shelters = withContext(Dispatchers.IO) {
                    ApiClient.authApi.getShelters()
                }
                requestAdapter = SheltersAdapter(shelters) {shelter ->
                    val prefs = requireActivity().getSharedPreferences("place_prefs",
                        Context.MODE_PRIVATE)
                    prefs.edit().apply {
                        putString("address", shelter.address)
                        putString("name", shelter.name)
                        putString("phone", shelter.phone)
                        putString("description", shelter.description)
                        putString("place", "приюте")
                        apply()
                    }
                    findNavController().navigate(R.id.action_sheltersFragment_to_infoFragment)
                }
                recyclerView.adapter = requestAdapter

            } catch (e: Exception) {
                Log.e("MYCLIENT", "Ошибка: ${e.message}")
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

    companion object {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}