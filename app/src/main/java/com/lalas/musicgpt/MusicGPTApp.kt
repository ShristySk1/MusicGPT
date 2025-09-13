package com.lalas.musicgpt

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lalas.musicgpt.data.MusicGPTViewModel
import com.lalas.musicgpt.data.ui.components.FloatingPlayerBar
import com.lalas.musicgpt.data.ui.components.GenerationScreen
import com.lalas.musicgpt.data.ui.screens.HomePage
import com.lalas.musicgpt.data.ui.screens.CreateSongInputEnhanced
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.lalas.musicgpt.R
import com.lalas.musicgpt.data.GenerationTask
import kotlinx.coroutines.delay
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MusicGPTApp() {
    val viewModel = remember { MusicGPTViewModel() }
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }

    // State for create button
    var showCreateInput by remember { mutableStateOf(false) }

    // Get IME bottom inset (keyboard height when visible)
    val imeBottom = WindowInsets.ime.getBottom(LocalDensity.current)
    val isKeyboardVisible = imeBottom > 100

    var inputText by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    // Handle keyboard visibility changes
    LaunchedEffect(isKeyboardVisible) {
        if (!isKeyboardVisible && showCreateInput) {
            // Keyboard was hidden, hide the input and show create button
            showCreateInput = false
            inputText = ""
        }
    }

    // Auto-focus when showCreateInput becomes true
    LaunchedEffect(showCreateInput) {
        if (showCreateInput) {
            focusRequester.requestFocus()
        }
    }

    val gradientColors = listOf(
        Color(0xFFFF8504),
        Color(0xFF990287),
    )

    val gradientBrush = Brush.linearGradient(
        colors = gradientColors
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xff0A0C0D))

    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Main content - apply keyboard insets only to scrollable content
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
                                onSkipClick = { task ->
//                                    viewModel.skipTask(task)
                                },
                                isPlayerVisible = uiState.isPlayerVisible,
                                currentPlayingTaskId = uiState.currentTrack?.id,
                                // Remove create functionality from HomePage since it's now handled here
                                showCreateButton = false
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

            // Bottom Navigation - removed windowInsetsPadding so it stays fixed
            if (uiState.currentScreen == "home") {
                BottomNavigationBar(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    isPlayerVisible = uiState.isPlayerVisible
                )
            }
        }

        // Floating Create Button - positioned above player and bottom nav
        // Only show on home screen, AI tab, and when input is not shown
        AnimatedVisibility(
            visible = uiState.currentScreen == "home" && selectedTab == 0 && !showCreateInput,
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
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    bottom = when {
                        // Both player and bottom nav visible
                        uiState.isPlayerVisible -> 190.dp // Player (80dp) + bottom nav (80dp)
                        // Only bottom nav visible
                        else -> 96.dp // Bottom nav (80dp) + spacing (16dp)
                    }
                )

        ) {
            CreateSongInputEnhanced(
                onShowInputChange = { showCreateInput = it },
            )
        }

        if(showCreateInput){
            // Full-width black background container
            Box(
                modifier = Modifier
                    .fillMaxWidth() // full screen width
                    .imePadding()
                    .align(Alignment.BottomCenter)
                    .imeNestedScroll()
                    .background(Color.Black).padding(top = 16.dp, bottom = 16.dp), // full-width black
                contentAlignment = Alignment.BottomCenter
            ) {
                // Glow box inside full-width black container
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f) // glow box width
                        .height(52.dp)
                        .drawBehind {
                            // Outer glow
                            drawRoundRect(
                                brush = gradientBrush,
                                cornerRadius = CornerRadius(26.dp.toPx()),
                                style = Stroke(width = 12.dp.toPx()),
                                alpha = 0.1f,
                                blendMode = BlendMode.Screen
                            )
                            // Middle glow
                            drawRoundRect(
                                brush = gradientBrush,
                                cornerRadius = CornerRadius(26.dp.toPx()),
                                style = Stroke(width = 6.dp.toPx()),
                                alpha = 0.3f,
                                blendMode = BlendMode.Screen
                            )
                            // Inner glow
                            drawRoundRect(
                                brush = gradientBrush,
                                cornerRadius = CornerRadius(26.dp.toPx()),
                                style = Stroke(width = 3.dp.toPx()),
                                alpha = 0.6f
                            )
                            // Sharp border
                            drawRoundRect(
                                brush = gradientBrush,
                                cornerRadius = CornerRadius(26.dp.toPx()),
                                style = Stroke(width = 1.dp.toPx())
                            )
                        }
                        .shadow(
                            elevation = 20.dp,
                            shape = RoundedCornerShape(26.dp),
                            ambientColor = Color(0xFFFF8504),
                            spotColor = Color(0xFFFF8504)
                        )
                        .background(
                            Color(0xff16191C),
                            RoundedCornerShape(26.dp)
                        ),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        IconButton(
                            onClick = {
                                showCreateInput = false
                                inputText = ""
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_add),
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }

                        BasicTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            singleLine = true,
                            textStyle = TextStyle(
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            cursorBrush = SolidColor(Color.White),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Send
                            ),
                            keyboardActions = KeyboardActions(
                                onSend = {
                                    if (inputText.isNotBlank()) {
                                        viewModel.createTask(inputText.trim())
                                        showCreateInput = false
                                        inputText = ""
                                    }
                                }
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 12.dp)
                                .focusRequester(focusRequester),
                            decorationBox = { innerTextField ->
                                Box {
                                    if (inputText.isEmpty()) {
                                        Text(
                                            "Create song",
                                            color = Color(0xff777A80),
                                            fontSize = 16.sp
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        )

                        IconButton(
                            onClick = {
                                if (inputText.isNotBlank()) {
                                    viewModel.createTask(inputText.trim())
                                    showCreateInput = false
                                    inputText = ""
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_send),
                                contentDescription = "Send",
                                tint = Color.Unspecified
                            )
                        }
                    }
                }
            }

        }

        // Floating Player with smooth animations - positioned above bottom nav
        // Hide when keyboard is visible on home screen
        AnimatedVisibility(
            visible = uiState.isPlayerVisible && !(uiState.currentScreen == "home" && isKeyboardVisible),
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
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    bottom = if (uiState.currentScreen == "home") 80.dp else 0.dp // Add padding for bottom nav
                )
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
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp), // Fixed height for consistent spacing
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
            .background(Color(0xff0A0C0D))
            .padding(bottom = 80.dp), // Add padding for bottom nav
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