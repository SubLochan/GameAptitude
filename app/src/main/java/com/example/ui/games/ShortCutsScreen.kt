package com.example.ui.games

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShortCutsScreen(
    onFinishGame: (score: Int, rounds: Int, maxScore: Int) -> Unit,
    onQuit: () -> Unit = {}
) {
    var round by remember { mutableIntStateOf(1) }
    var score by remember { mutableIntStateOf(0) }
    var showQuitDialog by remember { mutableStateOf(false) }
    val maxRounds = 7

    var distanceTraveled by remember { mutableIntStateOf(0) }
    var currentNode by remember { mutableStateOf("F") } // Start at F
    val targetNode = "C" // Star at C

    val thresholdGreat = when (round) {
        1 -> 14
        2 -> 10
        3 -> 15
        4 -> 13
        5 -> 17
        6 -> 25
        else -> 18
    }

    fun moveToNode(target: String, cost: Int) {
        distanceTraveled += cost
        currentNode = target

        if (currentNode == targetNode) {
            val roundScore = when {
                distanceTraveled <= thresholdGreat -> 50
                distanceTraveled <= thresholdGreat + 3 -> 35
                else -> 20
            }
            score += roundScore

            if (round < maxRounds) {
                round++
                distanceTraveled = 0
                currentNode = "F"
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
                title = { Text("Short Cuts", fontWeight = FontWeight.Bold) },
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Move Blue Marble 'F' to Star 'C' with minimal distance!",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // Rating Meter & Distance Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Distance: $distanceTraveled", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Target: $thresholdGreat ", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFEAB308), modifier = Modifier.size(18.dp))
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFEAB308), modifier = Modifier.size(18.dp))
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFEAB308), modifier = Modifier.size(18.dp))
                    }
                }
            }

            // Node Graph Canvas Visualizer
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    val lineColor = MaterialTheme.colorScheme.outline
                    val activeColor = MaterialTheme.colorScheme.primary
                    val targetColor = Color(0xFFEAB308)
                    val redNodeColor = Color(0xFFEF4444)

                    Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        val w = size.width
                        val h = size.height

                        val nodeCoords = mapOf(
                            "F" to Offset(w * 0.1f, h * 0.5f),
                            "E" to Offset(w * 0.3f, h * 0.2f),
                            "A" to Offset(w * 0.3f, h * 0.8f),
                            "B" to Offset(w * 0.55f, h * 0.8f),
                            "D" to Offset(w * 0.55f, h * 0.2f),
                            "R1" to Offset(w * 0.78f, h * 0.2f),
                            "R2" to Offset(w * 0.78f, h * 0.8f),
                            "C" to Offset(w * 0.9f, h * 0.5f)
                        )

                        val edges = listOf(
                            Triple("F", "E", "2"), Triple("F", "A", "3"),
                            Triple("E", "D", "4"), Triple("E", "A", "3"),
                            Triple("A", "B", "3"), Triple("B", "D", "2"),
                            Triple("B", "R2", "2"), Triple("D", "R1", "2"),
                            Triple("D", "C", "3"), Triple("R1", "C", "3"), Triple("R2", "C", "3")
                        )

                        // Draw lines
                        edges.forEach { (src, dst, cost) ->
                            val p1 = nodeCoords[src]
                            val p2 = nodeCoords[dst]
                            if (p1 != null && p2 != null) {
                                drawLine(color = lineColor, start = p1, end = p2, strokeWidth = 3f)
                            }
                        }

                        // Draw Node Circles
                        nodeCoords.forEach { (name, pos) ->
                            val isCurrent = (name == currentNode)
                            val isTarget = (name == targetNode)
                            val isRedNode = (name == "R1" || name == "R2")

                            val nodeColor = when {
                                isCurrent -> activeColor
                                isTarget -> targetColor
                                isRedNode -> redNodeColor
                                else -> lineColor
                            }

                            drawCircle(color = nodeColor, radius = if (isCurrent) 22f else 16f, center = pos)
                            drawCircle(color = Color.White, radius = if (isCurrent) 22f else 16f, center = pos, style = Stroke(width = 2f))
                        }
                    }
                }
            }

            // Available Next Moves Controls
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Current Position: Node $currentNode",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        when (currentNode) {
                            "F" -> {
                                Button(onClick = { moveToNode("E", 2) }) { Text("F → E (+2)") }
                                Button(onClick = { moveToNode("A", 3) }) { Text("F → A (+3)") }
                            }
                            "E" -> {
                                Button(onClick = { moveToNode("D", 4) }) { Text("E → D (+4)") }
                                Button(onClick = { moveToNode("A", 3) }) { Text("E → A (+3)") }
                            }
                            "A" -> {
                                Button(onClick = { moveToNode("B", 3) }) { Text("A → B (+3)") }
                            }
                            "B" -> {
                                Button(onClick = { moveToNode("R2", 2) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))) { Text("B → Red R2 (+2)") }
                                Button(onClick = { moveToNode("D", 2) }) { Text("B → D (+2)") }
                            }
                            "R2" -> {
                                Button(onClick = { moveToNode("C", 3) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEAB308))) { Text("R2 → Star C (+3)") }
                            }
                            "D" -> {
                                Button(onClick = { moveToNode("R1", 2) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))) { Text("D → Red R1 (+2)") }
                                Button(onClick = { moveToNode("C", 3) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEAB308))) { Text("D → Star C (+3)") }
                            }
                            "R1" -> {
                                Button(onClick = { moveToNode("C", 3) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEAB308))) { Text("R1 → Star C (+3)") }
                            }
                        }
                    }

                    OutlinedButton(onClick = {
                        distanceTraveled = 0
                        currentNode = "F"
                    }) {
                        Text("Reset Path")
                    }
                }
            }
        }
    }
}

