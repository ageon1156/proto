package org.meshtastic.feature.settings.radio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import org.jetbrains.compose.resources.stringResource
import org.meshtastic.core.database.entity.NodeEntity
import org.meshtastic.core.strings.Res
import org.meshtastic.core.strings.are_you_sure
import org.meshtastic.core.strings.cancel
import org.meshtastic.core.strings.clean_node_database_confirmation
import org.meshtastic.core.strings.clean_node_database_description
import org.meshtastic.core.strings.clean_node_database_title
import org.meshtastic.core.strings.clean_nodes_older_than
import org.meshtastic.core.strings.clean_now
import org.meshtastic.core.strings.clean_unknown_nodes
import org.meshtastic.core.strings.nodes_queued_for_deletion
import org.meshtastic.core.ui.component.NodeChip


@Composable
fun CleanNodeDatabaseScreen(viewModel: CleanNodeDatabaseViewModel = hiltViewModel()) {
    val olderThanDays by viewModel.olderThanDays.collectAsState()
    val onlyUnknownNodes by viewModel.onlyUnknownNodes.collectAsState()
    val nodesToDelete by viewModel.nodesToDelete.collectAsState()
    var showConfirmationDialog by remember { mutableStateOf(false) }

    LaunchedEffect(olderThanDays, onlyUnknownNodes) { viewModel.getNodesToDelete() }

    if (showConfirmationDialog) {
        ConfirmationDialog(
            nodesToDeleteCount = nodesToDelete.size,
            onConfirm = {
                viewModel.cleanNodes()
                showConfirmationDialog = false
            },
            onDismiss = { showConfirmationDialog = false },
        )
    }

    Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
        Text(stringResource(Res.string.clean_node_database_title))
        Text(stringResource(Res.string.clean_node_database_description), style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(16.dp))

        DaysThresholdFilter(
            olderThanDays = olderThanDays,
            onlyUnknownNodes = onlyUnknownNodes,
            onDaysChanged = viewModel::onOlderThanDaysChanged,
        )

        Spacer(modifier = Modifier.height(8.dp))

        UnknownNodesFilter(onlyUnknownNodes = onlyUnknownNodes, onCheckedChanged = viewModel::onOnlyUnknownNodesChanged)

        Spacer(modifier = Modifier.height(32.dp))

        NodesDeletionPreview(nodesToDelete = nodesToDelete)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { if (nodesToDelete.isNotEmpty()) showConfirmationDialog = true },
            modifier = Modifier.fillMaxWidth(),
            enabled = nodesToDelete.isNotEmpty(),
        ) {
            Text(stringResource(Res.string.clean_now))
        }
    }
}

private const val MIN_UNKNOWN_DAYS_THRESHOLD = 0f
private const val MIN_KNOWN_DAYS_THRESHOLD = 7f
private const val MAX_DAYS_THRESHOLD = 365f


@Composable
private fun DaysThresholdFilter(olderThanDays: Float, onlyUnknownNodes: Boolean, onDaysChanged: (Float) -> Unit) {
    val valueRange =
        if (onlyUnknownNodes) {
            MIN_UNKNOWN_DAYS_THRESHOLD..MAX_DAYS_THRESHOLD
        } else {
            MIN_KNOWN_DAYS_THRESHOLD..MAX_DAYS_THRESHOLD
        }
    val steps = (valueRange.endInclusive - valueRange.start - 1).toInt().coerceAtLeast(0)

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier.padding(bottom = 8.dp),
            text = stringResource(Res.string.clean_nodes_older_than, olderThanDays.toInt()),
        )
        Slider(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            value = olderThanDays,
            onValueChange = onDaysChanged,
            valueRange = valueRange,
            steps = steps,
        )
    }
}


@Composable
private fun UnknownNodesFilter(onlyUnknownNodes: Boolean, onCheckedChanged: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(stringResource(Res.string.clean_unknown_nodes))
        Spacer(Modifier.weight(1f))
        Switch(checked = onlyUnknownNodes, onCheckedChange = onCheckedChanged)
    }
}


@Composable
private fun NodesDeletionPreview(nodesToDelete: List<NodeEntity>) {
    Text(
        stringResource(Res.string.nodes_queued_for_deletion, nodesToDelete.size),
        modifier = Modifier.padding(bottom = 16.dp),
    )
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.Center,
    ) {
        nodesToDelete.forEach { node ->
            NodeChip(node = node.toModel(), modifier = Modifier.padding(end = 8.dp, bottom = 8.dp))
        }
    }
}


@Composable
private fun ConfirmationDialog(nodesToDeleteCount: Int, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.are_you_sure)) },
        text = { Text(stringResource(Res.string.clean_node_database_confirmation, nodesToDeleteCount)) },
        confirmButton = { Button(onClick = onConfirm) { Text(stringResource(Res.string.clean_now)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(Res.string.cancel)) } },
    )
}
