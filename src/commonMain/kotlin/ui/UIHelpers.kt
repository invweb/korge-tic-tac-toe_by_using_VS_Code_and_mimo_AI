package ui

import korlibs.korge.view.*
import korlibs.image.color.RGBA
import korlibs.image.color.Colors
import model.GameState
import i18n.*

val themesDark = listOf(Triple(RGBA(0x1a, 0x1a, 0x2e), RGBA(0x16, 0x21, 0x3e), ""), Triple(RGBA(0x0a, 0x1a, 0x2e), RGBA(0x0a, 0x2a, 0x4e), ""), Triple(RGBA(0x0a, 0x2e, 0x1a), RGBA(0x0a, 0x3e, 0x16), ""))
val themesLight = listOf(Triple(RGBA(0xf0, 0xf0, 0xf5), RGBA(0xd0, 0xd0, 0xe0), ""), Triple(RGBA(0xe8, 0xf0, 0xf8), RGBA(0xc0, 0xd8, 0xf0), ""), Triple(RGBA(0xe8, 0xf8, 0xf0), RGBA(0xc0, 0xf0, 0xd0), ""))
var isDarkTheme = true
fun currentThemes() = if (isDarkTheme) themesDark else themesLight
fun accentColor() = if (isDarkTheme) RGBA(0xe9, 0x45, 0x60) else RGBA(0xc0, 0x30, 0x50)

val allThemeNames = listOf(themesRu, themesEn, themesDe)
fun themeName(index: Int) = allThemeNames[GameState.settings.langIndex][index]

val langNames = listOf("Русский", "English", "Deutsch")
val allStrings = listOf(stringsRu, stringsEn, stringsDe)
fun S(): Strings = allStrings[GameState.settings.langIndex]

val langLabels = mutableListOf<Pair<Text, () -> String>>()
fun updateAllLabels() { for ((view, getter) in langLabels) view.text = getter() }
var refreshHistoryScreen: () -> Unit = {}

fun labeledText(parent: Container, getter: () -> String, textSize: Double = 28.0, color: RGBA = Colors.WHITE, x: Double = 0.0, y: Double = 0.0): Text {
    val t = parent.text(getter(), textSize = textSize, color = color) { position(x, y) }
    langLabels.add(t to getter)
    return t
}
