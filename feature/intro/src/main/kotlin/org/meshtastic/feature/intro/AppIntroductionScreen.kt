package org.meshtastic.feature.intro

import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.serialization.Serializable


@OptIn(ExperimentalPermissionsApi::class)
@Suppress("LongMethod")
@Composable
fun AppIntroductionScreen(onDone: () -> Unit) {
    val context = LocalContext.current

    val notificationPermissionState: PermissionState? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            null
        }

    val locationPermissions =
        listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    val locationPermissionState = rememberMultiplePermissionsState(permissions = locationPermissions)

    val backStack = rememberNavBackStack(Welcome)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider =
        entryProvider {
            entry<Welcome> { WelcomeScreen(onGetStarted = { backStack.add(Notifications) }) }

            entry<Notifications> {
                val notificationsAlreadyGranted = notificationPermissionState?.status?.isGranted ?: true
                NotificationsScreen(
                    showNextButton = notificationsAlreadyGranted,
                    onSkip = {
                        
                        backStack.add(Location)
                    },
                    onConfigure = {
                        if (notificationsAlreadyGranted) {
                            backStack.add(CriticalAlerts)
                        } else {
                            
                            
                            notificationPermissionState?.launchPermissionRequest()
                        }
                    },
                )
            }

            entry<CriticalAlerts> {
                CriticalAlertsScreen(
                    onSkip = { backStack.add(Location) },
                    onConfigure = {
                        
                        
                        val intent =
                            Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                                putExtra(Settings.EXTRA_CHANNEL_ID, "my_alerts")
                            }
                        context.startActivity(intent)
                        backStack.add(Location)
                    },
                )
            }

            entry<Location> {
                val locationAlreadyGranted = locationPermissionState.allPermissionsGranted
                LocationScreen(
                    showNextButton = locationAlreadyGranted,
                    onSkip = onDone, 
                    onConfigure = {
                        if (locationAlreadyGranted) {
                            onDone() 
                        } else {
                            locationPermissionState.launchMultiplePermissionRequest()
                        }
                    },
                )
            }
        },
    )
}

@Serializable private data object Welcome : NavKey

@Serializable private data object Notifications : NavKey

@Serializable private data object CriticalAlerts : NavKey

@Serializable private data object Location : NavKey
