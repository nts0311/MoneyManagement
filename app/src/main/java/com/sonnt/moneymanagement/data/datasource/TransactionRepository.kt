package com.sonnt.moneymanagement.data.datasource


import com.sonnt.moneymanagement.MMApplication
import com.sonnt.moneymanagement.constant.Constants
import com.sonnt.moneymanagement.constant.TimeRange
import com.sonnt.moneymanagement.data.AppDatabase
import com.sonnt.moneymanagement.data.dao.TransactionDao
import com.sonnt.moneymanagement.data.entities.Category
import com.sonnt.moneymanagement.data.entities.Transaction
import com.sonnt.moneymanagement.data.entities.Wallet
import com.sonnt.moneymanagement.features.report.report_record_fragment.ChartEntryGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

object TransactionRepository {
    private val appDatabase = AppDatabase.getInstance(MMApplication.self)

    private val transactionDao: TransactionDao = appDatabase.transactionDao

    //caching list of transactions of each wallet, avoiding database query
    private var fetchedRange: MutableMap<String, Flow<List<Transaction>>> = mutableMapOf()

    fun getTransaction(id: Long) = appDatabase.transactionDao.getTransaction(id)

    //Get the list of transactions in a specific wallet and specific period
    //If the list is not cached, fetch the list from database and cache it
    fun getTransactionsBetweenRange(
        start: Long,
        end: Long,
        walletId: Long
    ): Flow<List<Transaction>> {

        val key: String = if (walletId == 1L)
            "all-$start-$end"
        else
            "$walletId-$start-$end"

        return if (fetchedRange.containsKey(key))
            fetchedRange[key]!!
        else {
            val transactions =
                if (walletId == 1L)
                    transactionDao.getTransactionsBetweenRange(start, end)
                else
                    transactionDao.getTransactionsBetweenRangeOfWallet(
                        start,
                        end,
                        walletId
                    )

            fetchedRange[key] = transactions.distinctUntilChanged()

            transactions
        }
    }

    fun insertTransaction(transaction: Transaction) : Job =
        GlobalScope.launch {
            val currentWallet = WalletRepository.currentWallet.value?.copy() ?: return@launch
            //walletMap[transaction.walletId]!!.copy()
            transactionDao.insertTransaction(transaction)

            if (transaction.type == Constants.TYPE_EXPENSE)
                currentWallet.amount -= transaction.amount
            else
                currentWallet.amount += transaction.amount

            WalletRepository.updateWallet(currentWallet)
        }

    fun deleteTransaction(transaction: Transaction) : Job =
        GlobalScope.launch {
            val currentWallet = WalletRepository.currentWallet.value?.copy() ?: return@launch//walletMap[transaction.walletId]!!.copy()
            transactionDao.deleteTransaction(transaction)

            if (transaction.type == Constants.TYPE_EXPENSE)
                currentWallet.amount += transaction.amount
            else
                currentWallet.amount -= transaction.amount

            WalletRepository.updateWallet(currentWallet)
        }

    fun updateTransaction(newTransaction: Transaction, oldTransaction: Transaction) {
        GlobalScope.launch {

            deleteTransaction(oldTransaction).join()
            insertTransaction(newTransaction).join()
        }
    }

    fun getBarData(start: Long, end: Long, walletId: Long, timeRange: TimeRange) =
        getTransactionsBetweenRange(start, end, walletId)
            .map { ChartEntryGenerator.getBarChartData(it, start, end, timeRange) }
            .flowOn(Dispatchers.Default)

    fun getPieEntries(
        start: Long,
        end: Long,
        walletId: Long,
        excludeSubCate: Boolean
    ) =
        getTransactionsBetweenRange(start, end, walletId)
            .map { ChartEntryGenerator.getPieEntries(it, excludeSubCate, MMApplication.self) }
            .flowOn(Dispatchers.Default)
}