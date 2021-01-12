package com.internshala.foodhub.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodhub.R
import com.internshala.foodhub.adapter.CartRecyclerAdapter
import com.internshala.foodhub.database.MenuDatabase
import com.internshala.foodhub.database.MenuEntity
import com.internshala.foodhub.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class CartActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar

    lateinit var txtOrderingFrom: TextView

    lateinit var txtRestaurantName: TextView

    lateinit var recyclerView: RecyclerView

    lateinit var layoutManager: RecyclerView.LayoutManager

    lateinit var recyclerAdapter: CartRecyclerAdapter

    lateinit var btnPlaceOrder: Button

    lateinit var profilePreferences: SharedPreferences

    var totalPrice = 0

    var dbMenuList = listOf<MenuEntity>()

    var userId: String? = "1"

    var restaurantId: String? = "100"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Cart"

        profilePreferences = getSharedPreferences(getString(R.string.preferences_file_profile), Context.MODE_PRIVATE)

        if (intent != null){
            restaurantId = intent.getStringExtra("restaurant_id")
        } else {
            Toast.makeText(this@CartActivity, "Some error occurred", Toast.LENGTH_SHORT).show()
        }

        userId = profilePreferences.getString("user_id", "1")

        txtOrderingFrom = findViewById(R.id.txtOrderingFrom)

        txtRestaurantName = findViewById(R.id.txtRestaurantName)

        recyclerView = findViewById(R.id.recyclerView)

        layoutManager = LinearLayoutManager(this@CartActivity)

        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)

        dbMenuList = RetrieveMenu(this@CartActivity).execute().get()

        for (i in 0 until dbMenuList.size){
            totalPrice += dbMenuList[i].costForOne.toInt()
        }

        recyclerAdapter = CartRecyclerAdapter(this@CartActivity, dbMenuList)
        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = layoutManager

        btnPlaceOrder.text = "Place Order (Total Rs. ${totalPrice})"

        val jsonParams = JSONObject()
        jsonParams.put("user_id", userId)
        jsonParams.put("restaurant_id", restaurantId)
        jsonParams.put("total_cost", totalPrice)
        jsonParams.put("food", dbMenuList)

        val queue = Volley.newRequestQueue(this@CartActivity)
        val url = "http://13.235.250.119/v2/place_order/fetch_result/"

        btnPlaceOrder.setOnClickListener {
            if (ConnectionManager().checkConnectivity(this@CartActivity)){
                val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {
                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")

                        if (success){
                            val intent = Intent(this@CartActivity, OrderPlacedActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@CartActivity,"Some error occurred", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException){
                        Toast.makeText(this@CartActivity,"Some unexpected error occurred", Toast.LENGTH_SHORT).show()
                    }
                }, Response.ErrorListener {

                    Toast.makeText(this@CartActivity,"Volley error occurred", Toast.LENGTH_SHORT).show()

                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "261f19083bbcb4"
                        return headers
                    }
                }

                queue.add(jsonObjectRequest)

                DeleteDatabase(this@CartActivity).execute()

            } else {
                val dialog = AlertDialog.Builder(this@CartActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection is not Found")
                dialog.setPositiveButton("Open Setting"){text, listener ->

                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    finish()

                }

                dialog.setNegativeButton("Exit"){ text, listener ->

                    ActivityCompat.finishAffinity(this@CartActivity)

                }
                dialog.create()
                dialog.show()
            }
        }

    }

    class DeleteDatabase(val context: Context): AsyncTask<Void, Void, Unit>(){
        override fun doInBackground(vararg params: Void?): Unit {
            val db = Room.databaseBuilder(context, MenuDatabase::class.java, "menu-db").build()
            return db.menuDao().deleteAllEntries()
        }

    }

    class RetrieveMenu(val context: Context):AsyncTask<Void, Void, List<MenuEntity>>(){

        override fun doInBackground(vararg params: Void?): List<MenuEntity> {

            val db = Room.databaseBuilder(context, MenuDatabase::class.java, "menu-db").build()

            return db.menuDao().getAllMenu()
        }

    }
}