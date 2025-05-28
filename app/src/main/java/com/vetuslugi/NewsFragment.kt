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
import com.vetuslugi.adapters.NewsAdapter
import com.vetuslugi.databinding.FragmentNewsBinding
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

class NewsFragment : Fragment() {
    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var requestAdapter: NewsAdapter

    private lateinit var searchEditText: EditText
    private val searchQuery = MutableStateFlow("")

    private var originalNews: List<AuthModels.NewsDTO> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @OptIn(FlowPreview::class)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentNewsBinding.inflate(layoutInflater, container, false)

        recyclerView = binding.allRequestsRecycler
        recyclerView.layoutManager = LinearLayoutManager(activity)

        lifecycleScope.launch {
            try {
                val news = withContext(Dispatchers.IO) {
                    ApiClient.authApi.getNews()
                }
                originalNews = news
                requestAdapter = NewsAdapter(news)
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

        binding.btnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_newsFragment_to_addNewsFragment)
        }

        binding.customBottomBar.iconShelter.setOnClickListener {
            findNavController().navigate(R.id.action_newsFragment_to_sheltersFragment)
        }

        binding.customBottomBar.iconNursery.setOnClickListener {
            findNavController().navigate(R.id.action_newsFragment_to_nurseriesFragment)
        }

        binding.customBottomBar.iconProfile.setOnClickListener {
            findNavController().navigate(R.id.action_newsFragment_to_profileFragment)
        }

        val prefs = requireContext().getSharedPreferences(
            "credentials",
            Context.MODE_PRIVATE
        )
        if (prefs.getString("role", "-") != "admin") {
            binding.btnAdd.visibility = View.GONE
        }

        return binding.root
    }

    private fun filterCards(query: String) {
        val filtered = if (query.isEmpty()) {
            originalNews
        } else {
            originalNews.filter { it.title.contains(query, ignoreCase = true) }
        }
        try {
            requestAdapter.updateList(filtered)
        } catch(_: Exception) {
            Log.e("VETUSLUGI", "Exception occurred")
        }
    }


    companion object {

    }
}