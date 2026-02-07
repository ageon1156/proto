package org.meshtastic.feature.firmware

import android.net.Uri
import org.meshtastic.core.database.entity.FirmwareRelease
import org.meshtastic.core.model.DeviceHardware
import java.io.File


data class ProgressState(val message: String = "", val progress: Float = 0f, val details: String? = null)

sealed interface FirmwareUpdateState {
    data object Idle : FirmwareUpdateState

    data object Checking : FirmwareUpdateState

    data class Ready(
        val release: FirmwareRelease?,
        val deviceHardware: DeviceHardware,
        val address: String,
        val showBootloaderWarning: Boolean,
        val updateMethod: FirmwareUpdateMethod,
        val currentFirmwareVersion: String? = null,
    ) : FirmwareUpdateState

    data class Downloading(val progressState: ProgressState) : FirmwareUpdateState

    data class Processing(val progressState: ProgressState) : FirmwareUpdateState

    data class Updating(val progressState: ProgressState) : FirmwareUpdateState

    data object Verifying : FirmwareUpdateState

    data object VerificationFailed : FirmwareUpdateState

    data class Error(val error: String) : FirmwareUpdateState

    data object Success : FirmwareUpdateState

    data class AwaitingFileSave(val uf2File: File?, val fileName: String, val sourceUri: Uri? = null) :
        FirmwareUpdateState
}
