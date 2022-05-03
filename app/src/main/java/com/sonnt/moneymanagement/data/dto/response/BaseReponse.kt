package com.sonnt.moneymanagement.data.dto.response

open class BaseResponse<T>(
    var code: Int = 0,
    var msg: String = ""
) {
   open var data: T? = null
}
