package com.sonnt.moneymanagement.data.datasource

import com.sonnt.moneymanagement.MMApplication
import com.sonnt.moneymanagement.data.AppDatabase
import com.sonnt.moneymanagement.data.entities.Category
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

object CategoryRepository {

    private val categoryDao = AppDatabase.getInstance(MMApplication.self).categoryDao

    private var _categoriesMap: MutableMap<Long, Category> = mutableMapOf()
    var categoryMap: Map<Long, Category> = _categoriesMap

    init {
        CoroutineScope(Dispatchers.Default).launch {
            val walletList = categoryDao.getCategories().first()

            for (category in walletList) {
                _categoriesMap[category.id] = category
            }
        }
    }

    fun getCategoriesByType(type: String) = categoryDao.getCategoriesByType(type)
}