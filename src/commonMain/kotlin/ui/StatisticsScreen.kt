package ui

import korlibs.image.color.RGBA
import korlibs.image.color.Colors
import korlibs.korge.view.*
import korlibs.korge.input.*
import model.GameState
import i18n.*

fun Container.buildStatistics(statisticsContainer: Container, menuContainer: Container) {
    statisticsContainer.apply {
        solidRect(600.0, 800.0, RGBA(0x1a, 0x1a, 0x2e))
        labeledText(this, { S().statistics }, textSize = 38.0, color = accentColor(), x = 195.0, y = 30.0)

        val totalGames = GameState.score.xWins + GameState.score.oWins + GameState.score.draws
        val xRate = if (totalGames > 0) (GameState.score.xWins * 100.0 / totalGames).toInt() else 0
        val oRate = if (totalGames > 0) (GameState.score.oWins * 100.0 / totalGames).toInt() else 0
        val drawRate = if (totalGames > 0) (GameState.score.draws * 100.0 / totalGames).toInt() else 0

        val bgColor = if (isDarkTheme) RGBA(0x20, 0x20, 0x35) else RGBA(0xe0, 0xe0, 0xf0)
        val labelColor = RGBA(0xa0, 0xa0, 0xb0)
        val valueColor = if (isDarkTheme) Colors.WHITE else RGBA(0x20, 0x20, 0x30)

        var y = 90.0

        solidRect(520.0, 200.0, bgColor) { position(40.0, y) }
        labeledText(this, { "${S().totalGames}: $totalGames" }, textSize = 22.0, color = valueColor, x = 60.0, y = y + 15.0)
        y += 50.0
        labeledText(this, { "X: ${GameState.score.xWins} (${xRate}%)" }, textSize = 18.0, color = RGBA(0xe9, 0x45, 0x60), x = 60.0, y = y)
        y += 30.0
        labeledText(this, { "O: ${GameState.score.oWins} (${oRate}%)" }, textSize = 18.0, color = RGBA(0x0f, 0x34, 0x60), x = 60.0, y = y)
        y += 30.0
        labeledText(this, { "${S().drawsStat}: ${GameState.score.draws} (${drawRate}%)" }, textSize = 18.0, color = RGBA(0xf5, 0xa6, 0x23), x = 60.0, y = y)
        y += 30.0
        labeledText(this, { "${S().winRate}: $xRate% / $oRate%" }, textSize = 16.0, color = labelColor, x = 60.0, y = y)

        y += 60.0
        solidRect(520.0, 120.0, bgColor) { position(40.0, y) }
        labeledText(this, { S().byBoardSize }, textSize = 22.0, color = valueColor, x = 60.0, y = y + 15.0)
        y += 50.0
        for (size in 3..5) {
            val count = GameState.history.count { it.boardSize == size }
            labeledText(this, { "${size}x${size}: $count ${S().gamesPlayed}" }, textSize = 16.0, color = labelColor, x = 60.0, y = y)
            y += 25.0
        }

        y += 20.0
        solidRect(520.0, 100.0, bgColor) { position(40.0, y) }
        labeledText(this, { S().byMode }, textSize = 22.0, color = valueColor, x = 60.0, y = y + 15.0)
        y += 50.0
        val aiGames = GameState.history.count { it.timestamp == "AI" || it.timestamp == "you" || it.timestamp == "timeout" }
        val mpGames = GameState.history.count { it.timestamp == "multiplayer" }
        labeledText(this, { "${S().vsAi}: $aiGames ${S().gamesPlayed}" }, textSize = 16.0, color = labelColor, x = 60.0, y = y)
        y += 25.0
        labeledText(this, { "${S().twoPlayers}: $mpGames ${S().gamesPlayed}" }, textSize = 16.0, color = labelColor, x = 60.0, y = y)

        val backBg = solidRect(200.0, 45.0, RGBA(0xe9, 0x45, 0x60)) { position(200.0, 645.0) }
        labeledText(this, { S().back }, textSize = 22.0, x = 268.0, y = 654.0)
        backBg.onOver { backBg.color = RGBA(0xff, 0x5a, 0x7a) }
        backBg.onOut { backBg.color = RGBA(0xe9, 0x45, 0x60) }
        backBg.onClick { statisticsContainer.visible = false; menuContainer.visible = true }
    }
}
