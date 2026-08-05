package com.example.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class DeductivePuzzle(
    val grid: List<List<String>>,
    val missingRow: Int,
    val missingCol: Int,
    val correctSymbolIndex: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeductiveLogicScreen(
    onFinishGame: (score: Int, rounds: Int, maxScore: Int) -> Unit,
    onQuit: () -> Unit = {}
) {
    var round by remember { mutableIntStateOf(1) }
    var score by remember { mutableIntStateOf(0) }
    var showQuitDialog by remember { mutableStateOf(false) }
    val maxRounds = 5

    val shapeSymbols = listOf("➕", "⚪", "▲", "⬛")

    val puzzles = remember {
        listOf(
            DeductivePuzzle(
                grid = listOf(
                    listOf("➕", "⚪", "▲", "⬛"),
                    listOf("▲", "⬛", "➕", "⚪"),
                    listOf("⚪", "➕", "⬛", "▲"),
                    listOf("⬛", "▲", "⚪", "➕")
                ),
                missingRow = 1, missingCol = 2, correctSymbolIndex = 0 // ➕
            ),
            DeductivePuzzle(
                grid = listOf(
                    listOf("⬛", "▲", "⚪", "➕"),
                    listOf("⚪", "➕", "⬛", "▲"),
                    listOf("➕", "⚪", "▲", "⬛"),
                    listOf("▲", "⬛", "➕", "⚪")
                ),
                missingRow = 2, missingCol = 3, correctSymbolIndex = 3 // ⬛
            ),
            DeductivePuzzle(
                grid = listOf(
                    listOf("▲", "⚪", "➕", "⬛"),
                    listOf("⬛", "➕", "▲", "⚪"),
                    listOf("⚪", "⬛", "⚪", "▲"),
                    listOf("➕", "▲", "⬛", "⚪")
                ),
                missingRow = 0, missingCol = 1, correctSymbolIndex = 1 // ⚪
            ),
            DeductivePuzzle(
                grid = listOf(
                    listOf("⚪", "⬛", "➕", "▲"),
                    listOf("➕", "▲", "⚪", "⬛"),
                    listOf("▲", "⚪", "⬛", "➕"),
                    listOf("⬛", "➕", "▲", "⚪")
                ),
                missingRow = 3, missingCol = 0, correctSymbolIndex = 3 // ⬛
            ),
            DeductivePuzzle(
                grid = listOf(
                    listOf("➕", "▲", "⬛", "⚪"),
                    listOf("⬛", "⚪", "▲", "➕"),
                    listOf("⚪", "➕", "⚪", "▲"),
                    listOf("▲", "⬛", "➕", "⚪")
                ),
                missingRow = 2, missingCol = 1, correctSymbolIndex = 0 // ➕
            )
        )
    }

    val currentPuzzle = puzzles[(round - 1) % puzzles.size]

    var selectedShapeIndex by remember { mutableStateOf<Int?>(null) }
    var feedbackText by remember { mutableStateOf<String?>(null) }

    fun submitAnswer(chosenIndex: Int) {
        selectedShapeIndex = chosenIndex
        if (chosenIndex == currentPuzzle.correctSymbolIndex) {
            score += 20
            feedbackText = "Correct! The missing symbol is ${shapeSymbols[currentPuzzle.correctSymbolIndex]}"
        } else {
            feedbackText = "Incorrect. Correct symbol is ${shapeSymbols[currentPuzzle.correctSymbolIndex]}"
        }
    }

    fun nextQuestion() {
        if (round < maxRounds) {
            round++
            selectedShapeIndex = null
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
                title = { Text("Deductive Logical Thinking", fontWeight = FontWeight.Bold) },
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
                            text = "Question $round/$maxRounds | Score: $score",
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
                text = "Each symbol appears only ONCE per row and column. What replaces '?' ?",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // 4x4 Grid Board matching Sudoku rules
            Card(
                modifier = Modifier
                    .size(290.dp)
                    .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    for (r in 0..3) {
                        Row(modifier = Modifier.weight(1f)) {
                            for (c in 0..3) {
                                val isQuestionCell = (r == currentPuzzle.missingRow && c == currentPuzzle.missingCol)
                                val symbol = if (isQuestionCell) "?" else currentPuzzle.grid[r][c]

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                        .background(if (isQuestionCell) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = symbol,
                                        fontSize = if (isQuestionCell) 32.sp else 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isQuestionCell) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Options Selection Bar
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Select Symbol for '?' :",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    shapeSymbols.forEachIndexed { index, symbol ->
                        OutlinedButton(
                            onClick = { if (selectedShapeIndex == null) submitAnswer(index) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.size(60.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(symbol, fontSize = 24.sp)
                        }
                    }
                }

                feedbackText?.let { fb ->
                    Text(
                        text = fb,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (fb.startsWith("Correct")) Color(0xFF16A34A) else Color.Red
                    )

                    Button(
                        onClick = { nextQuestion() },
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(48.dp)
                    ) {
                        Text("Next Question")
                    }
                }
            }
        }
    }
}

