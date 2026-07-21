import korlibs.korge.Korge
import korlibs.korge.view.*
import korlibs.math.geom.Size
import model.GameState
import ui.*

suspend fun main() = Korge(windowSize = Size(600, 800), virtualSize = Size(600, 800)) {
    GameState.load()
    langLabels.clear()
    SoundManager.init(views)
    val splashContainer = container {}
    val menuContainer = container { visible = false }
    val gameContainer = container { visible = false }
    val settingsContainer = container { visible = false }
    val historyContainer = container { visible = false }
    val statisticsContainer = container { visible = false }

    buildSplash(splashContainer, menuContainer)
    buildMenu(menuContainer, gameContainer, settingsContainer, historyContainer, statisticsContainer)
    buildGame(gameContainer, menuContainer)
    buildSettings(settingsContainer, menuContainer)
    buildHistory(historyContainer, menuContainer)
    buildStatistics(statisticsContainer, menuContainer)
}
