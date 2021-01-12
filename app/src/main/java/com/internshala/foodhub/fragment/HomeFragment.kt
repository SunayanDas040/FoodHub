package com.internshala.foodhub.fragment


import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodhub.adapter.HomeRecyclerAdapter
import com.internshala.foodhub.R
import com.internshala.foodhub.database.RestaurantDatabase
import com.internshala.foodhub.database.RestaurantEntity
import com.internshala.foodhub.model.Restaurant
import com.internshala.foodhub.util.ConnectionManager
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    lateinit var recyclerHome: RecyclerView

    lateinit var layoutManager: RecyclerView.LayoutManager

    lateinit var recyclerAdapter: HomeRecyclerAdapter

    lateinit var progressLayout: RelativeLayout

    lateinit var progressBar: ProgressBar

    var ratingComparator= Comparator<Restaurant>{ restaurant1, restaurant2 ->
        if (restaurant1.restaurantRating.compareTo(restaurant2.restaurantRating, true) == 0){
            restaurant1.restaurantName.compareTo(restaurant2.restaurantName, true)
        } else {
            restaurant1.restaurantRating.compareTo(restaurant2.restaurantRating, true)
        }
    }

    val restaurantInfoList = arrayListOf<Restaurant>()

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

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        setHasOptionsMenu(true)

        recyclerHome = view.findViewById(R.id.recyclerHome)

        layoutManager = LinearLayoutManager(activity)

        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(activity as Context)

        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

        if (ConnectionManager().checkConnectivity(activity as Context)){

            val jsonOdjectRequest = object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {

                try {

                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")

                    if (success){

                        progressLayout.visibility = View.GONE

                        val resArray = data.getJSONArray("data")
                        for (i in 0 until resArray.length()){

                            val restaurantJsonObject = resArray.getJSONObject(i)
                            val restaurantObject = Restaurant(
                                    restaurantJsonObject.getString("id"),
                                    restaurantJsonObject.getString("name"),
                                    restaurantJsonObject.getString("rating"),
                                    restaurantJsonObject.getString("cost_for_one"),
                                    restaurantJsonObject.getString("image_url")
                            )
                            restaurantInfoList.add(restaurantObject)

                            recyclerAdapter = HomeRecyclerAdapter(activity as Context, restaurantInfoList)

                            recyclerHome.adapter = recyclerAdapter
                            recyclerHome.layoutManager = layoutManager

                        }

                    } else {
                        Toast.makeText(activity as Context, "Some error occurred", Toast.LENGTH_SHORT).show()
                    }

                } catch (e: JSONException){
                    Toast.makeText(activity as Context, "Some error occurred", Toast.LENGTH_SHORT).show()
                }

            }, Response.ErrorListener {

                if (activity != null){
                    Toast.makeText(activity as Context, "Some error occurred", Toast.LENGTH_SHORT).show()
                }

            }) {

                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "261f19083bbcb4"
                    return headers
                }

            }

            queue.add(jsonOdjectRequest)

        } else {

            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Setting"){text, listener ->

                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()

            }

            dialog.setNegativeButton("Exit"){ text, listener ->

                ActivityCompat.finishAffinity(activity as Activity)

            }
            dialog.create()
            dialog.show()

        }


        return view
    }

    class DBAsyncTask(val context: Context, val restaurantEntity: RestaurantEntity, val mode: Int) : AsyncTask<Void, Void, Boolean>(){


        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurants-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {

            when (mode){

                1 ->{

                    val restaurant: RestaurantEntity? = db.restaurantDao().getRestaurantById(restaurantEntity.res_id)
                    db.close()
                    return restaurant != null

                }

                2 -> {

                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true

                }

                3 -> {

                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true

                }

            }
            return false
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.menu_home, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item?.itemId

        if (id == R.id.action_sort){
            Collections.sort(restaurantInfoList, ratingComparator)
            restaurantInfoList.reverse()
        }

        recyclerAdapter.notifyDataSetChanged()

        return super.onOptionsItemSelected(item)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}