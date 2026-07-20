import korlibs.korge.Korge
import korlibs.korge.view.*
import korlibs.korge.input.*
import korlibs.image.color.*
import korlibs.math.geom.*
import korlibs.math.geom.Angle
import korlibs.time.*

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

val themesDark = listOf(Triple(RGBA(0x1a, 0x1a, 0x2e), RGBA(0x16, 0x21, 0x3e), ""), Triple(RGBA(0x0a, 0x1a, 0x2e), RGBA(0x0a, 0x2a, 0x4e), ""), Triple(RGBA(0x0a, 0x2e, 0x1a), RGBA(0x0a, 0x3e, 0x16), ""))
val themesLight = listOf(Triple(RGBA(0xf0, 0xf0, 0xf5), RGBA(0xd0, 0xd0, 0xe0), ""), Triple(RGBA(0xe8, 0xf0, 0xf8), RGBA(0xc0, 0xd8, 0xf0), ""), Triple(RGBA(0xe8, 0xf8, 0xf0), RGBA(0xc0, 0xf0, 0xd0), ""))
var isDarkTheme = true
fun currentThemes() = if (isDarkTheme) themesDark else themesLight
fun accentColor() = if (isDarkTheme) RGBA(0xe9, 0x45, 0x60) else RGBA(0xc0, 0x30, 0x50)

val themesRu = listOf("Тёмная", "Синяя", "Зелёная")
val themesEn = listOf("Dark", "Blue", "Green")
val themesDe = listOf("Dunkel", "Blau", "Grün")
val allThemes = listOf(themesRu, themesEn, themesDe)
fun themeName(index: Int) = allThemes[settings.langIndex][index]

val langNames = listOf("Русский", "English", "Deutsch")

data class Strings(
    val tapToStart: String, val newGame: String, val settings: String, val exit: String,
    val back: String, val firstTurn: String, val colorTheme: String, val grid: String,
    val on: String, val off: String, val turn: String, val winner: String, val draw: String,
    val restart: String, val menu: String, val language: String, val score: String,
    val boardSize: String, val timer: String, val seconds5: String,
    val seconds15: String, val seconds30: String, val ai: String, val history: String,
    val clear: String, val darkMode: String, val light: String, val dark: String,
    val noHistory: String, val timerExpired: String
)

val stringsRu = Strings("Нажмите для начала", "Новая игра", "Настройки", "Выход", "Назад", "Первый ход", "Цветовая тема", "Сетка", "Вкл", "Выкл", "Ход", "Победил", "Ничья!", "Заново", "Меню", "Язык", "Счёт", "Размер поля", "Таймер", "5 сек", "15 сек", "30 сек", "ИИ-противник", "История", "Очистить", "Тёмный режим", "Светлая", "Тёмная", "Нет истории", "Время вышло!")
val stringsEn = Strings("Tap to start", "New Game", "Settings", "Exit", "Back", "First turn", "Color theme", "Grid", "On", "Off", "Turn", "Winner", "Draw!", "Restart", "Menu", "Language", "Score", "Board size", "Timer", "5 sec", "15 sec", "30 sec", "AI opponent", "History", "Clear", "Dark mode", "Light", "Dark", "No history", "Time's up!")
val stringsDe = Strings("Tippen zum Starten", "Neues Spiel", "Einstellungen", "Beenden", "Zurück", "Erster Zug", "Farbschema", "Gitter", "Ein", "Aus", "Zug", "Gewinner", "Unentschieden!", "Neustart", "Menü", "Sprache", "Punkte", "Spielfeld", "Timer", "5 Sek", "15 Sek", "30 Sek", "KI-Gegner", "Verlauf", "Löschen", "Dunkler Modus", "Hell", "Dunkel", "Kein Verlauf", "Zeit abgelaufen!")

val allStrings = listOf(stringsRu, stringsEn, stringsDe)
fun S(): Strings = allStrings[settings.langIndex]

val settings = GameSettings()
val langLabels = mutableListOf<Pair<Text, () -> String>>()
fun updateAllLabels() { for ((view, getter) in langLabels) view.text = getter() }
var refreshHistoryScreen: () -> Unit = {}

suspend fun main() = Korge(windowSize = Size(600, 700)) {
    langLabels.clear()
    val splashContainer = container {}
    val menuContainer = container { visible = false }
    val gameContainer = container { visible = false }
    val settingsContainer = container { visible = false }
    val historyContainer = container { visible = false }

    buildSplash(splashContainer, menuContainer)
    buildMenu(menuContainer, gameContainer, settingsContainer, historyContainer)
    buildGame(gameContainer, menuContainer)
    buildSettings(settingsContainer, menuContainer)
    buildHistory(historyContainer, menuContainer)
}

fun labeledText(parent: Container, getter: () -> String, textSize: Double = 28.0, color: RGBA = Colors.WHITE, x: Double = 0.0, y: Double = 0.0): Text {
    val t = parent.text(getter(), textSize = textSize, color = color) { position(x, y) }
    langLabels.add(t to getter)
    return t
}

fun Container.buildSplash(splashContainer: Container, menuContainer: Container) {
    splashContainer.apply {
        solidRect(600.0, 700.0, RGBA(0x0a, 0x0a, 0x1a))
        solidRect(600.0, 4.0, RGBA(0xe9, 0x45, 0x60)) { position(0.0, 200.0) }
        solidRect(600.0, 4.0, RGBA(0x0f, 0x34, 0x60)) { position(0.0, 496.0) }
        val xDecor = text("X", textSize = 120.0, color = RGBA(0xe9, 0x45, 0x60, 0x40)) { position(50.0, 250.0) }
        val oDecor = text("O", textSize = 120.0, color = RGBA(0x0f, 0x34, 0x60, 0x40)) { position(420.0, 320.0) }
        text("X", textSize = 60.0, color = RGBA(0xe9, 0x45, 0x60, 0x25)) { position(480.0, 230.0) }
        text("O", textSize = 60.0, color = RGBA(0x0f, 0x34, 0x60, 0x25)) { position(80.0, 380.0) }
        val titleText = text("Tic-Tac-Toe", textSize = 56.0, color = RGBA(0xe9, 0x45, 0x60)) { position(125.0, 310.0); alpha = 0.0 }
        val subtitleText = text("KorGE Edition", textSize = 22.0, color = Colors.WHITE) { position(230.0, 380.0); alpha = 0.0 }
        val tapText = labeledText(this, { S().tapToStart }, textSize = 18.0, color = RGBA(0x80, 0x80, 0x80), x = 190.0, y = 440.0)
        tapText.alpha = 0.0
        var time = 0.0
        addUpdater {
            time += it.seconds
            titleText.alpha = (time * 2.0).coerceIn(0.0, 1.0)
            subtitleText.alpha = ((time - 0.5) * 2.0).coerceIn(0.0, 1.0)
            val t3 = ((time - 1.2) * 2.0).coerceIn(0.0, 1.0)
            tapText.alpha = t3 * (kotlin.math.sin(time * 3.0) * 0.3 + 0.7)
            xDecor.rotation = Angle.fromRadians(kotlin.math.sin(time * 0.5))
            oDecor.rotation = Angle.fromRadians(kotlin.math.cos(time * 0.5))
        }
        onClick { splashContainer.visible = false; menuContainer.visible = true }
    }
}

fun Container.buildMenu(menuContainer: Container, gameContainer: Container, settingsContainer: Container, historyContainer: Container) {
    menuContainer.apply {
        solidRect(600.0, 700.0, RGBA(0x1a, 0x1a, 0x2e))
        text("Tic-Tac-Toe", textSize = 48.0, color = RGBA(0xe9, 0x45, 0x60)) { position(165.0, 60.0) }
        text("X", textSize = 60.0, color = RGBA(0xe9, 0x45, 0x60)) { position(130.0, 140.0) }
        text("O", textSize = 60.0, color = RGBA(0x0f, 0x34, 0x60)) { position(410.0, 140.0) }
        labeledText(this, { "${S().score}: X=${score.xWins}  O=${score.oWins}  ${S().draw}=${score.draws}" }, textSize = 18.0, color = RGBA(0xa0, 0xa0, 0xb0), x = 135.0, y = 220.0)

        val newGameBg = solidRect(260.0, 50.0, RGBA(0xe9, 0x45, 0x60)) { position(170.0, 280.0) }
        labeledText(this, { S().newGame }, textSize = 26.0, x = 230.0, y = 289.0)
        val settingsBg = solidRect(260.0, 50.0, RGBA(0x0f, 0x34, 0x60)) { position(170.0, 345.0) }
        labeledText(this, { S().settings }, textSize = 26.0, x = 233.0, y = 354.0)
        val historyBg = solidRect(260.0, 50.0, RGBA(0x0f, 0x34, 0x60)) { position(170.0, 410.0) }
        labeledText(this, { S().history }, textSize = 26.0, x = 245.0, y = 419.0)
        val exitBg = solidRect(260.0, 50.0, RGBA(0x40, 0x40, 0x50)) { position(170.0, 475.0) }
        labeledText(this, { S().exit }, textSize = 26.0, x = 258.0, y = 484.0)

        newGameBg.onOver { newGameBg.color = RGBA(0xff, 0x5a, 0x7a) }
        newGameBg.onOut { newGameBg.color = RGBA(0xe9, 0x45, 0x60) }
        settingsBg.onOver { settingsBg.color = RGBA(0x1a, 0x50, 0x80) }
        settingsBg.onOut { settingsBg.color = RGBA(0x0f, 0x34, 0x60) }
        historyBg.onOver { historyBg.color = RGBA(0x1a, 0x50, 0x80) }
        historyBg.onOut { historyBg.color = RGBA(0x0f, 0x34, 0x60) }
        exitBg.onOver { exitBg.color = RGBA(0x60, 0x60, 0x70) }
        exitBg.onOut { exitBg.color = RGBA(0x40, 0x40, 0x50) }

        newGameBg.onClick { menuContainer.visible = false; gameContainer.visible = true }
        settingsBg.onClick { menuContainer.visible = false; settingsContainer.visible = true }
        historyBg.onClick { menuContainer.visible = false; refreshHistoryScreen(); historyContainer.visible = true }
        exitBg.onClick { kotlin.system.exitProcess(0) }
    }
}

fun Container.buildSettings(settingsContainer: Container, menuContainer: Container) {
    settingsContainer.apply {
        solidRect(600.0, 700.0, RGBA(0x1a, 0x1a, 0x2e))
        labeledText(this, { S().settings }, textSize = 38.0, color = accentColor(), x = 195.0, y = 20.0)

        labeledText(this, { S().language }, textSize = 22.0, x = 210.0, y = 80.0)
        val langButtons = mutableListOf<SolidRect>()
        val langTexts = listOf("RU", "EN", "DE")
        for (i in langNames.indices) {
            val bx = 140.0 + i * 110.0
            val btn = solidRect(90.0, 40.0, RGBA(0x30, 0x30, 0x40)) { position(bx, 110.0) }
            text(langTexts[i], textSize = 18.0, color = Colors.WHITE) { position(bx + 22.0, 117.0) }
            text(langNames[i], textSize = 12.0, color = RGBA(0x80, 0x80, 0x80)) { position(bx + 5.0, 153.0) }
            langButtons.add(btn)
        }
        fun updateLangButtons() { for (i in langButtons.indices) langButtons[i].color = if (i == settings.langIndex) RGBA(0xe9, 0x45, 0x60) else RGBA(0x30, 0x30, 0x40) }
        updateLangButtons()
        for (i in langButtons.indices) langButtons[i].onClick { settings.langIndex = i; updateLangButtons(); updateAllLabels() }

        labeledText(this, { S().darkMode }, textSize = 22.0, x = 210.0, y = 185.0)
        val darkBtn = solidRect(110.0, 40.0, RGBA(0x1a, 0x1a, 0x2e)) { position(180.0, 215.0) }
        labeledText(this, { S().dark }, textSize = 16.0, x = 210.0, y = 222.0)
        val lightBtn = solidRect(110.0, 40.0, RGBA(0xc0, 0xc0, 0xd0)) { position(310.0, 215.0) }
        labeledText(this, { S().light }, textSize = 16.0, color = RGBA(0x20, 0x20, 0x30), x = 345.0, y = 222.0)
        fun updateThemeModeButtons() {
            darkBtn.color = if (isDarkTheme) RGBA(0xe9, 0x45, 0x60) else RGBA(0x1a, 0x1a, 0x2e)
            lightBtn.color = if (!isDarkTheme) RGBA(0xe9, 0x45, 0x60) else RGBA(0xc0, 0xc0, 0xd0)
        }
        updateThemeModeButtons()
        darkBtn.onClick { isDarkTheme = true; updateThemeModeButtons(); updateAllLabels() }
        lightBtn.onClick { isDarkTheme = false; updateThemeModeButtons(); updateAllLabels() }

        labeledText(this, { S().firstTurn }, textSize = 22.0, x = 210.0, y = 275.0)
        val xBtn = solidRect(90.0, 45.0, RGBA(0xe9, 0x45, 0x60)) { position(200.0, 305.0) }
        text("X", textSize = 28.0, color = Colors.WHITE) { position(228.0, 312.0) }
        val oBtn = solidRect(90.0, 45.0, RGBA(0x30, 0x30, 0x40)) { position(310.0, 305.0) }
        text("O", textSize = 28.0, color = Colors.WHITE) { position(338.0, 312.0) }
        fun updateFP() { xBtn.color = if (settings.firstPlayer == "X") RGBA(0xe9, 0x45, 0x60) else RGBA(0x30, 0x30, 0x40); oBtn.color = if (settings.firstPlayer == "O") RGBA(0x0f, 0x34, 0x60) else RGBA(0x30, 0x30, 0x40) }
        updateFP()
        xBtn.onClick { settings.firstPlayer = "X"; updateFP() }
        oBtn.onClick { settings.firstPlayer = "O"; updateFP() }

        labeledText(this, { S().boardSize }, textSize = 22.0, x = 210.0, y = 365.0)
        val sizeButtons = mutableListOf<SolidRect>()
        for (sz in 3..5) {
            val bx = 190.0 + (sz - 3) * 90.0
            val btn = solidRect(75.0, 40.0, if (settings.boardSize == sz) RGBA(0xe9, 0x45, 0x60) else RGBA(0x30, 0x30, 0x40)) { position(bx, 395.0) }
            text("${sz}x${sz}", textSize = 18.0, color = Colors.WHITE) { position(bx + 14.0, 402.0) }
            sizeButtons.add(btn)
        }
        fun updateSizeButtons() { for (i in 0..2) sizeButtons[i].color = if (settings.boardSize == i + 3) RGBA(0xe9, 0x45, 0x60) else RGBA(0x30, 0x30, 0x40) }
        for (i in 0..2) sizeButtons[i].onClick { settings.boardSize = i + 3; updateSizeButtons() }

        labeledText(this, { S().timer }, textSize = 22.0, x = 210.0, y = 450.0)
        val timerOpts = listOf(0, 5, 15, 30)
        val timerLabels = listOf({ S().off }, { S().seconds5 }, { S().seconds15 }, { S().seconds30 })
        val timerButtons = mutableListOf<SolidRect>()
        for (i in timerOpts.indices) {
            val bx = 140.0 + i * 110.0
            val btn = solidRect(95.0, 35.0, RGBA(0x30, 0x30, 0x40)) { position(bx, 480.0) }
            val idx = i
            labeledText(this, timerLabels[idx], textSize = 14.0, x = bx + 8.0, y = 487.0)
            timerButtons.add(btn)
        }
        fun updateTimerButtons() { for (i in timerOpts.indices) timerButtons[i].color = if (settings.timerSeconds == timerOpts[i]) RGBA(0xe9, 0x45, 0x60) else RGBA(0x30, 0x30, 0x40) }
        updateTimerButtons()
        for (i in timerOpts.indices) timerButtons[i].onClick { settings.timerSeconds = timerOpts[i]; updateTimerButtons(); updateAllLabels() }

        labeledText(this, { S().ai }, textSize = 22.0, x = 150.0, y = 530.0)
        val aiBtn = solidRect(120.0, 40.0, if (settings.aiEnabled) RGBA(0x0f, 0x34, 0x60) else RGBA(0x40, 0x40, 0x50)) { position(400.0, 525.0) }
        labeledText(this, { if (settings.aiEnabled) S().on else S().off }, textSize = 20.0, x = 430.0, y = 533.0)
        aiBtn.onClick { settings.aiEnabled = !settings.aiEnabled; aiBtn.color = if (settings.aiEnabled) RGBA(0x0f, 0x34, 0x60) else RGBA(0x40, 0x40, 0x50); updateAllLabels() }

        val backBg = solidRect(200.0, 45.0, RGBA(0xe9, 0x45, 0x60)) { position(200.0, 600.0) }
        labeledText(this, { S().back }, textSize = 22.0, x = 268.0, y = 609.0)
        backBg.onOver { backBg.color = RGBA(0xff, 0x5a, 0x7a) }
        backBg.onOut { backBg.color = RGBA(0xe9, 0x45, 0x60) }
        backBg.onClick { settingsContainer.visible = false; menuContainer.visible = true }
    }
}

fun Container.buildHistory(historyContainer: Container, menuContainer: Container) {
    historyContainer.apply {
        solidRect(600.0, 700.0, RGBA(0x1a, 0x1a, 0x2e))
        labeledText(this, { S().history }, textSize = 38.0, color = accentColor(), x = 210.0, y = 30.0)

        val listContainer = container { position(0.0, 90.0) }

        fun refreshHistory() {
            listContainer.removeChildren()
            if (history.isEmpty()) {
                listContainer.text(S().noHistory, textSize = 20.0, color = RGBA(0x80, 0x80, 0x80)) { position(210.0, 30.0) }
            } else {
                for ((idx, entry) in history.takeLast(20).reversed().withIndex()) {
                    val y = idx * 35.0
                    val bgColor = if (isDarkTheme) RGBA(0x20, 0x20, 0x35) else RGBA(0xe0, 0xe0, 0xf0)
                    listContainer.solidRect(520.0, 30.0, bgColor) { position(40.0, y) }
                    val label = if (entry.winner == "draw") S().draw else "${S().winner}: ${entry.winner}"
                    listContainer.text("${idx + 1}. ${entry.boardSize}x${entry.boardSize} - $label  (${entry.timestamp})", textSize = 14.0, color = Colors.WHITE) { position(50.0, y + 7.0) }
                }
            }
        }
        refreshHistory()
        refreshHistoryScreen = { refreshHistory() }

        val clearBg = solidRect(200.0, 45.0, RGBA(0x80, 0x30, 0x30)) { position(200.0, 600.0) }
        labeledText(this, { S().clear }, textSize = 22.0, x = 268.0, y = 609.0)
        clearBg.onOver { clearBg.color = RGBA(0xa0, 0x40, 0x40) }
        clearBg.onOut { clearBg.color = RGBA(0x80, 0x30, 0x30) }
        clearBg.onClick { history.clear(); score.xWins = 0; score.oWins = 0; score.draws = 0; refreshHistory(); updateAllLabels() }

        val backBg = solidRect(200.0, 45.0, RGBA(0xe9, 0x45, 0x60)) { position(200.0, 545.0) }
        labeledText(this, { S().back }, textSize = 22.0, x = 268.0, y = 554.0)
        backBg.onOver { backBg.color = RGBA(0xff, 0x5a, 0x7a) }
        backBg.onOut { backBg.color = RGBA(0xe9, 0x45, 0x60) }
        backBg.onClick { historyContainer.visible = false; menuContainer.visible = true }
    }
}

fun minimax(board: Array<Array<String>>, depth: Int, isMax: Boolean, aiMark: String, humanMark: String): Int {
    if (checkWinBoard(board, aiMark)) return 10 - depth
    if (checkWinBoard(board, humanMark)) return depth - 10
    if (board.all { row -> row.all { it.isNotEmpty() } }) return 0

    if (isMax) {
        var best = -100
        for (r in board.indices) for (c in board[r].indices) {
            if (board[r][c].isEmpty()) {
                board[r][c] = aiMark
                best = maxOf(best, minimax(board, depth + 1, false, aiMark, humanMark))
                board[r][c] = ""
            }
        }
        return best
    } else {
        var best = 100
        for (r in board.indices) for (c in board[r].indices) {
            if (board[r][c].isEmpty()) {
                board[r][c] = humanMark
                best = minOf(best, minimax(board, depth + 1, true, aiMark, humanMark))
                board[r][c] = ""
            }
        }
        return best
    }
}

fun checkWinBoard(board: Array<Array<String>>, player: String): Boolean {
    val n = board.size
    for (i in 0 until n) {
        if (board[i].all { it == player }) return true
        if (board.all { it[i] == player }) return true
    }
    if ((0 until n).all { board[it][it] == player }) return true
    if ((0 until n).all { board[it][n - 1 - it] == player }) return true
    return false
}

fun findBestMove(board: Array<Array<String>>, aiMark: String): Pair<Int, Int>? {
    val humanMark = if (aiMark == "X") "O" else "X"
    var bestScore = -100
    var bestMove: Pair<Int, Int>? = null
    for (r in board.indices) for (c in board[r].indices) {
        if (board[r][c].isEmpty()) {
            board[r][c] = aiMark
            val s = minimax(board, 0, false, aiMark, humanMark)
            board[r][c] = ""
            if (s > bestScore) { bestScore = s; bestMove = Pair(r, c) }
        }
    }
    return bestMove
}

fun Container.buildGame(gameContainer: Container, menuContainer: Container) {
    val bg = currentThemes()[settings.themeIndex]
    val bgColor = bg.first
    val cellColor = bg.second
    val accent = accentColor()

    gameContainer.apply {
        solidRect(600.0, 700.0, bgColor)
        text("Tic-Tac-Toe", textSize = 36.0, color = accent) { position(205.0, 10.0) }

        val n = settings.boardSize
        val board = Array(n) { Array(n) { "" } }
        var currentTurn = settings.firstPlayer
        var gameOver = false
        var timerValue = settings.timerSeconds
        val timerActive = settings.timerSeconds > 0
        var lastTimerUpdate = 0.0

        val totalGrid = 450.0
        val cellSize = (totalGrid - (n - 1) * 4.0) / n
        val gridOffsetX = (600.0 - totalGrid) / 2
        val gridOffsetY = 55.0

        labeledText(this, { "X=${score.xWins}  O=${score.oWins}  ${S().draw}=${score.draws}" }, textSize = 16.0, color = RGBA(0xa0, 0xa0, 0xb0), x = 195.0, y = gridOffsetY + totalGrid + 12.0)

        val statusY = gridOffsetY + totalGrid + 35.0
        val statusText = labeledText(this, { "${S().turn}: $currentTurn" }, textSize = 24.0, x = 250.0, y = statusY)

        val timerText = if (timerActive) {
            labeledText(this, { "$timerValue" }, textSize = 22.0, color = RGBA(0xf5, 0xa6, 0x23), x = 500.0, y = statusY)
        } else null

        if (settings.showGrid) {
            for (i in 1 until n) {
                solidRect(3.0, totalGrid, RGBA(0x40, 0x40, 0x60)) { position(gridOffsetX + i * (cellSize + 4.0) - 1.5, gridOffsetY) }
                solidRect(totalGrid, 3.0, RGBA(0x40, 0x40, 0x60)) { position(gridOffsetX, gridOffsetY + i * (cellSize + 4.0) - 1.5) }
            }
        }

        data class CellInfo(val row: Int, val col: Int, val bg: SolidRect, val txt: Text)
        val cells = mutableListOf<CellInfo>()

        for (row in 0 until n) {
            for (col in 0 until n) {
                val cx = gridOffsetX + col * (cellSize + 4.0)
                val cy = gridOffsetY + row * (cellSize + 4.0)
                val cellBg = solidRect(cellSize, cellSize, cellColor) { position(cx, cy) }
                val cellText = text("", textSize = cellSize * 0.45, color = Colors.WHITE) { position(cx + cellSize * 0.3, cy + cellSize * 0.2) }
                cells.add(CellInfo(row, col, cellBg, cellText))
            }
        }

        fun aiMove() {
            if (!settings.aiEnabled || gameOver) return
            val aiMark = currentTurn
            val move = findBestMove(board, aiMark)
            if (move != null) {
                val (r, c) = move
                board[r][c] = aiMark
                val cv = cells.first { it.row == r && it.col == c }
                cv.txt.text = aiMark
                cv.txt.color = if (aiMark == "X") accent else RGBA(0x0f, 0x34, 0x60)

                if (checkWinBoard(board, aiMark)) {
                    statusText.text = "${S().winner}: $aiMark!"
                    statusText.color = accent
                    gameOver = true
                    score.apply { if (aiMark == "X") xWins++ else oWins++ }
                    history.add(HistoryEntry(aiMark, n, "AI"))
                } else if (board.all { row -> row.all { it.isNotEmpty() } }) {
                    statusText.text = S().draw
                    statusText.color = RGBA(0xf5, 0xa6, 0x23)
                    gameOver = true
                    score.draws++
                    history.add(HistoryEntry("draw", n, "AI"))
                } else {
                    currentTurn = if (currentTurn == "X") "O" else "X"
                    statusText.text = "${S().turn}: $currentTurn"
                    timerValue = settings.timerSeconds
                    lastTimerUpdate = 0.0
                }
            }
        }

        for (cell in cells) {
            val cellBg = cell.bg
            val cellText = cell.txt
            val r = cell.row
            val c = cell.col

            cellBg.onClick {
                if (!gameOver && board[r][c].isEmpty() && (!settings.aiEnabled || currentTurn == settings.firstPlayer)) {
                    board[r][c] = currentTurn
                    cellText.text = currentTurn
                    cellText.color = if (currentTurn == "X") accent else RGBA(0x0f, 0x34, 0x60)

                    if (checkWinBoard(board, currentTurn)) {
                        statusText.text = "${S().winner}: $currentTurn!"
                        statusText.color = accent
                        gameOver = true
                        score.apply { if (currentTurn == "X") xWins++ else oWins++ }
                        history.add(HistoryEntry(currentTurn, n, "you"))
                    } else if (board.all { row2 -> row2.all { it.isNotEmpty() } }) {
                        statusText.text = S().draw
                        statusText.color = RGBA(0xf5, 0xa6, 0x23)
                        gameOver = true
                        score.draws++
                        history.add(HistoryEntry("draw", n, "you"))
                    } else {
                        currentTurn = if (currentTurn == "X") "O" else "X"
                        statusText.text = "${S().turn}: $currentTurn"
                        timerValue = settings.timerSeconds
                        lastTimerUpdate = 0.0
                        if (settings.aiEnabled) aiMove()
                    }
                }
            }
            cellBg.onOver { if (!gameOver && board[r][c].isEmpty()) cellBg.color = RGBA(0x2a, 0x2a, 0x5e) }
            cellBg.onOut { if (!gameOver && board[r][c].isEmpty()) cellBg.color = cellColor }
        }

        if (timerActive) {
            addUpdater {
                if (!gameOver && timerActive) {
                    lastTimerUpdate += it.seconds
                    if (lastTimerUpdate >= 1.0) {
                        lastTimerUpdate -= 1.0
                        timerValue--
                        if (timerText != null) timerText.text = "$timerValue"
                        if (timerValue <= 0) {
                            gameOver = true
                            val winner = if (currentTurn == "X") "O" else "X"
                            statusText.text = "${S().timerExpired} ${S().winner}: $winner"
                            statusText.color = RGBA(0xf5, 0xa6, 0x23)
                            score.apply { if (winner == "X") xWins++ else oWins++ }
                            history.add(HistoryEntry(winner, n, "timeout"))
                        }
                    }
                }
            }
        }

        val btnY = gridOffsetY + totalGrid + 65.0
        val restartBg = solidRect(200.0, 45.0, RGBA(0xe9, 0x45, 0x60)) { position(95.0, btnY) }
        labeledText(this, { S().restart }, textSize = 22.0, x = 153.0, y = btnY + 10.0)
        val menuBg = solidRect(200.0, 45.0, RGBA(0x0f, 0x34, 0x60)) { position(305.0, btnY) }
        labeledText(this, { S().menu }, textSize = 22.0, x = 377.0, y = btnY + 10.0)
        menuBg.onOver { menuBg.color = RGBA(0x1a, 0x50, 0x80) }
        menuBg.onOut { menuBg.color = RGBA(0x0f, 0x34, 0x60) }

        fun resetBoard() {
            for (r2 in 0 until n) for (c2 in 0 until n) board[r2][c2] = ""
            currentTurn = settings.firstPlayer
            gameOver = false
            timerValue = settings.timerSeconds
            lastTimerUpdate = 0.0
            updateAllLabels()
            for (cell in cells) { cell.txt.text = ""; cell.bg.color = cellColor }
            if (timerText != null) timerText.text = "$timerValue"
        }

        restartBg.onClick { resetBoard() }
        menuBg.onClick { resetBoard(); gameContainer.visible = false; menuContainer.visible = true }
    }
}

fun checkWin(board: Array<Array<String>>, player: String): Boolean = checkWinBoard(board, player)
