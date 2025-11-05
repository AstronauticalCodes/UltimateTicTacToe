package com.example.ultimatetictactoe.domain.model

data class Game(
    val board: List<List<Player?>> = List(9) { List(9) { null } },
    val currentPlayer: Player = Player.X,
    val activeSmallBoard: Int? = null,
    val xScore: Int = 0,
    val oScore: Int = 0,
    val winningLines: List<List<WinningLine>> = List(9) { emptyList() }
)

enum class Player {
    X, O
}

data class WinningLine(
    val startCell: Int,
    val endCell: Int
)