package com.internshala.foodhub.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.internshala.foodhub.R
import com.internshala.foodhub.activity.DetailsActivity
import com.internshala.foodhub.database.RestaurantEntity
import com.squareup.picasso.Picasso

class FavouriteRecyclerAdapter(val context: Context, val itemList: List<RestaurantEntity>):RecyclerView.Adapter<FavouriteRecyclerAdapter.FavouriteViewHolder>() {

    class FavouriteViewHolder(view: View): RecyclerView.ViewHolder(view){
        val llContent: LinearLayout = view.findViewById(R.id.llContent)
        val imgResImage: ImageView = view.findViewById(R.id.imgResImage)
        val txtResName: TextView = view.findViewById(R.id.txtResName)
        val txtCostForOne: TextView = view.findViewById(R.id.txtCostForOne)
        val imgFav: ImageView = view.findViewById(R.id.imgFav)
        val txtResRating: TextView = view.findViewById(R.id.txtResRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_favourite_single_row, parent, false)

        return FavouriteViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {

        val restaurant = itemList[position]

        Picasso.get().load(restaurant.restaurantImage).error(R.drawable.foodiction).into(holder.imgResImage)
        holder.txtResName.text = restaurant.restaurantName
        holder.txtCostForOne.text = restaurant.costForOne
        holder.txtResRating.text = restaurant.restaurantRating

        holder.llContent.setOnClickListener {
            val intent = Intent(context, DetailsActivity::class.java)
            intent.putExtra("id", restaurant.res_id)
            intent.putExtra("name", restaurant.restaurantName)
            context.startActivity(intent)

        }
    }
}