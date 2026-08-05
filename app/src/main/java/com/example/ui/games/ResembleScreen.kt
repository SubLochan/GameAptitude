package com.example.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResembleScreen(
    onFinishGame: (score: Int, rounds: Int, maxScore: Int) -> Unit,
    onQuit: () -> Unit = {}
) {
    var round by remember { mutableIntStateOf(1) }
    var score by remember { mutableIntStateOf(0) }
    var showQuitDialog by remember { mutableStateOf(false) }
    val maxRounds = 5

    // Rotation angle targets: 90 (Clockwise), 180, 270 (Counter-Clockwise), 90, 180
    val targetRotationAngle = when (round) {
        1 -> 90f
        2 -> 180f
        3 -> 270f
        4 -> 90f
        else -> 180f
    }

    val rotationInstruction = when (targetRotationAngle) {
        90f -> "Rotate image 90° Clockwise (Right)"
        180f -> "Rotate image 180°"
        else -> "Rotate image 90° Counter-Clockwise (Left)"
    }

    // Option angles: A=180°, B=270° (Left), C=0° (Original), D=90° (Right)
    val optionAngles = listOf(180f, 270f, 0f, 90f)

    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var feedbackText by remember { mutableStateOf<String?>(null) }

    fun chooseOption(index: Int) {
        selectedOptionIndex = index
        val chosenAngle = optionAngles[index]
        val isCorrect = (chosenAngle == targetRotationAngle)

        if (isCorrect) {
            score += 20
            feedbackText = "Correct Rotation!"
        } else {
            val correctOptionName = when (targetRotationAngle) {
                180f -> "A"
                270f -> "B"
                0f -> "C"
                else -> "D"
            }
            feedbackText = "Incorrect. Correct option is $correctOptionName."
        }
    }

    fun nextQuestion() {
        if (round < maxRounds) {
            round++
            selectedOptionIndex = null
            feedbackText = null
        } else {
            onFinishGame(score, maxRounds, maxRounds * 20)
        }
    }

    if (showQuitDialog) {
        AlertDialog(
            onDismissRequest = { showQuitDialog = false },
            title = { Text("Quit Game?") },
            text = { Text("Are you sure you want to quit this game and switch to another?") },
            confirmButton = {
                Button(
                    onClick = {
                        showQuitDialog = false
                        onQuit()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Quit & Switch")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showQuitDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("RESEMBLE", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { showQuitDialog = true }) {
                        Icon(Icons.Default.Close, contentDescription = "Quit Game")
                    }
                },
                actions = {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Round $round/$maxRounds | Score: $score",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = rotationInstruction,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // Target Image Preview
            Card(
                modifier = Modifier
                    .size(160.dp)
                    .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .border(1.dp, MaterialTheme.colorScheme.outline)
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(35.dp)
                                .align(Alignment.BottomStart)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                }
            }

            // Options A, B, C, D
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("A (180°)", "B (90° Left)", "C (Same)", "D (90° Right)").forEachIndexed { index, label ->
                    Card(
                        modifier = Modifier
                            .size(80.dp)
                            .border(
                                width = if (selectedOptionIndex == index) 3.dp else 1.dp,
                                color = if (selectedOptionIndex == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { if (selectedOptionIndex == null) chooseOption(index) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedOptionIndex == index) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            val rotateDeg = optionAngles[index]
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .rotate(rotateDeg)
                                    .border(1.dp, MaterialTheme.colorScheme.outline)
                                    .background(MaterialTheme.colorScheme.surface)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .align(Alignment.BottomStart)
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                            }
                        }
                    }
                }
            }

            feedbackText?.let { fb ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = fb,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (fb.startsWith("Correct")) Color(0xFF16A34A) else Color.Red
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(onClick = { nextQuestion() }) {
                        Text("Next Question")
                    }
                }
            }
        }
    }
}

