package com.example.jetpackdraggablemusicknob

import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetpackdraggablemusicknob.ui.theme.JetpackDraggableMusicKnobTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Box (
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF101010))
            ){
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .border(1.dp, Color.Green, RoundedCornerShape(10.dp))
                        .padding(15.dp)
                ) {
                    var volume by remember { mutableFloatStateOf(0f) }
                    val barCount = 20
                    MusicKnob(
                        modifier = Modifier.size(100.dp),
                    ){ volume = it}

                    Spacer(modifier = Modifier.width(20.dp))

                    VolumeBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(30.dp),
                        activeBar = (barCount * volume).toInt(),
                        barCount = barCount
                    )
                }
            }
        }
    }
}

@Composable
fun VolumeBar(
    modifier: Modifier,
    activeBar:Int = 0,
    barCount:Int= 10
){

    BoxWithConstraints(
        modifier = modifier
    ) {
        val barWidth = remember {
            constraints.maxWidth / (2f*barCount)
        }

        Canvas(modifier = modifier) {
            for(i in 0 until barCount){
                drawRoundRect(
                    color = if(i in 0..activeBar) {
                        Color.Green
                    } else {
                        Color.DarkGray
                    },
                    topLeft = Offset(i*barWidth *2f+barWidth/2f, 0f),
                    size = Size(barWidth, constraints.maxHeight.toFloat()),
                    cornerRadius = CornerRadius(0f, 0f)
                )

            }
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MusicKnob(
    modifier: Modifier,
    limitingAngle:Float = 25f,
    onValueChange: (Float) -> Unit,
){
    var rotation by remember { mutableFloatStateOf(limitingAngle) }
    var touchX by remember { mutableFloatStateOf(0f) }
    var touchY by remember { mutableFloatStateOf(0f) }
    var centerX by remember { mutableFloatStateOf(0f) }
    var centerY by remember { mutableFloatStateOf(0f) }

    Image(
        painter = painterResource(id = R.drawable.music_knob),
        contentDescription = "Music Knob",
        modifier = modifier
            .fillMaxSize()
            .padding(all = 24.dp)
            .onGloballyPositioned { //when the image is positioned on the screen we get its position.
                val windowBounds = it.boundsInWindow()
                centerX = windowBounds.size.width
                centerY = windowBounds.size.height
            }
            .pointerInteropFilter {  //detect touches
                    event ->
                touchX = event.x
                touchY = event.y

                val angle = (-Math.atan2(
                    (centerX- touchX ).toDouble(),
                    (centerY - touchY ).toDouble()
                ) * (180 / Math.PI).toFloat()).toFloat()
                when (event.action) {
                    MotionEvent.ACTION_DOWN,
                    MotionEvent.ACTION_MOVE -> {
                        if (angle !in -limitingAngle..limitingAngle) {
                            val fixedAngle = if (angle in -180f..-limitingAngle) {
                                360f + angle
                            } else {
                                angle
                            }
                            rotation = fixedAngle
                            val percent = (fixedAngle - limitingAngle) / (360f - 2 * limitingAngle)
                            onValueChange(percent)
                            true
                        } else {
                            false
                        }
                    }

                    else -> false

                }
            }
            .rotate(rotation)
    )
}