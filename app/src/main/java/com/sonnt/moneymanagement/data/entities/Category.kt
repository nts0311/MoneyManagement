package com.sonnt.moneymanagement.data.entities


data class Category(
    var id:Long,
    var parentId :Long,
    var name: String,
    var type : String = "",
    var imageId:Int
) {
}