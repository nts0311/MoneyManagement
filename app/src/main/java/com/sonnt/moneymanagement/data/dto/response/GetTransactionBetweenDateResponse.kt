package com.sonnt.moneymanagement.data.dto.response

import com.sonnt.moneymanagement.data.entities.Transaction

class GetTransactionBetweenDateResponse: BaseResponse<TransactionPage>()
{
    override var data: TransactionPage? = null
}

data class TransactionPage(
    var page: Int = 0,
    var size: Int = 0,
    var data: List<Transaction> = listOf()
)
