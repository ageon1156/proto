package org.meshtastic.feature.firmware

import android.net.Uri
import org.meshtastic.core.database.entity.FirmwareRelease
import org.meshtastic.core.model.DeviceHardware
import java.io.File


interface FirmwareUpdateHandler {
    

    suspend fun startUpdate(
        release: FirmwareRelease,
        hardware: DeviceHardware,
        target: String,
        updateState: (FirmwareUpdateState) -> Unit,
        firmwareUri: Uri? = null,
    ): File?
}
