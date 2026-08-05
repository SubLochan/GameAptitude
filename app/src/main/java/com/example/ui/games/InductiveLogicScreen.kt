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

data class InductiveQuestion(
    val ruleDescription: String,
    val questionGrids: List<List<String>>, // 2 grids showing the rule
    val optionGrids: Map<String, List<String>>, // A, B, C, D candidate grids
    val correctPair: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InductiveLogicScreen(
    onFinishGame: (score: Int, rounds: Int, maxScore: Int) -> Unit,
    onQuit: () -> Unit = {}
) {
    var questionNum by remember { mutableIntStateOf(1) }
    var score by remember { mutableIntStateOf(0) }
    var showQuitDialog by remember { mutableStateOf(false) }
    val maxQuestions = 5

    val questions = remember {
        listOf(
            InductiveQuestion(
                ruleDescription = "Rule: Grid contains equal number of Circles 🔵 and Triangles 🔺",
                questionGrids = listOf(
                    listOf("🔵", "🔺", "➕", "🔵", "🔺", "⬛"),
                    listOf("🔺", "➕", "🔵", "⬛", "🔺", "🔵")
                ),
                optionGrids = mapOf(
                    "A" to listOf("🔵", "🔵", "➕", "🔺", "⬛", "➕"),
                    "B" to listOf("🔵", "🔺", "➕", "🔵", "🔺", "⬛"),
                    "C" to listOf("🔺", "🔵", "⬛", "🔵", "🔺", "➕"),
                    "D" to listOf("⬛", "⬛", "➕", "🔵", "🔺", "🔺")
                ),
                correctPair = "B-C"
            ),
            InductiveQuestion(
                ruleDescription = "Rule: Top-Left cell is always a Plus ➕",
                questionGrids = listOf(
                    listOf("➕", "🔵", "🔺", "⬛", "🔵", "🔺"),
                    listOf("➕", "🔺", "⬛", "🔵", "➕", "🔴")
                ),
                optionGrids = mapOf(
                    "A" to listOf("🔴", "🔵", "🔺", "⬛", "🔵", "🔺"),
                    "B" to listOf("⬛", "🔺", "⬛", "🔵", "➕", "🔴"),
                    "C" to listOf("➕", "🔵", "⬛", "🔺", "➕", "🔵"),
                    "D" to listOf("➕", "🔺", "🔴", "🔵", "⬛", "🔺")
                ),
                correctPair = "C-D"
            ),
            InductiveQuestion(
                ruleDescription = "Rule: Grid contains at least 3 Blue Circles 🔵",
                questionGrids = listOf(
                    listOf("🔵", "🔵", "🔵", "➕", "🔺", "⬛"),
                    listOf("🔵", "🔺", "🔵", "⬛", "🔵", "➕")
                ),
                optionGrids = mapOf(
                    "A" to listOf("🔵", "🔵", "🔵", "⬛", "➕", "🔺"),
                    "B" to listOf("🔵", "🔺", "⬛", "🔵", "➕", "🔺"),
                    "C" to listOf("🔺", "⬛", "➕", "🔵", "🔵", "🔺"),
                    "D" to listOf("🔵", "🔵", "🔺", "🔵", "⬛", "➕")
                ),
                correctPair = "A-D"
            ),
            InductiveQuestion(
                ruleDescription = "Rule: Bottom row ends with a Black Square ⬛",
                questionGrids = listOf(
                    listOf("🔵", "🔺", "➕", "➕", "🔴", "⬛"),
                    listOf("🔺", "➕", "🔵", "🔵", "🔺", "⬛")
                ),
                optionGrids = mapOf(
                    "A" to listOf("➕", "🔵", "🔺", "🔴", "➕", "⬛"),
                    "B" to listOf("🔵", "🔺", "🔴", "🔺", "🔵", "⬛"),
                    "C" to listOf("🔺", "➕", "🔵", "⬛", "🔴", "➕"),
                    "D" to listOf("🔴", "🔵", "➕", "➕", "⬛", "🔺")
                ),
                correctPair = "A-B"
            ),
            InductiveQuestion(
                ruleDescription = "Rule: Both rows have at least one Red Circle 🔴",
                questionGrids = listOf(
                    listOf("🔵", "🔴", "🔺", "➕", "🔴", "⬛"),
                    listOf("🔴", "➕", "🔵", "🔺", "⬛", "🔴")
                ),
                optionGrids = mapOf(
                    "A" to listOf("🔵", "🔺", "➕", "🔴", "⬛", "➕"),
                    "B" to listOf("🔵", "🔴", "🔺", "➕", "🔴", "⬛"),
                    "C" to listOf("🔺", "➕", "🔵", "⬛", "➕", "🔺"),
                    "D" to listOf("🔴", "🔵", "⬛", "🔺", "🔴", "➕")
                ),
                correctPair = "B-D"
            )
        )
    }

    val currentQ = questions[(questionNum - 1) % questions.size]

    var selectedPair by remember { mutableStateOf<String?>(null) }
    var feedbackText by remember { mutableStateOf<String?>(null) }

    fun handleSelection(pair: String) {
        selectedPair = pair
        if (pair == currentQ.correctPair) {
            score += 20
            feedbackText = "Correct! Grids $pair follow the question rule."
        } else {
            feedbackText = "Incorrect. Correct pair is ${currentQ.correctPair}."
        }
    }

    fun nextQuestion() {
        if (questionNum < maxQuestions) {
            questionNum++
            selectedPair = null
            feedbackText = null
        } else {
            onFinishGame(score, maxQuestions, maxQuestions * 20)
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
                title = { Text("Inductive Logical Thinking", fontWeight = FontWeight.Bold) },
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
                            text = "Q$questionNum/$maxQuestions | Score: $score",
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
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Which set of candidate grids follow the rule in Question?",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // Question Rule Box
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("QUESTION RULE PATTERN", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        currentQ.questionGrids.forEach { symbols ->
                            MiniRuleGrid(symbols = symbols)
                        }
                    }
                }
            }

            // Candidate Options Grids A, B, C, D
            Text("CANDIDATE GRIDS (A, B, C, D):", fontSize = 12.sp, fontWeight = FontWeight.Bold)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("A", "B", "C", "D").forEach { label ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(label, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        currentQ.optionGrids[label]?.let { symbols ->
                            MiniRuleGrid(symbols = symbols)
                        }
                    }
                }
            }

            // Options Pair Selection Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("A-B", "B-C", "C-D", "A-D", "B-D").forEach { pairOption ->
                    Button(
                        onClick = { if (selectedPair == null) handleSelection(pairOption) },
                        modifier = Modifier.height(42.dp),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedPair == pairOption) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (selectedPair == pairOption) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text(pairOption, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            feedbackText?.let { fb ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = fb,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (fb.startsWith("Correct")) Color(0xFF16A34A) else Color.Red
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(onClick = { nextQuestion() }) {
                        Text("Next Question")
                    }
                }
            }
        }
    }
}

@Composable
private fun MiniRuleGrid(symbols: List<String>) {
    Card(
        modifier = Modifier
            .size(72.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(6.dp)),
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                Text(symbols.getOrElse(0) { "" }, fontSize = 13.sp)
                Text(symbols.getOrElse(1) { "" }, fontSize = 13.sp)
                Text(symbols.getOrElse(2) { "" }, fontSize = 13.sp)
            }
            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                Text(symbols.getOrElse(3) { "" }, fontSize = 13.sp)
                Text(symbols.getOrElse(4) { "" }, fontSize = 13.sp)
                Text(symbols.getOrElse(5) { "" }, fontSize = 13.sp)
            }
        }
    }
}

