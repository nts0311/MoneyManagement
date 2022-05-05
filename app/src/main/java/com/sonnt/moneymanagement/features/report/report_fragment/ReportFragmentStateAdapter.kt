package com.sonnt.moneymanagement.features.report.report_fragment

import androidx.fragment.app.Fragment
import com.sonnt.moneymanagement.features.base.viewpager2_fragment.TabFragmentStateAdapter
import com.sonnt.moneymanagement.features.report.report_record_fragment.ReportRecordFragment

class ReportFragmentStateAdapter(fragment:Fragment) : TabFragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment {
        val tabInfo = tabInfoList[position]
        return ReportRecordFragment.newInstance(
            tabInfo.startTime,
            tabInfo.endTime,
            tabInfo.walletId,
            timeRange
        )
    }
}