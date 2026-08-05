package com.example.ui.games

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

data class BoxItem(
    val value: Int,
    val isStrikethrough: Boolean = false,
    val multiplier: Int = 1,
    val shapeType: Int = 0 // 0: circle, 1: square, 2: triangle
)

data class BoxData(
    val items: List<BoxItem>,
    val boxMultiplier: Int = 1
) {
    fun calculatedSum(): Int {
        val baseSum = items.sumOf { item ->
            if (item.isStrikethrough) 0 else item.value * item.multiplier
        }
        return baseSum * boxMultiplier
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TallyUpScreen(
    onFinishGame: (score: Int, rounds: Int, maxScore: Int) -> Unit,
    onQuit: () -> Unit = {}
) {
    var round by remember { mutableIntStateOf(1) }
    var score by remember { mutableIntStateOf(0) }
    var showQuitDialog by remember { mutableStateOf(false) }
    var timeRemaining by remember { mutableFloatStateOf(4f) }
    val maxRounds = 35

    var leftBox by remember { mutableStateOf(generateBoxData(round)) }
    var rightBox by remember { mutableStateOf(generateBoxData(round + 1)) }
    var feedbackText by remember { mutableStateOf<String?>(null) }

    // 4 second timer per question
    LaunchedEffect(round, timeRemaining) {
        if (timeRemaining > 0) {
            delay(100)
            timeRemaining -= 0.1f
        } else {
            // Time expired for round
            nextRound(userSelection = -1, leftBox, rightBox) { isCorrect ->
                if (isCorrect) score += 10
            }
        }
    }

    fun handleAnswer(selection: Int) { // 0 = Left, 1 = Right, 2 = Equal
        val leftSum = leftBox.calculatedSum()
        val rightSum = rightBox.calculatedSum()

        val isCorrect = when (selection) {
            0 -> leftSum > rightSum
            1 -> rightSum > leftSum
            2 -> leftSum == rightSum
            else -> false
        }

        if (isCorrect) {
            score += 10
            feedbackText = "Correct! (+10)"
        } else {
            feedbackText = "Wrong! ($leftSum vs $rightSum)"
        }

        if (round < maxRounds) {
            round++
            timeRemaining = 4f
            leftBox = generateBoxData(round)
            rightBox = generateBoxData(round + 1)
        } else {
            onFinishGame(score, maxRounds, maxRounds * 10)
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
                title = { Text("TALLY UP", fontWeight = FontWeight.Bold) },
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
            // Timer bar (4s)
            LinearProgressIndicator(
                progress = { (timeRemaining / 4f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (timeRemaining < 1.5f) Color.Red else Color(0xFF16A34A)
            )

            Text(
                text = "${String.format("%.1f", timeRemaining)}s",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (timeRemaining < 1.5f) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Two Boxes
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Left Box
                BoxCard(
                    boxData = leftBox,
                    isBlackTheme = true,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { handleAnswer(0) }
                )

                // Right Box
                BoxCard(
                    boxData = rightBox,
                    isBlackTheme = false,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { handleAnswer(1) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // "=" Equal Button
            Button(
                onClick = { handleAnswer(2) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE2E8F0),
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = "=",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            feedbackText?.let { feedback ->
                Text(
                    text = feedback,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (feedback.startsWith("Correct")) Color(0xFF16A34A) else Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun BoxCard(
    boxData: BoxData,
    isBlackTheme: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.border(
            width = 2.dp,
            color = Color.DarkGray,
            shape = RoundedCornerShape(16.dp)
        ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                boxData.items.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        rowItems.forEach { item ->
                            ItemGraphic(item = item, isBlackTheme = isBlackTheme)
                        }
                    }
                }
            }

            if (boxData.boxMultiplier > 1) {
                Surface(
                    color = Color.Red,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    Text(
                        text = "x${boxData.boxMultiplier}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ItemGraphic(item: BoxItem, isBlackTheme: Boolean) {
    val bg = if (isBlackTheme) Color.Black else Color.Red
    val textColor = if (isBlackTheme) Color.Red else Color.White

    val shape = when (item.shapeType) {
        0 -> CircleShape
        1 -> RoundedCornerShape(6.dp)
        else -> RoundedCornerShape(4.dp)
    }

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(shape)
            .background(bg),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "${item.value}",
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            textDecoration = if (item.isStrikethrough) TextDecoration.LineThrough else TextDecoration.None
        )

        if (item.multiplier > 1) {
            Text(
                text = "x${item.multiplier}",
                color = Color.Yellow,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}

private fun generateBoxData(seed: Int): BoxData {
    val count = kotlin.random.Random.nextInt(2, 5)
    val items = (1..count).map {
        BoxItem(
            value = kotlin.random.Random.nextInt(1, 6),
            isStrikethrough = kotlin.random.Random.nextFloat() < 0.2f,
            multiplier = if (kotlin.random.Random.nextFloat() < 0.15f) 2 else 1,
            shapeType = kotlin.random.Random.nextInt(3)
        )
    }
    val boxMultiplier = if (kotlin.random.Random.nextFloat() < 0.1f) 4 else 1
    return BoxData(items, boxMultiplier)
}

private fun nextRound(userSelection: Int, left: BoxData, right: BoxData, callback: (Boolean) -> Unit) {
    val leftSum = left.calculatedSum()
    val rightSum = right.calculatedSum()
    val correct = when (userSelection) {
        0 -> leftSum > rightSum
        1 -> rightSum > leftSum
        2 -> leftSum == rightSum
        else -> false
    }
    callback(correct)
}
