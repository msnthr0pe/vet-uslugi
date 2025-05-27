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
import com.vetuslugi.adapters.SheltersAdapter
import com.vetuslugi.databinding.FragmentProfileBinding
import com.vetuslugi.ktor.ApiClient
import com.vetuslugi.ktor.AuthModels
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var sheltersRecyclerView: RecyclerView
    private lateinit var sheltersAdapter: SheltersAdapter
    private lateinit var nurseriesRecyclerView: RecyclerView
    private lateinit var nurseriesAdapter: NurseriesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)

        binding.customBottomBar4.iconNews.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_newsFragment)
        }
        binding.customBottomBar4.iconShelter.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_sheltersFragment)
        }
        binding.customBottomBar4.iconNursery.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_nurseriesFragment)
        }

        val fragmentPrefs = requireActivity().getSharedPreferences("fragment_prefs",
            Context.MODE_PRIVATE)
        fragmentPrefs.edit().apply {
            putBoolean("fromProfile", true)
            apply()
        }

        sheltersRecyclerView = binding.shelterRecycler
        sheltersRecyclerView.layoutManager = LinearLayoutManager(activity)
        nurseriesRecyclerView = binding.nurseryRecycler
        nurseriesRecyclerView.layoutManager = LinearLayoutManager(activity)

        val prefs = requireActivity().getSharedPreferences("credentials",
            Context.MODE_PRIVATE)
        val login = prefs.getString("login", "не указано")

        lifecycleScope.launch {
            try {
                if (login != null) {
                    val shelters = withContext(Dispatchers.IO) {
                        ApiClient.authApi.getShelterBy(
                            AuthModels.InfoDTO(
                                login
                            )
                        )
                    }

                    val nurseries = withContext(Dispatchers.IO) {
                        ApiClient.authApi.getNurseryBy(
                            AuthModels.InfoDTO(
                                login
                            )
                        )
                    }

                    sheltersAdapter = SheltersAdapter(shelters) {shelter ->
                        putPrefs(shelter, "приюте")
                        findNavController().navigate(R.id.action_profileFragment_to_infoFragment)
                    }
                    sheltersRecyclerView.adapter = sheltersAdapter

                    nurseriesAdapter = NurseriesAdapter(nurseries) { nursery ->
                        putPrefs(nursery, "питомнике")
                        findNavController().navigate(R.id.action_profileFragment_to_infoFragment)
                    }
                    nurseriesRecyclerView.adapter = nurseriesAdapter
                }
            } catch (e: Exception) {
                Log.e("MYCLIENT", "Ошибка: ${e.message}")
            }
        }

        return binding.root
    }

    private fun putPrefs(place: AuthModels.PlaceDTO, namePlace: String) {
        val prefs = requireActivity().getSharedPreferences("place_prefs",
            Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString("address", place.address)
            putString("name", place.name)
            putString("phone", place.phone)
            putString("description", place.description)
            putString("owner", place.owner)
            putString("place", namePlace)
            putBoolean("editable", true)
            apply()
        }
    }

    companion object {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}