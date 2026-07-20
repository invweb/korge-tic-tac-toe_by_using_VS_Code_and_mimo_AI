package ui

import korlibs.image.color.RGBA
import korlibs.image.color.Colors
import korlibs.korge.view.*
import korlibs.korge.input.*
import model.GameState
import i18n.*

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
        fun updateLangButtons() { for (i in langButtons.indices) langButtons[i].color = if (i == GameState.settings.langIndex) RGBA(0xe9, 0x45, 0x60) else RGBA(0x30, 0x30, 0x40) }
        updateLangButtons()
        for (i in langButtons.indices) langButtons[i].onClick { GameState.settings.langIndex = i; updateLangButtons(); updateAllLabels() }

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
        fun updateFP() { xBtn.color = if (GameState.settings.firstPlayer == "X") RGBA(0xe9, 0x45, 0x60) else RGBA(0x30, 0x30, 0x40); oBtn.color = if (GameState.settings.firstPlayer == "O") RGBA(0x0f, 0x34, 0x60) else RGBA(0x30, 0x30, 0x40) }
        updateFP()
        xBtn.onClick { GameState.settings.firstPlayer = "X"; updateFP() }
        oBtn.onClick { GameState.settings.firstPlayer = "O"; updateFP() }

        labeledText(this, { S().boardSize }, textSize = 22.0, x = 210.0, y = 365.0)
        val sizeButtons = mutableListOf<SolidRect>()
        for (sz in 3..5) {
            val bx = 190.0 + (sz - 3) * 90.0
            val btn = solidRect(75.0, 40.0, if (GameState.settings.boardSize == sz) RGBA(0xe9, 0x45, 0x60) else RGBA(0x30, 0x30, 0x40)) { position(bx, 395.0) }
            text("${sz}x${sz}", textSize = 18.0, color = Colors.WHITE) { position(bx + 14.0, 402.0) }
            sizeButtons.add(btn)
        }
        fun updateSizeButtons() { for (i in 0..2) sizeButtons[i].color = if (GameState.settings.boardSize == i + 3) RGBA(0xe9, 0x45, 0x60) else RGBA(0x30, 0x30, 0x40) }
        for (i in 0..2) sizeButtons[i].onClick { GameState.settings.boardSize = i + 3; updateSizeButtons() }

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
        fun updateTimerButtons() { for (i in timerOpts.indices) timerButtons[i].color = if (GameState.settings.timerSeconds == timerOpts[i]) RGBA(0xe9, 0x45, 0x60) else RGBA(0x30, 0x30, 0x40) }
        updateTimerButtons()
        for (i in timerOpts.indices) timerButtons[i].onClick { GameState.settings.timerSeconds = timerOpts[i]; updateTimerButtons(); updateAllLabels() }

        labeledText(this, { S().ai }, textSize = 22.0, x = 150.0, y = 530.0)
        val aiBtn = solidRect(120.0, 40.0, if (GameState.settings.aiEnabled) RGBA(0x0f, 0x34, 0x60) else RGBA(0x40, 0x40, 0x50)) { position(400.0, 525.0) }
        labeledText(this, { if (GameState.settings.aiEnabled) S().on else S().off }, textSize = 20.0, x = 430.0, y = 533.0)
        aiBtn.onClick { GameState.settings.aiEnabled = !GameState.settings.aiEnabled; aiBtn.color = if (GameState.settings.aiEnabled) RGBA(0x0f, 0x34, 0x60) else RGBA(0x40, 0x40, 0x50); updateAllLabels() }

        val backBg = solidRect(200.0, 45.0, RGBA(0xe9, 0x45, 0x60)) { position(200.0, 600.0) }
        labeledText(this, { S().back }, textSize = 22.0, x = 268.0, y = 609.0)
        backBg.onOver { backBg.color = RGBA(0xff, 0x5a, 0x7a) }
        backBg.onOut { backBg.color = RGBA(0xe9, 0x45, 0x60) }
        backBg.onClick { settingsContainer.visible = false; menuContainer.visible = true }
    }
}
