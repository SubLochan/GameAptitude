package com.example.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Swipe
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MotionChallengeScreen(
    onFinishGame: (score: Int, rounds: Int, maxScore: Int) -> Unit,
    onQuit: () -> Unit = {}
) {
    var round by remember { mutableIntStateOf(1) }
    var score by remember { mutableIntStateOf(0) }
    var movesCount by remember { mutableIntStateOf(0) }
    var showQuitDialog by remember { mutableStateOf(false) }
    val maxRounds = 5

    // 5 rows x 4 columns grid
    var redBallPos by remember { mutableStateOf(Pair(4, 0)) } // row 4, col 0
    val blackHolePos = Pair(0, 3) // row 0, col 3

    // Level configuration
    val cyanBlockRow = when (round) {
        1 -> 1
        2 -> 2
        3 -> 1
        4 -> 2
        else -> 1
    }

    var cyanBlockCol by remember { mutableIntStateOf(1) } // occupies 2 adjacent cells

    val obstacles = when (round) {
        1 -> listOf(Pair(2, 1), Pair(3, 2))
        2 -> listOf(Pair(1, 0), Pair(3, 1), Pair(2, 3))
        3 -> listOf(Pair(2, 0), Pair(2, 2), Pair(4, 1))
        4 -> listOf(Pair(1, 3), Pair(3, 0), Pair(3, 3))
        else -> listOf(Pair(0, 1), Pair(2, 1), Pair(4, 2))
    }

    val targetMinMoves = when (round) {
        1 -> 4
        2 -> 5
        3 -> 6
        4 -> 7
        else -> 5
    }

    fun tryMoveRedBall(dRow: Int, dCol: Int) {
        val newRow = redBallPos.first + dRow
        val newCol = redBallPos.second + dCol

        if (newRow in 0..4 && newCol in 0..3) {
            // Check obstacle collisions
            if (obstacles.contains(Pair(newRow, newCol))) return

            // Check cyan block collision
            if (newRow == cyanBlockRow && (newCol == cyanBlockCol || newCol == cyanBlockCol + 1)) return

            redBallPos = Pair(newRow, newCol)
            movesCount++

            // Check goal
            if (redBallPos == blackHolePos) {
                val roundScore = if (movesCount <= targetMinMoves) 50 else 30
                score += roundScore

                if (round < maxRounds) {
                    round++
                    movesCount = 0
                    redBallPos = Pair(4, 0)
                    cyanBlockCol = 1
                } else {
                    onFinishGame(score, maxRounds, maxRounds * 50)
                }
            }
        }
    }

    fun moveCyanBlock(dCol: Int) {
        val newCol = cyanBlockCol + dCol
        if (newCol in 0..2) { // occupies newCol and newCol+1
            // check red ball collision
            if (redBallPos.first == cyanBlockRow && (redBallPos.second == newCol || redBallPos.second == newCol + 1)) return
            cyanBlockCol = newCol
            movesCount++
        }
    }

    // Drag tracking for swipe gestures
    var totalDragX by remember { mutableFloatStateOf(0f) }
    var totalDragY by remember { mutableFloatStateOf(0f) }
    var isDraggingBar by remember { mutableStateOf(false) }

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
                title = { Text("Motion Challenge", fontWeight = FontWeight.Bold) },
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
                            text = "Level $round/$maxRounds | Moves: $movesCount (Min: $targetMinMoves)",
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
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Swipe, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text(
                        text = "SWIPE anywhere to move Ball • SWIPE on Blue Bar to slide it!",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            // Grid Board with gesture detector
            Card(
                modifier = Modifier
                    .size(310.dp)
                    .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                    .pointerInput(round) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                totalDragX = 0f
                                totalDragY = 0f
                                // Determine if drag started near the cyan block row
                                val rowHeight = size.height / 5f
                                val touchedRow = (offset.y / rowHeight).toInt().coerceIn(0, 4)
                                isDraggingBar = (touchedRow == cyanBlockRow)
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                totalDragX += dragAmount.x
                                totalDragY += dragAmount.y

                                val threshold = 40f
                                if (isDraggingBar) {
                                    if (totalDragX > threshold) {
                                        moveCyanBlock(1)
                                        totalDragX = 0f
                                    } else if (totalDragX < -threshold) {
                                        moveCyanBlock(-1)
                                        totalDragX = 0f
                                    }
                                } else {
                                    if (abs(totalDragX) > abs(totalDragY)) {
                                        if (totalDragX > threshold) {
                                            tryMoveRedBall(0, 1) // Right
                                            totalDragX = 0f
                                            totalDragY = 0f
                                        } else if (totalDragX < -threshold) {
                                            tryMoveRedBall(0, -1) // Left
                                            totalDragX = 0f
                                            totalDragY = 0f
                                        }
                                    } else {
                                        if (totalDragY > threshold) {
                                            tryMoveRedBall(1, 0) // Down
                                            totalDragX = 0f
                                            totalDragY = 0f
                                        } else if (totalDragY < -threshold) {
                                            tryMoveRedBall(-1, 0) // Up
                                            totalDragX = 0f
                                            totalDragY = 0f
                                        }
                                    }
                                }
                            }
                        )
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    for (r in 0..4) {
                        Row(modifier = Modifier.weight(1f)) {
                            for (c in 0..3) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    when {
                                        Pair(r, c) == redBallPos -> {
                                            Box(
                                                modifier = Modifier
                                                    .size(42.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFFEF4444)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(16.dp)
                                                        .clip(CircleShape)
                                                        .background(Color.White.copy(alpha = 0.6f))
                                                )
                                            }
                                        }

                                        Pair(r, c) == blackHolePos -> {
                                            Box(
                                                modifier = Modifier
                                                    .size(44.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFF0F172A)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(24.dp)
                                                        .clip(CircleShape)
                                                        .background(Color(0xFF38BDF8))
                                                )
                                            }
                                        }

                                        obstacles.contains(Pair(r, c)) -> {
                                            Text(
                                                text = "✖",
                                                fontSize = 26.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                                            )
                                        }

                                        r == cyanBlockRow && (c == cyanBlockCol || c == cyanBlockCol + 1) -> {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(2.dp)
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(Color(0xFF0284C7)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("↔", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Quick reset button & level stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        redBallPos = Pair(4, 0)
                        cyanBlockCol = 1
                        movesCount = 0
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Reset Board")
                }
            }
        }
    }
}

