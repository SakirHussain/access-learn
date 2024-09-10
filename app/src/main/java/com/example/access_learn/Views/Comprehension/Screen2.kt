package com.example.access_learn.Views.Comprehension

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.access_learn.R
import kotlinx.coroutines.delay
import java.util.Locale


@Composable
fun LanguageLearningApp2(navController: NavController) {
    var tts: TextToSpeech? = null
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.UK
            }
        }
    }

    YesNoQuizScreen(
        onSpeakRequest = { text ->
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
        },
        navigate = navController
    )

    DisposableEffect(Unit) {
        onDispose {
            tts?.shutdown()
        }
    }
}

@Composable
fun YesNoQuizScreen(onSpeakRequest: (String) -> Unit, navigate: NavController) {
    // State variables to store answers and track current question
    var score by remember { mutableStateOf(0) }
    var currentQuestion by remember { mutableStateOf(0) }
    var timeLeft by remember { mutableStateOf(30) }

    // List of questions
    val questions = listOf(
        "Do you like Android development?",
        "Is Kotlin your favorite programming language?",
        "Do you use Jetpack Compose?",
        "Are coroutines useful for concurrency?",
        "Is Android Studio your preferred IDE?"
    )

    // Colors for vibrant design and accessibility
    val backgroundColor = Color(0xFFF5F5F5)  // Light beige
    val textColor = Color(0xFF3C3C3C)        // Dark grey for text
    val titleColor = Color(0xFF2C2C2C)       // Almost black for title text
    val yesButtonColor = Color(0xFF6DD5FA)   // Bright blue for "Yes" button
    val noButtonColor = Color(0xFFFC5185)    // Vivid red for "No" button
    val borderColor = Color(0xFF6C757D)      // Grey for borders

    // Control panel colors
    val leftPanelColor = Color(0xFF2C3E50)  // Dark blue
    val rightPanelColor = Color(0xFFE74C3C) // Bright red

    // Timer logic: only show it during the questions
    LaunchedEffect(currentQuestion) {
        timeLeft = 30 // Reset timer
        while (timeLeft > 0 && currentQuestion < questions.size) {
            delay(1000L)
            timeLeft -= 1
        }
        // Automatically move to next question when timer ends
        if (currentQuestion < questions.size) {
            currentQuestion++
        }
    }

    // Custom font using the new FontFamily and Font API
    val customFont = FontFamily(
        Font(R.font.pt_serif_bold, FontWeight.Bold),
        Font(R.font.pt_serif_bold, FontWeight.Bold)
    )
    val vibrator = LocalContext.current.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    val hapticFeedback = LocalHapticFeedback.current

    // List of options to iterate over
    val options = listOf("Read Question Again", "Yes", "No")
    var selectedOptionIndex by remember { mutableStateOf(0) }

    fun moveToNextOption() {
        selectedOptionIndex = (selectedOptionIndex + 1) % options.size
    }

    fun moveToPreviousOption() {
        selectedOptionIndex = (selectedOptionIndex - 1 + options.size) % options.size
    }

    // Narrate the first question immediately after composable is launched
    LaunchedEffect(Unit) {
        if (currentQuestion < questions.size) {
            onSpeakRequest(questions[currentQuestion])
        }
    }

    // Narrate the question whenever it changes
    LaunchedEffect(currentQuestion) {
        if (currentQuestion < questions.size) {
            onSpeakRequest(questions[currentQuestion])
        }
    }

    Surface(color = backgroundColor) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Quiz content (Top 75% of the screen)
            Column(
                modifier = Modifier
                    .weight(3f) // Use weight to proportionally take up 75% of the space
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Timer at the top-right corner
                if (currentQuestion < questions.size) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.AccessTime, contentDescription = "Timer", tint = titleColor)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$timeLeft",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontFamily = customFont,
                                fontWeight = FontWeight.Bold
                            ),
                            color = titleColor,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Display current question
                if (currentQuestion < questions.size) {
                    Text(
                        text = questions[currentQuestion],
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = customFont,
                            fontWeight = FontWeight.Bold
                        ),
                        color = titleColor, // Darker grey for title text
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .clickable { onSpeakRequest(questions[currentQuestion]) } // Reads out the question on tap
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Yes Button with blue color
                    Button(
                        onClick = {
                            score++ // Increment score for Yes
                            currentQuestion++ // Move to the next question
//                            onSpeakRequest("Yes")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = yesButtonColor,
                            contentColor = Color.White
                        ),
                        shape = CircleShape, // Rounded buttons for modern look
                        border = BorderStroke(2.dp, borderColor)
                    ) {
                        Text(text = "Yes", fontFamily = customFont)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // No Button with red color
                    Button(
                        onClick = {
                            currentQuestion++ // Move to the next question
//                            onSpeakRequest("No")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = noButtonColor,
                            contentColor = Color.White
                        ),
                        shape = CircleShape, // Rounded buttons for modern look
                        border = BorderStroke(2.dp, borderColor)
                    ) {
                        Text(text = "No", fontFamily = customFont)
                    }
                } else {
                    // Show the score when the quiz is finished
//                    Text(
//                        text = "Your score: $score / ${questions.size}",
//                        style = MaterialTheme.typography.headlineMedium.copy(
//                            fontFamily = customFont,
//                            fontWeight = FontWeight.Bold
//                        ),
//                        color = textColor, // Darker grey for the score text
//                        modifier = Modifier
//                            .clickable { onSpeakRequest("Your score is $score out of ${questions.size}") } // Reads out the score on tap
//                    )
                    navigate.navigate("screen3")
                }
            }

            // Control Panel (Bottom 25% of the screen)
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
                                        "Read Question Again" -> onSpeakRequest(questions[currentQuestion])
                                        "Yes" -> {
                                            score++
                                            currentQuestion++
                                        }
                                        "No" -> {
                                            currentQuestion++
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
                                        "Read Question Again" -> onSpeakRequest(questions[currentQuestion])
                                        "Yes" -> {
                                            score++
                                            currentQuestion++
                                        }
                                        "No" -> {
                                            currentQuestion++
                                        }
                                    }
                                }
                            )
                        }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

//            // Display the currently selected option at the bottom of the screen
//            Text(
//                text = "Current option: ${options[selectedOptionIndex]}",
//                style = MaterialTheme.typography.headlineSmall.copy(
//                    fontFamily = customFont,
//                    fontWeight = FontWeight.Bold
//                ),
//                color = titleColor,
//                textAlign = TextAlign.Center
//            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun YesNoQuizScreenPreview() {
//    LanguageLearningApp2()
//}