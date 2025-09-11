package com.lalas.musicgpt

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.lalas.musicgpt.data.GenerationTask
import com.lalas.musicgpt.data.MusicGPTViewModel
import com.lalas.musicgpt.data.ui.components.FloatingPlayerBar
import com.lalas.musicgpt.data.ui.components.GenerationScreen
import com.lalas.musicgpt.data.ui.screens.HomePage

@Composable
fun MusicGPTApp() {
    val viewModel = remember { MusicGPTViewModel() }
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xff0A0C0D))
    ) {
        // Main content
        when (uiState.currentScreen) {
            "home" -> HomePage(
                tasks = uiState.tasks,
                onTaskClick = { task ->
                    // When a completed task is clicked, start playing music
                    if (task.progress == 100) {
                        viewModel.playTrack(task)
                    }
                    // Note: Tasks in progress are not clickable due to UI state
                },
                onCreateClick = { prompt ->
                    viewModel.createTask(prompt)
                },
                onSkipClick = { task ->
//                    viewModel.skipTask(task)
                },
                isPlayerVisible = uiState.isPlayerVisible,
                currentPlayingTaskId = uiState.currentTrack?.id
            )
            "generation" -> GenerationScreen(
                currentTask = uiState.currentTask,
                progress = uiState.generationProgress,
                onBack = { viewModel.goHome() }
            )
        }

        // Floating Player with smooth animations
        AnimatedVisibility(
            visible = uiState.isPlayerVisible,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(
                    durationMillis = 400,
                    easing = FastOutSlowInEasing
                )
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = 300,
                    delayMillis = 100
                )
            ),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutLinearInEasing
                )
            ) + fadeOut(
                animationSpec = tween(durationMillis = 200)
            ),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            FloatingPlayerBar(
                currentTrack = uiState.currentTrack,
                isPlaying = uiState.isPlaying,
                onPlayPause = { viewModel.togglePlayPause() },
                onNext = { viewModel.nextTrack() },
                onPrevious = { viewModel.previousTrack() },
                onClose = { viewModel.hidePlayer() }
            )
        }
    }
}