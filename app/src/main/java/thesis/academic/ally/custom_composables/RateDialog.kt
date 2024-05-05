package thesis.academic.ally.custom_composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun RateDialog(
    text: String,
    buttonOneText: String,
    buttonTwoText: String,
    star: Int,
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
    onCancelClick: () -> Unit,
    onStarClick: (Int) -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        SelectionContainer {
            Column(
                modifier = Modifier
                    .size(300.dp, 300.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.onBackground),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(20.dp),
                    color = MaterialTheme.colorScheme.background
                )
                RatingBar(
                    rating = star.toFloat(),
                    modifier = Modifier
                        .padding(20.dp)
                        .height(45.dp),
                    onStarClick = onStarClick
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    BlackButton(text = buttonOneText, action = onCancelClick)
                    BlackButton(text = buttonTwoText, action = onConfirmClick)
                }
            }
        }
    }
}