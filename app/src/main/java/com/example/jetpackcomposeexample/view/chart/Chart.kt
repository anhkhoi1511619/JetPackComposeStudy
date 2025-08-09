package com.example.jetpackcomposeexample.view.chart

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetpackcomposeexample.R
import com.example.jetpackcomposeexample.model.balance.BalanceResponse
import com.example.jetpackcomposeexample.view.theme.JetpackComposeExampleTheme
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.endAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.line.lineSpec
import com.patrykandpatrick.vico.compose.component.lineComponent
import com.patrykandpatrick.vico.compose.component.shape.shader.verticalGradient
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.chart.column.ColumnChart
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.marker.Marker
import com.patrykandpatrick.vico.core.axis.AxisPosition.Vertical
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.composed.plus
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.chart.values.ChartValues
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.composed.plus
import com.patrykandpatrick.vico.core.entry.entryModelOf
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale


@Composable
fun ChartCode(
    balanceList: ArrayList<BalanceResponse.SFInfo>,
    modifier: Modifier
){

    Column(
        modifier = modifier
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = stringResource(id = R.string.home_chart) ,
            style = MaterialTheme.typography.titleLarge
        )
        val formatter = DateTimeFormatter.ofPattern("yyyy MMMM dd", Locale.ENGLISH)
        var baseDateTime = LocalDate.now()
        try {
            baseDateTime = LocalDate.parse("2025 ${balanceList.first().date}", formatter)
        }catch(e: Exception){
            println(e)
            baseDateTime = LocalDate.now()
        }
        val floatEntries = balanceList.map {
            val date = LocalDate.parse("2025 ${it.date}", formatter)
            val daysFromBase = ChronoUnit.DAYS.between(baseDateTime, date).toFloat()
            FloatEntry(daysFromBase, (it.postSubtractBalance.toFloatOrNull()?.div(10000f)) ?: 0f)
        }

        val chartEntryModelProducer = ChartEntryModelProducer(floatEntries)

        val xLabelsMap = floatEntries.zip(balanceList.map { it.date }).associate { (entry, date) ->
            entry.x to date
        }
// 1. Formatter cho nhãn trục X
        val xAxisFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
            if (value.toInt() % 2 == 0) xLabelsMap[value] ?: "" else ""
        }

// 2. Khai báo bottomAxis với itemPlacer
        val customBottomAxis = bottomAxis(
            valueFormatter = xAxisFormatter,
            itemPlacer = AxisItemPlacer.Horizontal.default(spacing = 1) // tăng khoảng cách label
        )

// 3. lineChart với entrySpacing để giãn các điểm dữ liệu
        val customLineChart = lineChart(
            lines = listOf(LineChart.LineSpec()),
            spacing = 100.dp // tăng khoảng cách giữa các entry
        )
        Chart(
            chart = customLineChart, // Dạng biểu đồ đường
            chartModelProducer = chartEntryModelProducer,
            startAxis = startAxis(),      // Trục Y bên trái
            bottomAxis = customBottomAxis
        )
    }
}
@Preview
@Composable
fun HorizontalAxisGuidelineDoesNotOverlayBottomAxisLine() {
    JetpackComposeExampleTheme {
        Surface {
            Chart(
                chart = columnChart(
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
@Preview(showBackground = true)
@Composable
fun LineChartPreview() {
    val chartEntryModelProducer = ChartEntryModelProducer(
        listOf(
            FloatEntry(0f, 1f),
            FloatEntry(1f, 2.5f),
            FloatEntry(2f, 1.8f),
            FloatEntry(3f, 3.6f),
            FloatEntry(4f, 2.2f)
        )
    )

    Chart(
        chart = lineChart(), // Dạng biểu đồ đường
        chartModelProducer = chartEntryModelProducer,
        startAxis = startAxis(),      // Trục Y bên trái
        bottomAxis = bottomAxis()     // Trục X bên dưới
    )
}

val model1 = entryModelOf( 0 to 1, 1 to 2, 2 to 4, 3 to 1, 4 to 4)
val model2 = entryModelOf(1 to 4, 2 to 1, 3 to 8, 4 to 12, 5 to 5)
@Composable
fun getColumnChart(
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
fun getLineChart(
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

val markerMap: Map<Float, Marker>
    @Composable get() = mapOf(4f to rememberMarker())

val modelK : List<FloatEntry> = listOf(
    FloatEntry(1F, 2F),
    FloatEntry(2F,3F),
    FloatEntry(3F,4F),
    FloatEntry(4F,5F)
);

@Preview
@Composable
fun ChartExample2Preview(){
    Chart(
        chart = getLineChart(markerMap = markerMap),
        model = entryModelOf(modelK),
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
        model = model1 + model2,
        startAxis = startAxis(),
        bottomAxis = bottomAxis(),
        endAxis = endAxis(),
        modifier = Modifier
        )
}
@Preview
@Composable
fun ChartExample5Preview(){
    val balanceList = arrayListOf(
        BalanceResponse.SFInfo(1.5f.toString(), "June 20", "12:00:00"),
        BalanceResponse.SFInfo(2.3f.toString(), "June 21", "19:00:00"),
        BalanceResponse.SFInfo(1.8f.toString(), "June 22", "18:00:00"),
        BalanceResponse.SFInfo(3.0f.toString(), "June 23", "08:30:00"),
        BalanceResponse.SFInfo(2.6f.toString(), "June 24", "15:00:00")
    )
    ChartCode(balanceList, modifier = Modifier.padding(16.dp))
}




