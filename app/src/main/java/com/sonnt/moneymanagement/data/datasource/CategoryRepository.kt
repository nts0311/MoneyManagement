package com.sonnt.moneymanagement.data.datasource

import androidx.lifecycle.LiveData
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
            val walletList = categoryDao.getCategoriesFlow().first()

            for (category in walletList) {
                _categoriesMap[category.id] = category
            }
        }
    }

    fun updateCategoriesMap(categories: List<Category>) {
        for (category in categories) {
            if (!_categoriesMap.containsKey(category.id))
                _categoriesMap[category.id] = category
        }
    }

    fun getCategoriesLiveData(): LiveData<List<Category>> = categoryDao.getCategories()

    fun getCategoriesByType(type: String) = categoryDao.getCategoriesByType(type)
}