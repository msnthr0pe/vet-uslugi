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
        val isEditable = prefs.getBoolean("editable", false)


        binding.addressCard.tvCardTitle.text = "Адрес"
        binding.addressCard.tvDescriptionCard.text = address
        binding.addressCard.tvEditCard.visibility = View.GONE

        binding.nameCard.tvCardTitle.text = "Название"
        binding.nameCard.tvDescriptionCard.text = name

        binding.phoneCard.tvCardTitle.text = "Контактный телефон"
        binding.phoneCard.tvDescriptionCard.text = phone

        binding.descriptionCard.tvCardTitle.text = "Описание"
        binding.descriptionCard.tvDescriptionCard.text = description

        if (isEditable) {
            binding.nameCard.tvEditCard.visibility = View.GONE
            binding.phoneCard.tvEditCard.visibility = View.GONE
            binding.descriptionCard.tvEditCard.visibility = View.GONE
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

        tvPlaceTitle.text = place

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
            obj.tvDescriptionCard.text = etInput.text.toString()
            dialog.dismiss()
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