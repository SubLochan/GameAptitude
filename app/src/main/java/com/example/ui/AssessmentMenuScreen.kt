package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameScore
import com.example.model.GameType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssessmentMenuScreen(
    gameHistory: List<GameScore>,
    onGameSelected: (GameType) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "Logic & Deductive", "Math & Speed", "Spatial & Memory")

    val filteredGames = GameType.entries.filter { game ->
        val matchesSearch = game.title.contains(searchQuery, ignoreCase = true) ||
                game.subtitle.contains(searchQuery, ignoreCase = true)
        val matchesCategory = when (selectedCategory) {
            "Logic & Deductive" -> game in listOf(
                GameType.INDUCTIVE_LOGIC, GameType.DEDUCTIVE_LOGIC, GameType.NON_VERBAL_REASONING, GameType.CRYPT_ARITHMETIC
            )
            "Math & Speed" -> game in listOf(
                GameType.NUM_BUBBLES, GameType.TALLY_UP, GameType.CRYPT_ARITHMETIC
            )
            "Spatial & Memory" -> game in listOf(
                GameType.GRID_CHALLENGE, GameType.MOTION_CHALLENGE, GameType.SHORT_CUTS, GameType.RESEMBLE
            )
            else -> true
        }
        matchesSearch && matchesCategory
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "MNC Assessment Hub",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "Gamification Recruitment Tests",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "Score",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            val totalScore = gameHistory.sumOf { it.score }
                            Text(
                                text = "$totalScore pts",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                // Hero Banner
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF1E3A8A),
                                        Color(0xFF0284C7)
                                    )
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Surface(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "MNC APTITUDE PATTERNS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Gamified Aptitude Assessments",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "10 interactive assessment modules measuring executive attention, spatial planning, speed math & deductive logic.",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }

            item {
                // Search & Filter
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search assessment game...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        categories.forEach { category ->
                            FilterChip(
                                selected = selectedCategory == category,
                                onClick = { selectedCategory = category },
                                label = { Text(category, fontSize = 12.sp) },
                                shape = RoundedCornerShape(20.dp)
                            )
                        }
                    }
                }
            }

            items(filteredGames) { game ->
                val lastScore = gameHistory.lastOrNull { it.gameType == game }
                GameCard(
                    game = game,
                    lastScore = lastScore,
                    onClick = { onGameSelected(game) }
                )
            }
        }
    }
}

@Composable
private fun GameCard(
    game: GameType,
    lastScore: GameScore?,
    onClick: () -> Unit
) {
    val iconVector: ImageVector = when (game) {
        GameType.GRID_CHALLENGE -> Icons.Default.Grid4x4
        GameType.MOTION_CHALLENGE -> Icons.Default.OpenWith
        GameType.INDUCTIVE_LOGIC -> Icons.Default.Category
        GameType.DEDUCTIVE_LOGIC -> Icons.Default.GridView
        GameType.NUM_BUBBLES -> Icons.Default.BubbleChart
        GameType.SHORT_CUTS -> Icons.Default.AltRoute
        GameType.RESEMBLE -> Icons.Default.RotateRight
        GameType.TALLY_UP -> Icons.Default.CompareArrows
        GameType.NON_VERBAL_REASONING -> Icons.Default.Flip
        GameType.CRYPT_ARITHMETIC -> Icons.Default.Pin
    }

    val cardColor = when (game) {
        GameType.GRID_CHALLENGE, GameType.MOTION_CHALLENGE -> Color(0xFF0F766E)
        GameType.INDUCTIVE_LOGIC, GameType.DEDUCTIVE_LOGIC -> Color(0xFF4338CA)
        GameType.NUM_BUBBLES, GameType.TALLY_UP -> Color(0xFFC2410C)
        GameType.SHORT_CUTS, GameType.RESEMBLE -> Color(0xFF15803D)
        else -> Color(0xFF6B21A8)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(cardColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = game.title,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = game.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = game.subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    AssistChip(
                        onClick = {},
                        label = { Text(game.durationText, fontSize = 10.sp) },
                        leadingIcon = { Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(12.dp)) },
                        modifier = Modifier.height(24.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    AssistChip(
                        onClick = {},
                        label = { Text(game.roundsText, fontSize = 10.sp) },
                        modifier = Modifier.height(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                if (lastScore != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${lastScore.score}/${lastScore.maxScore}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Icon(
                    imageVector = Icons.Default.ArrowForwardIos,
                    contentDescription = "Start",
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
