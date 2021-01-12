package com.internshala.foodhub.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "restaurants")
data class RestaurantEntity (
    @PrimaryKey val res_id: String,
    @ColumnInfo(name = "res_name") val restaurantName: String,
    @ColumnInfo(name = "res_rating") val restaurantRating: String,
    @ColumnInfo(name = "cost_for_one") val costForOne: String,
    @ColumnInfo(name = "res_image") val restaurantImage: String
)