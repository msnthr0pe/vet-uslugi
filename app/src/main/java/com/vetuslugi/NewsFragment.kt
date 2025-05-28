package com.vetuslugi

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vetuslugi.adapters.HistoryAdapter
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
    private lateinit var newsAdapter: NewsAdapter

    private lateinit var searchEditText: EditText
    private val searchQuery = MutableStateFlow("")

    private var originalNews: List<AuthModels.NewsDTO> = emptyList()

    private lateinit var noResultsTextView: View
    private lateinit var errorLayout: View
    private lateinit var retryButton: View

    private var lastQueryFailed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @OptIn(FlowPreview::class)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentNewsBinding.inflate(layoutInflater, container, false)

        recyclerView = binding.allRequestsRecycler
        recyclerView.layoutManager = LinearLayoutManager(activity)

        noResultsTextView = binding.tvNoResults
        errorLayout = binding.layoutError
        retryButton = binding.btnRetry

        loadNews()

        retryButton.setOnClickListener {
            lastQueryFailed = false
            loadNews()
        }

        searchEditText = binding.etSearch
        val restoredQuery = savedInstanceState?.getString("search_query") ?: ""
        searchEditText.setText(restoredQuery)
        searchQuery.value = restoredQuery

        val clearButton = binding.btnClearSearch
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery.value = s.toString()
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                binding.progressBar.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE

            }
            override fun afterTextChanged(s: Editable?) {
            }
        })

        clearButton.setOnClickListener {
            searchEditText.text.clear()
            clearButton.visibility = View.GONE
            hideKeyboard()
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

        val historyRecycler = binding.historyRecycler
        historyRecycler.layoutManager = LinearLayoutManager(activity)
        val clearHistoryButton = binding.btnClearHistory

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val history = loadSearchHistory()
                if (history.isNotEmpty()) {
                    historyRecycler.visibility = View.VISIBLE
                    clearHistoryButton.visibility = View.VISIBLE
                    historyRecycler.adapter = HistoryAdapter(history) { selected ->
                        searchEditText.setText(selected)
                        searchEditText.setSelection(selected.length)
                        searchEditText.clearFocus()
                        historyRecycler.visibility = View.GONE
                        clearHistoryButton.visibility = View.GONE
                    }
                }
            } else {
                historyRecycler.visibility = View.GONE
                clearHistoryButton.visibility = View.GONE
            }
        }

        clearHistoryButton.setOnClickListener {
            clearSearchHistory()
            historyRecycler.visibility = View.GONE
            clearHistoryButton.visibility = View.GONE
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

    private fun loadNews() {
        lifecycleScope.launch {
            try {
                errorLayout.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE

                val news = withContext(Dispatchers.IO) {
                    ApiClient.authApi.getNews()
                }

                originalNews = news
                newsAdapter = NewsAdapter(news)
                recyclerView.adapter = newsAdapter
                filterCards(searchQuery.value)

            } catch (e: Exception) {
                Log.e("CLIENT", "Ошибка: ${e.message}")
                recyclerView.visibility = View.GONE
                noResultsTextView.visibility = View.GONE
                errorLayout.visibility = View.VISIBLE
                lastQueryFailed = true
            }
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("search_query", searchEditText.text.toString())
    }

    private fun filterCards(query: String) {
        val filtered = if (query.isEmpty()) {
            originalNews
        } else {
            originalNews.filter { it.title.contains(query, ignoreCase = true) }
        }

        if (query.isNotEmpty()) {
            val history = loadSearchHistory()
            history.remove(query)
            history.add(0, query)
            val trimmed = history.take(10)
            saveSearchHistory(trimmed)
        }

        try {
            newsAdapter.updateList(filtered)
            noResultsTextView.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
            recyclerView.visibility = if (filtered.isEmpty()) View.GONE else View.VISIBLE
        } catch (e: Exception) {
            Log.e("VETUSLUGI", "Exception occurred")
        }
        binding.progressBar.visibility = View.GONE
    }

    private fun getHistoryPrefs(): SharedPreferences {
        return requireContext().getSharedPreferences("search_history", Context.MODE_PRIVATE)
    }

    private fun loadSearchHistory(): MutableList<String> {
        val json = getHistoryPrefs().getString("history", null) ?: return mutableListOf()
        return Gson().fromJson(json, object : TypeToken<MutableList<String>>() {}.type)
    }

    private fun saveSearchHistory(history: List<String>) {
        val json = Gson().toJson(history)
        getHistoryPrefs().edit().putString("history", json).apply()
    }

    private fun clearSearchHistory() {
        getHistoryPrefs().edit().remove("history").apply()
    }


    companion object {

    }
}