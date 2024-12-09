package com.faist.stockexchange.presentation

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import com.faist.stockexchange.data.Bar
import kotlinx.parcelize.Parcelize
import kotlin.math.roundToInt

@Parcelize
data class TerminalState(
    val bars: List<Bar>,
    val visibleBarsCount: Int = 100,
    val terminalWidth: Float = 1f,
    val terminalHeight: Float = 1f,
    val scrolledBy: Float = 0f
): Parcelable {

    val barWidth: Float
        get() = terminalWidth / visibleBarsCount

    private val visibleBars: List<Bar>
        get() {
            val startIndex = (scrolledBy / barWidth).roundToInt().coerceAtLeast(0)
            val endIndex = (startIndex + visibleBarsCount).coerceAtMost(bars.size)
            return bars.subList(startIndex, endIndex)
        }

    val max: Float
        get() = visibleBars.maxOf { it.high }
    val min: Float
        get() = visibleBars.minOf { it.low }
    val pxPerPoint: Float
        get() = terminalHeight / (max - min)

//    companion object {
//
//        val Saver: Saver<MutableState<TerminalState>, Any> = listSaver(
//            save = {
//                val terminalState = it.value
//                listOf(
//                    terminalState.bars,
//                    terminalState.visibleBarsCount,
//                    terminalState.terminalWidth,
//                    terminalState.scrolledBy
//                )
//            },
//            restore = {
//                val terminalState = TerminalState(
//                    bars = it[0] as? List<Bar> ?: emptyList(),
//                    visibleBarsCount = it[1] as Int,
//                    terminalWidth = it[2] as Float,
//                    scrolledBy = it[3] as Float
//                )
//                mutableStateOf(terminalState)
//            }
//        )
//    }
}

@Composable
fun rememberTerminalState(bars: List<Bar>): MutableState<TerminalState> {
    return rememberSaveable {
        mutableStateOf(TerminalState(bars))
    }
}