package org.meshtastic.feature.firmware.ota


sealed class OtaCommand {
    
    data class StartOta(val sizeBytes: Long, val sha256Hash: String) : OtaCommand() {
        override fun toString() = "OTA $sizeBytes $sha256Hash\n"
    }
}


sealed class OtaResponse {
    
    data class Ok(
        val hwVersion: String? = null,
        val fwVersion: String? = null,
        val rebootCount: Int? = null,
        val gitHash: String? = null,
    ) : OtaResponse()

    
    data object Erasing : OtaResponse()

    
    data object Ack : OtaResponse()

    
    data class Error(val message: String) : OtaResponse()

    companion object {
        private const val OK_PREFIX_LENGTH = 3
        private const val ERR_PREFIX_LENGTH = 4
        private const val VERSION_PARTS_COUNT = 4

        
        fun parse(response: String): OtaResponse {
            val trimmed = response.trim()

            return when {
                trimmed == "OK" -> Ok()
                trimmed.startsWith("OK ") -> {
                    val parts = trimmed.substring(OK_PREFIX_LENGTH).split(" ")
                    when (parts.size) {
                        VERSION_PARTS_COUNT ->
                            Ok(
                                hwVersion = parts[0],
                                fwVersion = parts[1],
                                rebootCount = parts[2].toIntOrNull(),
                                gitHash = parts[3],
                            )
                        else -> Ok()
                    }
                }
                trimmed == "ERASING" -> Erasing
                trimmed == "ACK" -> Ack
                trimmed.startsWith("ERR ") -> Error(trimmed.substring(ERR_PREFIX_LENGTH))
                trimmed == "ERR" -> Error("Unknown error")
                else -> Error("Unknown response: $trimmed")
            }
        }
    }
}


sealed class OtaHandshakeStatus {
    
    data object Erasing : OtaHandshakeStatus()
}


interface UnifiedOtaProtocol {
    

    suspend fun connect(): Result<Unit>

    
    suspend fun startOta(
        sizeBytes: Long,
        sha256Hash: String,
        onHandshakeStatus: suspend (OtaHandshakeStatus) -> Unit = {},
    ): Result<Unit>

    
    suspend fun streamFirmware(data: ByteArray, chunkSize: Int, onProgress: suspend (Float) -> Unit): Result<Unit>

    
    suspend fun close()
}


sealed class OtaProtocolException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class ConnectionFailed(message: String, cause: Throwable? = null) : OtaProtocolException(message, cause)

    class CommandFailed(val command: OtaCommand, val response: OtaResponse.Error) :
        OtaProtocolException("Command $command failed: ${response.message}")

    class HashRejected(val providedHash: String) :
        OtaProtocolException("Device rejected hash: $providedHash (NVS mismatch)")

    class TransferFailed(message: String, cause: Throwable? = null) : OtaProtocolException(message, cause)

    class VerificationFailed(message: String) : OtaProtocolException(message)

    class Timeout(message: String) : OtaProtocolException(message)
}
