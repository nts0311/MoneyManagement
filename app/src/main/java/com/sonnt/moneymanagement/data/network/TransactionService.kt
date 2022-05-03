package com.sonnt.moneymanagement.data.network

import com.sonnt.moneymanagement.data.dto.request.GetTransactionBetweenDateRequest
import com.sonnt.moneymanagement.data.dto.response.BaseResponse
import com.sonnt.moneymanagement.data.dto.response.GetTransactionBetweenDateResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TransactionService {
    @POST("transaction/get-between-date")
    fun getTransactionsBetweenDate(@Body body: GetTransactionBetweenDateRequest): Response<GetTransactionBetweenDateResponse>
}