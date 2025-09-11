package com.lalas.musicgpt.data

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MusicGPTViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MusicGPTUiState())
    val uiState: StateFlow<MusicGPTUiState> = _uiState.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        val tasks = listOf(
            GenerationTask(
                id = "1",
                title = "Create a funky house",
                originalDescription = "Create a funky house song with upbeat rhythm",
                progress = 100, // Completed task
                colorStart = Color(0xFFE91E63),
                colorEnd = Color(0xFF9C27B0)
            ),
            GenerationTask(
                id = "2",
                title = "Lo-fi hip hop",
                originalDescription = "Lo-fi hip hop beats for studying and relaxation",
                progress = 45, // In progress
                colorStart = Color(0xFFE91E63),
                colorEnd = Color(0xFF9C27B0)
            ),
            GenerationTask(
                id = "3",
                title = "Classical piano composition",
                originalDescription = "Classical piano composition in the style of Chopin",
                progress = 100, // Completed task
                colorStart = Color(0xFF607D8B),
                colorEnd = Color(0xFF455A64)
            ),
            GenerationTask(
                id = "4",
                title = "Electronic dance music",
                originalDescription = "Electronic dance music with heavy bass drops",
                progress = 75, // In progress
                colorStart = Color(0xFF795548),
                colorEnd = Color(0xFF5D4037)
            ),
            GenerationTask(
                id = "5",
                title = "Ambient space sounds",
                originalDescription = "Ambient space sounds for meditation and focus",
                progress = 100, // Completed task
                colorStart = Color(0xFF2196F3),
                colorEnd = Color(0xFF1976D2)
            ),
        )

        _uiState.value = _uiState.value.copy(
            tasks = tasks,
            currentTrack = null,
            isPlayerVisible = false
        )
    }

    fun startGeneration(task: GenerationTask) {
        _uiState.value = _uiState.value.copy(
            currentScreen = "generation",
            currentTask = task,
            generationProgress = 0
        )

        viewModelScope.launch {
            for (i in 0..100 step 25) {
                delay(1000)
                _uiState.value = _uiState.value.copy(generationProgress = i)
            }
        }
    }

    fun goHome() {
        _uiState.value = _uiState.value.copy(
            currentScreen = "home",
            currentTask = null,
            generationProgress = 0
        )
    }

    fun showCreateDialog() {
        // Implementation for showing create dialog
    }

    fun playTrack(task: GenerationTask) {
        // Only allow playing completed tracks
        if (task.progress == 100) {
            _uiState.value = _uiState.value.copy(
                currentTrack = task,
                isPlayerVisible = true,
                isPlaying = true
            )
            // Start actual music playback here
        }
    }

    fun hidePlayer() {
        _uiState.value = _uiState.value.copy(
            isPlayerVisible = false,
            isPlaying = false,
            currentTrack = null
        )
        // Stop music playback here
    }

    fun togglePlayPause() {
        val currentState = _uiState.value
        _uiState.value = currentState.copy(
            isPlaying = !currentState.isPlaying
        )
        // Toggle actual music playback here
    }

    fun nextTrack() {
        // Find next completed track in tasks list and play it
        val completedTasks = _uiState.value.tasks.filter { it.progress == 100 }
        val currentIndex = completedTasks.indexOfFirst {
            it.id == _uiState.value.currentTrack?.id
        }

        if (currentIndex != -1 && currentIndex < completedTasks.size - 1) {
            val nextTask = completedTasks[currentIndex + 1]
            playTrack(nextTask)
        } else if (completedTasks.isNotEmpty()) {
            // Loop back to first track if at end
            playTrack(completedTasks[0])
        }
    }

    fun previousTrack() {
        // Find previous completed track in tasks list and play it
        val completedTasks = _uiState.value.tasks.filter { it.progress == 100 }
        val currentIndex = completedTasks.indexOfFirst {
            it.id == _uiState.value.currentTrack?.id
        }

        if (currentIndex > 0) {
            val previousTask = completedTasks[currentIndex - 1]
            playTrack(previousTask)
        } else if (completedTasks.isNotEmpty()) {
            // Loop to last track if at beginning
            playTrack(completedTasks.last())
        }
    }

    // Add method to create new task with proper title and description
    fun createTask(prompt: String) {
        val words = prompt.split(" ").filter { it.isNotBlank() }
        val title = words.take(4).joinToString(" ") // First 3-4 words for title

        val newTask = GenerationTask(
            id = java.util.UUID.randomUUID().toString(),
            title = title,
            originalDescription = prompt, // Store original prompt
            progress = 0,
            queueCount = kotlin.random.Random.nextInt(15000, 25000), // Random initial queue count
            colorStart = Color(0xFFFF8504),
            colorEnd = Color(0xFF990287)
        )

        val currentTasks = _uiState.value.tasks.toMutableList()
        currentTasks.add(newTask)

        _uiState.value = _uiState.value.copy(tasks = currentTasks)

        // Start progress simulation
        simulateProgress(newTask.id, newTask.queueCount!!)
    }

    private fun simulateProgress(taskId: String, initialQueueCount: Int) {
        viewModelScope.launch {
            for (progress in 0..100 step 1) {
                delay(100) // Simulate work
                val currentTasks = _uiState.value.tasks
                val updatedTasks = currentTasks.map { task ->
                    if (task.id == taskId) {
                        val newQueueCount = if (progress > 0) {
                            // Gradually decrease queue count as progress increases
                            val remainingRatio = (100 - progress) / 100f
                            (initialQueueCount * remainingRatio).toInt()
                        } else initialQueueCount

                        task.copy(
                            progress = progress,
                            queueCount = newQueueCount
                        )
                    } else task
                }
                _uiState.value = _uiState.value.copy(tasks = updatedTasks)
            }
        }
    }

    // Add skip functionality
    fun skipTask(task: GenerationTask) {
        val currentTasks = _uiState.value.tasks
        val updatedTasks = currentTasks.map {
            if (it.id == task.id) {
                it.copy(progress = 100, queueCount = 0)
            } else it
        }
        _uiState.value = _uiState.value.copy(tasks = updatedTasks)
    }
}