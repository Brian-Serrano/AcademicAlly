package thesis.academic.ally.custom_composables

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState

@Composable
fun ErrorComposable(
    navController: NavController,
    paddingValues: PaddingValues,
    message: String,
    swipeRefreshState: SwipeRefreshState,
    onRefresh: () -> Unit
) {
    val activity = LocalContext.current as Activity

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = onRefresh,
        refreshTriggerDistance = 50.dp,
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No Internet Connection",
                    style = MaterialTheme.typography.displayMedium,
                    modifier = Modifier.padding(10.dp)
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(10.dp)
                )
                BlackButton(
                    text = "Go back",
                    action = {
                        if (!navController.popBackStack()) {
                            activity.finish()
                        }
                    },
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}