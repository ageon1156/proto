package org.meshtastic.feature.firmware

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import kotlinx.coroutines.runBlocking
import no.nordicsemi.android.dfu.DfuBaseService
import org.jetbrains.compose.resources.getString
import org.meshtastic.core.model.BuildConfig
import org.meshtastic.core.strings.Res
import org.meshtastic.core.strings.firmware_update_channel_description
import org.meshtastic.core.strings.firmware_update_channel_name

class FirmwareDfuService : DfuBaseService() {
    override fun onCreate() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        
        val (channelName, channelDesc) =
            runBlocking {
                getString(Res.string.firmware_update_channel_name) to
                    getString(Res.string.firmware_update_channel_description)
            }

        val channel =
            NotificationChannel(NOTIFICATION_CHANNEL_DFU, channelName, NotificationManager.IMPORTANCE_LOW).apply {
                description = channelDesc
                setShowBadge(false)
            }
        manager.createNotificationChannel(channel)
        super.onCreate()
    }

    override fun getNotificationTarget(): Class<out Activity>? = try {
        
        @Suppress("UNCHECKED_CAST")
        Class.forName("com.geeksville.mesh.MainActivity") as Class<out Activity>
    } catch (_: ClassNotFoundException) {
        null
    }

    override fun isDebug(): Boolean = BuildConfig.DEBUG
}
