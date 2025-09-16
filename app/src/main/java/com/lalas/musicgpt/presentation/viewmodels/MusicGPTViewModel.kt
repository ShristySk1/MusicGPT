package com.lalas.musicgpt.presentation.viewmodels

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lalas.musicgpt.R
import com.lalas.musicgpt.data.model.GenerationTask
import com.lalas.musicgpt.data.model.MusicGPTUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

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
                progress = 100,
                colorStart = Color(0xFFE91E63),
                colorEnd = Color(0xFF9C27B0),
                audioUrl =  R.raw.sample1,
                image = R.drawable.random_1

            ),
            GenerationTask(
                id = "2",
                title = "Lo-fi hip hop",
                originalDescription = "Lo-fi hip hop beats for studying and relaxation",
                progress = 100,
                colorStart = Color(0xFFE91E63),
                colorEnd = Color(0xFF9C27B0),
                audioUrl =R.raw.sample2,
                image = R.drawable.random_2
            ),
            GenerationTask(
                id = "3",
                title = "Classical piano composition",
                originalDescription = "Classical piano composition in the style of Chopin",
                progress = 100,
                colorStart = Color(0xFF607D8B),
                colorEnd = Color(0xFF455A64),
                audioUrl = R.raw.sample1,
                image = R.drawable.random_3
            ),
            GenerationTask(
                id = "4",
                title = "Electronic dance music",
                originalDescription = "Electronic dance music with heavy bass drops",
                progress = 100,
                colorStart = Color(0xFF795548),
                colorEnd = Color(0xFF5D4037),
                audioUrl = R.raw.sample2,
                image = R.drawable.random_1
            ),
            GenerationTask(
                id = "5",
                title = "Ambient space sounds",
                originalDescription = "Ambient space sounds for meditation and focus",
                progress = 100,
                colorStart = Color(0xFF2196F3),
                colorEnd = Color(0xFF1976D2),
                audioUrl=R.raw.sample1,
                image = R.drawable.random_2
            ),
        )

        _uiState.value = _uiState.value.copy(
            tasks = tasks,
            currentTrack = null,
            isPlayerVisible = false
        )
    }

    fun playTrack(task: GenerationTask) {
        // Only allow playing completed tracks
        if (task.progress == 100) {
            _uiState.value = _uiState.value.copy(
                currentTrack = task,
                isPlayerVisible = true,
                isPlaying = true,
                isLoading = true,  // Set loading to true
            )
        }
    }

    /**
     * Directly hide player without animation
     */
    fun hidePlayer() {
        _uiState.value = _uiState.value.copy(
            isPlayerVisible = false,
            isPlaying = false,
            isLoading = false,  // Set loading to true
            currentTrack = null
        )
    }

    /**
     * Need to hide player with animation
     */
    fun setPlayerVisible(visible: Boolean) {
        _uiState.value = _uiState.value.copy(isPlayerVisible = visible)
        // Clear the track after animation completes (400ms + buffer)
        if (!visible) {
            viewModelScope.launch {
                delay(500) // Wait for animation to complete
                hidePlayer()
            }
        }
    }

    fun togglePlayPause() {
        val currentState = _uiState.value
        _uiState.value = currentState.copy(
            isPlaying = !currentState.isPlaying
        )
    }
    fun setLoadingState(isLoading: Boolean) {
        _uiState.value = _uiState.value.copy(
            isLoading = isLoading
        )
    }

    fun nextTrack() {
        val currentTrackId = _uiState.value.currentTrack?.id
        val allTasks = _uiState.value.tasks.filter { it.progress == 100 }

        if (allTasks.isNotEmpty()) {
            val currentIndex = allTasks.indexOfFirst { it.id == currentTrackId }
            val nextIndex = if (currentIndex < allTasks.size - 1) currentIndex + 1 else 0
            val nextTask = allTasks[nextIndex]

            // Immediately update UI state - this cancels any current loading
            _uiState.value = _uiState.value.copy(
                currentTrack = nextTask,
                isPlaying = false,
                isLoading = true,
                isPlayerVisible = true
            )
        }
    }

    fun previousTrack() {
        val currentTrackId = _uiState.value.currentTrack?.id
        val allTasks = _uiState.value.tasks.filter { it.progress == 100 }

        if (allTasks.isNotEmpty()) {
            val currentIndex = allTasks.indexOfFirst { it.id == currentTrackId }
            val previousIndex = if (currentIndex > 0) currentIndex - 1 else allTasks.size - 1
            val previousTask = allTasks[previousIndex]

            // Immediately update UI state - this cancels any current loading
            _uiState.value = _uiState.value.copy(
                currentTrack = previousTask,
                isPlaying = false,
                isLoading = true,
                isPlayerVisible = true
            )
        }
    }
    fun updatePlayingState(isPlaying: Boolean) {
        _uiState.value = _uiState.value.copy(
            isPlaying = isPlaying,
            isLoading = false
        )
    }

    fun resetPlayerState() {
        _uiState.value = _uiState.value.copy(
            isPlaying = false,
            isPlayerVisible = false,
            currentTrack = null,
            isLoading = false,
        )
    }


    fun createTask(prompt: String) {
        val words = prompt.split(" ").filter { it.isNotBlank() }
        val title = words.take(3).joinToString(" ") // First 3-4 words for title

        val newTask = GenerationTask(
            id = UUID.randomUUID().toString(),
            title = title,
            originalDescription = prompt, // Store original prompt
            progress = 0,
            queueCount = Random.nextInt(15000, 25000), // Random initial queue count
            colorStart = Color(0xFFFF8504),
            colorEnd = Color(0xFF990287),
            audioUrl = R.raw.sample3,
            image = R.drawable.property_1_finish
        )

        val currentTasks = _uiState.value.tasks.toMutableList()
        currentTasks.add(newTask)

        _uiState.value = _uiState.value.copy(tasks = currentTasks)

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
}