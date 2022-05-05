package com.sonnt.moneymanagement.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Wallet(
    @PrimaryKey(autoGenerate = true)
    var id:Long,
    var name:String,
    var imageId:Int,
    var amount: Long,
    var currency: String
)