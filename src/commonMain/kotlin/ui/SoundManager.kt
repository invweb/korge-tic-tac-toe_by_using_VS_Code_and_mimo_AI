package ui

import korlibs.korge.view.Views

object SoundManager {
    private var initialized = false

    fun init(@Suppress("UNUSED_PARAMETER") views: Views) {
        initialized = true
    }

    fun playMove(@Suppress("UNUSED_PARAMETER") views: Views) {}
    fun playWin(@Suppress("UNUSED_PARAMETER") views: Views) {}
    fun playDraw(@Suppress("UNUSED_PARAMETER") views: Views) {}
    fun playTick(@Suppress("UNUSED_PARAMETER") views: Views) {}
}
