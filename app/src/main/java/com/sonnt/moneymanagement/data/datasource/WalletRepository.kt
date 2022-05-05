package com.sonnt.moneymanagement.data.datasource

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.asLiveData
import com.sonnt.moneymanagement.MMApplication
import com.sonnt.moneymanagement.data.AppDatabase
import com.sonnt.moneymanagement.data.entities.Wallet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

object WalletRepository {
    private val walletDao = AppDatabase.getInstance(MMApplication.self).walletDao

    private var _walletsMap: MutableMap<Long, Wallet> = mutableMapOf()
    val walletMap: Map<Long, Wallet> = _walletsMap

    private val scope = CoroutineScope(Dispatchers.Default)

    private var _currentWalletId: MutableLiveData<Long> = MutableLiveData()
    var currentWallet: LiveData<Wallet> = Transformations.switchMap(_currentWalletId)
    {
        getWalletById(it).asLiveData()
    }

    fun setCurrentWallet(walletId: Long) {
        _currentWalletId.value = walletId
    }

    init {
        scope.launch {
            val walletList = walletDao.getWallets().first()

            for (wallet in walletList) {
                _walletsMap[wallet.id] = wallet
            }
        }
    }

    suspend fun insertWallet(wallet: Wallet) {
        val newId = walletDao.insertWallet(wallet)
        wallet.id = newId
        _walletsMap[newId] = wallet
        //update the master wallet
        val masterWallet = _walletsMap[1L]
        if (masterWallet!=null) {
            masterWallet.amount += wallet.amount
            updateWallet(masterWallet)

            _walletsMap[1L] = masterWallet
        }
    }

    suspend fun updateWallet(wallet: Wallet) {
        walletDao.updateWallet(wallet)


        //update the master wallet
        val masterWallet = _walletsMap[1L]
        if (masterWallet!=null) {
            val oldAmount = walletMap[wallet.id]?.amount
            masterWallet.amount += (wallet.amount - oldAmount!!)

            walletDao.updateWallet(masterWallet)

            _walletsMap[1L] = masterWallet

        }

        _walletsMap[wallet.id] = wallet
    }

    suspend fun deleteWallet(wallet: Wallet) {
        val balance = walletMap[wallet.id]?.amount
        walletDao.deleteWallet(wallet)

        //update the master wallet
        val masterWallet = _walletsMap[1L]
        if (masterWallet!=null) {

            masterWallet.amount -= balance!!
            walletDao.updateWallet(masterWallet)

            _walletsMap[1L] = masterWallet
        }

        _walletsMap.remove(wallet.id)
    }

    fun getWalletById(id: Long) = walletDao.getWalletById(id)

    fun getWallets() = walletDao.getWallets()
}