package com.sonnt.moneymanagement.features.base.viewpager2_fragment

import androidx.lifecycle.*
import com.sonnt.moneymanagement.data.mm_context.MMContext

open class ViewPager2FragViewModel() : ViewModel() {
    var tabInfoList = MMContext.tabInfoList
    var timeRange = MMContext.timeRange

    fun setViewPagerPage(page : Int)
    {
        MMContext.currentPage = page
    }

    fun getCurrentPage(): Int = MMContext.currentPage
}