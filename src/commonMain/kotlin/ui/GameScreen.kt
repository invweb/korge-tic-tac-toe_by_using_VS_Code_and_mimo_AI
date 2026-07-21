package ui

import korlibs.image.color.RGBA
import korlibs.image.color.Colors
import korlibs.image.text.TextAlignment
import korlibs.korge.view.*
import korlibs.korge.input.*
import korlibs.time.*
import model.GameState
import logic.findWinLine
import logic.findBestMove
import i18n.*

fun Container.buildGame(gameContainer: Container, menuContainer: Container) {
    val bg = currentThemes()[GameState.settings.themeIndex]
    val bgColor = bg.first
    val cellColor = bg.second
    val accent = accentColor()
    val gameViews = gameContainer.stage!!.views

    gameContainer.apply {
        solidRect(600.0, 800.0, bgColor)
        val modeLabel = if (GameState.settings.multiplayer) S().twoPlayers else S().vsAi
        text("Tic-Tac-Toe [$modeLabel]", textSize = 28.0, color = accent) { position(130.0, 10.0) }

        val n = GameState.settings.boardSize
        val board = Array(n) { Array(n) { "" } }
        var currentTurn = GameState.settings.firstPlayer
        var gameOver = false
        var timerValue = GameState.settings.timerSeconds
        val timerActive = GameState.settings.timerSeconds > 0
        var lastTimerUpdate = 0.0

        data class MoveSnapshot(val boardState: Array<Array<String>>, val turn: String)
        val moveHistory = mutableListOf<MoveSnapshot>()

        val totalGrid = 380.0
        val cellSize = (totalGrid - (n - 1) * 4.0) / n
        val gridOffsetX = (600.0 - totalGrid) / 2
        val gridOffsetY = 50.0

        labeledText(this, { "X=${GameState.score.xWins}  O=${GameState.score.oWins}  ${S().draw}=${GameState.score.draws}" }, textSize = 16.0, color = RGBA(0xa0, 0xa0, 0xb0), x = 195.0, y = gridOffsetY + totalGrid + 12.0)

        val statusY = gridOffsetY + totalGrid + 35.0
        fun playerLabel(mark: String): String {
            return if (GameState.settings.multiplayer) {
                if (mark == "X") "Игрок 1 (X)" else "Игрок 2 (O)"
            } else "$mark"
        }
        val statusText = labeledText(this, { "${S().turn}: ${playerLabel(currentTurn)}" }, textSize = 22.0, x = 200.0, y = statusY)

        val timerText = if (timerActive) {
            labeledText(this, { "$timerValue" }, textSize = 22.0, color = RGBA(0xf5, 0xa6, 0x23), x = 500.0, y = statusY)
        } else null

        if (GameState.settings.showGrid) {
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

        fun animateWinLine(line: List<Pair<Int, Int>>) {
            var elapsed = 0.0
            addUpdater {
                elapsed += it.seconds
                val brightness = (kotlin.math.sin(elapsed * 6.0) * 0.3 + 0.7).coerceIn(0.0, 1.0)
                for ((r, c) in line) {
                    val cv = cells.first { it.row == r && it.col == c }
                    val cr = (accent.r.toInt() * brightness).toInt()
                    val cg = (accent.g.toInt() * brightness).toInt()
                    val cb = (accent.b.toInt() * brightness).toInt()
                    cv.bg.color = RGBA(cr.coerceIn(0, 255), cg.coerceIn(0, 255), cb.coerceIn(0, 255))
                }
            }
        }

        fun syncBoardToCells() {
            for (cell in cells) {
                val v = board[cell.row][cell.col]
                cell.txt.text = v
                cell.txt.color = if (v == "X") accent else if (v == "O") RGBA(0x0f, 0x34, 0x60) else Colors.WHITE
                cell.bg.color = if (v.isEmpty()) cellColor else cell.bg.color
            }
        }

        fun saveSnapshot(): MoveSnapshot {
            val snap = Array(n) { r -> Array(n) { c -> board[r][c] } }
            return MoveSnapshot(snap, currentTurn)
        }

        fun aiMove() {
            if (!GameState.settings.aiEnabled || gameOver) return
            val aiMark = currentTurn
            val move = findBestMove(board, aiMark, GameState.settings.aiDifficulty)
            if (move != null) {
                val (r, c) = move
                board[r][c] = aiMark
                val cv = cells.first { it.row == r && it.col == c }
                cv.txt.text = aiMark
                cv.txt.color = if (aiMark == "X") accent else RGBA(0x0f, 0x34, 0x60)
                SoundManager.playMove(gameViews)

                val winLine = findWinLine(board, aiMark)
                if (winLine != null) {
                    statusText.text = "${S().winner}: $aiMark!"
                    statusText.color = accent
                    gameOver = true
                    GameState.recordWin(aiMark, n, "AI")
                    SoundManager.playWin(gameViews)
                    animateWinLine(winLine)
                } else if (board.all { row -> row.all { it.isNotEmpty() } }) {
                    statusText.text = S().draw
                    statusText.color = RGBA(0xf5, 0xa6, 0x23)
                    gameOver = true
                    GameState.recordDraw(n, "AI")
                    SoundManager.playDraw(gameViews)
                } else {
                    currentTurn = if (currentTurn == "X") "O" else "X"
                    statusText.text = "${S().turn}: ${playerLabel(currentTurn)}"
                    timerValue = GameState.settings.timerSeconds
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
                if (!gameOver && board[r][c].isEmpty() && (!GameState.settings.aiEnabled || currentTurn == GameState.settings.firstPlayer)) {
                    moveHistory.add(saveSnapshot())
                    board[r][c] = currentTurn
                    cellText.text = currentTurn
                    cellText.color = if (currentTurn == "X") accent else RGBA(0x0f, 0x34, 0x60)
                    SoundManager.playMove(gameViews)

                    val winLine = findWinLine(board, currentTurn)
                    val source = if (GameState.settings.multiplayer) "multiplayer" else "you"
                    if (winLine != null) {
                        statusText.text = "${S().winner}: $currentTurn!"
                        statusText.color = accent
                        gameOver = true
                        GameState.recordWin(currentTurn, n, source)
                        SoundManager.playWin(gameViews)
                        animateWinLine(winLine)
                    } else if (board.all { row2 -> row2.all { it.isNotEmpty() } }) {
                        statusText.text = S().draw
                        statusText.color = RGBA(0xf5, 0xa6, 0x23)
                        gameOver = true
                        GameState.recordDraw(n, source)
                        SoundManager.playDraw(gameViews)
                    } else {
                        currentTurn = if (currentTurn == "X") "O" else "X"
                        statusText.text = "${S().turn}: ${playerLabel(currentTurn)}"
                        timerValue = GameState.settings.timerSeconds
                        lastTimerUpdate = 0.0
                        if (GameState.settings.aiEnabled) aiMove()
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
                        SoundManager.playTick(gameViews)
                        if (timerValue <= 0) {
                            gameOver = true
                            val winner = if (currentTurn == "X") "O" else "X"
                            statusText.text = "${S().timerExpired} ${S().winner}: $winner"
                            statusText.color = RGBA(0xf5, 0xa6, 0x23)
                            GameState.recordWin(winner, n, "timeout")
                            SoundManager.playWin(gameViews)
                        }
                    }
                }
            }
        }

        val btnY = gridOffsetY + totalGrid + 65.0
        val undoContainer = container { position(20.0, btnY) }
        val undoBg = undoContainer.solidRect(180.0, 45.0, RGBA(0x60, 0x60, 0x80))
        undoContainer.text("", textSize = 18.0, color = Colors.WHITE) {
            position(90.0, 13.0)
            alignment = TextAlignment.CENTER
            addUpdater { text = S().undo }
        }
        undoBg.onOver { undoBg.color = RGBA(0x80, 0x80, 0xa0) }
        undoBg.onOut { undoBg.color = RGBA(0x60, 0x60, 0x80) }

        val restartContainer = container { position(210.0, btnY) }
        val restartBg = restartContainer.solidRect(180.0, 45.0, RGBA(0xe9, 0x45, 0x60))
        restartContainer.text("", textSize = 18.0, color = Colors.WHITE) {
            position(90.0, 13.0)
            alignment = TextAlignment.CENTER
            addUpdater { text = S().restart }
        }
        val menuContainer2 = container { position(400.0, btnY) }
        val menuBg = menuContainer2.solidRect(180.0, 45.0, RGBA(0x0f, 0x34, 0x60))
        menuContainer2.text("", textSize = 18.0, color = Colors.WHITE) {
            position(90.0, 13.0)
            alignment = TextAlignment.CENTER
            addUpdater { text = S().menu }
        }
        menuBg.onOver { menuBg.color = RGBA(0x1a, 0x50, 0x80) }
        menuBg.onOut { menuBg.color = RGBA(0x0f, 0x34, 0x60) }

        undoBg.onClick {
            if (moveHistory.isNotEmpty()) {
                val prev = moveHistory.removeAt(moveHistory.lastIndex)
                for (r2 in 0 until n) for (c2 in 0 until n) board[r2][c2] = prev.boardState[r2][c2]
                currentTurn = prev.turn
                gameOver = false
                timerValue = GameState.settings.timerSeconds
                lastTimerUpdate = 0.0
                statusText.text = "${S().turn}: ${playerLabel(currentTurn)}"
                statusText.color = accent
                syncBoardToCells()
                if (timerText != null) timerText.text = "$timerValue"
            }
        }

        fun resetBoard() {
            for (r2 in 0 until n) for (c2 in 0 until n) board[r2][c2] = ""
            currentTurn = GameState.settings.firstPlayer
            gameOver = false
            timerValue = GameState.settings.timerSeconds
            lastTimerUpdate = 0.0
            moveHistory.clear()
            updateAllLabels()
            for (cell in cells) { cell.txt.text = ""; cell.bg.color = cellColor }
            if (timerText != null) timerText.text = "$timerValue"
        }

        restartBg.onClick { resetBoard() }
        menuBg.onClick { resetBoard(); gameContainer.visible = false; menuContainer.visible = true }
    }
}
