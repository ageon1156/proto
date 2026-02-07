package org.meshtastic.feature.map.component

import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun MapButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconTint: Color? = null,
    contentDescription: String,
    onClick: () -> Unit,
) {
    FilledIconButton(onClick = onClick, modifier = modifier) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconTint ?: IconButtonDefaults.filledIconButtonColors().contentColor,
        )
    }
}
