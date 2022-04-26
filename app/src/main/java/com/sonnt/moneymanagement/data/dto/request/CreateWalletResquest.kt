package com.sonnt.moneymanagement.data.dto.request

data class CreateWalletRequest(
    val userId: Long,
    val name:String = "",
    val imageUrl: String = "",
    val amount: Long = 0L,
    val currency: String = ""
)
