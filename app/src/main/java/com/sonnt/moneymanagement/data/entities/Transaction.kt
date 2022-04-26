package com.sonnt.moneymanagement.data.entities


data class Transaction(
    var id: Long,
    var categoryId: Long,
    var walletId: Long,
    var type: String,
    var amount: Long,
    var note: String,
    var date: Long
) {
}