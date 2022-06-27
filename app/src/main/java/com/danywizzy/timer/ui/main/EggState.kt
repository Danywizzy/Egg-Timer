package com.danywizzy.timer.ui.main

enum class EggState(val time: Long) {
    Soft(3 * 60 * 1_000),
    Medium(5 * 60 * 1_000),
    Hard(9 * 60 * 1_000)
}