package com.sonnt.moneymanagement.features.report.bar_chart_detail_activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import com.sonnt.moneymanagement.features.transactions.transaction_list_activity.TransactionListActivity
import com.sonnt.moneymanagement.R
import com.sonnt.moneymanagement.constant.TimeRange
import com.sonnt.moneymanagement.features.report.common.BarChartData
import com.sonnt.moneymanagement.features.report.common.ChartsDrawer
import com.sonnt.moneymanagement.features.transactions.transaction_list_fragment.END_TIME_PARAM
import com.sonnt.moneymanagement.features.transactions.transaction_list_fragment.START_TIME_PARAM
import com.sonnt.moneymanagement.features.transactions.transaction_list_fragment.TIME_RANGE_PARAM
import com.sonnt.moneymanagement.features.transactions.transaction_list_fragment.WALLET_ID_PARAM
import com.sonnt.moneymanagement.utils.NumberFormatter
import kotlinx.android.synthetic.main.activity_bar_chart_detail.*
import kotlin.math.absoluteValue

class BarChartDetailActivity : AppCompatActivity() {

    companion object {
        const val BAR_DATA_KEY = "bar_data_key"
        const val WALLET_ID_KEY = "wallet_id"
    }

    private var walletId = 1L;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bar_chart_detail)

        setSupportActionBar(bar_chart_detail_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val barDataList = intent.getSerializableExtra(BAR_DATA_KEY) as List<BarChartData>
        walletId = intent.getLongExtra(WALLET_ID_KEY, 1L)

        ChartsDrawer.setupBarChart(bar_chart)
        ChartsDrawer.drawBarChartChart(bar_chart, barDataList)

        addRangeData(barDataList)
    }

    private fun addRangeData(barDataList: List<BarChartData>) {
        val inflater = LayoutInflater.from(this)

        barDataList.forEach {
            val itemRoot = inflater.inflate(R.layout.item_bar_data, root_layout, false)

            itemRoot.findViewById<TextView>(R.id.range_label_txt).text = it.xAxisLabel
            itemRoot.findViewById<TextView>(R.id.income_txt).text = NumberFormatter.format(it.totalIncome)
            itemRoot.findViewById<TextView>(R.id.expense_txt).text =
                NumberFormatter.format(it.totalExpense.absoluteValue)
            itemRoot.findViewById<TextView>(R.id.net_income_txt).text =
                NumberFormatter.format((it.totalIncome - it.totalExpense.absoluteValue))

            itemRoot.setOnClickListener { _ ->
                val transactionListIntent =
                    Intent(this@BarChartDetailActivity, TransactionListActivity::class.java)

                transactionListIntent.putExtra(START_TIME_PARAM, it.startDate)
                transactionListIntent.putExtra(END_TIME_PARAM, it.endDate)
                transactionListIntent.putExtra(WALLET_ID_PARAM, walletId)
                transactionListIntent.putExtra(TIME_RANGE_PARAM, TimeRange.MONTH.value)

                startActivity(transactionListIntent)
            }

            root_layout.addView(itemRoot)
        }
    }
}