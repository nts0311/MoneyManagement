package com.sonnt.moneymanagement.features.wallet.wallet_detail_activity

import androidx.lifecycle.*
import com.sonnt.moneymanagement.data.datasource.WalletRepository
import com.sonnt.moneymanagement.data.entities.Wallet
import com.sonnt.moneymanagement.features.base.BaseViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class WalletDetailActivityViewModel() : BaseViewModel() {

    var wallet: LiveData<Wallet> = MutableLiveData()

    fun setCurrentWallet(id: Long) {
        wallet = WalletRepository.getWalletById(id).asLiveData()
    }

    fun addWallet(wallet: Wallet) {
        viewModelScope.launch {
            WalletRepository.insertWallet(wallet)
        }
    }

    fun updateWallet(newWallet: Wallet)
    {
        viewModelScope.launch {
            WalletRepository.updateWallet(newWallet)
        }
    }

    fun deleteCurrentWallet()
    {
        viewModelScope.launch {
            WalletRepository.deleteWallet(wallet.value!!)
        }
    }
}