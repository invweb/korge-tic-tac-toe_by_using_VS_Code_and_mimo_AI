package model

import korlibs.io.file.std.rootLocalVfs
import korlibs.io.file.VfsFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class GameSettings(
    var firstPlayer: String = "X",
    var themeIndex: Int = 0,
    var showGrid: Boolean = true,
    var langIndex: Int = 0,
    var boardSize: Int = 3,
    var timerSeconds: Int = 0,
    var aiEnabled: Boolean = false,
    var darkTheme: Boolean = true,
    var aiDifficulty: Int = 2
)

data class Score(var xWins: Int = 0, var oWins: Int = 0, var draws: Int = 0)

data class HistoryEntry(val winner: String, val boardSize: Int, val timestamp: String)

object GameState {
    val score = Score()
    val history = mutableListOf<HistoryEntry>()
    val settings = GameSettings()

    private val saveFile: VfsFile get() = rootLocalVfs["tictactoe_save.txt"]
    private val scope = CoroutineScope(Dispatchers.Default)

    fun recordWin(winner: String, boardSize: Int, source: String) {
        if (winner == "X") score.xWins++ else score.oWins++
        history.add(HistoryEntry(winner, boardSize, source))
        save()
    }

    fun recordDraw(boardSize: Int, source: String) {
        score.draws++
        history.add(HistoryEntry("draw", boardSize, source))
        save()
    }

    fun clearAll() {
        score.xWins = 0
        score.oWins = 0
        score.draws = 0
        history.clear()
        save()
    }

    fun updateSettings(block: GameSettings.() -> Unit) {
        settings.block()
        save()
    }

    suspend fun load() {
        try {
            if (!saveFile.exists()) return
            val content = saveFile.readString()
            val lines = content.lines().filter { it.isNotEmpty() }
            if (lines.size < 3) return
            val scoreParts = lines[0].split(",")
            if (scoreParts.size >= 3) {
                score.xWins = scoreParts[0].toIntOrNull() ?: 0
                score.oWins = scoreParts[1].toIntOrNull() ?: 0
                score.draws = scoreParts[2].toIntOrNull() ?: 0
            }
            val settingsParts = lines[1].split(",")
            if (settingsParts.size >= 9) {
                settings.firstPlayer = settingsParts[0].ifEmpty { "X" }
                settings.themeIndex = settingsParts[1].toIntOrNull() ?: 0
                settings.showGrid = settingsParts[2].toBooleanStrictOrNull() ?: true
                settings.langIndex = settingsParts[3].toIntOrNull() ?: 0
                settings.boardSize = settingsParts[4].toIntOrNull() ?: 3
                settings.timerSeconds = settingsParts[5].toIntOrNull() ?: 0
                settings.aiEnabled = settingsParts[6].toBooleanStrictOrNull() ?: false
                settings.darkTheme = settingsParts[7].toBooleanStrictOrNull() ?: true
                settings.aiDifficulty = settingsParts[8].toIntOrNull() ?: 2
            }
            history.clear()
            for (i in 2 until lines.size) {
                val parts = lines[i].split(",")
                if (parts.size >= 3) {
                    history.add(HistoryEntry(parts[0], parts[1].toIntOrNull() ?: 3, parts[2]))
                }
            }
        } catch (_: Exception) {
        }
    }

    private fun save() {
        scope.launch {
            try {
                val sb = StringBuilder()
                sb.appendLine("${score.xWins},${score.oWins},${score.draws}")
                sb.appendLine("${settings.firstPlayer},${settings.themeIndex},${settings.showGrid},${settings.langIndex},${settings.boardSize},${settings.timerSeconds},${settings.aiEnabled},${settings.darkTheme},${settings.aiDifficulty}")
                for (entry in history) {
                    sb.appendLine("${entry.winner},${entry.boardSize},${entry.timestamp}")
                }
                saveFile.writeString(sb.toString())
            } catch (_: Exception) {
            }
        }
    }
}
