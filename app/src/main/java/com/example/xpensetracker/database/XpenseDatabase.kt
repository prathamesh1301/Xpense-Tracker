package com.example.xpensetracker.database


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(XpenseData::class),version = 1,exportSchema = false)
abstract class XpenseDatabase:RoomDatabase() {
    abstract fun getXpenseDao() : XpenseDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: XpenseDatabase? = null

        fun getDatabase(context: Context): XpenseDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    XpenseDatabase::class.java,
                    "xpensedatabase2"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }


}