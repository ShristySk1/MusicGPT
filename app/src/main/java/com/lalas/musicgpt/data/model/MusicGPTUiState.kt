package com.lalas.musicgpt.data.model

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