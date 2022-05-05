package com.sonnt.moneymanagement.data.entities


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sonnt.moneymanagement.constant.Constants

@Entity
data class Category(
    @PrimaryKey(autoGenerate = true)
    var id:Long,
    var parentId :Long,
    var name: String,
    var type : String = Constants.TYPE_EXPENSE,
    var imageId:Int
)