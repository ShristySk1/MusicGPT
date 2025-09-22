package com.lalas.musicgpt.data.repository

import com.lalas.musicgpt.data.datasource.MusicDataSource
import com.lalas.musicgpt.data.model.GenerationTask
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepositoryImpl @Inject constructor(
    private val localDataSource: MusicDataSource
) : MusicRepository {
    
    private val _tasks = MutableStateFlow<List<GenerationTask>>(emptyList())
    private val tasks: StateFlow<List<GenerationTask>> = _tasks.asStateFlow()
    
    init {
        _tasks.value = localDataSource.getTasks()
    }
    
    override suspend fun getTasks(): List<GenerationTask> {
        return _tasks.value
    }
    
    override suspend fun addTask(task: GenerationTask) {
        val currentTasks = _tasks.value.toMutableList()
        currentTasks.add(task)
        _tasks.value = currentTasks
    }
    
    override suspend fun updateTask(task: GenerationTask) {
        val currentTasks = _tasks.value.toMutableList()
        val index = currentTasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            currentTasks[index] = task
            _tasks.value = currentTasks
        }
    }
    
    override fun getTasksFlow(): Flow<List<GenerationTask>> {
        return tasks
    }
}
