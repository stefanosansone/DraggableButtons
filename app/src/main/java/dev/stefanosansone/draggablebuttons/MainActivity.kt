package dev.stefanosansone.draggablebuttons

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import dev.stefanosansone.draggablebuttons.ui.theme.DraggableButtonsTheme
import kotlinx.coroutines.launch
import java.lang.Float.min
import kotlin.math.abs
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DraggableButtonsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    DraggableButtons()
                }
            }
        }
    }
}

@Composable
fun DraggableButtons() {
    val coroutineScope = rememberCoroutineScope()
    var size by remember { mutableStateOf(IntSize.Zero) }

    Box(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
        .onSizeChanged {
            size = it
        }
        .border(color = Color.Black, width = 1.dp)
    ) {
        var offsetXRed by remember { mutableStateOf(0f) }
        var offsetYRed by remember { mutableStateOf(0f) }
        var offsetXBlue by remember { mutableStateOf(Animatable(0f)) }
        var offsetYBlue by remember { mutableStateOf(Animatable(0f)) }

        Box(
            Modifier
                .offset { IntOffset(offsetXBlue.value.roundToInt(), offsetYBlue.value.roundToInt()) }
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Blue)
                .size(65.dp)
                .pointerInput(Unit) {
                    detectDragGestures(onDragEnd = {
                        coroutineScope.launch {
                            val targetX = if (offsetXBlue.value > (size.width/2)) {
                                size.width.toFloat() - 150.dp.value
                            } else {
                                0F
                            }
                            val targetY = if (offsetYBlue.value > (size.height/2)) {
                                size.height.toFloat() - 150.dp.value
                            } else {
                                0F
                            }
                            if (abs(offsetXBlue.value-targetX) < abs(offsetYBlue.value-targetY)) {
                                offsetXBlue.animateTo(
                                    targetValue = targetX,
                                    animationSpec = tween(
                                        durationMillis = 300,
                                        delayMillis = 0
                                    )
                                )
                            } else {
                                offsetYBlue.animateTo(
                                    targetValue = targetY,
                                    animationSpec = tween(
                                        durationMillis = 300,
                                        delayMillis = 0
                                    )
                                )
                            }
                        }
                    }) { change, dragAmount ->
                        change.consume()
                        coroutineScope.launch {
                            offsetXBlue.snapTo(offsetXBlue.value + dragAmount.x)
                            offsetYBlue.snapTo(offsetYBlue.value + dragAmount.y)
                        }
                    }
                }
        ){
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("BOUND",color = Color.White, textAlign = TextAlign.Center)
            }
        }

        Box(
            Modifier
                .offset { IntOffset(offsetXRed.roundToInt(), offsetYRed.roundToInt()) }
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Red)
                .size(65.dp)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetXRed += dragAmount.x
                        offsetYRed += dragAmount.y
                    }

                }
        ){
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("FREE",color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DraggableButtonsTheme {
        DraggableButtons()
    }
}