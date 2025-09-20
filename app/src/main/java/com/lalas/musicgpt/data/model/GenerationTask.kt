package com.lalas.musicgpt.data.model

import androidx.compose.ui.graphics.Color

data class GenerationTask(
    val id: String,
    val title: String,
    val originalDescription: String, // Store the original prompt
    val progress: Int = 0, // 0-100
    val queueCount: Int? = null, // For queue simulation
    val audioUrl : Int? = null,
    val image :Int
) {
    val isCompleted: Boolean get() = progress == 100
    val isInProgress: Boolean get() = progress in 1..99
    val description: String get() = originalDescription

}