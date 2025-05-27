package com.vetuslugi

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.vetuslugi.databinding.CardInfoBinding
import com.vetuslugi.databinding.FragmentAddPlaceBinding
import com.vetuslugi.databinding.FragmentEditProfileBinding
import com.vetuslugi.databinding.FragmentInfoBinding
import com.vetuslugi.databinding.FragmentLoginBinding
import com.vetuslugi.databinding.FragmentProfileBinding
import com.vetuslugi.databinding.FragmentTitleBinding
import com.vetuslugi.ktor.ApiClient
import com.vetuslugi.ktor.AuthModels
import kotlinx.coroutines.launch

class InfoFragment : Fragment() {

    private var _binding: FragmentInfoBinding? = null
    private val binding get() = _binding!!

    private lateinit var tvPlaceTitle: TextView
    private lateinit var btnSaveChanges: Button

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
        var name = prefs.getString("name", "не указано")
        var phone = prefs.getString("phone", "не указано")
        var description = prefs.getString("description", "не указано")
        val place = prefs.getString("place", "не указано")
        val isEditable = prefs.getBoolean("editable", false)

        tvPlaceTitle.text = place

        binding.addressCard.tvCardTitle.text = "Адрес"
        binding.addressCard.tvDescriptionCard.text = address
        binding.addressCard.tvEditCard.visibility = View.GONE

        binding.nameCard.tvCardTitle.text = "Название"
        binding.nameCard.tvDescriptionCard.text = name

        binding.phoneCard.tvCardTitle.text = "Контактный телефон"
        binding.phoneCard.tvDescriptionCard.text = phone

        binding.descriptionCard.tvCardTitle.text = "Описание"
        binding.descriptionCard.tvDescriptionCard.text = description

        btnSaveChanges = binding.btnSaveChanges

        if (!isEditable) {
            binding.nameCard.tvEditCard.visibility = View.GONE
            binding.phoneCard.tvEditCard.visibility = View.GONE
            binding.descriptionCard.tvEditCard.visibility = View.GONE
            btnSaveChanges.visibility = View.GONE
        }

        btnSaveChanges.setOnClickListener {
            val fragmentPrefs = requireActivity().getSharedPreferences("fragment_prefs",
                Context.MODE_PRIVATE)

            name = binding.nameCard.tvDescriptionCard.text.toString()
            phone = binding.phoneCard.tvDescriptionCard.text.toString()
            description = binding.descriptionCard.tvDescriptionCard.text.toString()
            if (
                name.isNotEmpty() &&
                phone.isNotEmpty() &&
                description.isNotEmpty()
                ) {
                lifecycleScope.launch {
                    if (place == "приюте") {
                        ApiClient.authApi.updateShelter(
                            AuthModels.PlaceDTO(
                                address.toString(),
                                name,
                                phone,
                                description,
                                "-"
                            )
                        )
                        if (fragmentPrefs.getBoolean("fromProfile", false)) {
                            findNavController().navigate(R.id.action_infoFragment_to_profileFragment)
                        } else
                            findNavController().navigate(R.id.action_infoFragment_to_sheltersFragment)
                        }

                    if (place == "питомнике") {
                        ApiClient.authApi.updateNursery(
                            AuthModels.PlaceDTO(
                                address.toString(),
                                name,
                                phone,
                                description,
                                "-"
                            )
                        )
                        if (fragmentPrefs.getBoolean("fromProfile", false)) {
                            findNavController().navigate(R.id.action_infoFragment_to_profileFragment)
                        } else
                            findNavController().navigate(R.id.action_infoFragment_to_nurseriesFragment)
                    }
                }
            } else {
                Toast.makeText(activity, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }

        binding.nameCard.tvEditCard.setOnClickListener {
            showEditDialog(binding.nameCard)
        }
        binding.phoneCard.tvEditCard.setOnClickListener {
            showEditDialog(binding.phoneCard)
        }
        binding.descriptionCard.tvEditCard.setOnClickListener {
            showEditDialog(binding.descriptionCard)
        }

    }

    private fun showEditDialog(obj: CardInfoBinding) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_text, null)
        val etInput = dialogView.findViewById<EditText>(R.id.etDialogInput)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSave)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)

        etInput.setText(obj.tvDescriptionCard.text)
        etInput.setSelection(etInput.text.length)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Редактировать ${obj.tvCardTitle.text.toString().lowercase()}")
            .setView(dialogView)
            .create()

        btnSave.setOnClickListener {
            val input = etInput.text.toString()
            if (input.isNotEmpty()) {
                obj.tvDescriptionCard.text = input
                dialog.dismiss()
            } else {
                Toast.makeText(activity, "Заполните поле", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_edittext_dialog)
        )

        dialog.show()
    }



    companion object {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}