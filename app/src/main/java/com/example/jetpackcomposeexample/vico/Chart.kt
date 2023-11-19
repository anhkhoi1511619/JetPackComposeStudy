package com.example.jetpackcomposeexample.vico

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetpackcomposeexample.ui.theme.JetpackComposeExampleTheme
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.endAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.line.lineSpec
import com.patrykandpatrick.vico.compose.component.lineComponent
import com.patrykandpatrick.vico.compose.component.shape.shader.verticalGradient
import com.patrykandpatrick.vico.core.chart.column.ColumnChart
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.marker.Marker
import com.patrykandpatrick.vico.core.axis.AxisPosition.Vertical
import com.patrykandpatrick.vico.core.chart.composed.plus
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.entry.composed.plus


@Preview
@Composable
fun HorizontalAxisGuidelineDoesNotOverlayBottomAxisLine() {
    JetpackComposeExampleTheme {
        Surface {
            Chart(
                chart = com.patrykandpatrick.vico.compose.chart.column.columnChart(
                    axisValuesOverrider = AxisValuesOverrider.fixed(
                        minY = -3f,
                        maxY = 5f,
                    ),
                ),
                model = entryModelOf(2f, -1f, 4f, -2f, 1f, 5f, -3f),
                startAxis = startAxis(maxLabelCount = 5),
                bottomAxis = bottomAxis(),
            )
        }
    }
}

private val model1 = entryModelOf(0 to 1, 1 to 2, 2 to 4, 3 to 1, 4 to 4)
private val model2 = entryModelOf(1 to 4, 2 to 1, 3 to 8, 4 to 12, 5 to 5)
@Composable
private fun getColumnChart(
    markerMap: Map<Float, Marker> = emptyMap(),
    targetVerticalAxisPosition: Vertical? = null,
): ColumnChart = columnChart(
    columns = listOf(
        lineComponent(
            color = Color.Blue,
            thickness = 8.dp,
            shape = Shapes.pillShape,
        ),
    ),
    persistentMarkers = markerMap,
    targetVerticalAxisPosition = targetVerticalAxisPosition,
)
@Composable
private fun getLineChart(
    markerMap: Map<Float, Marker> = emptyMap(),
    targetVerticalAxisPosition: Vertical? = null,
): LineChart = lineChart(
    lines = listOf(
        lineSpec(
            lineColor = Color.Blue,
            lineBackgroundShader = verticalGradient(
                arrayOf(Color.DarkGray, Color.DarkGray.copy(alpha = 0f))
            )
        )
    ),
    persistentMarkers = markerMap,
    targetVerticalAxisPosition = targetVerticalAxisPosition,
)

private val markerMap: Map<Float, Marker>
    @Composable get() = mapOf(4f to rememberMarker())

@Preview
@Composable
fun ChartExample2Preview(){
    Chart(
        chart = getColumnChart(markerMap = markerMap),
        model = model1,
        startAxis = startAxis(),
        bottomAxis = bottomAxis(),
        modifier = Modifier
        )
}

@Preview
@Composable
fun ChartExample3Preview(){
    Chart(
        chart = getLineChart(markerMap = markerMap),
        model = model2,
        startAxis = startAxis(),
        bottomAxis = bottomAxis()
    )
}

@Preview
@Composable
fun ChartExample4Preview(){
    val  composeChart = getColumnChart() + getLineChart()
    composeChart.setPersistentMarkers(markers = markerMap)
    Chart(chart = composeChart,
        model = model1+ model2,
        startAxis = startAxis(),
        bottomAxis = bottomAxis(),
        endAxis = endAxis(),
        modifier = Modifier
        )
}




