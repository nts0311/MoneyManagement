package com.sonnt.moneymanagement.features.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModel: ViewModel() {
    private val _errorMsg = MutableLiveData<String>()
    val errorMsg: LiveData<String> = _errorMsg

    fun displayError(msg: String) {
        _errorMsg.value = msg
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<String> = _errorMsg

    fun setLoading(isLoading: Boolean) {
       _isLoading.value = isLoading
    }
}