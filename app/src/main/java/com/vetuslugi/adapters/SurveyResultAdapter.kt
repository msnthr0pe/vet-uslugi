package com.vetuslugi.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vetuslugi.databinding.ItemSurveyResultBinding
import com.vetuslugi.domain.model.SurveyResult
import kotlin.math.roundToInt

class SurveyResultAdapter(
    private var items: List<SurveyResult>,
    private val onClick: (SurveyResult) -> Unit
) : RecyclerView.Adapter<SurveyResultAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemSurveyResultBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemSurveyResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val animal = item.animal
        holder.binding.tvAnimalNickname.text = animal.nickname
        holder.binding.tvAnimalDetails.text = "${animal.species} · ${animal.breed}"
        holder.binding.tvAnimalAge.text = "Возраст: ${animal.age} лет"
        val percent = (item.coefficient * 100).roundToInt()
        holder.binding.tvCoefficient.text = "$percent%"
        holder.binding.root.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = items.size

    fun updateList(newItems: List<SurveyResult>) {
        items = newItems
        notifyDataSetChanged()
    }
}
