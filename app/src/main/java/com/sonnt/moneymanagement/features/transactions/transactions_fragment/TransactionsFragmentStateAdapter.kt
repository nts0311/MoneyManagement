package com.sonnt.moneymanagement.features.transactions.transactions_fragment

import androidx.fragment.app.Fragment
import com.sonnt.moneymanagement.features.transactions.transaction_list_fragment.TransactionListFragment
import com.sonnt.moneymanagement.features.base.viewpager2_fragment.TabFragmentStateAdapter

class TransactionsFragmentStateAdapter(fragment: Fragment) : TabFragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment {
        val tabInfo = tabInfoList[position]
        return TransactionListFragment.newInstance(
            tabInfo.startTime,
            tabInfo.endTime,
            tabInfo.walletId,
            timeRange,
            null
        )
    }
}