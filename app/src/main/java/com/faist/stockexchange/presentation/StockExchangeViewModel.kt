package com.faist.stockexchange.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faist.stockexchange.data.ApiFactory
import com.faist.stockexchange.presentation.StockExchangeScreenState.Initial
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StockExchangeViewModel : ViewModel() {

    private val apiService = ApiFactory.apiService

    private val _state =
        MutableStateFlow<StockExchangeScreenState>(Initial)
    val state = _state.asStateFlow()

    init {
        loadBarList()
    }

    private fun loadBarList() {
        viewModelScope.launch {
            try {
                val barList = apiService.loadBars().barList
                Log.d("MyStockExchangeViewModel", "barList: ${barList.size}")
                _state.value = StockExchangeScreenState.Content(barList = barList)
            } catch (e: Exception) {
                Log.d("MyStockExchangeViewModel", "Exception caught: $e")
            }
        }
    }
}