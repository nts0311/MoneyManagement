package com.sonnt.moneymanagement.features.report.common

import java.io.Serializable

data class BarChartData(
    var xAxisLabel: String,
    var totalIncome: Long,
    var totalExpense: Long,
    var startDate: Long,
    var endDate: Long
) : Serializable