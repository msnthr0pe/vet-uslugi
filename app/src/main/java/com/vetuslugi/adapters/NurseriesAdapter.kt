package com.vetuslugi.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vetuslugi.R
import com.vetuslugi.ktor.AuthModels

class NurseriesAdapter(private val nurseries: List<AuthModels.PlaceDTO>,
                       private val onItemClick: (AuthModels.PlaceDTO) -> Unit) :
    RecyclerView.Adapter<NurseriesAdapter.NurseriesViewHolder>() {

    inner class NurseriesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val addressText: TextView = itemView.findViewById(R.id.addressNurseryInput)
        val nameText: TextView = itemView.findViewById(R.id.nameNurseryText)
        val phoneText: TextView = itemView.findViewById(R.id.phoneNurseryInput)
        val descriptionText: TextView = itemView.findViewById(R.id.descriptionNurseryInput)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(nurseries[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NurseriesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_nursery, parent, false)
        return NurseriesViewHolder(view)
    }

    override fun onBindViewHolder(holder: NurseriesViewHolder, position: Int) {
        val item = nurseries[position]
        holder.addressText.text = item.address
        holder.nameText.text = item.name
        holder.phoneText.text = item.phone
        holder.descriptionText.text = item.description
    }

    override fun getItemCount(): Int = nurseries.size

}
