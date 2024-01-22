package com.serrano.academically.custom_composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.serrano.academically.utils.ChartData
import com.serrano.academically.utils.ChartState
import com.serrano.academically.utils.Utils
import kotlin.math.ceil

@Composable
fun BarGraph(
    text: String,
    yValues: List<Double>,
    data: List<ChartData>,
    verticalStep: Float,
    chartState: ChartState,
    onChartStateChange: (ChartState) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(10.dp)
        )

        val textMeasure = rememberTextMeasurer()
        val lineColor = MaterialTheme.colorScheme.background

        val transformState = rememberTransformableState { zoom, pan, _ ->
            onChartStateChange(
                chartState.copy(
                    camera = Offset(
                        x = ((chartState.camera.x - (chartState.scale * pan.x)) + zoom).coerceIn(0f, chartState.size.x - chartState.viewSize.x),
                        y = ((chartState.camera.y - (chartState.scale * pan.y)) + zoom).coerceIn(0f, chartState.size.y - chartState.viewSize.y)
                    ),
                    scale = (chartState.scale * zoom).coerceIn(1f, 2f)
                )
            )
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds()
                .background(MaterialTheme.colorScheme.onBackground)
                .border(5.dp, lineColor)
                .padding(20.dp)
                .transformable(state = transformState)
        ) {
            val canvasHeight = chartState.scale * size.height
            val textSpaceY = canvasHeight * 0.1f
            val cellYSize = (canvasHeight * 0.8f) / ((yValues.size * 2) + 1)
            val textSpaceX = textSpaceY * 1.25f
            val cellXSize = cellYSize * 2f
            val canvasWidth = (cellXSize * ((data.size * 2) + 1)) + (textSpaceX * 2)

            val numberOfXCells = ceil((canvasWidth - textSpaceX * 2) / cellXSize).toInt()
            val numberOfYCells = ceil((canvasHeight - textSpaceY * 2) / cellYSize).toInt()

            onChartStateChange(
                chartState.copy(
                    size = Offset(canvasWidth, canvasHeight),
                    viewSize = Offset(size.width, size.height)
                )
            )

            drawPath(
                path = Path().apply {
                    val xOffset = textSpaceX - chartState.camera.x
                    val yOffset = (canvasHeight - textSpaceY) - chartState.camera.y
                    moveTo(xOffset, yOffset)
                    lineTo(xOffset, yOffset - (numberOfYCells * cellYSize))
                    lineTo(
                        xOffset + (numberOfXCells * cellXSize),
                        yOffset - (numberOfYCells * cellYSize)
                    )
                    lineTo(xOffset + (numberOfXCells * cellXSize), yOffset)
                    lineTo(xOffset, yOffset)
                },
                color = lineColor,
                style = Stroke(5F)
            )

            for (i in 0..<numberOfXCells) {
                for (j in 0..<numberOfYCells) {
                    drawPath(
                        path = Path().apply {
                            val xOffset = (i * cellXSize + textSpaceX) - chartState.camera.x
                            val yOffset = (canvasHeight - (j * cellYSize + textSpaceY)) - chartState.camera.y
                            moveTo(xOffset, yOffset)
                            lineTo(xOffset, yOffset - cellYSize)
                            lineTo(xOffset + cellXSize, yOffset - cellYSize)
                            lineTo(xOffset + cellXSize, yOffset)
                            lineTo(xOffset, yOffset)
                        },
                        color = lineColor,
                        style = Stroke(1.5F)
                    )
                }
            }

            for (i in data.indices) {
                val textCenter = textMeasure.measure(data[i].text).size.toSize() / 2f
                val offset = Offset(
                    ((cellXSize * 2) * (i + 1)) + textSpaceX - (cellXSize / 2) - chartState.camera.x,
                    (canvasHeight - (textSpaceY / 2f)) - chartState.camera.y
                ) - Offset(textCenter.width, textCenter.height)

                translate(offset.x, offset.y) {
                    drawText(
                        textMeasurer = textMeasure,
                        text = data[i].text,
                        maxLines = 1
                    )
                }

                val x = ((cellXSize * 2) * (i + 1)) + textSpaceX - cellXSize - chartState.camera.x
                val y = (canvasHeight - (((cellYSize * 2) * (data[i].value.value / verticalStep)) + textSpaceY)) - chartState.camera.y

                drawRect(
                    color = data[i].color,
                    topLeft = Offset(x, y),
                    size = Size(cellXSize, (cellYSize * 2) * (data[i].value.value / verticalStep))
                )
            }

            for (i in yValues.indices) {
                val yValue = Utils.roundRating(yValues[i]).toString()
                val textCenter = textMeasure.measure(yValue).size / 2
                val offset = Offset(
                    (textSpaceX / 2f) - textCenter.width - chartState.camera.x,
                    (canvasHeight - (((cellYSize * 2) * (i + 1)) - textCenter.height + textSpaceY) - (cellYSize / 2)) - chartState.camera.y
                )
                translate(offset.x, offset.y) {
                    drawText(
                        textMeasurer = textMeasure,
                        text = yValue,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
