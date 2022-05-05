package com.sonnt.moneymanagement.features.report.report_record_fragment

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sonnt.moneymanagement.features.report.bar_chart_detail_activity.BarChartDetailActivity
import com.sonnt.moneymanagement.features.report.pie_chart_detail_activity.PieChartDetailActivity
import com.sonnt.moneymanagement.features.report.pie_chart_detail_activity.PieChartDetailData
import com.sonnt.moneymanagement.R
import com.sonnt.moneymanagement.constant.TimeRange
import com.sonnt.moneymanagement.features.report.common.BarChartData
import com.sonnt.moneymanagement.features.report.common.ChartsDrawer
import com.sonnt.moneymanagement.features.report.common.PieChartData
import com.sonnt.moneymanagement.utils.NumberFormatter
import kotlinx.android.synthetic.main.fragment_report_record.*
import java.io.Serializable
import kotlin.math.absoluteValue

private const val START_TIME_PARAM = "startTime"
private const val END_TIME_PARAM = "endTime"
private const val WALLET_ID_PARAM = "walletId"
private const val TIME_RANGE_PARAM = "timeRange"

class ReportRecordFragment : Fragment() {
    private var startTime: Long? = null
    private var endTime: Long? = null
    private var walletId: Long? = null
    private var timeRange: String? = null
    private var viewModelKey = ""

    private val viewModel: ReportRecordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            startTime = it.getLong(START_TIME_PARAM)
            endTime = it.getLong(END_TIME_PARAM)
            walletId = it.getLong(WALLET_ID_PARAM)
            timeRange = it.getString(TIME_RANGE_PARAM, TimeRange.MONTH.value)
            viewModelKey = "$startTime - $endTime"
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (startTime != null && endTime != null && timeRange != null) {
            viewModel.setTimeRange(startTime!!, endTime!!, timeRange!!, walletId!!)
            viewModel.getPieEntries(startTime!!, endTime!!, walletId!!)
        }

        return inflater.inflate(R.layout.fragment_report_record, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ChartsDrawer.setupBarChart(income_expense_chart)

        registerObservers()
        registerClickListeners()

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.report_frag_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.change_pie_mode) {
            viewModel.excludeSubCate = !viewModel.excludeSubCate
            viewModel.getPieEntries(startTime!!, endTime!!, walletId!!)
            observePieChartData()
        }

        return true
    }

    private fun registerObservers() {
        viewModel.currentWallet.observe(viewLifecycleOwner)
        {
            viewModel.setTimeRange(startTime!!, endTime!!, timeRange!!, it.id)
            viewModel.getPieEntries(startTime!!, endTime!!, it.id)

            income_expense_chart.clear()
            income_chart.clear()
            expense_chart.clear()

            observeBarChartData()
            observePieChartData()
        }
    }

    private fun observeBarChartData() {
        viewModel.barData.removeObservers(viewLifecycleOwner)
        viewModel.barData.observe(viewLifecycleOwner) {
            ChartsDrawer.drawBarChartChart(income_expense_chart, it)
            displayIncomeExpenseInfo(it)
        }
    }

    private fun observePieChartData() {
        viewModel.pieChartData.removeObservers(viewLifecycleOwner)
        viewModel.pieChartData.observe(viewLifecycleOwner) {
            drawPieCharts(it)
        }
    }

    private fun registerClickListeners() {
        income_expense_chart.setOnClickListener {
            val intent = Intent(requireContext(), BarChartDetailActivity::class.java)
            intent.putExtra(
                BarChartDetailActivity.BAR_DATA_KEY,
                viewModel.barData.value as Serializable
            )
            intent.putExtra(BarChartDetailActivity.WALLET_ID_KEY, walletId)
            startActivity(intent)
        }

        expense_chart.setTouchEnabled(false)
        expense_chart.setOnClickListener {
            startPieChartDetailActivity(true)
        }

        income_chart.setTouchEnabled(false)
        income_chart.setOnClickListener {
            startPieChartDetailActivity(false)
        }

    }

    private fun startPieChartDetailActivity(isExpense : Boolean)
    {
        val intent = Intent(requireContext(), PieChartDetailActivity::class.java)

        PieChartDetailData.pieChartData = viewModel.pieChartData.value!!

        intent.putExtra(
            PieChartDetailActivity.IS_EXPENSE_KEY,
            isExpense
        )
        intent.putExtra(PieChartDetailActivity.WALLET_ID_KEY, walletId)
        intent.putExtra(PieChartDetailActivity.START_DATE_KEY, startTime)
        intent.putExtra(PieChartDetailActivity.END_DATE_KEY, endTime)
        intent.putExtra(PieChartDetailActivity.EXCLUDE_SUB_CATE_KEY, viewModel.excludeSubCate)

        startActivity(intent)
    }

    private fun displayIncomeExpenseInfo(list: List<BarChartData>) {
        var income = 0L
        var expense = 0L

        list.forEach {
            income += it.totalIncome
            expense += it.totalExpense.absoluteValue
        }

        income_txt.text = NumberFormatter.format(income)
        expense_txt.text = NumberFormatter.format(expense)
        net_income_txt.text = NumberFormatter.format((income - expense))
    }

    private fun drawPieCharts(pieChartData: PieChartData) {
        if (pieChartData.incomePieEntries.isNotEmpty())
            ChartsDrawer.drawPieChart(income_chart, pieChartData.incomePieEntries, false)
        if (pieChartData.expensePieEntries.isNotEmpty())
            ChartsDrawer.drawPieChart(expense_chart, pieChartData.expensePieEntries, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(startTime: Long, endTime: Long, walletId: Long, timeRange: TimeRange) =
            ReportRecordFragment().apply {
                arguments = Bundle().apply {
                    putLong(START_TIME_PARAM, startTime)
                    putLong(END_TIME_PARAM, endTime)
                    putLong(WALLET_ID_PARAM, walletId)
                    putString(TIME_RANGE_PARAM, timeRange.value)
                }
            }
    }
}