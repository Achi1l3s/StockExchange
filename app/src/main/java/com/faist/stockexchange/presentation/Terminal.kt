package com.faist.stockexchange.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.faist.stockexchange.data.Bar
import kotlin.math.roundToInt

private const val MIN_VISIBLE_BARS_COUNT = 20

@Composable
fun Terminal(
    modifier: Modifier = Modifier,
    bars: List<Bar>
) {

    var terminalState by rememberTerminalState(bars)

    Chart(
        modifier = modifier,
        terminalState = terminalState
    ) {
        terminalState = it
    }

    bars.firstOrNull()?.let {
        Prices(
            modifier = modifier,
            max = terminalState.max,
            min = terminalState.min,
            pxPerPoint = terminalState.pxPerPoint,
            currentPrice = it.close
        )
    }
}

@Composable
private fun Chart(
    modifier: Modifier = Modifier,
    terminalState: TerminalState,
    onTerminalStateChanged: (TerminalState) -> Unit
) {
    val transformableState =
        rememberTransformableState { zoomChange, panChange, _ ->
            val visibleBarsCount = (terminalState.visibleBarsCount / zoomChange).roundToInt()
                .coerceIn(MIN_VISIBLE_BARS_COUNT, terminalState.bars.size)

            val scrolledBy = (terminalState.scrolledBy + panChange.x)
                .coerceIn(
                    0f,
                    terminalState.bars.size * terminalState.barWidth - terminalState.terminalWidth
                )

            onTerminalStateChanged(
                terminalState.copy(
                    visibleBarsCount = visibleBarsCount,
                    scrolledBy = scrolledBy
                )
            )
        }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .clipToBounds()
            .padding(
                top = 32.dp,
                bottom = 32.dp,
                end = 128.dp
            )
            .transformable(transformableState)
            .onSizeChanged {
                onTerminalStateChanged(
                    terminalState.copy(
                        terminalWidth = it.width.toFloat(),
                        terminalHeight = it.height.toFloat()
                    )
                )
            }
    ) {
        val min = terminalState.min
        val pxPerPoint = terminalState.pxPerPoint

        translate(left = terminalState.scrolledBy) {
            terminalState.bars.forEachIndexed { index, bar ->
                val offsetX = size.width - index * terminalState.barWidth

                drawLine(
                    color = Color.White,
                    start = Offset(offsetX, size.height - ((bar.low - min) * pxPerPoint)),
                    end = Offset(offsetX, size.height - ((bar.high - min) * pxPerPoint)),
                    strokeWidth = 1f
                )
                drawLine(
                    color = if (bar.open > bar.close) Color.Red else Color.Green,
                    start = Offset(offsetX, size.height - ((bar.open - min) * pxPerPoint)),
                    end = Offset(offsetX, size.height - ((bar.close - min) * pxPerPoint)),
                    strokeWidth = terminalState.barWidth / 2
                )
            }
        }
    }
}

@Composable
private fun Prices(
    modifier: Modifier = Modifier,
    max: Float,
    min: Float,
    pxPerPoint: Float,
    currentPrice: Float
) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
            .padding(vertical = 32.dp)
    ) {
        drawPrices(max, min, pxPerPoint, currentPrice, textMeasurer)
    }
}

private fun DrawScope.drawPrices(
    max: Float,
    min: Float,
    pxPerPoint: Float,
    currentPrice: Float,
    textMeasurer: TextMeasurer
) {
    //max price
    drawDashedLine(
        start = Offset(0f, 0f),
        end = Offset(size.width, 0f)
    )
    drawTextPrice(textMeasurer = textMeasurer, price = max, offsetY = 0f)

    //current price
    val currentPriceOffsetY = size.height - ((currentPrice - min) * pxPerPoint)
    drawDashedLine(
        color = Color.Yellow,
        start = Offset(0f, currentPriceOffsetY),
        end = Offset(size.width, currentPriceOffsetY),
        strokeWidth = 3f
    )
    drawTextPrice(textMeasurer = textMeasurer, price = currentPrice, offsetY = currentPriceOffsetY)

    //min price
    val minPriceOffsetY = size.height
    drawDashedLine(
        start = Offset(0f, minPriceOffsetY),
        end = Offset(size.width, minPriceOffsetY)
    )
    drawTextPrice(textMeasurer = textMeasurer, price = min, offsetY = minPriceOffsetY)

}

private fun DrawScope.drawTextPrice(
    textMeasurer: TextMeasurer,
    price: Float,
    offsetY: Float
) {
    val textLayoutResult = textMeasurer.measure(
        text = price.toString(),
        style = TextStyle(
            color = Color.White,
            fontSize = 12.sp
        )
    )
    drawText(
        textLayoutResult = textLayoutResult,
        topLeft = Offset(size.width - textLayoutResult.size.width - 4.dp.toPx(), offsetY)
    )
}

private fun DrawScope.drawDashedLine(
    color: Color = Color.White,
    start: Offset,
    end: Offset,
    strokeWidth: Float = 1f
) {
    drawLine(
        color = color,
        start = start,
        end = end,
        strokeWidth = strokeWidth,
        pathEffect = PathEffect.dashPathEffect(
            intervals = floatArrayOf(
                4.dp.toPx(), 4.dp.toPx()
            )
        )
    )
}