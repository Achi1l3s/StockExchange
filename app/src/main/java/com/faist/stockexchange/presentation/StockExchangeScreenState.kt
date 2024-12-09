package com.faist.stockexchange.presentation

import com.faist.stockexchange.data.Bar

sealed class StockExchangeScreenState {

    data object Initial: StockExchangeScreenState()

    data class Content(val barList: List<Bar>): StockExchangeScreenState()
}