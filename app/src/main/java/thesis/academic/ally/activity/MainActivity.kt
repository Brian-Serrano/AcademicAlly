package thesis.academic.ally.activity

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.dataStore
import androidx.navigation.compose.rememberNavController
import thesis.academic.ally.datastore.CryptoManager
import thesis.academic.ally.datastore.UserCacheSerializer
import thesis.academic.ally.ui.theme.AcademicAllyPrototypeTheme
import dagger.hilt.android.AndroidEntryPoint


val Context.userDataStore by dataStore("user-cache.json", UserCacheSerializer(CryptoManager()))


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0)
            }
        }

        setContent {
            AcademicAllyPrototypeTheme {
                NavigationGraph(
                    navController = rememberNavController(),
                    scope = rememberCoroutineScope(),
                    drawerState = rememberDrawerState(DrawerValue.Closed),
                    context = this
                )
            }
        }
    }
}