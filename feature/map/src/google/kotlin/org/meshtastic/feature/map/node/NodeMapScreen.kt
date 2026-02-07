package org.meshtastic.feature.map.node

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.meshtastic.core.ui.component.MainAppBar
import org.meshtastic.feature.map.MapView

@Composable
fun NodeMapScreen(nodeMapViewModel: NodeMapViewModel, onNavigateUp: () -> Unit) {
    val node by nodeMapViewModel.node.collectAsStateWithLifecycle()
    val positions by nodeMapViewModel.positionLogs.collectAsStateWithLifecycle()
    val destNum = node?.num

    Scaffold(
        topBar = {
            MainAppBar(
                title = node?.user?.longName ?: "",
                ourNode = null,
                showNodeChip = false,
                canNavigateUp = true,
                onNavigateUp = onNavigateUp,
                actions = {},
                onClickChip = {},
            )
        },
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            MapView(focusedNodeNum = destNum, nodeTracks = positions, navigateToNodeDetails = {})
        }
    }
}
