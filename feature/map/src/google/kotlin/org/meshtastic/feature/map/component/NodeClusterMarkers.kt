package org.meshtastic.feature.map.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.graphics.Color
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.clustering.Clustering
import org.meshtastic.feature.map.BaseMapViewModel
import org.meshtastic.feature.map.model.NodeClusterItem

@OptIn(MapsComposeExperimentalApi::class)
@Suppress("NestedBlockDepth")
@Composable
fun NodeClusterMarkers(
    nodeClusterItems: List<NodeClusterItem>,
    mapFilterState: BaseMapViewModel.MapFilterState,
    navigateToNodeDetails: (Int) -> Unit,
    onClusterClick: (Cluster<NodeClusterItem>) -> Boolean,
) {
    if (mapFilterState.showPrecisionCircle) {
        nodeClusterItems.forEach { clusterItem ->
            key(clusterItem.node.num) {
                
                clusterItem.getPrecisionMeters()?.let { precisionMeters ->
                    if (precisionMeters > 0) {
                        Circle(
                            center = clusterItem.position,
                            radius = precisionMeters,
                            fillColor = Color(clusterItem.node.colors.second).copy(alpha = 0.2f),
                            strokeColor = Color(clusterItem.node.colors.second),
                            strokeWidth = 2f,
                            zIndex = 0f,
                        )
                    }
                }
            }
        }
    }
    Clustering(
        items = nodeClusterItems,
        onClusterClick = onClusterClick,
        onClusterItemInfoWindowClick = { item ->
            navigateToNodeDetails(item.node.num)
            false
        },
        clusterItemContent = { clusterItem -> PulsingNodeChip(node = clusterItem.node) },
        onClusterManager = { clusterManager ->
            (clusterManager.renderer as DefaultClusterRenderer).minClusterSize = 10
        },
    )
}
