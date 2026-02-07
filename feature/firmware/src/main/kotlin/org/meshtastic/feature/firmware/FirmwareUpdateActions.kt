package org.meshtastic.feature.firmware

import org.meshtastic.core.database.entity.FirmwareReleaseType

data class FirmwareUpdateActions(
    val onReleaseTypeSelect: (FirmwareReleaseType) -> Unit,
    val onStartUpdate: () -> Unit,
    val onPickFile: () -> Unit,
    val onSaveFile: (String) -> Unit,
    val onRetry: () -> Unit,
    val onCancel: () -> Unit,
    val onDone: () -> Unit,
    val onDismissBootloaderWarning: () -> Unit,
)
