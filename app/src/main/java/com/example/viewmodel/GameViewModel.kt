package com.example.viewmodel

import androidx.lifecycle.ViewModel
import com.example.model.GameScore
import com.example.model.GameType
import com.example.model.ScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameViewModel : ViewModel() {

    private val _screenState = MutableStateFlow(ScreenState.MENU)
    val screenState: StateFlow<ScreenState> = _screenState.asStateFlow()

    private val _selectedGame = MutableStateFlow<GameType?>(null)
    val selectedGame: StateFlow<GameType?> = _selectedGame.asStateFlow()

    private val _currentScore = MutableStateFlow(0)
    val currentScore: StateFlow<Int> = _currentScore.asStateFlow()

    private val _totalRoundsCompleted = MutableStateFlow(0)
    val totalRoundsCompleted: StateFlow<Int> = _totalRoundsCompleted.asStateFlow()

    private val _gameHistory = MutableStateFlow<List<GameScore>>(emptyList())
    val gameHistory: StateFlow<List<GameScore>> = _gameHistory.asStateFlow()

    fun selectGame(game: GameType) {
        _selectedGame.value = game
        _screenState.value = ScreenState.INSTRUCTIONS
    }

    fun startGame() {
        _currentScore.value = 0
        _totalRoundsCompleted.value = 0
        _screenState.value = ScreenState.PLAYING
    }

    fun addPoints(points: Int) {
        _currentScore.value += points
    }

    fun incrementRound() {
        _totalRoundsCompleted.value += 1
    }

    fun finishGame(finalScore: Int, totalRounds: Int, maxPossibleScore: Int) {
        val game = _selectedGame.value ?: return
        val record = GameScore(
            gameType = game,
            score = finalScore,
            totalRounds = totalRounds,
            maxScore = maxPossibleScore
        )
        _gameHistory.value = _gameHistory.value + record
        _currentScore.value = finalScore
        _totalRoundsCompleted.value = totalRounds
        _screenState.value = ScreenState.RESULT
    }

    fun returnToMenu() {
        _screenState.value = ScreenState.MENU
        _selectedGame.value = null
    }

    fun retryGame() {
        _selectedGame.value?.let {
            selectGame(it)
        } ?: returnToMenu()
    }
}
