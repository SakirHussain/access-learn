import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.input.pointer.pointerInput
import java.util.*

@Composable
fun LanguageLearningApp() {
    var tts: TextToSpeech? = null
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
            }
        }
    }

    CustomScoreScreen(
        onSpeakRequest = { text ->
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
        },
        score = 100,
        onRetry = {},
        onTryDifferentGame = {}
    )

    DisposableEffect(Unit) {
        onDispose {
            tts?.shutdown()
        }
    }
}

@Composable
fun CustomScoreScreen(
    onSpeakRequest: (String) -> Unit,
    score: Int,
    onRetry: () -> Unit,
    onTryDifferentGame: () -> Unit
) {
    // Function to trigger TTS
    fun speak(text: String) {
        onSpeakRequest(text)
    }

    LaunchedEffect(Unit) {
        speak("The score is $score")
    }

    // List of elements to navigate through
    val elements = listOf("The score is $score", "Try Again Button", "Other Games Button")

    // Current element index
    var currentElementIndex by remember { mutableStateOf(0) }

    // Function to move to the previous element
    fun previousElement() {
        currentElementIndex = if (currentElementIndex > 0) {
            currentElementIndex - 1
        } else {
            elements.size - 1 // Cycle back to the last element
        }
        speak(elements[currentElementIndex])
    }

    // Function to move to the next element
    fun nextElement() {
        currentElementIndex = if (currentElementIndex < elements.size - 1) {
            currentElementIndex + 1
        } else {
            0 // Cycle back to the first element
        }
        speak(elements[currentElementIndex])
    }

    // Function to handle double-tap action for selecting the current element
    fun selectCurrentElement() {
        speak("You selected ${elements[currentElementIndex]}")
    }

    // Main layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFECEFF1)) // Light background color
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween // Space the top and bottom sections
    ) {
        // Top section - 75% of the screen
        Column(
            modifier = Modifier
                .weight(0.75f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // Grouped Score Title, Score Value, and Message together
            Column(
                modifier = Modifier
                    .clickable { speak("Your score is $score.") }
                    .semantics { contentDescription = "Your score is $score." },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Score",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = Color.Black
                )

                Text(
                    text = "$score",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Text(
                    text = "Congratulations, Great Job!",
                    fontSize = 20.sp,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center
                )
            }

            // Buttons Section (Try Again and Other Games)
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        onRetry()
                        speak("Try Again")
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.8f),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text(text = "Try Again")
                }

                Button(
                    onClick = {
                        onTryDifferentGame()
                        speak("Other Games")
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.8f),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text(text = "Other Games")
                }
            }

            // Tip Section - Placed above the bottom section
            Text(
                text = "Tip: Keep practicing to improve your score!",
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier
                    .clickable { speak("Tip: Keep practicing to improve your score!") }
                    .semantics { contentDescription = "Tip: Keep practicing to improve your score!" }
            )
        }

        // Bottom section - 25% of the screen, divided into two parts with a thin line
        Row(
            modifier = Modifier
                .weight(0.25f)
                .fillMaxWidth()
                .background(LightGray)
                .padding(16.dp)
        ) {
            // Left Section - Previous Element with both single and double-tap detection
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                previousElement() // Single tap to navigate to the previous element
                            },
                            onDoubleTap = {
                                selectCurrentElement() // Double tap to select the current element
                            }
                        )
                    }
                    .background(Color(0xFFD3D3D3)) // Grey background for the clickable area
            ) {
                Text(
                    text = "Previous",
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }

            // Thin vertical line in the middle
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(Gray)
            )

            // Right Section - Next Element with both single and double-tap detection
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                nextElement() // Single tap to navigate to the next element
                            },
                            onDoubleTap = {
                                selectCurrentElement() // Double tap to select the current element
                            }
                        )
                    }
                    .background(Color(0xFFD3D3D3)) // Grey background for the clickable area
            ) {
                Text(
                    text = "Next",
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCustomScoreScreen() {
    LanguageLearningApp()
}
