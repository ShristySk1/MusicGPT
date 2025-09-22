package com.lalas.musicgpt.data.repository

import com.lalas.musicgpt.data.model.GenerationTask
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    suspend fun getTasks(): List<GenerationTask>
    suspend fun addTask(task: GenerationTask)
    suspend fun updateTask(task: GenerationTask)
    fun getTasksFlow(): Flow<List<GenerationTask>>
}
