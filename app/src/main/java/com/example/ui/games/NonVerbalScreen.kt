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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class NonVerbalQuestion(
    val topic: String,
    val questionText: String,
    val options: List<String>,
    val correctIndex: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NonVerbalScreen(
    onFinishGame: (score: Int, rounds: Int, maxScore: Int) -> Unit,
    onQuit: () -> Unit = {}
) {
    var qIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var showQuitDialog by remember { mutableStateOf(false) }

    val questions = remember {
        listOf(
            NonVerbalQuestion(
                topic = "Mirror Images",
                questionText = "Mirror image of word 'REASONING' (left-right flip):",
                options = listOf("(1) GNINOSAER", "(2) ЯƎAƧOИIИӘ", "(3) ЯEAƧOИIИӘ", "(4) ӘNIИOSAƎЯ"),
                correctIndex = 1
            ),
            NonVerbalQuestion(
                topic = "Water Images",
                questionText = "Water image of 'FROG' (inverted upside down):",
                options = listOf("(1) ℲЯOӘ", "(2) GORF", "(3) FROG", "(4) ℲROӘ"),
                correctIndex = 0
            ),
            NonVerbalQuestion(
                topic = "Logical Venn Diagrams",
                questionText = "Select Venn diagram for: Elephant, Wolf, Animal",
                options = listOf("3 concentric circles", "3 separate circles", "Two inner separate circles inside one outer circle", "Intersecting circles"),
                correctIndex = 2
            ),
            NonVerbalQuestion(
                topic = "Counting Figures",
                questionText = "A triangle divided into 3 vertical parts (1, 2, 3). Total triangles:",
                options = listOf("3", "6 (1+2+3)", "9", "10"),
                correctIndex = 1
            )
        )
    }

    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var feedbackText by remember { mutableStateOf<String?>(null) }

    val currentQ = questions[qIndex]

    fun submitChoice(index: Int) {
        selectedIndex = index
        if (index == currentQ.correctIndex) {
            score += 25
            feedbackText = "Correct!"
        } else {
            feedbackText = "Incorrect. Correct option: ${currentQ.options[currentQ.correctIndex]}"
        }
    }

    fun nextQuestion() {
        if (qIndex < questions.size - 1) {
            qIndex++
            selectedIndex = null
            feedbackText = null
        } else {
            onFinishGame(score, questions.size, questions.size * 25)
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
                title = { Text("Non-Verbal Reasoning", fontWeight = FontWeight.Bold) },
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
                            text = "Q${qIndex + 1}/${questions.size} | Score: $score",
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
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = currentQ.topic,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentQ.questionText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E3A8A)
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                currentQ.options.forEachIndexed { index, option ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = if (selectedIndex == index) 2.dp else 1.dp,
                                color = if (selectedIndex == index) Color(0xFF0284C7) else Color.LightGray,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { if (selectedIndex == null) submitChoice(index) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedIndex == index) Color(0xFFE0F2FE) else Color.White
                        )
                    ) {
                        Text(
                            text = option,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            feedbackText?.let { fb ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = fb,
                        fontSize = 15.sp,
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
