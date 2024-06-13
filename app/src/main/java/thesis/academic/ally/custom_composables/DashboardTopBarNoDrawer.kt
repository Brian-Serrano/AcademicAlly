package thesis.academic.ally.custom_composables

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardTopBarNoDrawer(
    content: @Composable (PaddingValues) -> Unit
) {
    SelectionContainer {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(color = MaterialTheme.colorScheme.onBackground)
                                ) {
                                    append("Academic")
                                }
                                withStyle(
                                    style = SpanStyle(color = MaterialTheme.colorScheme.secondary)
                                ) {
                                    append("Ally")
                                }
                            },
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    colors = DashboardTopBarColors()
                )
            },
            content = content
        )
    }
}