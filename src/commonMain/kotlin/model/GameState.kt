package model

data class GameSettings(
    var firstPlayer: String = "X",
    var themeIndex: Int = 0,
    var showGrid: Boolean = true,
    var langIndex: Int = 0,
    var boardSize: Int = 3,
    var timerSeconds: Int = 0,
    var aiEnabled: Boolean = false
)

data class Score(var xWins: Int = 0, var oWins: Int = 0, var draws: Int = 0)

data class HistoryEntry(val winner: String, val boardSize: Int, val timestamp: String)

val score = Score()
val history = mutableListOf<HistoryEntry>()
val settings = GameSettings()
