package com.example.xpensetracker.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "xpenseTable")
data class XpenseData(val title:String,val amount:String,val date:String,val category:String):Serializable {
    @PrimaryKey(autoGenerate = true)
    var id:Int=0
}