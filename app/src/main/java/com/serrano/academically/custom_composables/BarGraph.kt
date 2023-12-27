package com.serrano.academically.custom_composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.serrano.academically.utils.ChartData
import com.serrano.academically.utils.HelperFunctions
import kotlin.math.ceil

@Composable
fun BarGraph(
    text: String,
    yValues: List<Double>,
    data: List<ChartData>,
    verticalStep: Float,
    camera: Float,
    onCameraChange: (Float) -> Unit,
    chartSize: Float,
    onChartSize: (Float) -> Unit
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

        val cellYSize = 60f
        val textMeasure = rememberTextMeasurer()
        val textSpace = 100
        val cellXSize = cellYSize * 2f
        val canvasWidth = (textSpace * 2) + (cellXSize * ((data.size * 2) + 1))
        val canvasHeight = (textSpace * 2) + (cellYSize * ((yValues.size * 2) + 1))

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds()
                .background(Color.White)
                .border(5.dp, Color.Black, MaterialTheme.shapes.extraSmall)
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState {
                        if (camera - it <= canvasWidth - chartSize && camera - it >= 0) {
                            onCameraChange(camera - it)
                        }
                    }
                )
                .padding(20.dp)
        ) {

            val numberOfXCells = ceil((canvasWidth - textSpace * 2) / cellXSize).toInt()
            val numberOfYCells = ceil((canvasHeight - textSpace * 2) / cellYSize).toInt()

            onChartSize(size.width)

            drawPath(
                path = Path().apply {
                    val xOffset = textSpace.toFloat() - camera
                    val yOffset = canvasHeight - textSpace
                    moveTo(xOffset, yOffset)
                    lineTo(xOffset, yOffset - (numberOfYCells * cellYSize))
                    lineTo(
                        xOffset + (numberOfXCells * cellXSize),
                        yOffset - (numberOfYCells * cellYSize)
                    )
                    lineTo(xOffset + (numberOfXCells * cellXSize), yOffset)
                    lineTo(xOffset, yOffset)
                },
                color = Color.Black,
                style = Stroke(5F)
            )

            for (i in 0..<numberOfXCells) {
                for (j in 0..<numberOfYCells) {
                    drawPath(
                        path = Path().apply {
                            val xOffset = (i * cellXSize + textSpace) - camera
                            val yOffset = canvasHeight - (j * cellYSize + textSpace)
                            moveTo(xOffset, yOffset)
                            lineTo(xOffset, yOffset - cellYSize)
                            lineTo(xOffset + cellXSize, yOffset - cellYSize)
                            lineTo(xOffset + cellXSize, yOffset)
                            lineTo(xOffset, yOffset)
                        },
                        color = Color.Black,
                        style = Stroke(1.5F)
                    )
                }
            }

            for (i in data.indices) {
                val textCenter = textMeasure.measure(data[i].text).size.toSize() / 2f
                val offset = Offset(
                    ((cellXSize * 2) * (i + 1)) + textSpace - (cellXSize / 2) - camera,
                    (canvasHeight - (textSpace / 2f))
                ) - Offset(textCenter.width, textCenter.height)

                translate(offset.x, offset.y) {
                    drawText(
                        textMeasurer = textMeasure,
                        text = data[i].text,
                        maxLines = 1
                    )
                }

                val x = ((cellXSize * 2) * (i + 1)) + textSpace - cellXSize - camera
                val y =
                    canvasHeight - (((cellYSize * 2) * (data[i].value.value / verticalStep)) + textSpace)

                drawRect(
                    color = data[i].color,
                    topLeft = Offset(x, y),
                    size = Size(cellXSize, (cellYSize * 2) * (data[i].value.value / verticalStep))
                )
            }

            for (i in yValues.indices) {
                val yValue = HelperFunctions.roundRating(yValues[i]).toString()
                val textCenter = textMeasure.measure(yValue).size / 2
                val offset = Offset(
                    (textSpace / 2f) - textCenter.width - camera,
                    canvasHeight - (((cellYSize * 2) * (i + 1)) - textCenter.height + textSpace) - (cellYSize / 2)
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