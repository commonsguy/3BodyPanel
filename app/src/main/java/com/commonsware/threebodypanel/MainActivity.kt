package com.commonsware.threebodypanel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

private val PANEL_WIDTH = 520.dp
private val SECTION_HEIGHT = 100.dp
private val PANEL_BACKGROUND_COLOR = Color(0x1e, 0x1e, 0x1e)
private val FULL_SECTION_COLOR = Color(0xff, 0xc2, 0x0a)
private val PERMANENT_SECTION_COLOR = Color(0x0c, 0x7b, 0xdc)

private enum class PanelState {
    Collapsed,
    Full,
    Partial
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        WindowCompat
            .getInsetsController(window, window.decorView)
            .hide(WindowInsetsCompat.Type.systemBars())

        setContent {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentWidth(Alignment.Start)
            ) {
                val panelState = remember { mutableStateOf(PanelState.Collapsed) }
                val showPanel = remember { MutableTransitionState(false) }

                Spacer(modifier = Modifier.weight(1.0f))

                AnimatingPanel(showPanel, panelState.value)

                ButtonBar {
                    when (panelState.value) {
                        PanelState.Collapsed -> {
                            panelState.value = PanelState.Full
                            showPanel.targetState = true
                        }

                        PanelState.Full -> {
                            panelState.value = PanelState.Partial
                            showPanel.targetState = true
                        }

                        PanelState.Partial -> {
                            panelState.value = PanelState.Full
                            showPanel.targetState = true
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun AnimatingPanel(
        showPanel: MutableTransitionState<Boolean>,
        panelState: PanelState,
        modifier: Modifier = Modifier
    ) {
        AnimatedVisibility(
            visibleState = showPanel,
            enter = slideInVertically { it },
            exit = slideOutVertically { it }
        ) {
            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val showFullPanel = remember { MutableTransitionState(true) }

                OptionalSection(showFullPanel)

                CollapsibleSections(panelState, showFullPanel, modifier)
            }
        }
    }

    @Composable
    private fun CollapsibleSections(
        panelState: PanelState,
        showFullPanel: MutableTransitionState<Boolean>,
        modifier: Modifier
    ) {
        val animatedHeight by animateDpAsState(
            targetValue = when (panelState) {
                PanelState.Collapsed -> SECTION_HEIGHT
                PanelState.Partial -> SECTION_HEIGHT
                PanelState.Full -> SECTION_HEIGHT * 2
            },
            animationSpec = tween(durationMillis = 500), // update this value as needed
            label = "animateDpAsState"
        ) {
            showFullPanel.targetState = panelState == PanelState.Full
        }

        Column(modifier = Modifier.height(animatedHeight)) {
            Box(
                modifier = Modifier
                    .background(PERMANENT_SECTION_COLOR)
                    .size(PANEL_WIDTH, SECTION_HEIGHT)
            )

            Box(
                modifier = modifier
                    .background(FULL_SECTION_COLOR)
                    .size(PANEL_WIDTH, SECTION_HEIGHT)
            )
        }
    }

    @Composable
    private fun OptionalSection(
        showFullPanel: MutableTransitionState<Boolean>,
        modifier: Modifier = Modifier
    ) {
        AnimatedVisibility(
            visibleState = showFullPanel,
            enter = slideInVertically { it },
            exit = slideOutVertically { it }
        ) {
            Box(
                modifier = modifier
                    .background(FULL_SECTION_COLOR)
                    .size(PANEL_WIDTH, SECTION_HEIGHT)
            )
        }
    }

    @Composable
    private fun ButtonBar(modifier: Modifier = Modifier, onButtonClick: () -> Unit = {}) {
        Box(
            modifier = modifier
                .size(width = PANEL_WIDTH, height = 120.dp)
                .background(PANEL_BACKGROUND_COLOR),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                onClick = onButtonClick,
                modifier = Modifier.size(width = 115.dp, height = 49.dp),
                isEnabled = true,
                shape = RoundedCornerShape(5.dp),
                colorNormal = Color(0xd9, 0xd9, 0xd9, 0x4d),
                colorPressed = Color(0xff, 0xff, 0xff, 0x4d),
                colorDisabled = Color(0xd9, 0xd9, 0xd9, 0x1a)
            )
        }
    }
}
