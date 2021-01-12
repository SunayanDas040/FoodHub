package com.internshala.foodhub.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "menu")
data class MenuEntity (
        @PrimaryKey val menu_id: String,
        @ColumnInfo(name = "menu_name") val menuName: String,
        @ColumnInfo(name = "cost_for_one") val costForOne: String,
        @ColumnInfo(name = "res_id") val res_id: String
)