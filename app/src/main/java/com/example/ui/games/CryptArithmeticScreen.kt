package com.example.ui.games

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryptArithmeticScreen(
    onFinishGame: (score: Int, rounds: Int, maxScore: Int) -> Unit,
    onQuit: () -> Unit = {}
) {
    var puzzleIndex by remember { mutableIntStateOf(1) }
    var score by remember { mutableIntStateOf(0) }
    var showQuitDialog by remember { mutableStateOf(false) }
    val maxPuzzles = 3

    var userInputValue by remember { mutableStateOf("") }
    var feedbackText by remember { mutableStateOf<String?>(null) }

    val currentPuzzle = when (puzzleIndex) {
        1 -> "BOX + BOO = EBB\nIf O = 5, find value of E + B + B?"
        2 -> "WERE + TWE = DUKET\nFind the value of R + E + W + E + R?"
        else -> "TWO + TWO + TWO + TWO = SOW\nFind the value of T + O + S + S?"
    }

    val correctAns = when (puzzleIndex) {
        1 -> "5" // E=3, B=1, E+B+B = 3+1+1 = 5
        2 -> "27" // K=3, R=5, D=1, W=9, E=4, T=8 -> 5+4+9+4+5 = 27
        else -> "17" // T+O+S+S = 17
    }

    fun submitAnswer() {
        if (userInputValue.trim() == correctAns) {
            score += 30
            feedbackText = "Correct! Answer is $correctAns."
        } else {
            feedbackText = "Incorrect. Correct answer is $correctAns."
        }
    }

    fun nextPuzzle() {
        if (puzzleIndex < maxPuzzles) {
            puzzleIndex++
            userInputValue = ""
            feedbackText = null
        } else {
            onFinishGame(score, maxPuzzles, maxPuzzles * 30)
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
                title = { Text("Crypt Arithmetic Aptitude", fontWeight = FontWeight.Bold) },
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
                            text = "Puzzle $puzzleIndex/$maxPuzzles | Score: $score",
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Rules:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("• Each letter takes a unique digit 0-9.", fontSize = 12.sp)
                    Text("• Numbers cannot begin with zero.", fontSize = 12.sp)
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentPuzzle,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E3A8A)
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = userInputValue,
                    onValueChange = { userInputValue = it },
                    label = { Text("Enter calculated value") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )

                Button(
                    onClick = { submitAnswer() },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(50.dp)
                ) {
                    Text("Submit Answer", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                feedbackText?.let { fb ->
                    Text(
                        text = fb,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (fb.startsWith("Correct")) Color(0xFF16A34A) else Color.Red
                    )

                    Button(onClick = { nextPuzzle() }) {
                        Text("Next Puzzle")
                    }
                }
            }
        }
    }
}
