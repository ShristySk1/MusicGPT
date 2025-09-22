package com.lalas.musicgpt.data.datasource

import com.lalas.musicgpt.R
import com.lalas.musicgpt.data.model.GenerationTask
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalMusicDataSource @Inject constructor() : MusicDataSource {
    
    override fun getTasks(): List<GenerationTask> {
        return listOf(
            GenerationTask(
                id = "1",
                title = "Create a funky house",
                originalDescription = "Create a funky house song with upbeat rhythm",
                progress = 100,
                audioUrl = R.raw.sample1,
                image = R.drawable.random_1
            ),
            GenerationTask(
                id = "2",
                title = "Lo-fi hip hop",
                originalDescription = "Lo-fi hip hop beats for studying and relaxation",
                progress = 100,
                audioUrl = R.raw.sample2,
                image = R.drawable.random_2
            ),
            GenerationTask(
                id = "3",
                title = "Classical piano composition",
                originalDescription = "Classical piano composition in the style of Chopin",
                progress = 100,
                audioUrl = R.raw.sample1,
                image = R.drawable.random_3
            ),
            GenerationTask(
                id = "4",
                title = "Electronic dance music",
                originalDescription = "Electronic dance music with heavy bass drops",
                progress = 100,
                audioUrl = R.raw.sample2,
                image = R.drawable.random_1
            ),
            GenerationTask(
                id = "5",
                title = "Ambient space sounds",
                originalDescription = "Ambient space sounds for meditation and focus",
                progress = 100,
                audioUrl = R.raw.sample1,
                image = R.drawable.random_2
            ),
        )
    }
}
