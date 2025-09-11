package com.lalas.musicgpt

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lalas.musicgpt.data.MusicGPTViewModel
import com.lalas.musicgpt.data.ui.components.FloatingPlayerBar
import com.lalas.musicgpt.data.ui.components.GenerationScreen
import com.lalas.musicgpt.data.ui.screens.HomePage

@Composable
fun MusicGPTApp() {
    val viewModel = remember { MusicGPTViewModel() }
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) } // Using index instead of string

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xff0A0C0D))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Main content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (uiState.currentScreen) {
                    "home" -> {
                        when (selectedTab) {
                            0 -> HomePage(
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
//                                    viewModel.skipTask(task)
                                },
                                isPlayerVisible = uiState.isPlayerVisible,
                                currentPlayingTaskId = uiState.currentTrack?.id
                            )
                            1 -> EmptyScreen("Discover", "🔍")
                            2 -> EmptyScreen("Reels", "🎵")
                            3 -> EmptyScreen("Profile", "👤")
                        }
                    }
                    "generation" -> GenerationScreen(
                        currentTask = uiState.currentTask,
                        progress = uiState.generationProgress,
                        onBack = { viewModel.goHome() }
                    )
                }
            }

            // Bottom Navigation (only show when not in generation screen)
            if (uiState.currentScreen == "home") {
                BottomNavigationBar(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    isPlayerVisible = uiState.isPlayerVisible
                )
            }
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

@Composable
fun BottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    isPlayerVisible: Boolean
) {
    val bottomPadding = if (isPlayerVisible) 80.dp else 0.dp

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = bottomPadding),
        containerColor = Color(0xff0A0C0D),
        contentColor = Color.White,

    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_ai),
                    contentDescription = "AI"
                )
            },
            label = null,
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color(0xff6B7280),
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_discover),
                    contentDescription = "Discover"
                )
            },
            label = null,
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color(0xff6B7280),
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_reel),
                    contentDescription = "Reels"
                )
            },
            label = null,
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color(0xff6B7280),
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_user),
                    contentDescription = "Profile"
                )
            },
            label = null,
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color(0xff6B7280),
                indicatorColor = Color.Transparent
            )
        )
    }
}

@Composable
fun EmptyScreen(title: String, emoji: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xff0A0C0D)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = emoji,
                fontSize = 48.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "$title Coming Soon",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "This feature is under development",
                color = Color(0xff6B7280),
                fontSize = 14.sp
            )
        }
    }
}