package org.meshtastic.feature.settings.radio.component

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.meshtastic.core.strings.Res
import org.meshtastic.core.strings.bluetooth
import org.meshtastic.core.strings.bluetooth_config
import org.meshtastic.core.strings.bluetooth_enabled
import org.meshtastic.core.strings.fixed_pin
import org.meshtastic.core.strings.pairing_mode
import org.meshtastic.core.ui.component.DropDownPreference
import org.meshtastic.core.ui.component.EditTextPreference
import org.meshtastic.core.ui.component.SwitchPreference
import org.meshtastic.core.ui.component.TitledCard
import org.meshtastic.feature.settings.radio.RadioConfigViewModel
import org.meshtastic.proto.ConfigProtos.Config.BluetoothConfig
import org.meshtastic.proto.config
import org.meshtastic.proto.copy

@Composable
fun BluetoothConfigScreen(viewModel: RadioConfigViewModel = hiltViewModel(), onBack: () -> Unit) {
    val state by viewModel.radioConfigState.collectAsStateWithLifecycle()
    val bluetoothConfig = state.radioConfig.bluetooth
    val formState = rememberConfigState(initialValue = bluetoothConfig)
    val focusManager = LocalFocusManager.current

    RadioConfigScreenList(
        title = stringResource(Res.string.bluetooth),
        onBack = onBack,
        configState = formState,
        enabled = state.connected,
        responseState = state.responseState,
        onDismissPacketResponse = viewModel::clearPacketResponse,
        onSave = {
            val config = config { bluetooth = it }
            viewModel.setConfig(config)
        },
    ) {
        item {
            TitledCard(title = stringResource(Res.string.bluetooth_config)) {
                SwitchPreference(
                    title = stringResource(Res.string.bluetooth_enabled),
                    checked = formState.value.enabled,
                    enabled = state.connected,
                    onCheckedChange = { formState.value = formState.value.copy { this.enabled = it } },
                    containerColor = CardDefaults.cardColors().containerColor,
                )
                HorizontalDivider()
                DropDownPreference(
                    title = stringResource(Res.string.pairing_mode),
                    enabled = state.connected,
                    items =
                    BluetoothConfig.PairingMode.entries
                        .filter { it != BluetoothConfig.PairingMode.UNRECOGNIZED }
                        .map { it to it.name },
                    selectedItem = formState.value.mode,
                    onItemSelected = { formState.value = formState.value.copy { mode = it } },
                )
                HorizontalDivider()
                EditTextPreference(
                    title = stringResource(Res.string.fixed_pin),
                    value = formState.value.fixedPin,
                    enabled = state.connected,
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    onValueChanged = {
                        if (it.toString().length == 6) { 
                            formState.value = formState.value.copy { fixedPin = it }
                        }
                    },
                )
            }
        }
    }
}
