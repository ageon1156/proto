package org.meshtastic.feature.node.metrics

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

internal object TracerouteMapOverlayInsets {
    val overlayAlignment: Alignment = Alignment.BottomEnd
    val overlayPadding: PaddingValues = PaddingValues(end = 16.dp, bottom = 16.dp)
    val contentHorizontalAlignment: Alignment.Horizontal = Alignment.End
}
