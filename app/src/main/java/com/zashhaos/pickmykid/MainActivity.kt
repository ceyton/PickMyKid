package com.zashhaos.pickmykid

import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zashhaos.pickmykid.ui.MainActivityViewModel
import com.zashhaos.pickmykid.ui.theme.PickMyKidTheme
import androidx.compose.runtime.livedata.observeAsState
import com.zashhaos.pickmykid.data.Kid
import androidx.compose.material.Surface as Surface1

const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainActivityViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ${viewModel.getName()}")
        viewModel.kids().observe(this) {
            for (kid in it) {
                Log.d(TAG, "onCreate: ${kid.name} ${kid.status}")
            }
        }

        setContent {
            val kids by viewModel.kids().observeAsState()
            PickMyKidTheme {
                // A surface container using the 'background' color from the theme
                Surface1(color = Color(0xffFAFAFA)) {
                    Column(

                        modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(start = 16.dp, end = 16.dp)
                            .verticalScroll(
                                rememberScrollState()
                            ),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start,


                        ) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Hello()
                        Name()
                        Spacer(modifier = Modifier.height(16.dp))
                        kids?.let { KidsList(kids = it) }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}


@Composable
fun Hello() {

    Text(text = "Hello", fontSize = 30.sp, fontWeight = FontWeight.Light)


}

@Composable
fun Name() {
    Text(text = "Mr. Fadi Talab!", fontSize = 30.sp, fontWeight = FontWeight.Bold)
}


@Composable
fun KidsList(kids: List<Kid>) {
    Column {

        for (kid in kids) {
            KidCard(kid)

        }


    }
}

@Composable
fun KidCard(kid: Kid) {
    val courses = listOf<String>(
        "Math",
        "Arabic",
        "English",
        "Physics",
        "Biology",
        "History",
        "Geology",
        "Religion"
    )
    var currentIndex by remember {
        mutableStateOf(0)
    }

    var openDialog by remember { mutableStateOf(false) }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {

            Row {
                Text(text = kid.name, fontWeight = FontWeight.Medium, fontSize = 20.sp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp), contentAlignment = Alignment.CenterEnd
                ) {

                    Row {
                        Box(
                            modifier = Modifier.fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = kid.status)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Box(
                            contentAlignment = Alignment.Center, modifier = Modifier
                                .fillMaxHeight()
                                .width(10.dp)
                        ) {
                            Surface1(
                                shape = CircleShape,
                                color = when (kid.status) {
                                    "present" -> Color.Green
                                    "absent" -> Color.Red
                                    else -> Color.Black
                                },
                                modifier = Modifier
                                    .height(10.dp)
                                    .fillMaxWidth()
                            ) {

                            }
                        }
                    }


                }
            }

            Text(
                text = "class ${kid.className}", fontWeight = FontWeight.Normal, fontSize = 16.sp,
                color = Color(0xff616161)
            )
            Course(course = courses[currentIndex])
            ClassSlider() {
                currentIndex = it.toInt()
            }
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(onClick = {
                openDialog = true
            })
            {
                Text("PICK UP")
            }

            if (openDialog) {
                AlertDialog(
                    onDismissRequest = {
                        // Dismiss the dialog when the user clicks outside the dialog or on the back
                        // button. If you want to disable that functionality, simply use an empty
                        // onCloseRequest.
                        openDialog = false
                    },
                    title = {
                        Text(text = "Pick up")
                    },
                    text = {
                        Text(
                            "Are sure do you want to request to pick up ${kid.name}?"

                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                openDialog = false
                            }
                        ) {
                            Text("Confirm")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                openDialog = false
                            }
                        ) {
                            Text("Dismiss")
                        }
                    }
                )
            }
        }

    }
}

@Composable
fun Course(course: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = course, fontWeight = FontWeight.Medium, fontSize = 20.sp)
    }
}


@Composable
fun ClassSlider(sliderValue: (Float) -> Unit) {
    val hoursList = listOf("8AM", "9AM", "10AM", "11AM", "12PM", "13PM", "14PM", "15PM")
    var value by remember {
        mutableStateOf(0.0f)
    }
    Log.d(TAG, "ClassSlider: $value")
    Box(contentAlignment = Alignment.Center) {


        Slider(
            value = value,
            onValueChange = { value = it },
            steps = 6,
            valueRange = 0.0f..7.0f,
            onValueChangeFinished = {
                value = value.toInt().toFloat()
                sliderValue(value)
            }
        )
        VerticalLines(dates = hoursList)
        Labels(hoursList)

    }

}


@Composable
fun VerticalLines(dates: List<String>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(15.dp)
    ) {
        val drawPadding: Float = with(LocalDensity.current) { 10.dp.toPx() }
        Canvas(modifier = Modifier.fillMaxSize()) {
            val yStart = 0f
            val yEnd = size.height
            //val distance: Float = (size.width.minus(2 * drawPadding)).div(dates.size.minus(1))
            val distance: Float = (size.width - (2 * drawPadding)) / (dates.size - 1)


            dates.forEachIndexed { index, _ ->
                val start = Offset(x = (index * distance) + drawPadding, y = yStart)
                val end = Offset(x = (index * distance) + drawPadding, y = yEnd)
                drawLine(
                    color = Color.Black,
                    start = start,
                    end = end
                )

            }


        }
    }
}

@Composable
fun Labels(dates: List<String>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
    ) {
        val drawPadding: Float = with(LocalDensity.current) { 10.dp.toPx() }
        val textOffset: Float = with(LocalDensity.current) { 10.dp.toPx() }
        Canvas(modifier = Modifier.fillMaxSize()) {

            val yEnd = size.height
            val distance: Float = (size.width - (2 * drawPadding)) / (dates.size - 1)
            val paint = Paint()
            paint.apply {
                textSize = 25f
            }
            dates.forEachIndexed { index, _ ->
                val end = Offset(x = (index * distance) + drawPadding, y = yEnd)
                drawContext.canvas.nativeCanvas.drawText(
                    dates[index],
                    end.x - textOffset,
                    end.y,
                    paint
                )

            }


        }

    }
}