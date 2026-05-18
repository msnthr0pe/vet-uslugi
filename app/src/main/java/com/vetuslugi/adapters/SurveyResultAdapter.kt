package com.vetuslugi.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vetuslugi.R
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
        Glide.with(holder.binding.root.context)
            .load(animal.imageUrl)
            .placeholder(R.drawable.nursery)
            .error(R.drawable.nursery)
            .centerCrop()
            .into(holder.binding.ivAnimalPhoto)
        holder.binding.tvAnimalNickname.text = animal.nickname
        holder.binding.tvAnimalDetails.text = "${animal.species} · ${animal.breed}"
        holder.binding.tvAnimalAge.text = "Возраст: ${animal.age} лет"
        val percent = (item.coefficient * 100).roundToInt()
        holder.binding.tvCoefficient.text = "$percent%"
        if (animal.diseases != null) {
            holder.binding.tvAnimalDiseases.visibility = View.VISIBLE
            holder.binding.tvAnimalDiseases.text = animal.diseases
            val colorRes = when (animal.diseaseSeverity) {
                1 -> R.color.disease_yellow
                2 -> R.color.disease_orange
                else -> R.color.red
            }
            holder.binding.tvAnimalDiseases.setTextColor(
                ContextCompat.getColor(holder.binding.root.context, colorRes)
            )
        } else {
            holder.binding.tvAnimalDiseases.visibility = View.GONE
        }
        holder.binding.root.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = items.size

    fun updateList(newItems: List<SurveyResult>) {
        items = newItems
        notifyDataSetChanged()
    }
}
