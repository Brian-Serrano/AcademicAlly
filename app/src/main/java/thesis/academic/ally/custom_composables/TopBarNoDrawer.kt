package thesis.academic.ally.custom_composables

import android.app.Activity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarNoDrawer(
    text: String,
    navController: NavController
): @Composable () -> Unit {
    return {
        TopAppBar(
            title = {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelMedium
                )
            },
            navigationIcon = {
                val activity = LocalContext.current as Activity

                IconButton(
                    onClick = {
                        if (!navController.popBackStack()) {
                            activity.finish()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            },
            colors = TopBarColors()
        )
    }
}