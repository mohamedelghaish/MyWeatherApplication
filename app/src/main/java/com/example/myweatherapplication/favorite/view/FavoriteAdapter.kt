package com.example.myweatherapplication.favorite.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myweatherapplication.Const
import com.example.myweatherapplication.R

import com.example.myweatherapplication.model.FavoriteLocation
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
class FavoriteAdapter (var click: OnFavoriteClick): ListAdapter<FavoriteLocation,FavoriteAdapter.ViewHolder>
    (FavoriteEntryDiffCallback()) {
    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_favorite, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentObj = getItem(position)
        holder.locationName.text = currentObj.selectedPlaces
        holder.date.text = formatDate(currentObj.selectedDate)
        holder.deleteIcon.setOnClickListener{
                click.deleteFromRoom(currentObj)
        }

        holder.showDetailsOfFavorite.setOnClickListener{
            click.showDetails(currentObj.lat.toString(),currentObj.lng.toString(),Const.language)
        }


    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var locationName:TextView = view.findViewById(R.id.tv_location_name)
        var date: TextView = view.findViewById(R.id.tv_location_date)
        var deleteIcon :ImageView = view.findViewById(R.id.delete_favorite_place)
        var showDetailsOfFavorite: ConstraintLayout =
            itemView.findViewById(R.id.favorite_item_constraintLayout)
    }



    class FavoriteEntryDiffCallback : DiffUtil.ItemCallback<FavoriteLocation>() {
        override fun areItemsTheSame(oldItem: FavoriteLocation, newItem: FavoriteLocation): Boolean {

            return oldItem.lat == newItem.lat
        }

        override fun areContentsTheSame(oldItem: FavoriteLocation, newItem: FavoriteLocation): Boolean {
            return false
        }
    }
    fun formatDate(dateInSeconds: Long): String {
        val time = dateInSeconds * 1000.toLong()
        val date = Date(time)
        val dateFormat = SimpleDateFormat("d MMM yyyy", Locale(Const.language))
        return dateFormat.format(date)
    }

}