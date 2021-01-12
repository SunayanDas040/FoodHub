package com.internshala.foodhub.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.ImageView
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
import com.internshala.foodhub.adapter.DetailsRecyclerAdapter
import com.internshala.foodhub.database.MenuDatabase
import com.internshala.foodhub.database.MenuEntity
import com.internshala.foodhub.model.Menu
import com.internshala.foodhub.util.ConnectionManager
import org.json.JSONException

class DetailsActivity : AppCompatActivity() {

    lateinit var txtChooseItem: TextView

    lateinit var imgFav: ImageView

    lateinit var recyclerDetails: RecyclerView

    lateinit var layoutManager: RecyclerView.LayoutManager

    lateinit var btnProceed: Button

    lateinit var toolbar: Toolbar

    lateinit var detailsRecyclerAdapter: DetailsRecyclerAdapter

    var restaurantId:String? = "0"

    var  restaurantName: String? = "Restaurant"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        txtChooseItem = findViewById(R.id.txtChooseItem)

        imgFav = findViewById(R.id.imgFav)

        recyclerDetails = findViewById(R.id.recyclerDetails)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        layoutManager = LinearLayoutManager(this@DetailsActivity)

        btnProceed = findViewById(R.id.btnProceed)

        val menuItemList = arrayListOf<Menu>()

        if (intent != null){
            restaurantId = intent.getStringExtra("id")
            restaurantName = intent.getStringExtra("name")
        } else {
            Toast.makeText(this@DetailsActivity, "Some error occurred", Toast.LENGTH_SHORT).show()
        }


        supportActionBar?.title = restaurantName


        if (restaurantId == "0"){
            finish()
            Toast.makeText(this@DetailsActivity, "Some error occurred", Toast.LENGTH_SHORT).show()
        }

        val queue = Volley.newRequestQueue(this@DetailsActivity)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/$restaurantId"

        if (ConnectionManager().checkConnectivity(this@DetailsActivity)){
            val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {

                try {
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if (success){
                        val menuArray = data.getJSONArray("data")
                        for (i in 0 until menuArray.length()){
                            val menuJsonObject = menuArray.getJSONObject(i)
                            val menuObject = Menu(
                                menuJsonObject.getString("id"),
                                menuJsonObject.getString("name"),
                                menuJsonObject.getString("cost_for_one"),
                                menuJsonObject.getString("restaurant_id")

                            )

                            menuItemList.add(menuObject)

                            detailsRecyclerAdapter = DetailsRecyclerAdapter(this@DetailsActivity, menuItemList)

                            recyclerDetails.adapter = detailsRecyclerAdapter
                            recyclerDetails.layoutManager = layoutManager

                        }

                    } else{
                        Toast.makeText(this@DetailsActivity, "Some error occurred", Toast.LENGTH_SHORT).show()
                    }
                } catch (e : JSONException){

                    Toast.makeText(this@DetailsActivity, "Some error occurred", Toast.LENGTH_SHORT).show()

                }

            }, Response.ErrorListener {

                if (applicationContext != null){
                    Toast.makeText(this@DetailsActivity, "Volley error occurred", Toast.LENGTH_SHORT).show()
                }

            }) {

                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "261f19083bbcb4"
                    return headers
                }
            }

            queue.add(jsonObjectRequest)

        } else {

            val dialog = AlertDialog.Builder(this@DetailsActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Setting"){text, listener ->

                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()

            }

            dialog.setNegativeButton("Exit"){ text, listener ->

                ActivityCompat.finishAffinity(this@DetailsActivity)

            }
            dialog.create()
            dialog.show()
        }

        for (i in 0 until menuItemList.size){
            val menuEntity = MenuEntity(
                    menuItemList[i].menuId,
                    menuItemList[i].menuName,
                    menuItemList[i].menuCostForOne,
                    menuItemList[i].resId
            )

            val isAdded = DBAsyncTask(applicationContext, menuEntity, 1).execute()
            val result = isAdded.get()

            if (result){
                btnProceed.visibility = View.VISIBLE
                break
            } else {
                btnProceed.visibility = View.GONE
            }
        }

        btnProceed.setOnClickListener {
            val intent = Intent(this@DetailsActivity, CartActivity::class.java)
            intent.putExtra("restaurant_id", restaurantId)
            startActivity(intent)
        }


    }

    class DBAsyncTask(val context: Context, val menuEntity: MenuEntity, val mode: Int) : AsyncTask<Void, Void, Boolean>(){


        val db = Room.databaseBuilder(context, MenuDatabase::class.java, "menu-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {

            when (mode){

                1 ->{

                    val menu: MenuEntity? = db.menuDao().getMenuById(menuEntity.menu_id)
                    db.close()
                    return menu != null

                }

                2 -> {

                    db.menuDao().menuInsert(menuEntity)
                    db.close()
                    return true

                }

                3 -> {

                    db.menuDao().menuDelete(menuEntity)
                    db.close()
                    return true

                }

            }
            return false
        }

    }
}