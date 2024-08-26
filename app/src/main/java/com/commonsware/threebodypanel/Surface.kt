package com.commonsware.threebodypanel

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape


@Composable
internal fun Surface(
    shape: Shape,
    colorNormal: Color,
    colorPressed: Color,
    colorDisabled: Color,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    border: BorderStroke? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onClick: () -> Unit = {},
    content: @Composable () -> Unit = {}
) {
    val isPressed = interactionSource.collectIsPressedAsState().value
    val color = when {
        !isEnabled -> colorDisabled
        isPressed -> colorPressed
        else -> colorNormal
    }

    Box(
        modifier =
        modifier
            .background(color)
            .apply { border?.let { border(it) } }
            .clip(shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = isEnabled,
                onClick = onClick
            ),
        propagateMinConstraints = true
    ) {
        content()
    }
}
