package com.sonnt.moneymanagement.data.dto.request

data class UpdateWalletRequest(
    val userId: Long,
    val walletId: Long,
    val name:String = "",
    val imageUrl: String = "",
    val amount: Long = 0L,
    val currency: String = ""
)
