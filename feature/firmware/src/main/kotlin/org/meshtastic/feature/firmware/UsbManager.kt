package org.meshtastic.feature.firmware

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbManager
import android.os.Build
import co.touchlab.kermit.Logger
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UsbManager @Inject constructor(@ApplicationContext private val context: Context) {
    
    fun deviceDetachFlow(): Flow<Unit> = callbackFlow {
        val receiver =
            object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    if (intent.action == UsbManager.ACTION_USB_DEVICE_DETACHED) {
                        trySend(Unit).isSuccess
                        close()
                    }
                }
            }
        val filter = IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            context.registerReceiver(receiver, filter)
        }

        awaitClose {
            runCatching { context.unregisterReceiver(receiver) }
                .onFailure { Logger.w(it) { "Failed to unregister USB receiver" } }
        }
    }
}
