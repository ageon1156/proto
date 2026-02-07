package org.meshtastic.feature.map.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberUpdatedMarkerState
import kotlinx.coroutines.launch
import org.meshtastic.core.strings.Res
import org.meshtastic.core.strings.locked
import org.meshtastic.core.ui.util.showToast
import org.meshtastic.feature.map.BaseMapViewModel
import org.meshtastic.proto.MeshProtos

private const val DEG_D = 1e-7

@Composable
fun WaypointMarkers(
    displayableWaypoints: List<MeshProtos.Waypoint>,
    mapFilterState: BaseMapViewModel.MapFilterState,
    myNodeNum: Int,
    isConnected: Boolean,
    unicodeEmojiToBitmapProvider: (Int) -> BitmapDescriptor,
    onEditWaypointRequest: (MeshProtos.Waypoint) -> Unit,
    selectedWaypointId: Int? = null,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    if (mapFilterState.showWaypoints) {
        displayableWaypoints.forEach { waypoint ->
            val markerState =
                rememberUpdatedMarkerState(position = LatLng(waypoint.latitudeI * DEG_D, waypoint.longitudeI * DEG_D))

            LaunchedEffect(selectedWaypointId) {
                if (selectedWaypointId == waypoint.id) {
                    markerState.showInfoWindow()
                }
            }

            Marker(
                state = markerState,
                icon =
                if (waypoint.icon == 0) {
                    unicodeEmojiToBitmapProvider(PUSHPIN) 
                } else {
                    unicodeEmojiToBitmapProvider(waypoint.icon)
                },
                title = waypoint.name.replace('\n', ' ').replace('\b', ' '),
                snippet = waypoint.description.replace('\n', ' ').replace('\b', ' '),
                visible = true,
                onInfoWindowClick = {
                    if (waypoint.lockedTo == 0 || waypoint.lockedTo == myNodeNum || !isConnected) {
                        onEditWaypointRequest(waypoint)
                    } else {
                        scope.launch { context.showToast(Res.string.locked) }
                    }
                },
            )
        }
    }
}

private const val PUSHPIN = 0x1F4CD 
