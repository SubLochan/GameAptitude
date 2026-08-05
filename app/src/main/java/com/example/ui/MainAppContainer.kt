package com.example.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.GameType
import com.example.model.ScreenState
import com.example.ui.games.*
import com.example.viewmodel.GameViewModel

@Composable
fun MainAppContainer(
    gameViewModel: GameViewModel = viewModel()
) {
    val screenState by gameViewModel.screenState.collectAsState()
    val selectedGame by gameViewModel.selectedGame.collectAsState()
    val currentScore by gameViewModel.currentScore.collectAsState()
    val roundsCompleted by gameViewModel.totalRoundsCompleted.collectAsState()
    val gameHistory by gameViewModel.gameHistory.collectAsState()

    when (screenState) {
        ScreenState.MENU -> {
            AssessmentMenuScreen(
                gameHistory = gameHistory,
                onGameSelected = { gameViewModel.selectGame(it) }
            )
        }

        ScreenState.INSTRUCTIONS -> {
            selectedGame?.let { game ->
                InstructionScreen(
                    game = game,
                    onBack = { gameViewModel.returnToMenu() },
                    onStartGame = { gameViewModel.startGame() }
                )
            } ?: gameViewModel.returnToMenu()
        }

        ScreenState.PLAYING -> {
            selectedGame?.let { game ->
                when (game) {
                    GameType.GRID_CHALLENGE -> GridChallengeScreen(
                        onFinishGame = { score, rounds, maxScore ->
                            gameViewModel.finishGame(score, rounds, maxScore)
                        },
                        onQuit = { gameViewModel.returnToMenu() }
                    )
                    GameType.MOTION_CHALLENGE -> MotionChallengeScreen(
                        onFinishGame = { score, rounds, maxScore ->
                            gameViewModel.finishGame(score, rounds, maxScore)
                        },
                        onQuit = { gameViewModel.returnToMenu() }
                    )
                    GameType.INDUCTIVE_LOGIC -> InductiveLogicScreen(
                        onFinishGame = { score, rounds, maxScore ->
                            gameViewModel.finishGame(score, rounds, maxScore)
                        },
                        onQuit = { gameViewModel.returnToMenu() }
                    )
                    GameType.DEDUCTIVE_LOGIC -> DeductiveLogicScreen(
                        onFinishGame = { score, rounds, maxScore ->
                            gameViewModel.finishGame(score, rounds, maxScore)
                        },
                        onQuit = { gameViewModel.returnToMenu() }
                    )
                    GameType.NUM_BUBBLES -> NumBubbleScreen(
                        onFinishGame = { score, rounds, maxScore ->
                            gameViewModel.finishGame(score, rounds, maxScore)
                        },
                        onQuit = { gameViewModel.returnToMenu() }
                    )
                    GameType.SHORT_CUTS -> ShortCutsScreen(
                        onFinishGame = { score, rounds, maxScore ->
                            gameViewModel.finishGame(score, rounds, maxScore)
                        },
                        onQuit = { gameViewModel.returnToMenu() }
                    )
                    GameType.RESEMBLE -> ResembleScreen(
                        onFinishGame = { score, rounds, maxScore ->
                            gameViewModel.finishGame(score, rounds, maxScore)
                        },
                        onQuit = { gameViewModel.returnToMenu() }
                    )
                    GameType.TALLY_UP -> TallyUpScreen(
                        onFinishGame = { score, rounds, maxScore ->
                            gameViewModel.finishGame(score, rounds, maxScore)
                        },
                        onQuit = { gameViewModel.returnToMenu() }
                    )
                    GameType.NON_VERBAL_REASONING -> NonVerbalScreen(
                        onFinishGame = { score, rounds, maxScore ->
                            gameViewModel.finishGame(score, rounds, maxScore)
                        },
                        onQuit = { gameViewModel.returnToMenu() }
                    )
                    GameType.CRYPT_ARITHMETIC -> CryptArithmeticScreen(
                        onFinishGame = { score, rounds, maxScore ->
                            gameViewModel.finishGame(score, rounds, maxScore)
                        },
                        onQuit = { gameViewModel.returnToMenu() }
                    )
                }
            } ?: gameViewModel.returnToMenu()
        }

        ScreenState.RESULT -> {
            selectedGame?.let { game ->
                ResultScreen(
                    game = game,
                    score = currentScore,
                    roundsCompleted = roundsCompleted,
                    onRetry = { gameViewModel.retryGame() },
                    onReturnMenu = { gameViewModel.returnToMenu() }
                )
            } ?: gameViewModel.returnToMenu()
        }
    }
}
