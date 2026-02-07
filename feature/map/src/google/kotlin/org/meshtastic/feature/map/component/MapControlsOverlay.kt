package org.meshtastic.feature.map.component

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationDisabled
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.Navigation
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import org.jetbrains.compose.resources.stringResource
import org.meshtastic.core.strings.Res
import org.meshtastic.core.strings.manage_map_layers
import org.meshtastic.core.strings.map_filter
import org.meshtastic.core.strings.map_tile_source
import org.meshtastic.core.strings.orient_north
import org.meshtastic.core.strings.toggle_my_position
import org.meshtastic.core.ui.theme.StatusColors.StatusRed
import org.meshtastic.feature.map.MapViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MapControlsOverlay(
    modifier: Modifier = Modifier,
    mapFilterMenuExpanded: Boolean,
    onMapFilterMenuDismissRequest: () -> Unit,
    onToggleMapFilterMenu: () -> Unit,
    mapViewModel: MapViewModel, 
    mapTypeMenuExpanded: Boolean,
    onMapTypeMenuDismissRequest: () -> Unit,
    onToggleMapTypeMenu: () -> Unit,
    onManageLayersClicked: () -> Unit,
    onManageCustomTileProvidersClicked: () -> Unit, 
    isNodeMap: Boolean,
    
    hasLocationPermission: Boolean = false,
    isLocationTrackingEnabled: Boolean = false,
    onToggleLocationTracking: () -> Unit = {},
    bearing: Float = 0f,
    onCompassClick: () -> Unit = {},
    followPhoneBearing: Boolean,
) {
    HorizontalFloatingToolbar(
        modifier = modifier,
        expanded = true,
        leadingContent = {},
        trailingContent = {},
        content = {
            CompassButton(onClick = onCompassClick, bearing = bearing, isFollowing = followPhoneBearing)
            if (isNodeMap) {
                MapButton(
                    icon = Icons.Outlined.Tune,
                    contentDescription = stringResource(Res.string.map_filter),
                    onClick = onToggleMapFilterMenu,
                )
                NodeMapFilterDropdown(
                    expanded = mapFilterMenuExpanded,
                    onDismissRequest = onMapFilterMenuDismissRequest,
                    mapViewModel = mapViewModel,
                )
            } else {
                Box {
                    MapButton(
                        icon = Icons.Outlined.Tune,
                        contentDescription = stringResource(Res.string.map_filter),
                        onClick = onToggleMapFilterMenu,
                    )
                    MapFilterDropdown(
                        expanded = mapFilterMenuExpanded,
                        onDismissRequest = onMapFilterMenuDismissRequest,
                        mapViewModel = mapViewModel,
                    )
                }
            }

            Box {
                MapButton(
                    icon = Icons.Outlined.Map,
                    contentDescription = stringResource(Res.string.map_tile_source),
                    onClick = onToggleMapTypeMenu,
                )
                MapTypeDropdown(
                    expanded = mapTypeMenuExpanded,
                    onDismissRequest = onMapTypeMenuDismissRequest,
                    mapViewModel = mapViewModel, 
                    onManageCustomTileProvidersClicked = onManageCustomTileProvidersClicked, 
                )
            }

            MapButton(
                icon = Icons.Outlined.Layers,
                contentDescription = stringResource(Res.string.manage_map_layers),
                onClick = onManageLayersClicked,
            )

            
            if (hasLocationPermission) {
                MapButton(
                    icon =
                    if (isLocationTrackingEnabled) {
                        Icons.Default.LocationDisabled
                    } else {
                        Icons.Outlined.MyLocation
                    },
                    contentDescription = stringResource(Res.string.toggle_my_position),
                    onClick = onToggleLocationTracking,
                )
            }
        },
    )
}

@Composable
private fun CompassButton(onClick: () -> Unit, bearing: Float, isFollowing: Boolean) {
    val icon = if (isFollowing) Icons.Filled.Navigation else Icons.Outlined.Navigation

    MapButton(
        modifier = Modifier.rotate(-bearing),
        icon = icon,
        iconTint = MaterialTheme.colorScheme.StatusRed.takeIf { bearing == 0f },
        contentDescription = stringResource(Res.string.orient_north),
        onClick = onClick,
    )
}
