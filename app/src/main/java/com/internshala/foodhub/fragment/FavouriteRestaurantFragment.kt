package com.internshala.foodhub.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.internshala.foodhub.R
import com.internshala.foodhub.adapter.FavouriteRecyclerAdapter
import com.internshala.foodhub.database.RestaurantDatabase
import com.internshala.foodhub.database.RestaurantEntity

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FavouriteRestaurantFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    lateinit var recyclerFavourite: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var recyclerFavouriteAdapter: FavouriteRecyclerAdapter
    var dbRestaurantList = listOf<RestaurantEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favourite_restaurant, container, false)

        recyclerFavourite = view.findViewById(R.id.recyclerFavourite)
        layoutManager = LinearLayoutManager(activity as Context)

        progressLayout.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE

        dbRestaurantList = RetrieveFavourites(activity as Context).execute().get()

        if (dbRestaurantList != null) {
            if (activity != null) {
                progressLayout.visibility = View.GONE
                recyclerFavouriteAdapter = FavouriteRecyclerAdapter(activity as Context, dbRestaurantList)
                recyclerFavourite.adapter = recyclerFavouriteAdapter
                recyclerFavourite.layoutManager = layoutManager
            }
        } else {
            Toast.makeText(activity as Context, "You have no favourite restaurants", Toast.LENGTH_SHORT).show()
        }




        return view
    }

    class RetrieveFavourites(val context: Context): AsyncTask<Void, Void, List<RestaurantEntity>>(){
        override fun doInBackground(vararg params: Void?): List<RestaurantEntity> {
            val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurants-db").build()
            return db.restaurantDao().getAllRestaurant()
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavouriteRestaurantFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}