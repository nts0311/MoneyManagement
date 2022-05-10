package com.sonnt.moneymanagement.features.report.common

import com.sonnt.moneymanagement.MMApplication
import com.sonnt.moneymanagement.constant.TimeRange
import com.sonnt.moneymanagement.data.datasource.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class ChartUtils {
    companion object {
        fun getBarData(start: Long, end: Long, walletId: Long, timeRange: TimeRange) =
            TransactionRepository.getTransactionsBetweenRange(start, end, walletId)
                .map { ChartEntryGenerator.getBarChartData(it, start, end, timeRange) }
                .flowOn(Dispatchers.Default)

        fun getPieEntries(
            start: Long,
            end: Long,
            walletId: Long,
            excludeSubCate: Boolean
        ) =
            TransactionRepository.getTransactionsBetweenRange(start, end, walletId)
                .map { ChartEntryGenerator.getPieEntries(it, excludeSubCate) }
                .flowOn(Dispatchers.Default)
    }
}