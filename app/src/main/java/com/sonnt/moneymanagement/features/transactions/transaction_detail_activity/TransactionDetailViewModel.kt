package com.sonnt.moneymanagement.features.transactions.transaction_detail_activity

import androidx.lifecycle.*
import com.sonnt.moneymanagement.data.datasource.CategoryRepository
import com.sonnt.moneymanagement.data.datasource.TransactionRepository
import com.sonnt.moneymanagement.data.datasource.WalletRepository
import com.sonnt.moneymanagement.data.entities.Transaction
import com.sonnt.moneymanagement.features.base.BaseViewModel
import kotlinx.coroutines.launch

class TransactionDetailViewModel() : BaseViewModel() {
    private val currentId = 0L
    var transaction: LiveData<Transaction> = MutableLiveData()
    val categories = CategoryRepository.categoryMap
    val wallets = WalletRepository.walletMap

    fun setTransactionId(id: Long) {
        if (currentId == id) return
        transaction = TransactionRepository.getTransaction(id).asLiveData()
    }

    fun updateTransaction(transaction: Transaction) {
        if (transaction == this.transaction.value)
            return

        viewModelScope.launch {
            TransactionRepository.updateTransaction(transaction, this@TransactionDetailViewModel.transaction.value!!)
        }
    }

    fun insertTransaction(transaction: Transaction) {
        viewModelScope.launch {
            TransactionRepository.insertTransaction(transaction)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            TransactionRepository.deleteTransaction(transaction)
        }
    }
}