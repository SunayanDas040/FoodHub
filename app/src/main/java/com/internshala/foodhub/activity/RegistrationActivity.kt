package com.internshala.foodhub.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
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

class RegistrationActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var etName : EditText
    lateinit var etEmail: EditText
    lateinit var etMobileNumber: EditText
    lateinit var etAddress: EditText
    lateinit var etPassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var btnRegister: Button
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Register"

        sharedPreferences = getSharedPreferences(getString(R.string.preferences_file_register),Context.MODE_PRIVATE)

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etMobileNumber = findViewById(R.id.etMobileNumber)
        etAddress = findViewById(R.id.etAddress)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)

        btnRegister = findViewById(R.id.btnRegister)

        val jsonParams = JSONObject()
        jsonParams.put("name", etName.text)
        jsonParams.put("mobile_number", etMobileNumber.text)
        jsonParams.put("password",etConfirmPassword.text)
        jsonParams.put("address", etAddress.text)
        jsonParams.put("email", etEmail.text)

        val queue = Volley.newRequestQueue(this@RegistrationActivity)
        val url = "http://13.235.250.119/v2/register/fetch_result"

        btnRegister.setOnClickListener {

            if (ConnectionManager().checkConnectivity(this@RegistrationActivity)) {

                val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {

                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")

                        if (success) {

                            val registerObject = data.getJSONObject("data")

//                            sharedPreferences.edit().putString("id", registerObject.getString("user_id")).apply()
//                            sharedPreferences.edit().putString("name", registerObject.getString("name")).apply()
//                            sharedPreferences.edit().putString("email", registerObject.getString("email")).apply()
//                            sharedPreferences.edit().putString("mobile_number", registerObject.getString("mobile_number")).apply()
//                            sharedPreferences.edit().putString("address", registerObject.getString("address")).apply()

                            val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
                            startActivity(intent)

                        } else {
                            Toast.makeText(this@RegistrationActivity, data.getString("errorMessage"), Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(this@RegistrationActivity, "Some unexpected error occurred", Toast.LENGTH_SHORT).show()
                    }


                }, Response.ErrorListener {

                    if (applicationContext != null){
                        Toast.makeText(this@RegistrationActivity, "Volley error occurred", Toast.LENGTH_SHORT).show()
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
                val dialog = AlertDialog.Builder(this@RegistrationActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection is not Found")
                dialog.setPositiveButton("Open Setting"){text, listener ->

                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    finish()

                }

                dialog.setNegativeButton("Exit"){ text, listener ->

                    ActivityCompat.finishAffinity(this@RegistrationActivity)

                }
                dialog.create()
                dialog.show()
            }

        }
        //finish()

    }
}