package org.meshtastic.feature.map.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.meshtastic.core.strings.Res
import org.meshtastic.core.strings.nodes_at_this_location
import org.meshtastic.core.strings.okay
import org.meshtastic.core.ui.theme.LeafShape
import org.meshtastic.core.ui.theme.organicSpring
import org.meshtastic.feature.map.model.NodeClusterItem


@Composable
fun OrganicClusterItemsListDialog(
    items: List<NodeClusterItem>,
    onDismiss: () -> Unit,
    onItemClick: (NodeClusterItem) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = LeafShape,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        title = {
            Text(
                text = stringResource(Res.string.nodes_at_this_location),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items, key = { it.node.num }) { item ->
                    OrganicClusterDialogListItem(
                        item = item,
                        onClick = { onItemClick(item) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = stringResource(Res.string.okay),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
    )
}


@Composable
private fun OrganicClusterDialogListItem(
    item: NodeClusterItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = organicSpring(),
        label = "cluster_item_scale"
    )

    
    val gradient = remember(item.node.num) {
        val hue = ((item.node.num * 137) % 360).toFloat()
        val color1 = android.graphics.Color.HSVToColor(floatArrayOf(hue, 0.5f, 0.7f))
        val color2 = android.graphics.Color.HSVToColor(floatArrayOf((hue + 60) % 360, 0.6f, 0.8f))
        Brush.linearGradient(colors = listOf(Color(color1), Color(color2)))
    }

    Row(
        modifier = modifier
            .scale(scale)
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .clickable(
                onClick = {
                    isPressed = true
                    onClick()
                }
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(gradient),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = item.node.user.shortName.take(2).ifEmpty { "??" },
                style = MaterialTheme.typography.labelLarge,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.nodeTitle,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (item.nodeSnippet.isNotBlank()) {
                Text(
                    text = item.nodeSnippet,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
