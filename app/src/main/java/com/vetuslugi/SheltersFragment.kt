package com.vetuslugi

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.vetuslugi.databinding.FragmentSheltersBinding
import com.vetuslugi.databinding.FragmentTitleBinding
import kotlinx.coroutines.launch

class SheltersFragment : Fragment() {

    private var _binding: FragmentSheltersBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSheltersBinding.inflate(layoutInflater, container, false)

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