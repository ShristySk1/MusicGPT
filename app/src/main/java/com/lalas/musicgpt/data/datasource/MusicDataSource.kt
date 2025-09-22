package com.lalas.musicgpt.data.datasource

import com.lalas.musicgpt.data.model.GenerationTask

interface MusicDataSource {
    fun getTasks(): List<GenerationTask>
}
