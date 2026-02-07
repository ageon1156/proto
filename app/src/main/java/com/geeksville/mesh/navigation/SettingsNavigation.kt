@file:Suppress("Wrapping", "SpacingAroundColon")

package com.geeksville.mesh.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import org.meshtastic.core.navigation.DEEP_LINK_BASE_URI
import org.meshtastic.core.navigation.Graph
import org.meshtastic.core.navigation.NodesRoutes
import org.meshtastic.core.navigation.Route
import org.meshtastic.core.navigation.SettingsRoutes
import org.meshtastic.feature.settings.AboutScreen
import org.meshtastic.feature.settings.SettingsScreen
import org.meshtastic.feature.settings.navigation.ConfigRoute
import org.meshtastic.feature.settings.navigation.ModuleRoute
import org.meshtastic.feature.settings.radio.CleanNodeDatabaseScreen
import org.meshtastic.feature.settings.radio.RadioConfigViewModel
import org.meshtastic.feature.settings.radio.channel.ChannelConfigScreen
import org.meshtastic.feature.settings.radio.component.BluetoothConfigScreen
import org.meshtastic.feature.settings.radio.component.CannedMessageConfigScreen
import org.meshtastic.feature.settings.radio.component.DeviceConfigScreen
import org.meshtastic.feature.settings.radio.component.DisplayConfigScreen
import org.meshtastic.feature.settings.radio.component.ExternalNotificationConfigScreen
import org.meshtastic.feature.settings.radio.component.LoRaConfigScreen
import org.meshtastic.feature.settings.radio.component.NetworkConfigScreen
import org.meshtastic.feature.settings.radio.component.PositionConfigScreen
import org.meshtastic.feature.settings.radio.component.PowerConfigScreen
import org.meshtastic.feature.settings.radio.component.SecurityConfigScreen
import org.meshtastic.feature.settings.radio.component.SerialConfigScreen
import org.meshtastic.feature.settings.radio.component.StoreForwardConfigScreen
import org.meshtastic.feature.settings.radio.component.UserConfigScreen
import kotlin.reflect.KClass

@Suppress("LongMethod")
fun NavGraphBuilder.settingsGraph(navController: NavHostController) {
    navigation<SettingsRoutes.SettingsGraph>(startDestination = SettingsRoutes.Settings()) {
        composable<SettingsRoutes.Settings>(
            deepLinks = listOf(navDeepLink<SettingsRoutes.Settings>(basePath = "$DEEP_LINK_BASE_URI/settings")),
        ) { backStackEntry ->
            val parentEntry =
                remember(backStackEntry) { navController.getBackStackEntry(SettingsRoutes.SettingsGraph::class) }
            SettingsScreen(
                viewModel = hiltViewModel(parentEntry),
                onClickNodeChip = {
                    navController.navigate(NodesRoutes.NodeDetailGraph(it)) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            ) {
                navController.navigate(it) { popUpTo(SettingsRoutes.Settings()) { inclusive = false } }
            }
        }

        composable<SettingsRoutes.CleanNodeDb>(
            deepLinks =
            listOf(
                navDeepLink<SettingsRoutes.CleanNodeDb>(
                    basePath = "$DEEP_LINK_BASE_URI/settings/radio/clean_node_db",
                ),
            ),
        ) {
            CleanNodeDatabaseScreen()
        }

        ConfigRoute.entries.forEach { entry ->
            navController.configComposable(
                route = entry.route::class,
                parentGraphRoute = SettingsRoutes.SettingsGraph::class,
            ) { viewModel ->
                when (entry) {
                    ConfigRoute.USER -> UserConfigScreen(viewModel, onBack = navController::popBackStack)

                    ConfigRoute.CHANNELS -> ChannelConfigScreen(viewModel, onBack = navController::popBackStack)

                    ConfigRoute.DEVICE -> DeviceConfigScreen(viewModel, onBack = navController::popBackStack)

                    ConfigRoute.POSITION -> PositionConfigScreen(viewModel, onBack = navController::popBackStack)

                    ConfigRoute.POWER -> PowerConfigScreen(viewModel, onBack = navController::popBackStack)

                    ConfigRoute.NETWORK -> NetworkConfigScreen(viewModel, onBack = navController::popBackStack)

                    ConfigRoute.DISPLAY -> DisplayConfigScreen(viewModel, onBack = navController::popBackStack)

                    ConfigRoute.LORA -> LoRaConfigScreen(viewModel, onBack = navController::popBackStack)

                    ConfigRoute.BLUETOOTH -> BluetoothConfigScreen(viewModel, onBack = navController::popBackStack)

                    ConfigRoute.SECURITY -> SecurityConfigScreen(viewModel, onBack = navController::popBackStack)
                }
            }
        }

        ModuleRoute.entries.forEach { entry ->
            navController.configComposable(
                route = entry.route::class,
                parentGraphRoute = SettingsRoutes.SettingsGraph::class,
            ) { viewModel ->
                when (entry) {
                    ModuleRoute.SERIAL -> SerialConfigScreen(viewModel, onBack = navController::popBackStack)

                    ModuleRoute.EXT_NOTIFICATION ->
                        ExternalNotificationConfigScreen(viewModel = viewModel, onBack = navController::popBackStack)

                    ModuleRoute.STORE_FORWARD ->
                        StoreForwardConfigScreen(viewModel, onBack = navController::popBackStack)

                    ModuleRoute.CANNED_MESSAGE ->
                        CannedMessageConfigScreen(viewModel, onBack = navController::popBackStack)
                }
            }
        }

        composable<SettingsRoutes.About> { AboutScreen(onNavigateUp = navController::navigateUp) }
    }
}

context(_: NavGraphBuilder)
inline fun <reified R : Route, reified G : Graph> NavHostController.configComposable(
    noinline content: @Composable (RadioConfigViewModel) -> Unit,
) {
    configComposable(route = R::class, parentGraphRoute = G::class, content = content)
}

context(navGraphBuilder: NavGraphBuilder)
fun <R : Route, G : Graph> NavHostController.configComposable(
    route: KClass<R>,
    parentGraphRoute: KClass<G>,
    content: @Composable (RadioConfigViewModel) -> Unit,
) {
    navGraphBuilder.composable(route = route) { backStackEntry ->
        val parentEntry = remember(backStackEntry) { getBackStackEntry(parentGraphRoute) }
        content(hiltViewModel(parentEntry))
    }
}
