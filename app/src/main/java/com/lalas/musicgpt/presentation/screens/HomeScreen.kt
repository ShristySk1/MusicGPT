package com.lalas.musicgpt.presentation.screens
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lalas.musicgpt.R
import com.lalas.musicgpt.data.model.GenerationTask
import com.lalas.musicgpt.presentation.components.TaskCard
import com.lalas.musicgpt.theme.AppBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    tasks: List<GenerationTask>,
    onTaskClick: (GenerationTask) -> Unit,
    onSkipClick: (GenerationTask) -> Unit,
    isPlayerVisible: Boolean = false,
    currentPlayingTaskId: String? = null,
    showCreateButton: Boolean = true // Add parameter to control create button visibility
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(horizontal = 16.dp)
    ) {
        // Header
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_logo),
                        tint = Color.Unspecified,
                        contentDescription = "Logo"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "MusicGPT",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = AppBackground
            )
        )

        // Task List - Now fills the entire remaining space
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(
                top = 16.dp,
                // Add bottom padding based on what's visible
                bottom = when {
                    // Player visible: need space for player
                    isPlayerVisible -> 96.dp // Player height + some spacing
                    // Create button handled externally, but still need some bottom padding
                    showCreateButton -> 80.dp // Space for floating create button
                    // No floating elements
                    else -> 16.dp
                }
            )
        ) {
            items(tasks) { task ->
                val isCurrentlyPlaying=currentPlayingTaskId == task.id && isPlayerVisible
                TaskCard(
                    task = task,
                    isCurrentlyPlaying = isCurrentlyPlaying,
                    onSkipClick = { onSkipClick(task) },
                    onClick = {
                        // Only allow click if task is completed (progress = 100)
                        if (task.progress == 100 && !isCurrentlyPlaying) {
                            onTaskClick(task)
                        }
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePagePreview() {
    val sampleTasks = listOf(
        GenerationTask(
            id = "1",
            title = "Midnight Dreams",
            originalDescription = "Ambient electronic music",
            progress = 100,
            image = R.drawable.property_1_finish
        ),
        GenerationTask(
            id = "2",
            title = "Ocean Waves",
            originalDescription = "Nature sounds with ambient backing",
            progress = 100,
            image = R.drawable.property_1_finish
        ),
        GenerationTask(
            id = "3",
            title = "Jazz Fusion",
            originalDescription = "Smooth jazz with electronic elements",
            progress = 65,
            image = R.drawable.property_1_finish
        ),
        GenerationTask(
            id = "4",
            title = "Rock Anthem",
            originalDescription = "Heavy guitar with driving drums",
            progress = 15,
            image = R.drawable.property_1_finish
        ),
        GenerationTask(
            id = "5",
            title = "Classical Symphony",
            originalDescription = "Orchestral composition in D minor",
            progress = 0,
            image = R.drawable.property_1_finish
        )
    )

    MaterialTheme {
        HomePage(
            tasks = sampleTasks,
            onTaskClick = { task -> /* Preview */ },
            onSkipClick = { task -> /* Preview */ },
            isPlayerVisible = false,
            currentPlayingTaskId = "1",
            showCreateButton = false
        )
    }
}


