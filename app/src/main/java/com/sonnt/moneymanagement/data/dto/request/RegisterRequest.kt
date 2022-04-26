package com.sonnt.moneymanagement.data.dto.request

data class RegisterRequest(
    val username: String = "",
    val password: String = "",
    val email: String = "",
    val name: String = ""
)
