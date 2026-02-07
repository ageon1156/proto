package org.meshtastic.feature.firmware.ota

import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest


object FirmwareHashUtil {

    private const val BUFFER_SIZE = 8192

    
    fun calculateSha256Bytes(file: File): ByteArray {
        val digest = MessageDigest.getInstance("SHA-256")
        FileInputStream(file).use { fis ->
            val buffer = ByteArray(BUFFER_SIZE)
            var bytesRead: Int
            while (fis.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }
        }
        return digest.digest()
    }

    
    fun bytesToHex(bytes: ByteArray): String = bytes.joinToString("") { "%02x".format(it) }
}
