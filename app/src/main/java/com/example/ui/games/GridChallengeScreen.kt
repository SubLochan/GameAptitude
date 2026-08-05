package com.example.ui.games

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

enum class GridStep {
    SHOW_DOT,
    SYMMETRY_TEST,
    RECALL
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GridChallengeScreen(
    onFinishGame: (score: Int, rounds: Int, maxScore: Int) -> Unit,
    onQuit: () -> Unit = {}
) {
    var round by remember { mutableIntStateOf(1) }
    var score by remember { mutableIntStateOf(0) }
    var showQuitDialog by remember { mutableStateOf(false) }
    val maxRounds = 5

    // Sequence of dot indices (out of 16 grid cells)
    var sequenceLength by remember { mutableIntStateOf(3) }
    var targetSequence by remember { mutableStateOf(listOf(2, 7, 13)) }
    var currentDotIndexInSeq by remember { mutableIntStateOf(0) }

    var step by remember { mutableStateOf(GridStep.SHOW_DOT) }

    // User recall state
    var userRecall by remember { mutableStateOf(listOf<Int>()) }

    // Symmetry test state
    var isSymmetricalPattern by remember { mutableStateOf(true) }

    // Show dot timer
    LaunchedEffect(step, currentDotIndexInSeq) {
        if (step == GridStep.SHOW_DOT) {
            delay(1500) // Show dot for 1.5s
            isSymmetricalPattern = kotlin.random.Random.nextBoolean()
            step = GridStep.SYMMETRY_TEST
        }
    }

    fun handleSymmetryAnswer(userAnswer: Boolean) {
        if (userAnswer == isSymmetricalPattern) {
            score += 5
        }
        if (currentDotIndexInSeq < targetSequence.size - 1) {
            currentDotIndexInSeq++
            step = GridStep.SHOW_DOT
        } else {
            // Sequence completed, time for recall
            userRecall = emptyList()
            step = GridStep.RECALL
        }
    }

    fun handleGridCellClick(cellIndex: Int) {
        if (step == GridStep.RECALL) {
            val updated = userRecall + cellIndex
            userRecall = updated
            if (updated.size == targetSequence.size) {
                // Check sequence match
                if (updated == targetSequence) {
                    score += 25
                }
                if (round < maxRounds) {
                    round++
                    sequenceLength = 3 + round / 2
                    targetSequence = (0..15).shuffled().take(sequenceLength)
                    currentDotIndexInSeq = 0
                    step = GridStep.SHOW_DOT
                } else {
                    onFinishGame(score, maxRounds, maxRounds * 50)
                }
            }
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
                title = { Text("Grid Challenge", fontWeight = FontWeight.Bold) },
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
                            text = "Level $round/$maxRounds | Score: $score",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
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
            when (step) {
                GridStep.SHOW_DOT -> {
                    Text(
                        text = "Remember the position of the highlighted dot!",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    GridBoard(
                        activeDotIndex = targetSequence[currentDotIndexInSeq],
                        userSelections = emptyList(),
                        onCellClick = {}
                    )

                    Text(
                        text = "Dot ${currentDotIndexInSeq + 1} of ${targetSequence.size}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                GridStep.SYMMETRY_TEST -> {
                    Text(
                        text = "Are they symmetrical?",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    SymmetryGraphic(isSymmetrical = isSymmetricalPattern)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = { handleSymmetryAnswer(true) },
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("YES", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { handleSymmetryAnswer(false) },
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("NO", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                GridStep.RECALL -> {
                    Text(
                        text = "Select the dots in the order they appeared!",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    GridBoard(
                        activeDotIndex = -1,
                        userSelections = userRecall,
                        onCellClick = { handleGridCellClick(it) }
                    )

                    Text(
                        text = "Selected ${userRecall.size} / ${targetSequence.size}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun GridBoard(
    activeDotIndex: Int,
    userSelections: List<Int>,
    onCellClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .size(300.dp)
            .border(2.dp, Color(0xFF0284C7), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            for (row in 0..3) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (col in 0..3) {
                        val cellIndex = row * 4 + col
                        val isActive = (cellIndex == activeDotIndex)
                        val selectionOrder = userSelections.indexOf(cellIndex)

                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isActive -> Color(0xFF38BDF8)
                                        selectionOrder >= 0 -> Color(0xFF16A34A)
                                        else -> Color(0xFF94A3B8)
                                    }
                                )
                                .clickable { onCellClick(cellIndex) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectionOrder >= 0) {
                                Text(
                                    text = "${selectionOrder + 1}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SymmetryGraphic(isSymmetrical: Boolean) {
    Card(
        modifier = Modifier
            .size(280.dp)
            .border(2.dp, Color.Black, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val midX = size.width / 2f
                drawLine(
                    color = Color.Red,
                    start = Offset(midX, 0f),
                    end = Offset(midX, size.height),
                    strokeWidth = 4f
                )

                // Draw symmetrical or asymmetrical pixel shape blocks
                val leftOffsets = listOf(
                    Offset(midX - 80f, 40f), Offset(midX - 40f, 80f),
                    Offset(midX - 60f, 140f), Offset(midX - 90f, 180f)
                )

                leftOffsets.forEach { pos ->
                    drawRect(Color.Black, topLeft = pos, size = androidx.compose.ui.geometry.Size(30f, 30f))
                }

                leftOffsets.forEach { pos ->
                    val mirroredX = if (isSymmetrical) {
                        midX + (midX - pos.x) - 30f
                    } else {
                        midX + (midX - pos.x) - 10f // Asymmetrical shift
                    }
                    drawRect(Color.Black, topLeft = Offset(mirroredX, pos.y), size = androidx.compose.ui.geometry.Size(30f, 30f))
                }
            }
        }
    }
}
