package com.vetuslugi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.vetuslugi.databinding.FragmentAddNewsBinding
import com.vetuslugi.ktor.ApiClient
import com.vetuslugi.ktor.AuthModels
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddNewsFragment : Fragment() {

    private var _binding: FragmentAddNewsBinding? = null
    private val binding get() = _binding!!

    private lateinit var etTitle: EditText
    private lateinit var etDescriptionRequest: EditText
    private lateinit var btnPostNews: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddNewsBinding.inflate(layoutInflater, container, false)

        etTitle = binding.etNewsTitle
        etDescriptionRequest = binding.etNewsDescription
        btnPostNews = binding.btnPublishNews

        btnPostNews.setOnClickListener {
            val title = etTitle.text.toString()
            val description = etDescriptionRequest.text.toString()
            if (
                title.isNotEmpty() &&
                description.isNotEmpty()
            ) {
                postNews(title, description)
            } else {
                Toast.makeText(activity, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun postNews(
        title: String,
        description: String,
    ) {
        val call = ApiClient.authApi.addNews(
            AuthModels.NewsDTO(
                title = title,
                description = description
            )
        )
        call.enqueue(object : Callback<AuthModels.AuthResponse> {
            override fun onResponse(
                call: Call<AuthModels.AuthResponse>,
                response: Response<AuthModels.AuthResponse>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Запись добавлена", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_addNewsFragment_to_newsFragment)
                } else {
                    Toast.makeText(requireContext(), "Ошибка добавления записи", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AuthModels.AuthResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}