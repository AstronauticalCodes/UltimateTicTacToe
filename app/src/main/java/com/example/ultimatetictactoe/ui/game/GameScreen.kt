package com.example.ultimatetictactoe.ui.game

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ultimatetictactoe.domain.model.Game
import com.example.ultimatetictactoe.domain.model.Player
import com.example.ultimatetictactoe.domain.model.WinningLine
import com.example.ultimatetictactoe.ui.theme.UltimateTicTacToeTheme

@Composable
fun GameScreen(gameViewModel: GameViewModel = viewModel()) {
    val game by gameViewModel.gameState.collectAsState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .navigationBarsPadding()
            .background(color = Color.White)
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Scoreboard(game.xScore, game.oScore, game.currentPlayer)
        UltimateTicTacToeBoard(
            game = game,
            onCellClicked = gameViewModel::onCellClicked
        )
    }
}

@Composable
fun Scoreboard(xScore: Int, oScore: Int, currentPlayer: Player) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Player O: $oScore",
            fontSize = 20.sp,
            fontWeight = if (currentPlayer == Player.O) FontWeight.Bold else FontWeight.Normal,
            color = Color.Black,
        )
        Text(
            text = "Player X: $xScore",
            fontSize = 20.sp,
            color = Color.Black,
            fontWeight = if (currentPlayer == Player.X) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun UltimateTicTacToeBoard(game: Game, onCellClicked: (Int, Int) -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.aspectRatio(1f).padding(8.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            for (i in 0..2) {
                Row(modifier = Modifier.weight(1f)) {
                    for (j in 0..2) {
                        val smallBoardIndex = i * 3 + j
                        val isActive = game.activeSmallBoard == null || game.activeSmallBoard == smallBoardIndex
                        SmallBoard(
                            board = game.board[smallBoardIndex],
                            winningLines = game.winningLines[smallBoardIndex],
                            onCellClicked = { cellIndex -> onCellClicked(smallBoardIndex, cellIndex) },
                            modifier = Modifier.weight(1f),
                            isActive = isActive
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SmallBoard(board: List<Player?>, winningLines: List<WinningLine>, onCellClicked: (Int) -> Unit, modifier: Modifier = Modifier, isActive: Boolean) {
    val borderColor = if (isActive) Color.Blue else Color.Black
    val borderWidth by animateDpAsState(
        targetValue = if (isActive) 3.dp else 2.dp,
        animationSpec = tween(300)
    )

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .border(borderWidth, borderColor)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            winningLines.forEach { line ->
                val startCol = line.startCell % 3
                val startRow = line.startCell / 3
                val endCol = line.endCell % 3
                val endRow = line.endCell / 3

                val startX = (startCol + 0.5f) * size.width / 3f
                val startY = (startRow + 0.5f) * size.height / 3f
                val endX = (endCol + 0.5f) * size.width / 3f
                val endY = (endRow + 0.5f) * size.height / 3f

                drawLine(
                    color = Color.Red,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 8f,
                    cap = StrokeCap.Round
                )
            }
        }
        Column(modifier = Modifier.fillMaxSize()) {
            for (i in 0..2) {
                Row(modifier = Modifier.weight(1f)) {
                    for (j in 0..2) {
                        val cellIndex = i * 3 + j
                        Cell(
                            player = board.getOrNull(cellIndex),
                            onClick = { onCellClicked(cellIndex) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Cell(player: Player?, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(1.dp)
            .border(1.dp, Color.Gray)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (player != null) {
            Text(
                text = player.name,
                color = Color.Gray,
                fontSize = 24.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    UltimateTicTacToeTheme {
        val game = Game(
            board = List(9) { smallBoardIndex ->
                when (smallBoardIndex) {
                    0 -> listOf(Player.X, Player.X, Player.X, null, null, null, null, null, null) // Row win
                    1 -> listOf(Player.O, null, null, Player.O, null, null, Player.O, null, null) // Col win
                    4 -> listOf(Player.X, null, null, null, Player.X, null, null, null, Player.X) // Diag win
                    else -> List(9) { null }
                }
            },
            activeSmallBoard = 2,
            xScore = 2,
            oScore = 1,
            winningLines = List(9) { smallBoardIndex ->
                 when (smallBoardIndex) {
                    0 -> listOf(WinningLine(0, 2))
                    1 -> listOf(WinningLine(0, 6))
                    4 -> listOf(WinningLine(0, 8))
                    else -> emptyList()
                }
            }
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Scoreboard(game.xScore, game.oScore, game.currentPlayer)
            UltimateTicTacToeBoard(game = game, onCellClicked = { _, _ -> })
        }
    }
}