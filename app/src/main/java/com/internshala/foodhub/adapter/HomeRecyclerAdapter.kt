package com.internshala.foodhub.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.internshala.foodhub.R
import com.internshala.foodhub.activity.DetailsActivity
import com.internshala.foodhub.database.RestaurantEntity
import com.internshala.foodhub.fragment.HomeFragment
import com.internshala.foodhub.model.Restaurant
import com.squareup.picasso.Picasso

class HomeRecyclerAdapter(val context: Context, val itemList: ArrayList<Restaurant>): RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>() {

    class HomeViewHolder(view : View): RecyclerView.ViewHolder(view) {

        val llContent: LinearLayout = view.findViewById(R.id.llContent)
        val imgResImage: ImageView = view.findViewById(R.id.imgResImage)
        val txtResName: TextView = view.findViewById(R.id.txtResName)
        val txtCostForOne: TextView = view.findViewById(R.id.txtCostForOne)
        val imgFav: ImageView = view.findViewById(R.id.imgFav)
        val txtResRating: TextView = view.findViewById(R.id.txtResRating)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_home_single_row, parent, false)

        return HomeViewHolder(view)

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {

        val restaurant = itemList[position]

        holder.txtResName.text = restaurant.restaurantName
        holder.txtResRating.text = restaurant.restaurantRating
        holder.txtCostForOne.text = "Rs." + restaurant.costForOne + "/person"
        Picasso.get().load(restaurant.restaurantImage).error(R.drawable.foodiction).into(holder.imgResImage)

        val restaurantEntity = RestaurantEntity(
            restaurant.restaurantId,
            restaurant.restaurantName,
            restaurant.restaurantRating,
            restaurant.costForOne,
            restaurant.restaurantImage
        )

        val checkFav = HomeFragment.DBAsyncTask(context, restaurantEntity, 1).execute()
        val isFav = checkFav.get()

        if (isFav){

            holder.imgFav.setImageResource(R.drawable.ic_favourite_checked)

        } else {

            holder.imgFav.setImageResource(R.drawable.ic_favourite)

        }

        holder.imgFav.setOnClickListener {
            if (!HomeFragment.DBAsyncTask(context, restaurantEntity, 1).execute().get()){

                val async = HomeFragment.DBAsyncTask(context, restaurantEntity, 2).execute()
                val result = async.get()

                if (result){
                    Toast.makeText(context, "Restaurant added to Favourites", Toast.LENGTH_SHORT).show()
                    holder.imgFav.setImageResource(R.drawable.ic_favourite_checked)
                } else {
                    Toast.makeText(context, "Some error occurred", Toast.LENGTH_SHORT).show()
                }
            } else {
                val async = HomeFragment.DBAsyncTask(context, restaurantEntity, 3).execute()
                val result = async.get()

                if (result){
                    Toast.makeText(context, "Restaurant removed from Favourites", Toast.LENGTH_SHORT).show()
                    holder.imgFav.setImageResource(R.drawable.ic_favourite)
                } else {
                    Toast.makeText(context, "Some error occurred", Toast.LENGTH_SHORT).show()
                }
            }
        }

        holder.llContent.setOnClickListener {
            val intent = Intent(context, DetailsActivity::class.java)
            intent.putExtra("id", restaurant.restaurantId)
            intent.putExtra("name", restaurant.restaurantName)
            context.startActivity(intent)

        }


    }
}