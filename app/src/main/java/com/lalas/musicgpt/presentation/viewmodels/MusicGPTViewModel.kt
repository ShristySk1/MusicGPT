package com.lalas.musicgpt.presentation.viewmodels

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lalas.musicgpt.R
import com.lalas.musicgpt.data.model.GenerationTask
import com.lalas.musicgpt.data.model.MusicGPTUiState
import com.lalas.musicgpt.data.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class MusicGPTViewModel @Inject constructor(
    private val musicRepository: MusicRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(MusicGPTUiState())
    val uiState: StateFlow<MusicGPTUiState> = _uiState.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            val tasks = musicRepository.getTasks()
            _uiState.value = _uiState.value.copy(
                tasks = tasks,
                currentTrack = null,
                isPlayerVisible = false
            )
        }
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
            audioUrl = R.raw.sample3,
            image = R.drawable.property_1_finish
        )

        viewModelScope.launch {
            musicRepository.addTask(newTask)
            
            // Update UI state
            val updatedTasks = musicRepository.getTasks()
            _uiState.value = _uiState.value.copy(tasks = updatedTasks)
            
            simulateProgress(newTask.id, newTask.queueCount!!)
        }
    }

    private fun simulateProgress(taskId: String, initialQueueCount: Int) {
        viewModelScope.launch {
            for (progress in 0..100 step 1) {
                delay(100) // Simulate work
                
                val currentTasks = musicRepository.getTasks()
                val taskToUpdate = currentTasks.find { it.id == taskId }
                
                taskToUpdate?.let { task ->
                    val newQueueCount = if (progress > 0) {
                        // Gradually decrease queue count as progress increases
                        val remainingRatio = (100 - progress) / 100f
                        (initialQueueCount * remainingRatio).toInt()
                    } else initialQueueCount

                    val updatedTask = task.copy(
                        progress = progress,
                        queueCount = newQueueCount
                    )
                    
                    // Update task in repository
                    musicRepository.updateTask(updatedTask)
                    
                    // Update UI state
                    val updatedTasks = musicRepository.getTasks()
                    _uiState.value = _uiState.value.copy(tasks = updatedTasks)
                }
            }
        }
    }
}