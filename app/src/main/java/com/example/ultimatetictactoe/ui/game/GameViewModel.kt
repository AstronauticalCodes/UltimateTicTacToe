package com.example.ultimatetictactoe.ui.game

import androidx.lifecycle.ViewModel
import com.example.ultimatetictactoe.domain.model.Game
import com.example.ultimatetictactoe.domain.model.Player
import com.example.ultimatetictactoe.domain.model.WinningLine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class GameViewModel : ViewModel() {

    private val _gameState = MutableStateFlow(Game())
    val gameState: StateFlow<Game> = _gameState

    fun onCellClicked(smallBoardIndex: Int, cellIndex: Int) {
        _gameState.update { currentState ->
            // Game logic validation
            if (currentState.board[smallBoardIndex][cellIndex] != null) {
                return@update currentState // Cell is already taken
            }
            if (currentState.activeSmallBoard != null && currentState.activeSmallBoard != smallBoardIndex) {
                return@update currentState // Not the active board
            }

            // Update board
            val newBoard = currentState.board.map { it.toMutableList() }.toMutableList()
            val currentPlayer = currentState.currentPlayer
            newBoard[smallBoardIndex][cellIndex] = currentPlayer

            // Check for new streaks and update score
            val (newStreaks, updatedLines) = checkForNewStreaks(
                smallBoard = newBoard[smallBoardIndex],
                existingLines = currentState.winningLines[smallBoardIndex],
                player = currentPlayer
            )

            val newWinningLines = currentState.winningLines.toMutableList()
            newWinningLines[smallBoardIndex] = updatedLines

            val newXScore = if (currentPlayer == Player.X) currentState.xScore + newStreaks else currentState.xScore
            val newOScore = if (currentPlayer == Player.O) currentState.oScore + newStreaks else currentState.oScore

            // Determine next active board
            val isNextBoardFull = newBoard[cellIndex].all { it != null }
            val nextActiveBoard = if (isNextBoardFull) null else cellIndex

            // Update game state
            currentState.copy(
                board = newBoard,
                currentPlayer = if (currentPlayer == Player.X) Player.O else Player.X,
                activeSmallBoard = nextActiveBoard,
                xScore = newXScore,
                oScore = newOScore,
                winningLines = newWinningLines
            )
        }
    }

    private fun checkForNewStreaks(
        smallBoard: List<Player?>, 
        existingLines: List<WinningLine>,
        player: Player
    ): Pair<Int, List<WinningLine>> {
        val winningCombinations = listOf(
            // Rows
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8),
            // Columns
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8),
            // Diagonals
            listOf(0, 4, 8), listOf(2, 4, 6)
        )

        var newStreaksCount = 0
        val allLines = existingLines.toMutableList()

        for (combination in winningCombinations) {
            val (c1, c2, c3) = combination
            if (smallBoard[c1] == player && smallBoard[c2] == player && smallBoard[c3] == player) {
                val newLine = WinningLine(c1, c3)
                // Check if this line is already counted
                if (!allLines.contains(newLine)) {
                    allLines.add(newLine)
                    newStreaksCount++
                }
            }
        }
        return Pair(newStreaksCount, allLines)
    }
}