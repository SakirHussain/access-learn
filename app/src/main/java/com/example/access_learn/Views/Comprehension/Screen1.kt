package com.example.access_learn.Views.Comprehension

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.util.*

@Composable
fun LanguageLearningApp1(navController: NavController) {
    var tts: TextToSpeech? = null
    val context = LocalContext.current

    // Initializing Text-to-Speech engine
    LaunchedEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
            }
        }
    }

    val textToRecite = remember { mutableStateOf("A butterfly’s life cycle is a fascinating process that occurs in four main stages. First, the butterfly begins as an egg, which is often laid on the leaves of plants. After a few days, the egg hatches, and a caterpillar, also known as a larva, emerges. During this stage, the caterpillar’s main task is to eat as much as possible to grow quickly.\n" +
            "\n" +
            "Once the caterpillar has grown large enough, it enters the third stage called the pupa or chrysalis.") }
    var currentRecitationIndex by remember { mutableStateOf(0) }

    LandingPage(
        text = textToRecite.value,
        onSpeakRequest = { text ->
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
        },
        onContinueRequest = {
            // Continue reciting
            tts?.speak(
                "continue selected",
                TextToSpeech.QUEUE_FLUSH,
                null,
                ""
            )
        },
        onRestartRequest = {
            // Restart the recitation
            currentRecitationIndex = 0
            tts?.speak(textToRecite.value, TextToSpeech.QUEUE_FLUSH, null, "")
        },
        navController = navController
    )

    DisposableEffect(Unit) {
        onDispose {
            tts?.shutdown()
        }
    }
}

@Composable
fun LandingPage(
    text: String,
    onSpeakRequest: (String) -> Unit,
    onContinueRequest: () -> Unit,
    onRestartRequest: () -> Unit,
    navController: NavController
) {
    val hapticFeedback = LocalHapticFeedback.current

    // Control panel colors
    val leftPanelColor = Color(0xc6c6c6)  // Dark blue
    val rightPanelColor = Color(0xc6c6c6) // Bright red

    val options = listOf("Recite Again", "Continue")
    var selectedOptionIndex by remember { mutableStateOf(0) }

    fun moveToNextOption() {
        selectedOptionIndex = (selectedOptionIndex + 1) % options.size
    }

    fun moveToPreviousOption() {
        selectedOptionIndex = (selectedOptionIndex - 1 + options.size) % options.size
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier
                .weight(3f) // Use weight to proportionally take up 75% of the space
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // TextBox for displaying the text that will be recited
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
                    .semantics { contentDescription = "Recited Text" }
            ) {
                Text(
                    text = text,
                    fontSize = 18.sp,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Row to place the Continue and Restart buttons side by side
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Button to continue reciting
                Button(
                    onClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        onRestartRequest()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xE6AA5FEC)),
                    modifier = Modifier
                        .width(150.dp) // Set the width (adjust as needed)
                        .height(60.dp) // Set the height (adjust as needed)
                        .clip(RoundedCornerShape(12.dp)) // Set rounded corners (adjust radius as needed)
                        .semantics { contentDescription = "Restart Button" }
                ) {
                    Icon(Icons.Filled.Autorenew, contentDescription = "Restart", tint = Color.White)
                }

                // Button to restart the recitation
                Button(
                    onClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        navController.navigate("screen2")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xE6AA5FEC)),
                    modifier = Modifier
                        .width(150.dp) // Set the width (adjust as needed)
                        .height(60.dp) // Set the height (adjust as needed)
                        .clip(RoundedCornerShape(12.dp)) // Set rounded corners (adjust radius as needed)
                        .semantics { contentDescription = "Continue Button" }
                ) {
                    Icon(Icons.Filled.ChevronRight, contentDescription = "Continue", tint = Color.White)
                }

            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .weight(1f) // Use weight to proportionally take up 25% of the space
                .fillMaxWidth()
        ) {
            // Left side of control panel (Navigate to previous option)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(leftPanelColor)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                moveToPreviousOption()
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onSpeakRequest(options[selectedOptionIndex])
                            },
                            onDoubleTap = {
                                // Select the current option
                                when (options[selectedOptionIndex]) {
                                    "Recite Again" -> onSpeakRequest("recite again selected")
                                    "Continue" -> {
                                        onSpeakRequest("continue selected")
                                    }
                                }
                            }
                        )
                    }
            )

            // Right side of control panel (Navigate to next option)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(rightPanelColor)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                moveToNextOption()
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onSpeakRequest(options[selectedOptionIndex])
                            },
                            onDoubleTap = {
                                // Select the current option
                                when (options[selectedOptionIndex]) {
                                    "Recite Again" -> onSpeakRequest("recite again selected")
                                    "Continue" -> {
                                        onSpeakRequest("continue selected")
                                    }
                                }
                            }
                        )
                    }
            )
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun MyPreview() {
//    LanguageLearningApp1()
//}
