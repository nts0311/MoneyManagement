package com.sonnt.moneymanagement.features.report.common

import com.github.mikephil.charting.data.PieEntry

class PieChartData{
    var incomePieEntries : List<PieEntry> = listOf()
    var expensePieEntries : List<PieEntry> = listOf()

    var incomeCategoryInfo : List<Pair<Long, Long>> = listOf()
    var expenseCategoryInfo : List<Pair<Long, Long>> = listOf()
}