package thesis.academic.ally.custom_composables

import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import thesis.academic.ally.api.DrawerData

@Composable
fun DrawerAndScaffold(
    scope: CoroutineScope,
    drawerState: DrawerState,
    user: DrawerData,
    topBarText: String,
    navController: NavController,
    context: Context,
    selected: String,
    content: @Composable (PaddingValues) -> Unit
) {
    Drawer(
        scope = scope,
        drawerState = drawerState,
        user = user,
        navController = navController,
        context = context,
        selected = selected
    ) {
        Scaffold(
            topBar = TopBar(
                scope = scope,
                drawerState = drawerState,
                text = topBarText,
                navController = navController
            ),
            content = content
        )
    }
}