package com.internshala.foodhub.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodhub.R
import com.internshala.foodhub.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    lateinit var sharedPreferences: SharedPreferences

    lateinit var loginPreferences: SharedPreferences

    lateinit var toolbar : Toolbar
    lateinit var imgLogoImage : ImageView
    lateinit var etMobileNumber: EditText
    lateinit var etPassword : EditText
    lateinit var btnLogin: Button
    lateinit var txtForgotPassword: TextView
    lateinit var txtRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Log In"

        sharedPreferences = getSharedPreferences(getString(R.string.preferences_file_profile), Context.MODE_PRIVATE)
        loginPreferences = getSharedPreferences(getString(R.string.login_preferences), Context.MODE_PRIVATE)

        val isLoggedIn = loginPreferences.getBoolean("isLoggedIn", false)

        if (isLoggedIn){

            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()

        }

        imgLogoImage = findViewById(R.id.imgLogoImage)
        etMobileNumber = findViewById(R.id.etMobileNumber)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        txtForgotPassword = findViewById(R.id.txtForgotPassword)
        txtRegister = findViewById(R.id.txtRegister)

        txtForgotPassword.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
        }

        txtRegister.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegistrationActivity::class.java))
        }




        val jsonParams = JSONObject()
        jsonParams.put("mobile_number", etMobileNumber.text)
        jsonParams.put("password", etPassword.text)

        val queue = Volley.newRequestQueue(this@LoginActivity)
        val url = "http://13.235.250.119/v2/login/fetch_result"

        btnLogin.setOnClickListener {
            if (ConnectionManager().checkConnectivity(this@LoginActivity)){

                val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {

                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")

                        if (success) {

                            val registerObject = data.getJSONObject("data")

                            sharedPreferences.edit().putString("id", registerObject.getString("user_id")).apply()
                            sharedPreferences.edit().putString("name", registerObject.getString("name")).apply()
                            sharedPreferences.edit().putString("email", registerObject.getString("email")).apply()
                            sharedPreferences.edit().putString("mobile_number", registerObject.getString("mobile_number")).apply()
                            sharedPreferences.edit().putString("address", registerObject.getString("address")).apply()

                            loginPreferences.edit().putBoolean("isLoggedIn", true).apply()

                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)

                        } else {
                            Toast.makeText(this@LoginActivity, "Invalid credentials", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(this@LoginActivity, "Some error occurred", Toast.LENGTH_SHORT).show()
                    }

                }, Response.ErrorListener {

                    if (applicationContext != null){
                        Toast.makeText(this@LoginActivity, "Volley error occurred", Toast.LENGTH_SHORT).show()
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
                val dialog = AlertDialog.Builder(this@LoginActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection is not Found")
                dialog.setPositiveButton("Open Setting"){text, listener ->

                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    finish()

                }

                dialog.setNegativeButton("Exit"){ text, listener ->

                    ActivityCompat.finishAffinity(this@LoginActivity)

                }
                dialog.create()
                dialog.show()
            }

        }
    }

}