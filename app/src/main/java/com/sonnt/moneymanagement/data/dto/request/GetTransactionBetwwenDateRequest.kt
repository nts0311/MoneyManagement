package com.sonnt.moneymanagement.data.dto.request
data class GetTransactionBetweenDateRequest(
    var walletId: Long? = null,
    var startDate: Long = 0L,
    var endDate: Long = 0L,
    var page: Int = 0,
    var size: Int = 30
)