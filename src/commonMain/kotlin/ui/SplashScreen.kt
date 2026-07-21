package ui

import korlibs.image.color.RGBA
import korlibs.image.color.Colors
import korlibs.korge.view.*
import korlibs.korge.input.*
import korlibs.math.geom.Angle
import korlibs.time.*
import model.GameState
import i18n.*

fun Container.buildSplash(splashContainer: Container, menuContainer: Container) {
    splashContainer.apply {
        solidRect(600.0, 800.0, RGBA(0x0a, 0x0a, 0x1a))
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

        val tapArea = solidRect(300.0, 40.0, RGBA(0x00, 0x00, 0x00, 0x0)) { position(150.0, 430.0) }
        tapArea.onClick { splashContainer.visible = false; menuContainer.visible = true }

        val langTexts = listOf("RU", "EN", "DE")
        val splashLangBtns = mutableListOf<SolidRect>()
        for (i in 0..2) {
            val bx = 190.0 + i * 75.0
            val btn = solidRect(65.0, 30.0, if (GameState.settings.langIndex == i) RGBA(0xe9, 0x45, 0x60) else RGBA(0x30, 0x30, 0x40)) {
                position(bx, 470.0)
            }
            text(langTexts[i], textSize = 14.0, color = Colors.WHITE) { position(bx + 16.0, 476.0) }
            splashLangBtns.add(btn)
        }
        fun updateSplashLang() { for (i in 0..2) splashLangBtns[i].color = if (GameState.settings.langIndex == i) RGBA(0xe9, 0x45, 0x60) else RGBA(0x30, 0x30, 0x40) }
        for (i in 0..2) {
            splashLangBtns[i].onClick {
                GameState.updateSettings { langIndex = i }
                updateSplashLang()
                updateAllLabels()
            }
        }

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
    }
}
