package com.commonsware.threebodypanel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

private val PANEL_WIDTH = 520.dp
private val SECTION_HEIGHT = 100.dp
private val PANEL_BACKGROUND_COLOR = Color(0x1e, 0x1e, 0x1e)
private val FULL_SECTION_COLOR = Color(0xff, 0xc2, 0x0a)
private val PERMANENT_SECTION_COLOR = Color(0x0c, 0x7b, 0xdc)

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
                Spacer(modifier = Modifier.weight(1.0f))

                val panelState = remember { mutableStateOf(ThreeBodyPanelState.Collapsed) }

                ThreeBodyPanel(
                    panelState,
                    top = { modifier ->
                        Box(
                            modifier = modifier
                                .background(Color.Magenta)
                                .size(PANEL_WIDTH, SECTION_HEIGHT)
                        )
                    },
                    middle = {
                        Box(
                            modifier = Modifier
                                .background(PERMANENT_SECTION_COLOR)
                                .size(PANEL_WIDTH, SECTION_HEIGHT)
                        )
                    },
                    bottom = {
                        Box(
                            modifier = Modifier
                                .background(FULL_SECTION_COLOR)
                                .size(PANEL_WIDTH, SECTION_HEIGHT)
                        )
                    },
                    topHeight = SECTION_HEIGHT,
                    middleHeight = SECTION_HEIGHT,
                    bottomHeight = SECTION_HEIGHT,
                    modifier = Modifier.background(Color.DarkGray)
                )

                ButtonBar {
                    when (panelState.value) {
                        ThreeBodyPanelState.Collapsed -> {
                            panelState.value = ThreeBodyPanelState.Full
                        }

                        ThreeBodyPanelState.Full -> {
                            panelState.value = ThreeBodyPanelState.Partial
                        }

                        ThreeBodyPanelState.Partial -> {
                            panelState.value = ThreeBodyPanelState.Full
                        }
                    }
                }
            }
        }
    }

    enum class ThreeBodyPanelState {
        Collapsed,
        Full,
        Partial
    }

    @Composable
    fun ThreeBodyPanel(
        panelState: State<ThreeBodyPanelState>,
        top: @Composable (modifier: Modifier) -> Unit,
        middle: @Composable () -> Unit,
        bottom: @Composable () -> Unit,
        topHeight: Dp,
        middleHeight: Dp,
        bottomHeight: Dp,
        modifier: Modifier = Modifier,
    ) {
        val animatedOverallHeight by animateDpAsState(
            targetValue = when (panelState.value) {
                ThreeBodyPanelState.Collapsed -> 0.dp
                ThreeBodyPanelState.Partial -> middleHeight
                ThreeBodyPanelState.Full -> topHeight + middleHeight + bottomHeight
            },
            animationSpec = tween(durationMillis = 500), // update this value as needed
            label = "animateDpAsState-overall"
        )

        Column(
            modifier = modifier.height(animatedOverallHeight),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            val animatedTopHeight by animateDpAsState(
                targetValue = when (panelState.value) {
                    ThreeBodyPanelState.Full -> topHeight
                    else -> 0.dp
                },
                animationSpec = tween(durationMillis = 500), // update this value as needed
                label = "animateDpAsState-top"
            )

            top(Modifier.height(animatedTopHeight))

            val animatedRemainingHeight by animateDpAsState(
                targetValue = when (panelState.value) {
                    ThreeBodyPanelState.Full -> middleHeight + bottomHeight
                    else -> middleHeight
                },
                animationSpec = tween(durationMillis = 500), // update this value as needed
                label = "animateDpAsState-remaining"
            )

            Column(modifier = Modifier.height(animatedRemainingHeight)) {
                middle()
                bottom()
            }
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
