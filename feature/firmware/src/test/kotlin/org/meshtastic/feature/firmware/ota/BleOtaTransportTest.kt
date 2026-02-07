package org.meshtastic.feature.firmware.ota

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import no.nordicsemi.kotlin.ble.client.RemoteCharacteristic
import no.nordicsemi.kotlin.ble.client.RemoteService
import no.nordicsemi.kotlin.ble.client.android.CentralManager
import no.nordicsemi.kotlin.ble.client.android.Peripheral
import no.nordicsemi.kotlin.ble.client.android.ScanResult
import no.nordicsemi.kotlin.ble.core.ConnectionState
import org.junit.Test
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.toKotlinUuid

private val SERVICE_UUID = UUID.fromString("4FAFC201-1FB5-459E-8FCC-C5C9C331914B")
private val OTA_CHARACTERISTIC_UUID = UUID.fromString("62ec0272-3ec5-11eb-b378-0242ac130005")
private val TX_CHARACTERISTIC_UUID = UUID.fromString("62ec0272-3ec5-11eb-b378-0242ac130003")

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalUuidApi::class)
class BleOtaTransportTest {

    private val centralManager: CentralManager = mockk()
    private val address = "00:11:22:33:44:55"
    private val transport = BleOtaTransport(centralManager, address)

    @Test
    fun `race condition check - response before waitForResponse`() = runTest {
        val peripheral: Peripheral = mockk(relaxed = true)
        val otaChar: RemoteCharacteristic = mockk(relaxed = true)
        val txChar: RemoteCharacteristic = mockk(relaxed = true)
        val service: RemoteService = mockk(relaxed = true)
        val scanResult: ScanResult = mockk()

        every { scanResult.peripheral } returns peripheral

        
        every { centralManager.scan(any(), any()) } returns flowOf(scanResult)

        every { peripheral.address } returns address
        every { peripheral.state } returns MutableStateFlow(ConnectionState.Connected)

        coEvery { peripheral.services(any()) } returns MutableStateFlow(listOf(service))
        every { service.uuid } returns SERVICE_UUID.toKotlinUuid()
        every { service.characteristics } returns listOf(otaChar, txChar)
        every { otaChar.uuid } returns OTA_CHARACTERISTIC_UUID.toKotlinUuid()
        every { txChar.uuid } returns TX_CHARACTERISTIC_UUID.toKotlinUuid()

        coEvery { centralManager.connect(any(), any()) } returns Unit

        val notificationFlow = MutableSharedFlow<ByteArray>()
        every { txChar.subscribe() } returns notificationFlow

        
        transport.connect().getOrThrow()

        
        val size = 100L
        val hash = "hash"

        
        coEvery { otaChar.write(any(), any()) } coAnswers { notificationFlow.emit("OK\n".toByteArray()) }

        val result = transport.startOta(size, hash) {}
        assert(result.isSuccess)
    }
}
