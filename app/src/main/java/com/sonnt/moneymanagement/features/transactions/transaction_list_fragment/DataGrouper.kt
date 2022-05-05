package com.sonnt.moneymanagement.features.transactions.transaction_list_fragment

import com.sonnt.moneymanagement.constant.Constants
import com.sonnt.moneymanagement.constant.TimeRange
import com.sonnt.moneymanagement.constant.ViewType
import com.sonnt.moneymanagement.data.datasource.CategoryRepository
import com.sonnt.moneymanagement.data.entities.Transaction
import com.sonnt.moneymanagement.data.mm_context.MMContext
import com.sonnt.moneymanagement.utils.toLocalDate
import kotlinx.coroutines.yield


class DataGrouper {

    private var transactions: List<Transaction> = listOf()

    private var result: MutableList<DataItem> = mutableListOf()
    private var viewType: ViewType = ViewType.TRANSACTION

    fun filterTransactions(transactions: List<Transaction>, filteringParams: FilteringParams?): List<Transaction> {
        return if (filteringParams == null) transactions
        else {
            //No category to filter
            if (filteringParams.categoryIdToFilter == -1L) {
                transactions.filter { transaction -> transaction.type==filteringParams.transactionType }
            }
            else {
                val subCategoryId = mutableListOf<Long>()
                subCategoryId.add(filteringParams.categoryIdToFilter)

                if (!filteringParams.excludeSubCate)
                    CategoryRepository.categoryMap.values.forEach { category ->
                        if (category.parentId == filteringParams.categoryIdToFilter)
                            subCategoryId.add(category.id)
                    }


                transactions.filter { transaction ->
                    subCategoryId.contains(transaction.categoryId)
                            && transaction.type==filteringParams.transactionType
                }
            }
        }
    }

    suspend fun doGrouping(
        transactions: List<Transaction>,
        timeRange: TimeRange,
        viewType: ViewType
    ): List<DataItem> {

        this.transactions = transactions
        this.viewType = viewType

        if (transactions.isEmpty()) return mutableListOf()

        if (result.isNotEmpty())
            result.clear()

        if (viewType == ViewType.TRANSACTION) {
            when (timeRange) {
                TimeRange.WEEK, TimeRange.MONTH -> group { dividerItem, transaction ->
                    dividerItem.date.dayOfMonth == toLocalDate(transaction.date).dayOfMonth
                }

                TimeRange.YEAR -> group { dividerItem, transaction ->
                    dividerItem.date.monthValue == toLocalDate(transaction.date).monthValue
                }

                TimeRange.CUSTOM -> group { dividerItem, transaction ->
                    val transactionDate = toLocalDate(transaction.date)
                    (dividerItem.date.monthValue == transactionDate.monthValue)
                            && (dividerItem.date.dayOfMonth == transactionDate.dayOfMonth)
                }
            }
        } else {
            group { dividerItem, transaction ->
                dividerItem.categoryId == transaction.categoryId
            }
        }

        return result
    }

    private fun cmpLong(l1: Long, l2: Long): Int {
        when {
            l1 > l2 -> return 1
            l1 == l2 -> return 0
            l1 < l2 -> return -1
        }
        return 0
    }

    private suspend fun group(belongToGroup: (DataItem.DividerItem, Transaction) -> Boolean) {
        if (transactions.isEmpty()) return

        if (result.isNotEmpty())
            result.clear()

        transactions = if (viewType == ViewType.TRANSACTION) {
            transactions.sortedWith { t1: Transaction, t2: Transaction ->

                if (t1.date == t2.date)
                    cmpLong(t2.id, t1.id)
                else
                    cmpLong(t2.date, t1.date)
            }
        } else {
            transactions.sortedWith { t1: Transaction, t2: Transaction ->

                if (t1.categoryId == t2.categoryId) {
                    if (t2.date == t1.date)
                        cmpLong(t2.id, t1.id)
                    else
                        cmpLong(t2.date, t1.date)
                } else
                    cmpLong(t1.categoryId, t2.categoryId)
            }
        }


        var totalAmount = 0L
        var numOfTransaction = 0

        var currentDividerItem = DataItem.DividerItem(
            toLocalDate(transactions[0].date), transactions[0].categoryId,
            totalAmount, numOfTransaction
        )

        result.add(currentDividerItem)

        for (transaction in transactions) {

            //check if the coroutine is canceled, stop the work immediately to prevent cases like
            //rotating the device and it adds some excited transaction to the list
            yield()

            if (belongToGroup(currentDividerItem, transaction)) {
                result.add(DataItem.TransactionItem(transaction))

                if (transaction.type == Constants.TYPE_EXPENSE)
                    totalAmount -= transaction.amount
                else
                    totalAmount += transaction.amount

                numOfTransaction++

                if (transactions.last() === transaction) {
                    currentDividerItem.numOfTransactions = numOfTransaction
                    currentDividerItem.totalAmount = totalAmount
                }
            } else {
                currentDividerItem.numOfTransactions = numOfTransaction
                currentDividerItem.totalAmount = totalAmount

                numOfTransaction = 1

                totalAmount = if (transaction.type == Constants.TYPE_EXPENSE)
                    -transaction.amount
                else
                    transaction.amount


                val dividerItem = DataItem.DividerItem(
                    toLocalDate(transaction.date), transaction.categoryId,
                    totalAmount, numOfTransaction
                )

                currentDividerItem = dividerItem

                result.add(dividerItem)
                result.add(DataItem.TransactionItem(transaction))
            }
        }

    }
}