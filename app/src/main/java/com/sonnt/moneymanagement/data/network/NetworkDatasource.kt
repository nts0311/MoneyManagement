package com.sonnt.moneymanagement.data.network

import com.sonnt.moneymanagement.data.dto.response.BaseResponse
import com.sonnt.moneymanagement.utils.Common
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

sealed class ApiResult <out T> (val data: T?, val message:String?) {
    data class Success<out R>(val _data: R?): ApiResult<R>(
        data = _data,
        message = null
    )

    data class Error(val exception: String): ApiResult<Nothing>(
        data = null,
        message = exception
    )

    class Loading<out R>: ApiResult<R>(
        data = null,
        message = null
    )
}

suspend fun <T, R> httpRequest(block: suspend () -> Response<R>): Flow<ApiResult<T>> where R: BaseResponse<T> = flow {
    emit(ApiResult.Loading())
    try {

        val response = block()

        if (response.isSuccessful) {

            val responseBody = response.body() as BaseResponse<T>

            if(responseBody.code == 0) {
                emit(ApiResult.Success(responseBody.data))
            }
            else {
                emit(ApiResult.Error(responseBody.msg))
            }
        }
        else {
            if (response.code() == 401 || response.code() == 403) Common.logout()
            else {
                emit(ApiResult.Error("An error occurred! Please try again later. HTTP ${response.code()}"))
            }
        }
    }
    catch (e: Exception) {
        emit(ApiResult.Error(e.message.toString()))
    }
}