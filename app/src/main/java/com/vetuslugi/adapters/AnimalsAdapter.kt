package com.vetuslugi.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vetuslugi.R
import com.vetuslugi.domain.model.Animal

class AnimalsAdapter(
    private var animals: List<Animal>,
    private val onItemClick: (Animal) -> Unit
) : RecyclerView.Adapter<AnimalsAdapter.AnimalViewHolder>() {

    inner class AnimalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPhoto: ImageView = itemView.findViewById(R.id.ivAnimalPhoto)
        val tvNickname: TextView = itemView.findViewById(R.id.tvAnimalNickname)
        val tvSpecies: TextView = itemView.findViewById(R.id.tvAnimalSpecies)
        val tvBreed: TextView = itemView.findViewById(R.id.tvAnimalBreed)
        val tvAge: TextView = itemView.findViewById(R.id.tvAnimalAge)
        val tvDiseases: TextView = itemView.findViewById(R.id.tvAnimalDiseases)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) onItemClick(animals[position])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_animal, parent, false)
        return AnimalViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnimalViewHolder, position: Int) {
        val item = animals[position]
        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .placeholder(R.drawable.nursery)
            .error(R.drawable.nursery)
            .centerCrop()
            .into(holder.ivPhoto)
        holder.tvNickname.text = item.nickname
        holder.tvSpecies.text = item.species
        holder.tvBreed.text = item.breed
        holder.tvAge.text = item.age.toString()
        if (item.diseases != null) {
            holder.tvDiseases.visibility = View.VISIBLE
            holder.tvDiseases.text = item.diseases
            holder.tvDiseases.setTextColor(severityColor(holder.itemView, item.diseaseSeverity))
        } else {
            holder.tvDiseases.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = animals.size

    fun updateList(newList: List<Animal>) {
        animals = newList
        notifyDataSetChanged()
    }

    private fun severityColor(view: View, severity: Int): Int {
        val colorRes = when (severity) {
            1 -> R.color.disease_yellow
            2 -> R.color.disease_orange
            else -> R.color.red
        }
        return ContextCompat.getColor(view.context, colorRes)
    }
}
