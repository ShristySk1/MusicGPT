package com.lalas.musicgpt

import android.content.ComponentName
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.lalas.musicgpt.data.model.GenerationTask
import com.lalas.musicgpt.presentation.components.BottomNavigationBar
import com.lalas.musicgpt.presentation.components.CreateButton
import com.lalas.musicgpt.presentation.components.FloatingPlayerBar
import com.lalas.musicgpt.presentation.screens.EmptyScreen
import com.lalas.musicgpt.presentation.screens.HomePage
import com.lalas.musicgpt.presentation.viewmodels.MusicGPTViewModel
import com.lalas.musicgpt.service.MusicService
import com.lalas.musicgpt.utils.toRawUrl
import kotlinx.coroutines.delay
import kotlinx.coroutines.guava.await
import kotlin.math.pow

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MusicGPTApp() {
    val viewModel: MusicGPTViewModel = hiltViewModel()
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
    //music player
    var controller by remember { mutableStateOf<MediaController?>(null) }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val sessionToken = SessionToken(context, ComponentName(context, MusicService::class.java))
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controller = controllerFuture.await()

        controller?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                // Get current media ID to ensure we're handling the right track
                val currentMediaId = controller?.currentMediaItem?.mediaId
                val expectedTrackId = uiState.currentTrack?.id

                // Only update state if this event is for the currently expected track
                if (currentMediaId == expectedTrackId) {
                    when (playbackState) {
                        Player.STATE_IDLE -> {
                            if (!uiState.isLoading) {
                                viewModel.resetPlayerState()
                            }
                        }

                        Player.STATE_ENDED -> {
                            viewModel.nextTrack()
                        }

                        Player.STATE_BUFFERING -> {
                            viewModel.setLoadingState(true)
                        }

                        Player.STATE_READY -> {
                            viewModel.setLoadingState(false)
                            // Auto-play when ready
                            if (uiState.currentTrack != null) {
                                viewModel.updatePlayingState(true)
                                controller?.play()
                            }
                        }
                    }
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                val currentMediaId = controller?.currentMediaItem?.mediaId
                val expectedTrackId = uiState.currentTrack?.id

                // Only update if this is for the current track and not loading
                if (currentMediaId == expectedTrackId && !uiState.isLoading) {
                    viewModel.updatePlayingState(isPlaying)
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                viewModel.resetPlayerState()
            }
        })
    }
    LaunchedEffect(uiState.currentTrack) {
        uiState.currentTrack?.let { track ->
            controller?.let { mediaController ->
                try {
                    // Create media item for the requested track
                    val mediaItem = MediaItem.Builder()
                        .setUri(
                            track.audioUrl?.toRawUrl(context)
                                ?: "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
                        )
                        .setMediaId(track.id)
                        .setMediaMetadata(
                            MediaMetadata.Builder()
                                .setTitle(track.title)
                                .setArtist("AI Generated")
                                .build()
                        )
                        .build()

                    // Stop current playback immediately
                    mediaController.stop()
                    mediaController.clearMediaItems()

                    // Set new media item
                    mediaController.setMediaItem(mediaItem)
                    mediaController.prepare()

                } catch (e: Exception) {
                    println("Error setting up media: ${e.message}")
                    viewModel.resetPlayerState()
                }
            }
        }
    }


// Add a separate LaunchedEffect for play/pause control
    LaunchedEffect(uiState.isPlaying) {
        controller?.let { mediaController ->
            try {
                if (uiState.isPlaying) {
                    mediaController.play()
                } else {
                    mediaController.pause()
                }
            } catch (e: Exception) {
                // Handle playback control errors
                println("Error controlling playback: ${e.message}")
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            controller?.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xff0A0C0D))

    ) {
        Column(modifier = Modifier.fillMaxSize()) {
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
                                },
                                onSkipClick = { task ->

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
                        uiState.isPlayerVisible -> 190.dp // Player  + bottom nav
                        // Only bottom nav visible
                        else -> 96.dp // Bottom nav + spacing
                    }
                )

        ) {
            CreateButton(
                onShowInputChange = { showCreateInput = it },
            )
        }

        if (showCreateInput) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .align(Alignment.BottomCenter)
                    .imeNestedScroll()
                    .background(Color.Black)
                    .padding(top = 16.dp, bottom = 16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                // Glow box
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f) // glow box width
                        .height(52.dp)
                        .drawBehind {
                            // Outermost glow (largest, most transparent)
                            drawRoundRect(
                                brush = gradientBrush,
                                cornerRadius = CornerRadius(26.dp.toPx()),
                                style = Stroke(width = 20.dp.toPx()),
                                alpha = 0.05f,
                                blendMode = BlendMode.Screen
                            )

                            // Second outer glow
                            drawRoundRect(
                                brush = gradientBrush,
                                cornerRadius = CornerRadius(26.dp.toPx()),
                                style = Stroke(width = 16.dp.toPx()),
                                alpha = 0.08f,
                                blendMode = BlendMode.Screen
                            )

                            // Third outer glow
                            drawRoundRect(
                                brush = gradientBrush,
                                cornerRadius = CornerRadius(26.dp.toPx()),
                                style = Stroke(width = 12.dp.toPx()),
                                alpha = 0.12f,
                                blendMode = BlendMode.Screen
                            )

                            // Middle glow (enhanced)
                            drawRoundRect(
                                brush = gradientBrush,
                                cornerRadius = CornerRadius(26.dp.toPx()),
                                style = Stroke(width = 8.dp.toPx()),
                                alpha = 0.25f,
                                blendMode = BlendMode.Screen
                            )

                            // Inner glow (brighter)
                            drawRoundRect(
                                brush = gradientBrush,
                                cornerRadius = CornerRadius(26.dp.toPx()),
                                style = Stroke(width = 4.dp.toPx()),
                                alpha = 0.5f,
                                blendMode = BlendMode.Screen
                            )

                            // Sharp border (brightest)
                            drawRoundRect(
                                brush = gradientBrush,
                                cornerRadius = CornerRadius(26.dp.toPx()),
                                style = Stroke(width = 1.5.dp.toPx()),
                                alpha = 0.9f
                            )
                        }
                        .shadow(
                            elevation = 32.dp, // Increased from 20.dp
                            shape = RoundedCornerShape(26.dp),
                            ambientColor = Color(0xFFFF8504),
                            spotColor = Color(0xFFFF8504)
                        )
                        // Additional shadow layers for more glow
                        .drawBehind {
                            // Extra ambient glow using drawCircle for softer edges
                            val center = Offset(size.width / 2, size.height / 2)

                            // Large soft glow
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0x20FF8504),
                                        Color(0x10FF8504),
                                        Color.Transparent
                                    ),
                                    radius = size.width * 0.8f
                                ),
                                radius = size.width * 0.7f,
                                center = center,
                                blendMode = BlendMode.Screen
                            )

                            // Medium soft glow
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0x30FF8504),
                                        Color(0x15FF8504),
                                        Color.Transparent
                                    ),
                                    radius = size.width * 0.6f
                                ),
                                radius = size.width * 0.5f,
                                center = center,
                                blendMode = BlendMode.Screen
                            )
                        }
                        .background(
                            Color(0xff16191C),
                            RoundedCornerShape(26.dp)
                        ),
                    contentAlignment = Alignment.CenterStart
                ) {
                    InputWithAnimatedBorder(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        isActive = true, // or based on focus state
                        borderWidth = 2.dp,
                        cornerRadius = 26.dp
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                        ) {
                            IconButton(
                                onClick = {

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
                                    .padding(horizontal = 6.dp)
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
        }

        // Floating Player with smooth animations - positioned above bottom nav
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
                    durationMillis = 400,
                    easing = FastOutSlowInEasing
                )
            ) + fadeOut(
                animationSpec = tween(
                    durationMillis = 300,
                    delayMillis = 50
                ) // Small delay to see slide before fade)
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
                isLoading = uiState.isLoading, // ADD THIS
                onPlayPause = { viewModel.togglePlayPause() },
                onNext = { viewModel.nextTrack() },
                onPrevious = { viewModel.previousTrack() },
                onClose = {  // don't clear currentTrack immediately so using setPlayerVisible
                    viewModel.setPlayerVisible(false)
                }
            )
        }
    }
}
@Composable
fun InputWithAnimatedBorder(
    modifier: Modifier = Modifier,
    isActive: Boolean = true,
    borderWidth: Dp = 2.dp,
    cornerRadius: Dp = 26.dp,
    content: @Composable () -> Unit
) {
    // Infinite rotation animation
    val infiniteTransition = rememberInfiniteTransition(label = "inputBorderAnimation")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "inputRotationAngle"
    )

    // Animate shadow intensity
    val shadowPulse by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shadowPulse"
    )

    // Animate shadow spread
    val shadowSpread by infiniteTransition.animateFloat(
        initialValue = 8f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shadowSpread"
    )

    // Animate border visibility based on active state
    val borderAlpha by animateFloatAsState(
        targetValue = if (isActive) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1800,
            easing = LinearEasing
        ),
        label = "inputBorderAlpha"
    )

    // Gradient colors for the animated border
    val borderColors = listOf(
        Color(0xFFFF8504), // Orange
        Color(0xFF990287), // Purple
        Color(0x00000000), // Transparent
        Color(0x00000000), // Transparent
        Color(0xFFFF8504), // Orange again for seamless loop
    )

    val brush = Brush.sweepGradient(borderColors)

    // Calculate shadow color based on rotation angle
    val shadowColorProgress = (angle / 360f)
    val shadowColor = when {
        shadowColorProgress < 0.5f -> {
            // Interpolate from orange to purple
            lerp(Color(0xFFFF8504), Color(0xFF990287), shadowColorProgress * 2f)
        }
        else -> {
            // Interpolate from purple back to orange
            lerp(Color(0xFF990287), Color(0xFFFF8504), (shadowColorProgress - 0.5f) * 2f)
        }
    }

    // Container with animated shadow
    Box(
        modifier = modifier
            .drawBehind {
                if (isActive && borderAlpha > 0f) {
                    // Draw multiple shadow layers for glow effect
                    val shadowAlpha = borderAlpha * shadowPulse

                    // Outer glow
                    drawRoundRect(
                        color = shadowColor.copy(alpha = 0.1f * shadowAlpha),
                        size = size,
                        cornerRadius = CornerRadius(
                            (cornerRadius + borderWidth).toPx()
                        ),
                        style = Stroke(
                            width = shadowSpread.dp.toPx(),
                            pathEffect = PathEffect.cornerPathEffect(
                                (cornerRadius + borderWidth).toPx()
                            )
                        )
                    )

                    // Middle glow
                    drawRoundRect(
                        color = shadowColor.copy(alpha = 0.2f * shadowAlpha),
                        size = size,
                        cornerRadius = CornerRadius(
                            (cornerRadius + borderWidth).toPx()
                        ),
                        style = Stroke(
                            width = (shadowSpread * 0.6f).dp.toPx(),
                            pathEffect = PathEffect.cornerPathEffect(
                                (cornerRadius + borderWidth).toPx()
                            )
                        )
                    )

                    // Inner glow
                    drawRoundRect(
                        color = shadowColor.copy(alpha = 0.3f * shadowAlpha),
                        size = size,
                        cornerRadius = CornerRadius(
                            (cornerRadius + borderWidth).toPx()
                        ),
                        style = Stroke(
                            width = (shadowSpread * 0.3f).dp.toPx(),
                            pathEffect = PathEffect.cornerPathEffect(
                                (cornerRadius + borderWidth).toPx()
                            )
                        )
                    )
                }
            }
    ) {
        // Outer surface for border
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(cornerRadius + borderWidth),
            color = Color.Transparent
        ) {
            // Inner surface with animated border
            Surface(
                modifier = Modifier
                    .clipToBounds()
                    .fillMaxSize()
                    .padding(borderWidth) // Border thickness
                    .drawWithContent {
                        if (borderAlpha > 0f) {
                            rotate(angle) {
                                drawCircle(
                                    brush = brush,
                                    radius = size.width,
                                    blendMode = BlendMode.ColorDodge,
                                )
                            }
                        }
                        drawContent()
                    },
                color = Color.Black,
                shape = RoundedCornerShape(cornerRadius)
            ) {
                content()
            }
        }
    }
}

// Helper function for color interpolation
fun lerp(start: Color, end: Color, fraction: Float): Color {
    return Color(
        red = start.red + (end.red - start.red) * fraction,
        green = start.green + (end.green - start.green) * fraction,
        blue = start.blue + (end.blue - start.blue) * fraction,
        alpha = start.alpha + (end.alpha - start.alpha) * fraction
    )
}

//// Helper function for color interpolation
//fun lerp(start: Color, end: Color, fraction: Float): Color {
//    return Color(
//        red = start.red + (end.red - start.red) * fraction,
//        green = start.green + (end.green - start.green) * fraction,
//        blue = start.blue + (end.blue - start.blue) * fraction,
//        alpha = start.alpha + (end.alpha - start.alpha) * fraction
//    )
//}
@Preview(showBackground = true, heightDp = 300, widthDp = 150)
@Composable
fun MusicGPTAppPreview() {
    val sampleTasks = listOf(
        GenerationTask(
            id = "1",
            title = "Midnight Dreams",
            originalDescription = "Ambient electronic music",
            progress = 100,
            image = R.drawable.property_1_finish,
        ),
        GenerationTask(
            id = "2",
            title = "Ocean Waves",
            originalDescription = "Nature sounds",
            progress = 100,
            image = R.drawable.property_1_finish,
        )
    )

    // Mock the remember and state management for preview
    CompositionLocalProvider(
        LocalDensity provides Density(1f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xff0A0C0D))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    HomePage(
                        tasks = sampleTasks,
                        onTaskClick = { /* Preview */ },
                        onSkipClick = { /* Preview */ },
                        isPlayerVisible = false,
                        currentPlayingTaskId = null,
                        showCreateButton = false
                    )
                }

                BottomNavigationBar(
                    selectedTab = 0,
                    onTabSelected = { /* Preview */ },
                    isPlayerVisible = false
                )
            }
        }
    }
}