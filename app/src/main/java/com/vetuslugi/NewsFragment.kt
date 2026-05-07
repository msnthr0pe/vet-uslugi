package com.vetuslugi

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.vetuslugi.adapters.HistoryAdapter
import com.vetuslugi.adapters.NewsAdapter
import com.vetuslugi.databinding.FragmentNewsBinding
import com.vetuslugi.presentation.viewmodel.NewsViewModel
import kotlinx.coroutines.launch

class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

    private lateinit var newsAdapter: NewsAdapter

    private val viewModel: NewsViewModel by viewModels {
        (requireActivity().application as VetUslugiApp).container.newsViewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newsAdapter = NewsAdapter(emptyList())
        binding.allRequestsRecycler.layoutManager = LinearLayoutManager(activity)
        binding.allRequestsRecycler.adapter = newsAdapter

        binding.historyRecycler.layoutManager = LinearLayoutManager(activity)

        binding.etSearch.setText(viewModel.searchQuery.value)

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchQuery.value = s.toString()
                binding.btnClearSearch.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                binding.progressBar.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnClearSearch.setOnClickListener {
            binding.etSearch.text.clear()
            binding.btnClearSearch.visibility = View.GONE
            hideKeyboard()
        }

        binding.etSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val history = viewModel.getSearchHistory()
                if (history.isNotEmpty()) {
                    binding.historyRecycler.visibility = View.VISIBLE
                    binding.btnClearHistory.visibility = View.VISIBLE
                    binding.historyRecycler.adapter = HistoryAdapter(history) { selected ->
                        binding.etSearch.setText(selected)
                        binding.etSearch.setSelection(selected.length)
                        binding.etSearch.clearFocus()
                        binding.historyRecycler.visibility = View.GONE
                        binding.btnClearHistory.visibility = View.GONE
                    }
                }
            } else {
                binding.historyRecycler.visibility = View.GONE
                binding.btnClearHistory.visibility = View.GONE
            }
        }

        binding.btnClearHistory.setOnClickListener {
            viewModel.clearSearchHistory()
            binding.historyRecycler.visibility = View.GONE
            binding.btnClearHistory.visibility = View.GONE
        }

        binding.btnRetry.setOnClickListener { viewModel.loadNews() }

        binding.btnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_newsFragment_to_addNewsFragment)
        }

        val role = (requireActivity().application as VetUslugiApp).container.userSession.getRole()
        if (role != "admin") binding.btnAdd.visibility = View.GONE

        binding.customBottomBar.iconShelter.setOnClickListener {
            findNavController().navigate(R.id.action_newsFragment_to_sheltersFragment)
        }
        binding.customBottomBar.iconNursery.setOnClickListener {
            findNavController().navigate(R.id.action_newsFragment_to_nurseriesFragment)
        }
        binding.customBottomBar.iconProfile.setOnClickListener {
            findNavController().navigate(R.id.action_newsFragment_to_profileFragment)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is NewsViewModel.UiState.Loading -> {
                        binding.layoutError.visibility = View.GONE
                        binding.allRequestsRecycler.visibility = View.VISIBLE
                    }
                    is NewsViewModel.UiState.Success -> {
                        binding.layoutError.visibility = View.GONE
                    }
                    is NewsViewModel.UiState.Error -> {
                        binding.allRequestsRecycler.visibility = View.GONE
                        binding.tvNoResults.visibility = View.GONE
                        binding.layoutError.visibility = View.VISIBLE
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.filteredNews.collect { news ->
                newsAdapter.updateList(news)
                binding.tvNoResults.visibility = if (news.isEmpty() && viewModel.searchQuery.value.isNotEmpty()) View.VISIBLE else View.GONE
                binding.allRequestsRecycler.visibility = if (news.isEmpty() && viewModel.searchQuery.value.isNotEmpty()) View.GONE else View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
