package com.vetuslugi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.vetuslugi.databinding.FragmentProfileInfoBinding
import com.vetuslugi.presentation.viewmodel.ProfileInfoViewModel

class ProfileInfoFragment : Fragment() {

    private var _binding: FragmentProfileInfoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileInfoViewModel by viewModels {
        (requireActivity().application as VetUslugiApp).container.profileInfoViewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = viewModel.currentUser
        binding.etName.setText(user?.name)
        binding.etSurname.setText(user?.surname)
        binding.etPhone.setText(user?.phone)
        binding.etLogin.setText(user?.login)
        binding.tvRole.text = user?.role

        binding.btnEditInformation.setOnClickListener {
            findNavController().navigate(R.id.action_profileInfoFragment_to_editProfileFragment)
        }
        binding.btnLogOut.setOnClickListener {
            findNavController().navigate(R.id.action_profileInfoFragment_to_titleFragment)
        }

        binding.customBottomBar.iconNews.setOnClickListener {
            findNavController().navigate(R.id.action_profileInfoFragment_to_newsFragment)
        }
        binding.customBottomBar.iconSurvey.setOnClickListener {
            findNavController().navigate(R.id.action_profileInfoFragment_to_surveyHomeFragment)
        }
        binding.customBottomBar.iconPlaces.setOnClickListener {
            findNavController().navigate(R.id.action_profileInfoFragment_to_placesFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
