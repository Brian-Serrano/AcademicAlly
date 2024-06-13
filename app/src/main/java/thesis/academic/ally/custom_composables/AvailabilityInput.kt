package thesis.academic.ally.custom_composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import thesis.academic.ally.utils.TutorAvailabilityData
import thesis.academic.ally.utils.Utils
import java.time.LocalTime

@Composable
fun AvailabilityInput(
    dates: List<TutorAvailabilityData>,
    openDialog: (LocalTime, Int, Int) -> Unit,
    onNotAvailable: (Int) -> Unit
) {
    Column {
        dates.forEachIndexed { idx, data ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Color.Black)) {
                            append(data.day)
                        }
                        if (Utils.timeEquivalent(data.from, data.to)) {
                            withStyle(style = SpanStyle(color = Color.Blue)) {
                                append(": Not Available")
                            }
                        }
                        if (Utils.timeFullRange(data.from, data.to)) {
                            withStyle(style = SpanStyle(color = Color.Blue)) {
                                append(": Always Available")
                            }
                        }
                        if (data.from.isAfter(data.to)) {
                            withStyle(style = SpanStyle(color = Color.Red)) {
                                append(": Invalid Range")
                            }
                        }
                    },
                    modifier = Modifier.padding(horizontal = 20.dp).weight(1f),
                    style = MaterialTheme.typography.labelMedium
                )
                IconButton(
                    onClick = { onNotAvailable(idx) },
                    modifier = Modifier
                        .size(30.dp)
                        .padding(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.EventBusy,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .background(Color.White)
                        .padding(5.dp)
                        .weight(1f)
                        .clickable { openDialog(data.from, idx, 0) }
                ) {
                    Text(
                        text = Utils.toMilitaryTime(data.from.hour, data.from.minute),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = null
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .background(Color.White)
                        .padding(5.dp)
                        .weight(1f)
                        .clickable { openDialog(data.to, idx, 1) }
                ) {
                    Text(
                        text = Utils.toMilitaryTime(data.to.hour, data.to.minute),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = null
                    )
                }
            }
        }
    }
}