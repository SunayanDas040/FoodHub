package com.internshala.foodhub.activity

import android.app.AlertDialog
import android.content.Intent
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

class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var imgLogoImage: ImageView
    lateinit var txtInstruction: TextView
    lateinit var etMobileNumber: EditText
    lateinit var etEmail: EditText
    lateinit var btnNext: Button
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Forgot Password"

        imgLogoImage = findViewById(R.id.imgLogoImage)
        txtInstruction = findViewById(R.id.txtInstruction)
        etMobileNumber = findViewById(R.id.etMobileNumber)
        etEmail = findViewById(R.id.etEmail)
        btnNext = findViewById(R.id.btnNext)

        val jsonParams = JSONObject()
        jsonParams.put("mobile_number", etMobileNumber.text.toString())
        jsonParams.put("email", etEmail.text.toString())

        val queue = Volley.newRequestQueue(this@ForgotPasswordActivity)
        val url = "http://13.235.250.119/v2/forgot_password/fetch_result"

        btnNext.setOnClickListener {
            if (ConnectionManager().checkConnectivity(this@ForgotPasswordActivity)){

                val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {

                    try {
                        val data = it.getJSONObject("data")

                        val success = data.getBoolean("success")

                        if (success) {
                            val firstTry = data.getBoolean("first_try")
                            if (firstTry){
//                                val intent = Intent(this@ForgotPasswordActivity, ResetPasswordActivity::class.java)
//                                intent.putExtra("mobile_number", etMobileNumber.text)
//                                startActivity(intent)
                                val builder = androidx.appcompat.app.AlertDialog.Builder(this@ForgotPasswordActivity)
                                builder.setTitle("Information")
                                builder.setMessage("Please check your registered Email for the OTP.")
                                builder.setCancelable(false)
                                builder.setPositiveButton("Ok") { _, _ ->
                                    val intent = Intent(
                                        this@ForgotPasswordActivity,
                                        ResetPasswordActivity::class.java
                                    )
                                    intent.putExtra("user_mobile", etMobileNumber.text.toString())
                                    startActivity(intent)
                                }

                                builder.create().show()


                            } else {
//                                Toast.makeText(this@ForgotPasswordActivity,
//                                    "The OTP email would be sent only once in 24 hours. Hence, if you need to reset the password\n" +
//                                            "multiple times within 24 hours kindly use the same OTP",
//                                    Toast.LENGTH_SHORT
//                                ).show()

                                val builder = androidx.appcompat.app.AlertDialog.Builder(this@ForgotPasswordActivity)
                                builder.setTitle("Information")
                                builder.setMessage("Please refer to the previous email for the OTP.")
                                builder.setCancelable(false)
                                builder.setPositiveButton("Ok") { _, _ ->
                                    val intent = Intent(
                                        this@ForgotPasswordActivity,
                                        ResetPasswordActivity::class.java
                                    )
                                    intent.putExtra("user_mobile", etMobileNumber.text)
                                    startActivity(intent)
                                }
                                builder.create().show()
                            }

                        } else {
                            Toast.makeText(this@ForgotPasswordActivity, "Some error occurred", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException){
                        Toast.makeText(this@ForgotPasswordActivity, "Some unexpected error occurred", Toast.LENGTH_SHORT).show()
                    }

                }, Response.ErrorListener {

                    if (applicationContext != null){
                        Toast.makeText(this@ForgotPasswordActivity, "Volley error occurred", Toast.LENGTH_SHORT).show()
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
                val dialog = AlertDialog.Builder(this@ForgotPasswordActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection is not Found")
                dialog.setPositiveButton("Open Setting"){text, listener ->

                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    finish()

                }

                dialog.setNegativeButton("Exit"){ text, listener ->

                    ActivityCompat.finishAffinity(this@ForgotPasswordActivity)

                }
                dialog.create()
                dialog.show()
            }
        }

    }
}