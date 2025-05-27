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
import com.vetuslugi.adapters.NurseriesAdapter
import com.vetuslugi.databinding.FragmentNurseriesBinding
import com.vetuslugi.ktor.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NurseriesFragment : Fragment() {

    private var _binding: FragmentNurseriesBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var requestAdapter: NurseriesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNurseriesBinding.inflate(layoutInflater, container, false)

        recyclerView = binding.nurseriesRecycler
        recyclerView.layoutManager = LinearLayoutManager(activity)

        lifecycleScope.launch {
            try {
                val nurseries = withContext(Dispatchers.IO) {
                    ApiClient.authApi.getNurseries()
                }
                requestAdapter = NurseriesAdapter(nurseries) {nursery ->
                    val prefs = requireActivity().getSharedPreferences("place_prefs",
                        Context.MODE_PRIVATE)
                    prefs.edit().apply {
                        putString("address", nursery.address)
                        putString("name", nursery.name)
                        putString("phone", nursery.phone)
                        putString("description", nursery.description)
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

    companion object {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}