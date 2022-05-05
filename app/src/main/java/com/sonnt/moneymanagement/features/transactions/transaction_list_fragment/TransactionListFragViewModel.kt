package com.sonnt.moneymanagement.features.transactions.transaction_list_fragment

import androidx.lifecycle.*
import com.sonnt.moneymanagement.constant.TimeRange
import com.sonnt.moneymanagement.constant.ViewType
import com.sonnt.moneymanagement.data.datasource.CategoryRepository
import com.sonnt.moneymanagement.data.datasource.TransactionRepository
import com.sonnt.moneymanagement.data.datasource.WalletRepository
import com.sonnt.moneymanagement.data.entities.Transaction
import com.sonnt.moneymanagement.data.mm_context.MMContext
import com.sonnt.moneymanagement.features.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TransactionListFragViewModel() : BaseViewModel() {

    val viewMode = MMContext.viewMode

    private var startTime: Long = 0L
    private var endTime: Long = 0L
    var currentWallet = WalletRepository.currentWallet
    var timeRange = TimeRange.MONTH
    var currentViewMode = ViewType.TRANSACTION
    private var dataGrouper = DataGrouper()

    private var groupDataJob: Job? = null

    private var _dataItemList: MutableLiveData<List<DataItem>> = MutableLiveData()
    var dataItemList: LiveData<List<DataItem>> = _dataItemList

    var previousWalletId = -1L

    var filteredList = flow<List<Transaction>> { }

    fun switchViewMode(viewType: ViewType) {
        if (currentViewMode==viewType) return

        currentViewMode = viewType

        groupData()
    }

    override fun onCleared() {
        super.onCleared()
        groupDataJob?.cancel()
    }

    fun setTimeRange(
        start: Long,
        end: Long,
        range: String,
        walletId: Long,
        filteringParams: FilteringParams? = null
    ) {

        if (startTime==start
            && endTime==end
            && range==timeRange.value
            && walletId==previousWalletId
        )
            return

        startTime = start
        endTime = end
        timeRange = TimeRange.valueOf(range)

        filteredList = TransactionRepository.getTransactionsBetweenRange(start, end, walletId)
        groupData()

        previousWalletId = walletId
    }

    private fun groupData(filteringParams: FilteringParams? = null) {
        groupDataJob?.cancel()

        groupDataJob = filteredList.map {
            val filteredList = dataGrouper.filterTransactions(it, filteringParams)
            dataGrouper.doGrouping(filteredList, timeRange, currentViewMode)
        }
            .flowOn(Dispatchers.Default)
            .onEach {
                _dataItemList.value = it
            }
            .launchIn(viewModelScope)
    }
}