package org.meshtastic.feature.map.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationDisabled
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.Navigation
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.meshtastic.core.strings.Res
import org.meshtastic.core.strings.manage_map_layers
import org.meshtastic.core.strings.map_filter
import org.meshtastic.core.strings.map_tile_source
import org.meshtastic.core.strings.orient_north
import org.meshtastic.core.strings.toggle_my_position
import org.meshtastic.core.ui.theme.StatusColors.StatusRed
import org.meshtastic.core.ui.theme.organicSpring
import org.meshtastic.feature.map.MapViewModel


@Composable
fun OrganicMapControlsOverlay(
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
    
    Box(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            )
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.95f))
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            
            OrganicCompassButton(
                onClick = onCompassClick,
                bearing = bearing,
                isFollowing = followPhoneBearing
            )

            if (isNodeMap) {
                OrganicMapButton(
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
                    OrganicMapButton(
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
                OrganicMapButton(
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

            OrganicMapButton(
                icon = Icons.Outlined.Layers,
                contentDescription = stringResource(Res.string.manage_map_layers),
                onClick = onManageLayersClicked,
            )

            
            if (hasLocationPermission) {
                OrganicMapButton(
                    icon = if (isLocationTrackingEnabled) {
                        Icons.Default.LocationDisabled
                    } else {
                        Icons.Outlined.MyLocation
                    },
                    iconTint = if (isLocationTrackingEnabled) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        null
                    },
                    contentDescription = stringResource(Res.string.toggle_my_position),
                    onClick = onToggleLocationTracking,
                )
            }
        }
    }
}


@Composable
private fun OrganicCompassButton(
    onClick: () -> Unit,
    bearing: Float,
    isFollowing: Boolean
) {
    val icon = if (isFollowing) Icons.Filled.Navigation else Icons.Outlined.Navigation

    
    val animatedBearing by animateFloatAsState(
        targetValue = -bearing,
        animationSpec = organicSpring(),
        label = "compass_rotation"
    )

    OrganicMapButton(
        modifier = Modifier.rotate(animatedBearing),
        icon = icon,
        iconTint = if (bearing == 0f) MaterialTheme.colorScheme.StatusRed else null,
        contentDescription = stringResource(Res.string.orient_north),
        onClick = onClick,
    )
}
