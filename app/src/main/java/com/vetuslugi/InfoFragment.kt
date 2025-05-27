package com.vetuslugi

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.vetuslugi.databinding.FragmentAddPlaceBinding
import com.vetuslugi.databinding.FragmentEditProfileBinding
import com.vetuslugi.databinding.FragmentInfoBinding
import com.vetuslugi.databinding.FragmentLoginBinding
import com.vetuslugi.databinding.FragmentProfileBinding
import com.vetuslugi.databinding.FragmentTitleBinding
import kotlinx.coroutines.launch

class InfoFragment : Fragment() {

    private var _binding: FragmentInfoBinding? = null
    private val binding get() = _binding!!

    private lateinit var tvPlaceTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInfoBinding.inflate(layoutInflater, container, false)

        tvPlaceTitle = binding.tvPlaceTitle

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireActivity().getSharedPreferences("place_prefs",
            Context.MODE_PRIVATE)
        val address = prefs.getString("address", "не указано")
        val name = prefs.getString("name", "не указано")
        val phone = prefs.getString("phone", "не указано")
        val description = prefs.getString("description", "не указано")
        val place = prefs.getString("place", "не указано")

        tvPlaceTitle.text = place

    }

    companion object {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}