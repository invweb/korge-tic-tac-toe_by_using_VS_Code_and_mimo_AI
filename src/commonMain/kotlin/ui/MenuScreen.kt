package ui

import korlibs.image.color.RGBA
import korlibs.korge.view.*
import korlibs.korge.input.*
import model.GameState
import i18n.*

fun Container.buildMenu(menuContainer: Container, gameContainer: Container, settingsContainer: Container, historyContainer: Container) {
    menuContainer.apply {
        solidRect(600.0, 700.0, RGBA(0x1a, 0x1a, 0x2e))
        text("Tic-Tac-Toe", textSize = 48.0, color = RGBA(0xe9, 0x45, 0x60)) { position(165.0, 60.0) }
        text("X", textSize = 60.0, color = RGBA(0xe9, 0x45, 0x60)) { position(130.0, 140.0) }
        text("O", textSize = 60.0, color = RGBA(0x0f, 0x34, 0x60)) { position(410.0, 140.0) }
        labeledText(this, { "${S().score}: X=${GameState.score.xWins}  O=${GameState.score.oWins}  ${S().draw}=${GameState.score.draws}" }, textSize = 18.0, color = RGBA(0xa0, 0xa0, 0xb0), x = 135.0, y = 220.0)

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
