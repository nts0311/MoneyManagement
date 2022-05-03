package com.sonnt.moneymanagement.data.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object NetworkModule {
    var moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    var retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://127.0.0.1:8080")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    var authService: AuthService = retrofit.create(AuthService::class.java)
    var tranactionService: TransactionService = retrofit.create(TransactionService::class.java)
}