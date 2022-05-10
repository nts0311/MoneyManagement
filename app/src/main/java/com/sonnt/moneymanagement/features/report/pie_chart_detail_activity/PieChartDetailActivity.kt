package com.sonnt.moneymanagement.features.report.pie_chart_detail_activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.sonnt.moneymanagement.features.transactions.transaction_list_activity.TransactionListActivity
import com.sonnt.moneymanagement.R
import com.sonnt.moneymanagement.constant.Constants
import com.sonnt.moneymanagement.constant.TimeRange
import com.sonnt.moneymanagement.data.datasource.CategoryRepository
import com.sonnt.moneymanagement.data.mm_context.MMContext
import com.sonnt.moneymanagement.features.report.common.ChartsDrawer
import com.sonnt.moneymanagement.features.transactions.transaction_list_fragment.*
import com.sonnt.moneymanagement.utils.NumberFormatter
import kotlinx.android.synthetic.main.activity_pie_chart_detail.*
import kotlinx.android.synthetic.main.activity_pie_chart_detail.root_layout

class PieChartDetailActivity : AppCompatActivity() {

    companion object {
        const val IS_EXPENSE_KEY = "is_expense_key"
        const val START_DATE_KEY = "start_date_key"
        const val END_DATE_KEY = "end_date_key"
        const val WALLET_ID_KEY = "wallet_id"
        const val EXCLUDE_SUB_CATE_KEY="exclude_sub_cate"
    }

    private var startDate : Long = 0L
    private var endDate : Long = 0L
    private var walletId : Long = 1L
    private var isExpenseChart = true

    private var excludeSubCategory = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pie_chart_detail)

        setSupportActionBar(pie_chart_detail_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val toolbarTitle = if(isExpenseChart) getString(R.string.expense)
        else getString(R.string.income)
        supportActionBar?.title = toolbarTitle

        val pieChartData = PieChartDetailData.pieChartData
        isExpenseChart = intent.getBooleanExtra(IS_EXPENSE_KEY, true)

        intent.apply {
            startDate = getLongExtra(START_DATE_KEY, 0L)
            endDate = getLongExtra(END_DATE_KEY, 0L)
            walletId = getLongExtra(WALLET_ID_KEY, 1L)
            excludeSubCategory = getBooleanExtra(EXCLUDE_SUB_CATE_KEY, false)
        }

        if(isExpenseChart)
        {
            ChartsDrawer.drawPieChart(pie_chart, pieChartData.expensePieEntries, true)
            addRangeInfo(pieChartData.expenseCategoryInfo)
        }
        else
        {
            ChartsDrawer.drawPieChart(pie_chart, pieChartData.incomePieEntries, true)
            addRangeInfo(pieChartData.incomeCategoryInfo)
        }
    }

    private fun addRangeInfo(data : List<Pair<Long, Long>>)
    {
        val inflater = LayoutInflater.from(this)
        val colorRed = ContextCompat.getColor(this, R.color.expense_text)
        val colorBlue = ContextCompat.getColor(this, R.color.income_text)

        for(pair in data)
        {
            val rootItem = inflater.inflate(R.layout.item_pie_data, root_layout, false)

            val category = CategoryRepository.categoryMap[pair.first]

            rootItem.findViewById<ImageView>(R.id.cate_image).setImageResource(category!!.imageId)
            rootItem.findViewById<TextView>(R.id.cate_name_txt).text = category.name

            val amountTxt = rootItem.findViewById<TextView>(R.id.amount_txt)

            if(category.type == Constants.TYPE_INCOME)
                amountTxt.setTextColor(colorBlue)
            else
                amountTxt.setTextColor(colorRed)

            amountTxt.text = NumberFormatter.format(pair.second)

            rootItem.setOnClickListener { _ ->
                val transactionListIntent =
                    Intent(this@PieChartDetailActivity, TransactionListActivity::class.java)

                transactionListIntent.putExtra(START_TIME_PARAM, startDate)
                transactionListIntent.putExtra(END_TIME_PARAM, endDate)
                transactionListIntent.putExtra(WALLET_ID_PARAM, walletId)
                transactionListIntent.putExtra(TIME_RANGE_PARAM, TimeRange.MONTH.value)

                val transactionType = if(isExpenseChart) Constants.TYPE_EXPENSE
                else Constants.TYPE_INCOME

                val filteringParams = FilteringParams(pair.first,transactionType,excludeSubCategory)

                transactionListIntent.putExtra(RANGE_PARAMS, filteringParams)

                startActivity(transactionListIntent)
            }

            root_layout.addView(rootItem)
        }
    }
}