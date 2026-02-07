package org.meshtastic.feature.map.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.meshtastic.core.database.model.Node
import org.meshtastic.core.ui.component.NodeChip
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds


@Composable
fun OrganicPulsingNodeChip(node: Node, modifier: Modifier = Modifier) {
    val animatedProgress = remember { Animatable(0f) }
    val pulseColor = MaterialTheme.colorScheme.primary

    LaunchedEffect(node) {
        
        if ((System.currentTimeMillis().milliseconds.inWholeSeconds - node.lastHeard.seconds.inWholeSeconds) <= 5) {
            launch {
                animatedProgress.snapTo(0f)
                
                animatedProgress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = 1200, 
                        easing = FastOutSlowInEasing
                    ),
                )
            }
        }
    }

    Box(
        modifier = modifier.drawWithContent {
            drawContent()
            if (animatedProgress.value > 0 && animatedProgress.value < 1f) {
                
                val progress = animatedProgress.value
                
                val alpha = (1f - progress * progress) * 0.25f
                
                val scale = 1f + (progress * 0.1f)

                drawRoundRect(
                    size = size.copy(
                        width = size.width * scale,
                        height = size.height * scale
                    ),
                    topLeft = androidx.compose.ui.geometry.Offset(
                        x = -size.width * (scale - 1f) / 2,
                        y = -size.height * (scale - 1f) / 2
                    ),
                    cornerRadius = CornerRadius(12.dp.toPx()),
                    color = pulseColor.copy(alpha = alpha),
                )
            }
        },
    ) {
        NodeChip(node = node)
    }
}
