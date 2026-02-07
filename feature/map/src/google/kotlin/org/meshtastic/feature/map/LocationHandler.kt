package org.meshtastic.feature.map

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import co.touchlab.kermit.Logger
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority

private const val INTERVAL_MILLIS = 10000L

@Suppress("LongMethod")
@Composable
fun LocationPermissionsHandler(onPermissionResult: (Boolean) -> Unit) {
    val context = LocalContext.current
    var localHasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED,
        )
    }

    val requestLocationPermissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
            localHasPermission = isGranted
            
            
            if (!isGranted) {
                onPermissionResult(false)
            }
        }

    val locationSettingsLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Logger.d { "Location settings changed by user." }
                
                onPermissionResult(true) 
            } else {
                Logger.d { "Location settings change cancelled by user." }
                
                
                onPermissionResult(localHasPermission)
            }
        }

    LaunchedEffect(Unit) {
        
        when (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
            PackageManager.PERMISSION_GRANTED -> {
                if (!localHasPermission) {
                    localHasPermission = true
                }
                
                
            }

            else -> {
                
                requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    LaunchedEffect(localHasPermission) {
        
        if (localHasPermission) {
            
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, INTERVAL_MILLIS).build()

            val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

            val client = LocationServices.getSettingsClient(context)
            val task = client.checkLocationSettings(builder.build())

            task.addOnSuccessListener {
                Logger.d { "Location settings are satisfied." }
                onPermissionResult(true) 
            }

            task.addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                        locationSettingsLauncher.launch(intentSenderRequest)
                        
                    } catch (sendEx: ActivityNotFoundException) {
                        Logger.d { "Error launching location settings resolution ${sendEx.message}." }
                        onPermissionResult(true) 
                    }
                } else {
                    Logger.d { "Location settings are not satisfiable.${exception.message}" }
                    onPermissionResult(true) 
                }
            }
        } else {
            
            
            onPermissionResult(false)
        }
    }
}
