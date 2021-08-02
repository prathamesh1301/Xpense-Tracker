package com.example.xpensetracker.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface XpenseDao {

    @Insert
    suspend fun insert(xpenseData: XpenseData)

    @Query("SELECT * FROM xpenseTable ORDER BY id DESC")
    suspend fun getAllData():List<XpenseData>

    @Delete
    suspend fun delete(xpenseData: XpenseData)

    @Query("DELETE FROM xpenseTable")
    suspend fun deleteAll()
}