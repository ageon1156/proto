package org.meshtastic.feature.map.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.meshtastic.core.strings.Res
import org.meshtastic.core.strings.nodes_at_this_location
import org.meshtastic.core.strings.okay
import org.meshtastic.core.ui.component.NodeChip
import org.meshtastic.feature.map.model.NodeClusterItem

@Composable
fun ClusterItemsListDialog(
    items: List<NodeClusterItem>,
    onDismiss: () -> Unit,
    onItemClick: (NodeClusterItem) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(Res.string.nodes_at_this_location)) },
        text = {
            
            LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
                items(items, key = { it.node.num }) { item ->
                    ClusterDialogListItem(item = item, onClick = { onItemClick(item) })
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text(stringResource(Res.string.okay)) } },
    )
}

@Composable
private fun ClusterDialogListItem(item: NodeClusterItem, onClick: () -> Unit, modifier: Modifier = Modifier) {
    ListItem(
        leadingContent = { NodeChip(node = item.node) },
        headlineContent = { Text(item.nodeTitle) },
        supportingContent = {
            if (item.nodeSnippet.isNotBlank()) {
                Text(item.nodeSnippet)
            }
        },
        modifier =
        modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp), 
    )
}
