package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstructionScreen(
    game: GameType,
    onBack: () -> Unit,
    onStartGame: () -> Unit
) {
    val instructions = getInstructionsForGame(game)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(game.title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Button(
                    onClick = onStartGame,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0284C7)
                    )
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Let's Begin the Game",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Assessment Focus",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = game.skillsMeasured,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "⏱️ Duration: ${game.durationText}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "🎯 Rounds: ${game.roundsText}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Text(
                text = "Instructions",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFDC2626) // Red header as shown in PDF slides
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    instructions.forEach { item ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = item,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getInstructionsForGame(game: GameType): List<String> {
    return when (game) {
        GameType.GRID_CHALLENGE -> listOf(
            "Measures executive attention and working memory.",
            "The goal is to remember where and in what order dots appear on each grid.",
            "There's only a certain amount of time to remember the position of the dot.",
            "In between grids, select whether or not the two pictures displayed are symmetrical before the NEXT DOT appears.",
            "Next, select where and in which order the dots appeared in the previous grids.",
            "The correct solution completes the level and opens the next level."
        )
        GameType.MOTION_CHALLENGE -> listOf(
            "Measures planning and problem-solving ability.",
            "The objective is to move the red ball into the black hole.",
            "Drag/slide the bars and blocks to clear a path for the ball.",
            "Once the path is clear, move the ball to the hole to complete the level.",
            "Try to solve the puzzle using minimum moves.",
            "Focus on planning before making each move."
        )
        GameType.INDUCTIVE_LOGIC -> listOf(
            "A question figure with a pattern rule will be displayed.",
            "Four answer figures (A, B, C, D) will be given.",
            "Observe the pattern followed in the question figure carefully.",
            "Do not focus only on symbols or shapes; focus on how positions are changing.",
            "Symbols may interchange positions and still follow the same rule.",
            "From the four options, identify the TWO figures (e.g. B-C, C-D) that follow the same pattern."
        )
        GameType.DEDUCTIVE_LOGIC -> listOf(
            "Measures your ability to plan ahead using Sudoku-based puzzles.",
            "Each object (Cross, Circle, Triangle, Square, Star) appears only once in every row and every column.",
            "One cell will contain a question mark (?).",
            "Identify the object that logically fits in the missing cell using row and column constraints.",
            "Grid size increases at higher levels."
        )
        GameType.NUM_BUBBLES -> listOf(
            "The goal of NUMBUBBLES is to POP BUBBLES containing equations that EQUAL the TARGET number.",
            "NUMBUBBLES measures your ability to use mathematics and quickly calculate formulas.",
            "Each target is shown for 12 seconds with 20 floating bubbles.",
            "Pop as many correct target bubbles as possible within the 12-second round timer."
        )
        GameType.SHORT_CUTS -> listOf(
            "Move the Blue marble to the Starred area as efficiently as possible along connected tracks.",
            "The red colored marble, whenever moved, will ADD up to the distance being covered.",
            "The grey color shaded region will REMOVE some of the distance covered if blue marble moves through it.",
            "Calculate shortest path and achieve minimum distance traveled for Great (⭐⭐⭐), Good (⭐⭐), or OK (⭐) ratings."
        )
        GameType.RESEMBLE -> listOf(
            "The goal of RESEMBLE is to RECREATE a pattern by constructing it from provided pieces or mental rotation.",
            "Sometimes patterns require mental rotation (90 or 180 degrees clockwise/anti-clockwise).",
            "Select the correctly rotated target image or place pieces to replicate the objective.",
            "Measures spatial visualization and mental rotation."
        )
        GameType.TALLY_UP -> listOf(
            "Measures ability to use basic mathematics at speed under time pressure.",
            "Each question has two boxes with values inside.",
            "Sum all values inside boxes: values with strikethroughs count as ZERO.",
            "Multipliers (x2, x4) multiply individual or total box values.",
            "Click the box with the GREATER SUM, or click the '=' button if sums are equal.",
            "Timer is strictly 4 seconds per question across 35 rounds!"
        )
        GameType.NON_VERBAL_REASONING -> listOf(
            "Covers Mirror Images (left-right flip), Water Images (top-bottom inverted), Figure Series, Analogy, and Logical Venn Diagrams.",
            "Carefully observe spatial orientation and structural transformations.",
            "Select the correct option figure for each prompt."
        )
        GameType.CRYPT_ARITHMETIC -> listOf(
            "Numbers are replaced by alphabets in arithmetic calculations (e.g. BOX+BOO = EBB, WERE+TWE = DUKET).",
            "Each alphabet takes a unique digit from 0 to 9.",
            "Numbers cannot begin with zero. Sum carryovers follow standard addition rules (max carry 1 for two digits).",
            "Decode the digits and calculate the target expression!"
        )
    }
}
