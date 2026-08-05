package com.example.ui.games

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

data class BubbleItem(
    val id: Int,
    val expression: String,
    val value: Int,
    val color: Color,
    var isPopped: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumBubbleScreen(
    onFinishGame: (score: Int, rounds: Int, maxScore: Int) -> Unit,
    onQuit: () -> Unit = {}
) {
    var round by remember { mutableIntStateOf(1) }
    var score by remember { mutableIntStateOf(0) }
    var showQuitDialog by remember { mutableStateOf(false) }
    var timeRemaining by remember { mutableFloatStateOf(12f) }
    val maxRounds = 10

    var currentTarget by remember { mutableIntStateOf(18) }
    var bubbles by remember { mutableStateOf(generateBubblesForTarget(18)) }
    var poppedInRound by remember { mutableIntStateOf(0) }

    // Timer effect
    LaunchedEffect(round, timeRemaining) {
        if (timeRemaining > 0) {
            delay(100)
            timeRemaining -= 0.1f
        } else {
            // Next round when time expires
            if (round < maxRounds) {
                round += 1
                currentTarget = listOf(18, 30, 17, 9, 50, -4, 6, 44, 90, 8)[(round - 1) % 10]
                bubbles = generateBubblesForTarget(currentTarget)
                timeRemaining = 12f
                poppedInRound = 0
            } else {
                onFinishGame(score, maxRounds, maxRounds * 50)
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
                title = { Text("NUMBUBBLES", fontWeight = FontWeight.Bold) },
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Progress Bar for Round Time
            LinearProgressIndicator(
                progress = { (timeRemaining / 12f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (timeRemaining < 3f) Color.Red else Color(0xFF0284C7)
            )

            Text(
                text = "⏱️ Time left: ${String.format("%.1f", timeRemaining)}s",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (timeRemaining < 3f) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Game Area Container
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE2E8F0)
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    // Render 20 Bubbles
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val rows = bubbles.chunked(4)
                        rows.forEach { rowBubbles ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                rowBubbles.forEach { bubble ->
                                    if (!bubble.isPopped) {
                                        Box(
                                            modifier = Modifier
                                                .size(62.dp)
                                                .clip(CircleShape)
                                                .background(bubble.color)
                                                .clickable {
                                                    val isCorrect = (bubble.value == currentTarget)
                                                    if (isCorrect) {
                                                        score += 10
                                                    } else {
                                                        score = (score - 5).coerceAtLeast(0)
                                                    }
                                                    bubbles = bubbles.map {
                                                        if (it.id == bubble.id) it.copy(isPopped = true) else it
                                                    }
                                                    poppedInRound++
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = bubble.expression,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.size(62.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Target banner matching PDF style: "Target : 18"
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(60.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF0284C7)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Target : $currentTarget",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

private fun generateBubblesForTarget(target: Int): List<BubbleItem> {
    val colors = listOf(
        Color(0xFFEC4899), // Pink
        Color(0xFF10B981), // Emerald
        Color(0xFFEAB308), // Yellow
        Color(0xFF0284C7), // Blue
        Color(0xFFEF4444), // Red
        Color(0xFF8B5CF6)  // Purple
    )

    val result = mutableListOf<BubbleItem>()
    var id = 1

    // Generate 6 correct equations
    val correctEqs = listOf(
        "${target + 5}-${5}",
        "${target * 2}/2",
        "${target - 3}+3",
        "${target + 10}-10",
        "${target}+0",
        "${target * 10}/10"
    )

    correctEqs.forEach { eq ->
        result.add(
            BubbleItem(
                id = id++,
                expression = eq,
                value = target,
                color = colors[Random.nextInt(colors.size)]
            )
        )
    }

    // Generate 14 incorrect equations
    val wrongEqs = listOf(
        "${target + 4}",
        "${target - 2}",
        "${target + 10}",
        "${target * 3}",
        "${target / 2 + 1}",
        "${target + 7}-2",
        "${target - 8}+1",
        "${target * 2 + 3}",
        "${target + 12}",
        "${target - 5}",
        "${target * 4}",
        "${target + 15}-3",
        "${target + 2}",
        "${target - 6}"
    )

    wrongEqs.forEach { eq ->
        result.add(
            BubbleItem(
                id = id++,
                expression = eq,
                value = target + Random.nextInt(1, 10) * if (Random.nextBoolean()) 1 else -1,
                color = colors[Random.nextInt(colors.size)]
            )
        )
    }

    return result.shuffled()
}
