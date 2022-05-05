package com.sonnt.moneymanagement.features.transactions.transaction_list_fragment

import java.io.Serializable

//for customizing transaction filtering while showing a list of transaction belong to a category
class FilteringParams(
    var categoryIdToFilter: Long = -1L,
    var transactionType: String? = "",
    var excludeSubCate: Boolean = false
) : Serializable