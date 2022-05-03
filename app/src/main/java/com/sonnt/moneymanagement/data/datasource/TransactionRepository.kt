package com.sonnt.moneymanagement.data.datasource

import com.sonnt.moneymanagement.data.dto.request.GetTransactionBetweenDateRequest
import com.sonnt.moneymanagement.data.dto.response.TransactionPage
import com.sonnt.moneymanagement.data.entities.Transaction
import com.sonnt.moneymanagement.data.network.ApiResult
import com.sonnt.moneymanagement.data.network.NetworkModule
import com.sonnt.moneymanagement.data.network.httpRequest
import kotlinx.coroutines.flow.Flow

object TransactionRepository {

    private val transactionService = NetworkModule.tranactionService

    suspend fun getTransactionsBetweenRange(
        start: Long,
        end: Long,
        walletId: Long
    ): Flow<ApiResult<TransactionPage>> {
        //no paging first lol
        val request = GetTransactionBetweenDateRequest(walletId,start,end,0,1000)
        return httpRequest {
            transactionService.getTransactionsBetweenDate(request)
        }
    }
}