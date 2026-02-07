package org.meshtastic.feature.firmware

import co.touchlab.kermit.Logger
import org.meshtastic.core.database.entity.FirmwareRelease
import org.meshtastic.core.model.DeviceHardware
import java.io.File
import javax.inject.Inject


class FirmwareRetriever @Inject constructor(private val fileHandler: FirmwareFileHandler) {
    suspend fun retrieveOtaFirmware(
        release: FirmwareRelease,
        hardware: DeviceHardware,
        onProgress: (Float) -> Unit,
    ): File? = retrieve(
        release = release,
        hardware = hardware,
        onProgress = onProgress,
        fileSuffix = "-ota.zip",
        internalFileExtension = ".zip",
    )

    suspend fun retrieveUsbFirmware(
        release: FirmwareRelease,
        hardware: DeviceHardware,
        onProgress: (Float) -> Unit,
    ): File? = retrieve(
        release = release,
        hardware = hardware,
        onProgress = onProgress,
        fileSuffix = ".uf2",
        internalFileExtension = ".uf2",
    )

    suspend fun retrieveEsp32Firmware(
        release: FirmwareRelease,
        hardware: DeviceHardware,
        onProgress: (Float) -> Unit,
    ): File? {
        val mcu = hardware.architecture.replace("-", "")
        val otaFilename = "mt-$mcu-ota.bin"
        retrieve(
            release = release,
            hardware = hardware,
            onProgress = onProgress,
            fileSuffix = ".bin",
            internalFileExtension = ".bin",
            preferredFilename = otaFilename,
        )
            ?.let {
                return it
            }

        
        return retrieve(
            release = release,
            hardware = hardware,
            onProgress = onProgress,
            fileSuffix = ".bin",
            internalFileExtension = ".bin",
        )
    }

    private suspend fun retrieve(
        release: FirmwareRelease,
        hardware: DeviceHardware,
        onProgress: (Float) -> Unit,
        fileSuffix: String,
        internalFileExtension: String,
        preferredFilename: String? = null,
    ): File? {
        val version = release.id.removePrefix("v")
        val target = hardware.platformioTarget.ifEmpty { hardware.hwModelSlug }
        val filename = preferredFilename ?: "firmware-$target-$version$fileSuffix"
        val directUrl =
            "https://raw.githubusercontent.com/meshtastic/meshtastic.github.io/master/firmware-$version/$filename"

        if (fileHandler.checkUrlExists(directUrl)) {
            try {
                fileHandler.downloadFile(directUrl, filename, onProgress)?.let {
                    return it
                }
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                Logger.w(e) { "Direct download for $filename failed, falling back to release zip" }
            }
        }

        
        val zipUrl = getDeviceFirmwareUrl(release.zipUrl, hardware.architecture)
        val downloadedZip = fileHandler.downloadFile(zipUrl, "firmware_release.zip", onProgress)
        return downloadedZip?.let {
            fileHandler.extractFirmware(it, hardware, internalFileExtension, preferredFilename)
        }
    }

    private fun getDeviceFirmwareUrl(url: String, targetArch: String): String {
        val knownArchs = listOf("esp32-s3", "esp32-c3", "esp32-c6", "nrf52840", "rp2040", "stm32", "esp32")
        for (arch in knownArchs) {
            if (url.contains(arch, ignoreCase = true)) {
                return url.replace(arch, targetArch.lowercase(), ignoreCase = true)
            }
        }
        return url
    }
}
