package com.vetuslugi

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.vetuslugi.databinding.FragmentAddPlaceBinding
import com.vetuslugi.databinding.FragmentEditProfileBinding
import com.vetuslugi.databinding.FragmentProfileBinding
import com.vetuslugi.databinding.FragmentTitleBinding
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)



        return binding.root
    }

    companion object {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}