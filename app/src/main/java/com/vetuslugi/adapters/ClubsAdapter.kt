package com.vetuslugi.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vetuslugi.R
import com.vetuslugi.domain.model.Place

class ClubsAdapter(
    private var clubs: List<Place>,
    private val onItemClick: (Place) -> Unit
) : RecyclerView.Adapter<ClubsAdapter.ClubViewHolder>() {

    inner class ClubViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.nameShelterText)
        val addressText: TextView = itemView.findViewById(R.id.addressShelterInput)
        val phoneText: TextView = itemView.findViewById(R.id.phoneShelterInput)
        val descriptionText: TextView = itemView.findViewById(R.id.descriptionShelterInput)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) onItemClick(clubs[position])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClubViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_shelter, parent, false)
        return ClubViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClubViewHolder, position: Int) {
        val item = clubs[position]
        holder.nameText.text = item.name
        holder.addressText.text = item.address
        holder.phoneText.text = item.phone
        holder.descriptionText.text = item.description
    }

    override fun getItemCount(): Int = clubs.size

    fun updateList(newList: List<Place>) {
        clubs = newList
        notifyDataSetChanged()
    }
}
