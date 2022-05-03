package com.sonnt.moneymanagement.data.datasource

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sonnt.moneymanagement.data.entities.Wallet

object WalletRepository {
    fun getWalletById(id: Long): LiveData<Wallet> {
        return MutableLiveData()
    }
}