plugins {
    id("com.soywiz.korge") version "6.0.0"
}

korge {
    targetAndroid()
}

afterEvaluate {
    tasks.matching { it.name.contains("Lint", ignoreCase = true) || it.name.contains("lint", ignoreCase = false) }.configureEach {
        enabled = false
    }
}
