package com.internshala.foodhub.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


@Dao
interface RestaurantDao {

    @Insert
    fun insertRestaurant(restaurantEntity: RestaurantEntity)

    @Delete
    fun deleteRestaurant(restaurantEntity: RestaurantEntity)

    @Query("SELECT * FROM restaurants")
    fun getAllRestaurant() : List<RestaurantEntity>

    @Query("SELECT * FROM restaurants WHERE res_id = :resId")
    fun getRestaurantById(resId: String) : RestaurantEntity
}