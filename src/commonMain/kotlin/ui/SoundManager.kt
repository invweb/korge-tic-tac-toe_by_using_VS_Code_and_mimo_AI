package ui

import korlibs.audio.sound.*
import korlibs.korge.view.Views
import kotlinx.coroutines.launch

object SoundManager {
    private var moveSound: Sound? = null
    private var winSound: Sound? = null
    private var drawSound: Sound? = null
    private var tickSound: Sound? = null
    private var initialized = false

    fun init(views: Views) {
        if (initialized) return
        views.launch {
            try {
                val vfs = views.currentVfs
                moveSound = vfs["playMove.wav"].readSound()
                winSound = vfs["playWin.wav"].readSound()
                drawSound = vfs["playDraw.wav"].readSound()
                tickSound = vfs["playTick.wav"].readSound()
                initialized = true
            } catch (_: Exception) {
            }
        }
    }

    fun playMove(views: Views) {
        if (!initialized) return
        moveSound?.play(views.coroutineContext)
    }

    fun playWin(views: Views) {
        if (!initialized) return
        winSound?.play(views.coroutineContext)
    }

    fun playDraw(views: Views) {
        if (!initialized) return
        drawSound?.play(views.coroutineContext)
    }

    fun playTick(views: Views) {
        if (!initialized) return
        tickSound?.play(views.coroutineContext)
    }
}
