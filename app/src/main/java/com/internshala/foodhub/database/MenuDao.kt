package com.internshala.foodhub.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


@Dao
interface MenuDao {

    @Insert
    fun menuInsert(menuEntity: MenuEntity)

    @Delete
    fun menuDelete(menuEntity: MenuEntity)

    @Query("SELECT * FROM menu")
    fun getAllMenu() : List<MenuEntity>

    @Query("SELECT * FROM menu WHERE menu_id = :menuId")
    fun getMenuById(menuId: String) : MenuEntity

    @Query("DELETE FROM menu")
    fun deleteAllEntries()
}