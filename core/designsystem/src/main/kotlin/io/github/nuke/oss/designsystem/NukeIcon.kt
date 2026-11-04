package io.github.nuke.oss.designsystem

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Takes its tint from LocalContentColor, so a parent can recolour a Phosphor icon without passing a colour down.
@Composable
fun NukeIcon(icon: ImageVector, description: String?, modifier: Modifier = Modifier, iconSize: Dp = 24.dp) {
    Icon(icon, description, modifier.size(iconSize))
}
