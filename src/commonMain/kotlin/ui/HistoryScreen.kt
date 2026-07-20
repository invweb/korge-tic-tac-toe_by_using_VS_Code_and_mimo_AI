package ui

import korlibs.image.color.RGBA
import korlibs.korge.view.*
import korlibs.korge.input.*
import model.GameState
import i18n.*

fun Container.buildHistory(historyContainer: Container, menuContainer: Container) {
    historyContainer.apply {
        solidRect(600.0, 700.0, RGBA(0x1a, 0x1a, 0x2e))
        labeledText(this, { S().history }, textSize = 38.0, color = accentColor(), x = 210.0, y = 30.0)

        val listContainer = container { position(0.0, 90.0) }

        fun refreshHistory() {
            listContainer.removeChildren()
            if (GameState.history.isEmpty()) {
                listContainer.text(S().noHistory, textSize = 20.0, color = RGBA(0x80, 0x80, 0x80)) { position(210.0, 30.0) }
            } else {
                for ((idx, entry) in GameState.history.takeLast(20).reversed().withIndex()) {
                    val y = idx * 35.0
                    val bgColor = if (isDarkTheme) RGBA(0x20, 0x20, 0x35) else RGBA(0xe0, 0xe0, 0xf0)
                    listContainer.solidRect(520.0, 30.0, bgColor) { position(40.0, y) }
                    val label = if (entry.winner == "draw") S().draw else "${S().winner}: ${entry.winner}"
                    listContainer.text("${idx + 1}. ${entry.boardSize}x${entry.boardSize} - $label  (${entry.timestamp})", textSize = 14.0, color = korlibs.image.color.Colors.WHITE) { position(50.0, y + 7.0) }
                }
            }
        }
        refreshHistory()
        refreshHistoryScreen = { refreshHistory() }

        val clearBg = solidRect(200.0, 45.0, RGBA(0x80, 0x30, 0x30)) { position(200.0, 600.0) }
        labeledText(this, { S().clear }, textSize = 22.0, x = 268.0, y = 609.0)
        clearBg.onOver { clearBg.color = RGBA(0xa0, 0x40, 0x40) }
        clearBg.onOut { clearBg.color = RGBA(0x80, 0x30, 0x30) }
        clearBg.onClick { GameState.clearAll(); refreshHistory(); updateAllLabels() }

        val backBg = solidRect(200.0, 45.0, RGBA(0xe9, 0x45, 0x60)) { position(200.0, 545.0) }
        labeledText(this, { S().back }, textSize = 22.0, x = 268.0, y = 554.0)
        backBg.onOver { backBg.color = RGBA(0xff, 0x5a, 0x7a) }
        backBg.onOut { backBg.color = RGBA(0xe9, 0x45, 0x60) }
        backBg.onClick { historyContainer.visible = false; menuContainer.visible = true }
    }
}
