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
import android.widget.TextView
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

class ResetPasswordActivity : AppCompatActivity() {

    lateinit var registerPreferences: SharedPreferences
    lateinit var loginPreferences: SharedPreferences
    lateinit var profilePreferences: SharedPreferences

    lateinit var toolbar: Toolbar
    lateinit var txtInsOtp: TextView
    lateinit var etOtp: EditText
    lateinit var etNewPassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var btnSubmit: Button
    var mobileNumber: String? = "100"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        registerPreferences = getSharedPreferences(getString(R.string.preferences_file_register), Context.MODE_PRIVATE)
        loginPreferences = getSharedPreferences(getString(R.string.login_preferences), Context.MODE_PRIVATE)
        profilePreferences = getSharedPreferences(getString(R.string.preferences_file_profile), Context.MODE_PRIVATE)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Reset Password"

        txtInsOtp = findViewById(R.id.txtInsOtp)
        etOtp = findViewById(R.id.etOTp)
        etNewPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)

        btnSubmit = findViewById(R.id.btnSubmit)

        if (intent != null){
            mobileNumber = intent.getStringExtra("mobile_number")
        } else {
            Toast.makeText(this@ResetPasswordActivity, "Some unexpected error occurred", Toast.LENGTH_SHORT).show()
        }

        val jsonParams = JSONObject()
        jsonParams.put("mobile_number", mobileNumber)
        jsonParams.put("password", etConfirmPassword.text)
        jsonParams.put("otp", etOtp.text)

        val queue = Volley.newRequestQueue(this@ResetPasswordActivity)
        val url = "http://13.235.250.119/v2/reset_password/fetch_result"

        btnSubmit.setOnClickListener {
            if (ConnectionManager().checkConnectivity(this@ResetPasswordActivity)){
                val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {

                    try {
                        val success = it.getBoolean("success")
                        val successMessage = it.getString("successMessage")

                        if (success) {

                            val intent = Intent(this@ResetPasswordActivity, LoginActivity::class.java)
                            Toast.makeText(this@ResetPasswordActivity, successMessage, Toast.LENGTH_SHORT).show()
                            registerPreferences.edit().clear().apply()
                            loginPreferences.edit().clear().apply()
                            profilePreferences.edit().clear().apply()
                            startActivity(intent)

                        } else {
                            Toast.makeText(this@ResetPasswordActivity, "Some error occurred", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException){
                        Toast.makeText(this@ResetPasswordActivity, "Some error occurred", Toast.LENGTH_SHORT).show()
                    }


                }, Response.ErrorListener {

                    if (applicationContext != null ){
                        Toast.makeText(this@ResetPasswordActivity, "Volley error occurred", Toast.LENGTH_SHORT).show()
                    }

                }){
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "261f19083bbcb4"
                        return headers
                    }
                }

                queue.add(jsonObjectRequest)


            } else {
                val dialog = AlertDialog.Builder(this@ResetPasswordActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection is not Found")
                dialog.setPositiveButton("Open Setting"){text, listener ->

                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    finish()

                }

                dialog.setNegativeButton("Exit"){ text, listener ->

                    ActivityCompat.finishAffinity(this@ResetPasswordActivity)

                }
                dialog.create()
                dialog.show()
            }
        }
    }
}