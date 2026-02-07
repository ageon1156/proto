package org.meshtastic.feature.map.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.maps.android.compose.MapType
import org.jetbrains.compose.resources.stringResource
import org.meshtastic.core.strings.Res
import org.meshtastic.core.strings.manage_custom_tile_sources
import org.meshtastic.core.strings.map_type_hybrid
import org.meshtastic.core.strings.map_type_normal
import org.meshtastic.core.strings.map_type_satellite
import org.meshtastic.core.strings.map_type_terrain
import org.meshtastic.core.strings.selected_map_type
import org.meshtastic.feature.map.MapViewModel

@Suppress("LongMethod")
@Composable
internal fun MapTypeDropdown(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    mapViewModel: MapViewModel,
    onManageCustomTileProvidersClicked: () -> Unit,
) {
    val customTileProviders by mapViewModel.customTileProviderConfigs.collectAsStateWithLifecycle()
    val selectedCustomUrl by mapViewModel.selectedCustomTileProviderUrl.collectAsStateWithLifecycle()
    val selectedGoogleMapType by mapViewModel.selectedGoogleMapType.collectAsStateWithLifecycle()

    val googleMapTypes =
        listOf(
            stringResource(Res.string.map_type_normal) to MapType.NORMAL,
            stringResource(Res.string.map_type_satellite) to MapType.SATELLITE,
            stringResource(Res.string.map_type_terrain) to MapType.TERRAIN,
            stringResource(Res.string.map_type_hybrid) to MapType.HYBRID,
        )

    DropdownMenu(expanded = expanded, onDismissRequest = onDismissRequest) {
        googleMapTypes.forEach { (name, type) ->
            DropdownMenuItem(
                text = { Text(name) },
                onClick = {
                    mapViewModel.setSelectedGoogleMapType(type)
                    onDismissRequest() 
                },
                trailingIcon =
                if (selectedCustomUrl == null && selectedGoogleMapType == type) {
                    { Icon(Icons.Filled.Check, contentDescription = stringResource(Res.string.selected_map_type)) }
                } else {
                    null
                },
            )
        }

        if (customTileProviders.isNotEmpty()) {
            HorizontalDivider()
            customTileProviders.forEach { config ->
                DropdownMenuItem(
                    text = { Text(config.name) },
                    onClick = {
                        mapViewModel.selectCustomTileProvider(config)
                        onDismissRequest() 
                    },
                    trailingIcon =
                    if (selectedCustomUrl == config.urlTemplate) {
                        {
                            Icon(
                                Icons.Filled.Check,
                                contentDescription = stringResource(Res.string.selected_map_type),
                            )
                        }
                    } else {
                        null
                    },
                )
            }
        }
        HorizontalDivider()
        DropdownMenuItem(
            text = { Text(stringResource(Res.string.manage_custom_tile_sources)) },
            onClick = {
                onManageCustomTileProvidersClicked()
                onDismissRequest()
            },
        )
    }
}
