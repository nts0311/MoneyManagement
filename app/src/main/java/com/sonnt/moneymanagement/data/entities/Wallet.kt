package com.sonnt.moneymanagement.data.entities


data class Wallet(
    var id:Long,
    var name:String,
    var imageId:Int,
    var amount: Long,
    var currency: String
) {

}