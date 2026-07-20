package ui

import korlibs.image.color.RGBA
import korlibs.image.color.Colors
import korlibs.korge.view.*
import korlibs.korge.input.*
import korlibs.time.*
import model.settings
import model.score
import model.history
import model.HistoryEntry
import logic.checkWinBoard
import logic.findBestMove
import i18n.*

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
