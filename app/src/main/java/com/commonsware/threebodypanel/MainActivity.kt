package com.commonsware.threebodypanel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
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

private val slideInUpwards = slideInVertically(
    initialOffsetY = {
        it / 2
    }
)
private val slideOutDownwards = slideOutVertically(
    targetOffsetY = {
        it / 2
    }
)

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
                val panelState = remember { mutableStateOf<PanelState>(PanelState.Collapsed) }

                Spacer(modifier = Modifier.weight(1.0f))

                AnimatingPanel(panelState.value)

                ButtonBar {
                    when (panelState.value) {
                        PanelState.Collapsed -> {
                            panelState.value = PanelState.Full
                        }

                        PanelState.Full -> {
                            panelState.value = PanelState.Partial
                        }

                        PanelState.Partial -> {
                            panelState.value = PanelState.Full
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun AnimatingPanel(
        panelState: PanelState,
        modifier: Modifier = Modifier
    ) {
        AnimatedContent(
            targetState = panelState,
            transitionSpec = {
                ContentTransform(
                    targetContentEnter = slideInUpwards,
                    initialContentExit = slideOutDownwards
                )
            },
            label = "AnimatedContent",
        ) { targetState ->
            Column(modifier = modifier) {
                when (targetState) {
                    PanelState.Collapsed -> {
                        // nothing is visible
                    }

                    PanelState.Partial -> {
                        Box(
                            modifier = Modifier
                                .background(PERMANENT_SECTION_COLOR)
                                .size(PANEL_WIDTH, SECTION_HEIGHT)
                        )
                    }

                    PanelState.Full -> {
                        OptionalSection()

                        Box(
                            modifier = Modifier
                                .background(PERMANENT_SECTION_COLOR)
                                .size(PANEL_WIDTH, SECTION_HEIGHT)
                        )

                        OptionalSection()
                    }
                }
            }
        }
    }

    @Composable
    private fun OptionalSection(modifier: Modifier = Modifier) {
        Box(
            modifier = modifier
                .background(FULL_SECTION_COLOR)
                .size(PANEL_WIDTH, SECTION_HEIGHT)
        )
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
