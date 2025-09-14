package com.lalas.musicgpt.data

import androidx.compose.ui.graphics.Color

data class GenerationTask(
    val id: String,
    val title: String,
    val originalDescription: String, // Store the original prompt
    val progress: Int = 0, // 0-100
    val queueCount: Int? = null, // For queue simulation
    val colorStart: Color = Color(0xFFFF8504),
    val colorEnd: Color = Color(0xFF990287),
    val audioUrl : Int? = null
) {
    val isCompleted: Boolean get() = progress == 100
    val isInProgress: Boolean get() = progress in 1..99
    val description: String get() = originalDescription

}

data class MusicGPTUiState(
    val currentScreen: String = "home",
    val tasks: List<GenerationTask> = emptyList(),
    val currentTask: GenerationTask? = null,
    val generationProgress: Int = 0,
    val currentTrack: GenerationTask? = null,
    val isPlayerVisible: Boolean = false,
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,

) {
    val completedTasks: List<GenerationTask> get() = tasks.filter { it.isCompleted }
    val inProgressTasks: List<GenerationTask> get() = tasks.filter { it.isInProgress }
}