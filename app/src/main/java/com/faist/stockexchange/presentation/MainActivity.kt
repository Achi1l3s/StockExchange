package com.faist.stockexchange.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.faist.stockexchange.presentation.StockExchangeScreenState.Content
import com.faist.stockexchange.presentation.StockExchangeScreenState.Initial
import com.faist.stockexchange.ui.theme.StockExchangeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StockExchangeTheme {
                val viewModel: StockExchangeViewModel = viewModel()
                val screenState = viewModel.state.collectAsState()

                when (val currentState = screenState.value) {
                    is Content -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color = Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Terminal(
                                modifier = Modifier
                                    .size(350.dp),
                                bars = currentState.barList
                            )
                        }
                    }

                    is Initial -> {
                    }
                }
            }
        }
    }
}