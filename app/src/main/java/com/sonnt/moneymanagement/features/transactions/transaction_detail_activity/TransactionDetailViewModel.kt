package com.android.walletforest.transaction_detail_activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sonnt.moneymanagement.data.datasource.CategoryRepository
import com.sonnt.moneymanagement.data.datasource.TransactionRepository
import com.sonnt.moneymanagement.data.datasource.WalletRepository
import com.sonnt.moneymanagement.data.entities.Transaction
import com.sonnt.moneymanagement.features.base.BaseViewModel

class TransactionDetailViewModel() : BaseViewModel() {
    private val currentId = 0L
    var transaction: LiveData<Transaction> = MutableLiveData()
    val categories = CategoryRepository.categoryMap
    val wallets = WalletRepository.walletMap

    fun setTransactionId(id: Long) {
        if (currentId == id) return
        transaction = TransactionRepository.getTransaction(id)
    }

    fun updateTransaction(transaction: Transaction) {
        if (transaction == this.transaction.value)
            return

        TransactionRepository.updateTransaction(transaction, this.transaction.value!!)
    }

    fun insertTransaction(transaction: Transaction) {
        TransactionRepository.insertTransaction(transaction)
    }

    fun deleteTransaction(transaction: Transaction) {
        TransactionRepository.deleteTransaction(transaction)
    }
}