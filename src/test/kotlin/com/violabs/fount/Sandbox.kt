package com.violabs.fount

import org.junit.jupiter.api.Test

// built on startup
val progressBarOptions: Map<Int, String> = (0..10).associate(::buildProgressBar)

fun buildProgressBar(progressLevel: Int): Pair<Int, String> {
    val bar: String = (0 until 10).joinToString("") { if (it < progressLevel) "●" else "○" }

    return progressLevel to bar
}

// called when needed
fun getProgressBar(percentage: Double): String {
    val progressLevel: Int = calculateProgressLevel(percentage)

    return progressBarOptions[progressLevel] ?: throw Exception("nope")
}

fun calculateProgressLevel(percentage: Double): Int {
    if (percentage < 0.0) return 0
    if (percentage > 1.0) return 10

    val rounded: Int = if (percentage % 0.1 > 0.0) 1 else 0
    val quotient: Int = (percentage / 0.1).toInt()

    return rounded + quotient
}

// testing
class Sandbox : TestHarness() {

    @Test
    fun test1() = testEquals<String> {
        whenever {
            getProgressBar(0.314)
        }

        expect {
            "●●●●○○○○○○"
        }
    }
}