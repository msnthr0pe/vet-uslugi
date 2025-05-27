package com.vetuslugi

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vetuslugi.adapters.NewsAdapter
import com.vetuslugi.databinding.FragmentNewsBinding
import com.vetuslugi.ktor.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewsFragment : Fragment() {
    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var requestAdapter: NewsAdapter

    private lateinit var tvSearchClients: TextView
    private lateinit var btnAdd: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

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
                requestAdapter = NewsAdapter(news)
                recyclerView.adapter = requestAdapter

            } catch (e: Exception) {
                Log.e("MYCLIENT", "Ошибка: ${e.message}")
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

        return binding.root
    }

    companion object {

    }
}