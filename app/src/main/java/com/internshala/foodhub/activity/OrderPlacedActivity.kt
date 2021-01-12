package com.internshala.foodhub.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.internshala.foodhub.R

class OrderPlacedActivity : AppCompatActivity() {

    lateinit var imgOrderPlaced: ImageView
    lateinit var txtOrderPlaced: TextView
    lateinit var btnOk: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_placed)

        imgOrderPlaced = findViewById(R.id.imgOrderPlaced)
        txtOrderPlaced = findViewById(R.id.txtOrderPlaced)
        btnOk = findViewById(R.id.btnOk)

        btnOk.setOnClickListener {
            val intent = Intent(this@OrderPlacedActivity, MainActivity::class.java)
            startActivity(intent)
        }
        finish()
    }
}