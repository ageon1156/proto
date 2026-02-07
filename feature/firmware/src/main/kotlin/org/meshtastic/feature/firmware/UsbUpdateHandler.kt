package org.meshtastic.feature.firmware

import android.net.Uri
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.getString
import org.meshtastic.core.database.entity.FirmwareRelease
import org.meshtastic.core.model.DeviceHardware
import org.meshtastic.core.service.ServiceRepository
import org.meshtastic.core.strings.Res
import org.meshtastic.core.strings.firmware_update_downloading_percent
import org.meshtastic.core.strings.firmware_update_rebooting
import org.meshtastic.core.strings.firmware_update_retrieval_failed
import org.meshtastic.core.strings.firmware_update_usb_failed
import java.io.File
import javax.inject.Inject

private const val REBOOT_DELAY = 5000L
private const val PERCENT_MAX = 100


class UsbUpdateHandler
@Inject
constructor(
    private val firmwareRetriever: FirmwareRetriever,
    private val serviceRepository: ServiceRepository,
) : FirmwareUpdateHandler {

    override suspend fun startUpdate(
        release: FirmwareRelease,
        hardware: DeviceHardware,
        target: String, 
        updateState: (FirmwareUpdateState) -> Unit,
        firmwareUri: Uri?,
    ): File? =
        try {
            val downloadingMsg =
                getString(Res.string.firmware_update_downloading_percent, 0)
                    .replace(Regex(":?\\s*%1\\\$d%?"), "")
                    .trim()

            updateState(FirmwareUpdateState.Downloading(ProgressState(message = downloadingMsg, progress = 0f)))

            val rebootingMsg = getString(Res.string.firmware_update_rebooting)

            if (firmwareUri != null) {
                updateState(FirmwareUpdateState.Processing(ProgressState(rebootingMsg)))
                val myNodeNum = serviceRepository.meshService?.getMyNodeInfo()?.myNodeNum ?: 0
                serviceRepository.meshService?.rebootToDfu(myNodeNum)
                delay(REBOOT_DELAY)

                updateState(FirmwareUpdateState.AwaitingFileSave(null, "firmware.uf2", firmwareUri))
                null
            } else {
                val firmwareFile =
                    firmwareRetriever.retrieveUsbFirmware(release, hardware) { progress ->
                        val percent = (progress * PERCENT_MAX).toInt()
                        updateState(
                            FirmwareUpdateState.Downloading(
                                ProgressState(message = downloadingMsg, progress = progress, details = "$percent%"),
                            ),
                        )
                    }

                if (firmwareFile == null) {
                    val retrievalFailedMsg = getString(Res.string.firmware_update_retrieval_failed)
                    updateState(FirmwareUpdateState.Error(retrievalFailedMsg))
                    null
                } else {
                    updateState(FirmwareUpdateState.Processing(ProgressState(rebootingMsg)))
                    val myNodeNum = serviceRepository.meshService?.getMyNodeInfo()?.myNodeNum ?: 0
                    serviceRepository.meshService?.rebootToDfu(myNodeNum)
                    delay(REBOOT_DELAY)

                    updateState(FirmwareUpdateState.AwaitingFileSave(firmwareFile, firmwareFile.name))
                    firmwareFile
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            Logger.e(e) { "USB Update failed" }
            val usbFailedMsg = getString(Res.string.firmware_update_usb_failed)
            updateState(FirmwareUpdateState.Error(e.message ?: usbFailedMsg))
            null
        }
}
