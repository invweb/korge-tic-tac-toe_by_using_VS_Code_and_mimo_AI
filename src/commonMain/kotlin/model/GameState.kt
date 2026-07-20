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

object GameState {
    val score = Score()
    val history = mutableListOf<HistoryEntry>()
    val settings = GameSettings()

    fun recordWin(winner: String, boardSize: Int, source: String) {
        if (winner == "X") score.xWins++ else score.oWins++
        history.add(HistoryEntry(winner, boardSize, source))
    }

    fun recordDraw(boardSize: Int, source: String) {
        score.draws++
        history.add(HistoryEntry("draw", boardSize, source))
    }

    fun clearAll() {
        score.xWins = 0
        score.oWins = 0
        score.draws = 0
        history.clear()
    }
}
