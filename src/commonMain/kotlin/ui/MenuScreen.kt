package ui

import korlibs.image.color.RGBA
import korlibs.image.color.Colors
import korlibs.image.text.TextAlignment
import korlibs.korge.view.*
import korlibs.korge.input.*
import model.GameState
import i18n.*

fun Container.buildMenu(menuContainer: Container, gameContainer: Container, settingsContainer: Container, historyContainer: Container, statisticsContainer: Container) {
    menuContainer.apply {
        solidRect(600.0, 800.0, RGBA(0x1a, 0x1a, 0x2e))
        text("Tic-Tac-Toe", textSize = 48.0, color = RGBA(0xe9, 0x45, 0x60)) { position(300.0, 80.0); alignment = TextAlignment.CENTER }
        text("X", textSize = 60.0, color = RGBA(0xe9, 0x45, 0x60)) { position(130.0, 140.0) }
        text("O", textSize = 60.0, color = RGBA(0x0f, 0x34, 0x60)) { position(410.0, 140.0) }
        labeledText(this, { "${S().score}: X=${GameState.score.xWins}  O=${GameState.score.oWins}  ${S().draw}=${GameState.score.draws}" }, textSize = 18.0, color = RGBA(0xa0, 0xa0, 0xb0), x = 135.0, y = 220.0)

        val btnX = 170.0
        val btnW = 260.0
        val btnCenter = btnW / 2

        val twoPlayersContainer = container { position(btnX, 280.0) }
        val twoPlayersBg = twoPlayersContainer.solidRect(btnW, 50.0, RGBA(0x0f, 0x34, 0x60))
        twoPlayersContainer.text("", textSize = 26.0, color = Colors.WHITE) { position(btnCenter, 13.0); alignment = TextAlignment.CENTER; addUpdater { text = S().twoPlayers } }

        val vsAiContainer = container { position(btnX, 345.0) }
        val vsAiBg = vsAiContainer.solidRect(btnW, 50.0, RGBA(0xe9, 0x45, 0x60))
        vsAiContainer.text("", textSize = 26.0, color = Colors.WHITE) { position(btnCenter, 13.0); alignment = TextAlignment.CENTER; addUpdater { text = S().vsAi } }

        val settingsContainer2 = container { position(btnX, 410.0) }
        val settingsBg = settingsContainer2.solidRect(btnW, 50.0, RGBA(0x0f, 0x34, 0x60))
        settingsContainer2.text("", textSize = 26.0, color = Colors.WHITE) { position(btnCenter, 13.0); alignment = TextAlignment.CENTER; addUpdater { text = S().settings } }

        val historyContainer2 = container { position(btnX, 475.0) }
        val historyBg = historyContainer2.solidRect(btnW, 50.0, RGBA(0x0f, 0x34, 0x60))
        historyContainer2.text("", textSize = 26.0, color = Colors.WHITE) { position(btnCenter, 13.0); alignment = TextAlignment.CENTER; addUpdater { text = S().history } }

        val statsContainer = container { position(btnX, 540.0) }
        val statsBg = statsContainer.solidRect(btnW, 50.0, RGBA(0x0f, 0x34, 0x60))
        statsContainer.text("", textSize = 26.0, color = Colors.WHITE) { position(btnCenter, 13.0); alignment = TextAlignment.CENTER; addUpdater { text = S().statistics } }

        val exitContainer = container { position(btnX, 605.0) }
        val exitBg = exitContainer.solidRect(btnW, 50.0, RGBA(0x40, 0x40, 0x50))
        exitContainer.text("", textSize = 26.0, color = Colors.WHITE) { position(btnCenter, 13.0); alignment = TextAlignment.CENTER; addUpdater { text = S().exit } }

        twoPlayersBg.onOver { twoPlayersBg.color = RGBA(0x1a, 0x50, 0x80) }
        twoPlayersBg.onOut { twoPlayersBg.color = RGBA(0x0f, 0x34, 0x60) }
        vsAiBg.onOver { vsAiBg.color = RGBA(0xff, 0x5a, 0x7a) }
        vsAiBg.onOut { vsAiBg.color = RGBA(0xe9, 0x45, 0x60) }
        settingsBg.onOver { settingsBg.color = RGBA(0x1a, 0x50, 0x80) }
        settingsBg.onOut { settingsBg.color = RGBA(0x0f, 0x34, 0x60) }
        historyBg.onOver { historyBg.color = RGBA(0x1a, 0x50, 0x80) }
        historyBg.onOut { historyBg.color = RGBA(0x0f, 0x34, 0x60) }
        statsBg.onOver { statsBg.color = RGBA(0x1a, 0x50, 0x80) }
        statsBg.onOut { statsBg.color = RGBA(0x0f, 0x34, 0x60) }
        exitBg.onOver { exitBg.color = RGBA(0x60, 0x60, 0x70) }
        exitBg.onOut { exitBg.color = RGBA(0x40, 0x40, 0x50) }

        twoPlayersBg.onClick {
            GameState.updateSettings { multiplayer = true; aiEnabled = false }
            menuContainer.visible = false; gameContainer.visible = true
        }
        vsAiBg.onClick {
            GameState.updateSettings { multiplayer = false; aiEnabled = true }
            menuContainer.visible = false; gameContainer.visible = true
        }
        settingsBg.onClick { menuContainer.visible = false; settingsContainer.visible = true }
        historyBg.onClick { menuContainer.visible = false; refreshHistoryScreen(); historyContainer.visible = true }
        statsBg.onClick { menuContainer.visible = false; statisticsContainer.visible = true }
        exitBg.onClick { kotlin.system.exitProcess(0) }
    }
}
