package com.vetuslugi.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vetuslugi.R
import com.vetuslugi.ktor.AuthModels

class SheltersAdapter(private var shelters: List<AuthModels.PlaceDTO>,
                      private val onItemClick: (AuthModels.PlaceDTO) -> Unit) :
    RecyclerView.Adapter<SheltersAdapter.SheltersViewHolder>() {

    inner class SheltersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val addressText: TextView = itemView.findViewById(R.id.addressShelterInput)
        val nameText: TextView = itemView.findViewById(R.id.nameShelterText)
        val phoneText: TextView = itemView.findViewById(R.id.phoneShelterInput)
        val descriptionText: TextView = itemView.findViewById(R.id.descriptionShelterInput)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(shelters[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SheltersViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_shelter, parent, false)
        return SheltersViewHolder(view)
    }

    override fun onBindViewHolder(holder: SheltersViewHolder, position: Int) {
        val item = shelters[position]
        holder.addressText.text = item.address
        holder.nameText.text = item.name
        holder.phoneText.text = item.phone
        holder.descriptionText.text = item.description
    }

    override fun getItemCount(): Int = shelters.size

    fun updateList(newList: List<AuthModels.PlaceDTO>) {
        shelters = newList
        notifyDataSetChanged()
    }

}
