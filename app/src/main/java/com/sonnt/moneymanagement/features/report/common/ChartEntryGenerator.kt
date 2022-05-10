package com.sonnt.moneymanagement.features.report.common

import android.content.Context
import android.graphics.drawable.ScaleDrawable
import android.view.Gravity
import com.github.mikephil.charting.data.PieEntry
import com.sonnt.moneymanagement.MMApplication
import com.sonnt.moneymanagement.R
import com.sonnt.moneymanagement.constant.Constants
import com.sonnt.moneymanagement.constant.TimeRange
import com.sonnt.moneymanagement.data.datasource.CategoryRepository
import com.sonnt.moneymanagement.data.entities.Transaction
import com.sonnt.moneymanagement.utils.*
import kotlinx.coroutines.yield


class ChartEntryGenerator() {

    companion object {
        suspend fun getBarChartData(
            transactions: List<Transaction>,
            start: Long,
            end: Long,
            timeRange: TimeRange
        ): List<BarChartData> {

            if (transactions.isEmpty()) return listOf()

            val rangeEndDate = end

            return when (timeRange) {
                TimeRange.MONTH -> {
                    getBarChartData(
                        transactions,
                        start,
                        toEpoch(toLocalDate(start).plusDays(6)),
                        timeRange
                    )
                    { _, end ->
                        val endDate = toLocalDate(end)
                        val nextStartDate = endDate.plusDays(1)
                        val nextEndDate = endDate.plusDays(7)

                        val nextEndDateL = toEpoch(nextEndDate)

                        if (nextEndDateL <= rangeEndDate)
                            Pair(toEpoch(nextStartDate), nextEndDateL)
                        else {
                            val endOfMonth = nextEndDate.minusDays(nextEndDate.dayOfMonth.toLong())
                            Pair(toEpoch(nextStartDate), toEpoch(endOfMonth))
                        }
                    }
                }

                TimeRange.WEEK -> {
                    getBarChartData(
                        transactions,
                        start,
                        toLocalDate(start).plusDays(1).toEpochMilli() - 1,
                        timeRange
                    ) { _, previousEnd ->

                        val nextStartDate = toLocalDate(previousEnd).plusDays(1L).toEpochMilli()
                        val nextEndDate = toLocalDate(previousEnd).plusDays(2L).toEpochMilli() - 1

                        if (nextEndDate <= rangeEndDate)
                            Pair(nextStartDate, nextEndDate)
                        else
                            Pair(nextStartDate, rangeEndDate)
                    }
                }

                TimeRange.YEAR -> {
                    getBarChartData(
                        transactions,
                        start,
                        toLocalDate(start).plusMonths(1).minusDays(1).toEpochMilli(),
                        timeRange
                    )
                    { _, previousEnd ->
                        val nextStartDate = toLocalDate(previousEnd).plusDays(1)
                        val nextEndDate = nextStartDate.plusMonths(1).minusDays(1)

                        if (nextEndDate.toEpochMilli() <= rangeEndDate)
                            Pair(nextStartDate.toEpochMilli(), nextEndDate.toEpochMilli())
                        else
                            Pair(nextStartDate.toEpochMilli(), rangeEndDate)
                    }
                }

                TimeRange.CUSTOM -> getBarChartData(
                    transactions,
                    start,
                    end,
                    timeRange
                ) { start, end ->
                    Pair(start, end)
                }
            }
        }

        private fun getLabel(timeRange: TimeRange, startTime: Long, endTime: Long): String =
            when (timeRange) {
                TimeRange.MONTH -> getMonthAxisLabel(startTime, endTime)
                TimeRange.WEEK -> getWeekAxisLabel(startTime)
                TimeRange.YEAR -> getYearAxisLabel(startTime)
                TimeRange.CUSTOM -> getCustomAxisLabel(startTime, endTime)
            }

        private suspend fun getBarChartData(
            transactions: List<Transaction>,
            start: Long,
            end: Long,
            timeRange: TimeRange,
            getNextRange: (Long, Long) -> Pair<Long, Long>
        ): List<BarChartData> {

            var maxEntryCount = when (timeRange) {
                TimeRange.MONTH -> 5
                TimeRange.WEEK -> 7
                TimeRange.YEAR -> 12
                TimeRange.CUSTOM -> 1
            }

            if (end == Long.MAX_VALUE) maxEntryCount = 1

            var startTime = start
            var endTime = end

            val result = List(maxEntryCount) {
                val label = getLabel(timeRange, startTime, endTime)

                val result = BarChartData(label, 0, 0, startTime, endTime)

                val nextRange = getNextRange(startTime, endTime)
                startTime = nextRange.first
                endTime = nextRange.second

                result
            }

            for (barData in result) {
                yield()
                val transactionsInBarRange = transactions.filter { it.date in barData.startDate..barData.endDate }

                barData.totalExpense = transactionsInBarRange.filter { it.type == Constants.TYPE_EXPENSE }.fold(0L) {sum, transaction ->
                    sum - transaction.amount
                }

                barData.totalIncome = transactionsInBarRange.filter { it.type == Constants.TYPE_INCOME }.fold(0L) {sum, transaction ->
                    sum + transaction.amount
                }
            }

            return result
        }

        private fun getDrawable(context: Context, imageId: Int): ScaleDrawable {
            return ScaleDrawable(
                context.getDrawable(imageId),
                Gravity.CENTER,
                1f,
                1f
            ).apply {
                level = 5000
                invalidateSelf()
            }
        }

        suspend fun getPieEntries(
            transactions: List<Transaction>,
            excludeSubCate: Boolean
        ): PieChartData {
            val categoriesMap = CategoryRepository.categoryMap

            val incomeCategoryMap = mutableMapOf<Long, Long>()
            val expenseCategoryMap = mutableMapOf<Long, Long>()

            for (transaction in transactions) {
                yield()

                val key = if (excludeSubCate) transaction.categoryId
                else categoriesMap[transaction.categoryId]!!.parentId

                if (transaction.type == Constants.TYPE_INCOME) {
                    val value = incomeCategoryMap.getOrDefault(key, 0L)
                    incomeCategoryMap[key] = value + transaction.amount
                } else {
                    val value = expenseCategoryMap.getOrDefault(key, 0L)
                    expenseCategoryMap[key] = value + transaction.amount
                }
            }

            val result = PieChartData()

            result.incomeCategoryInfo =
                incomeCategoryMap.toList().sortedByDescending { (_, value) -> value }
            result.expenseCategoryInfo =
                expenseCategoryMap.toList().sortedByDescending { (_, value) -> value }
            result.incomePieEntries = toPieEntries(result.incomeCategoryInfo)
            result.expensePieEntries = toPieEntries(result.expenseCategoryInfo)

            return result
        }

        private suspend fun toPieEntries(
            pieList: List<Pair<Long, Long>>
        ): List<PieEntry> {
            val categoriesMap = CategoryRepository.categoryMap

            val pieEntries = mutableListOf<PieEntry>()

            if (pieList.size <= 5) {
                pieList.forEach {
                    yield()
                    val cateImage = getDrawable(MMApplication.self, categoriesMap[it.first]!!.imageId)
                    pieEntries.add(PieEntry(it.second.toFloat(), cateImage))
                }
            } else {
                for (i in 0..4) {
                    yield()
                    val cateImage = getDrawable(MMApplication.self, categoriesMap[pieList[i].first]!!.imageId)
                    pieEntries.add(PieEntry(pieList[i].second.toFloat(), cateImage))
                }

                var otherCateAmount = 0L
                for (i in 5 until pieList.size) {
                    yield()
                    otherCateAmount += pieList[i].second
                }

                pieEntries.add(
                    PieEntry(
                        otherCateAmount.toFloat(),
                        getDrawable(MMApplication.self, R.drawable.ic_category_other_chart)
                    )
                )
            }
            return pieEntries
        }

    }
}

