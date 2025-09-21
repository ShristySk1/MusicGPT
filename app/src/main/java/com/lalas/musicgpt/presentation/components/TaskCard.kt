package com.lalas.musicgpt.presentation.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.lalas.musicgpt.R
import com.lalas.musicgpt.data.model.GenerationTask
import com.lalas.musicgpt.theme.AppBackground
import com.lalas.musicgpt.theme.Dimensions
import kotlin.random.Random

@Composable
fun TaskCard(
    task: GenerationTask,
    isCurrentlyPlaying: Boolean,
    onSkipClick: () -> Unit,
    onClick: () -> Unit
) {
    val isCompleted = task.progress == 100
    val isInProgress = task.progress in 1..99
    val imageSize = remember { Dimensions.imageSizeLarge }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(imageSize + 16.dp)
            .then(
                if (task.progress == 100 && !isCurrentlyPlaying) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(Dimensions.cornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentlyPlaying) Color(0x8D2C2C2C) else AppBackground
        )
    ) {
        Box {
            // Animated progress overlay with spring animation
            if (isInProgress) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(task.progress / 100f)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xff16191C),
                                    Color(0xFF16191C),
                                )
                            )
                        )
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .padding(start = 8.dp)
            ) {
                // Image container with enhanced animations
                Box(
                    modifier = Modifier.size(imageSize),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(Dimensions.cornerRadius)),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = task.image,
                                contentScale = ContentScale.FillBounds,
                                modifier = Modifier.width(imageSize).height(imageSize),
                                contentDescription = "Album Cover",
                                error = painterResource(R.drawable.property_1_finish), // Fallback
                                placeholder = painterResource(R.drawable.property_1_finish)
                            )
                        }
                    } else {
                        val progressKey = when {
                            task.progress == 0 -> "0"
                            task.progress in 1..15 -> "0"
                            task.progress in 16..35 -> "25"
                            task.progress in 36..55 -> "50"
                            task.progress in 56..75 -> "75"
                            task.progress in 76..99 -> "90"
                            task.progress == 100 -> "100"
                            task.isCompleted -> "finish"
                            else -> "complete"
                        }

                        ImageWithAnimatedBorder (
                            progress = task.progress,
                            imageSize = imageSize,
                        ) {
                            // Your existing Crossfade with drawables
                            Crossfade(
                                targetState = progressKey,
                                animationSpec = tween(
                                    durationMillis = 1800,
                                    delayMillis = 1,
                                    easing = LinearEasing
                                ),
                                label = "iconCrossfade"
                            ) { key ->
                                val iconRes = when (key) {
                                    "0" -> R.drawable.property_1_0
                                    "25" -> R.drawable.property_1_25
                                    "50" -> R.drawable.property_1_50
                                    "75" -> R.drawable.property_1_75
                                    "90" -> R.drawable.property_1_90
                                    "100" -> R.drawable.property_1_100
                                    "finish" -> R.drawable.property_1_finish
                                    else -> R.drawable.property_1_finish
                                }

                                AsyncImage(
                                    model = iconRes,
                                    contentScale = ContentScale.FillBounds,
                                    modifier = Modifier
                                        .fillMaxSize() // Fill the inner box
                                        .clip(RoundedCornerShape(16.dp)), // Slightly smaller radius for inner content
                                    contentDescription = "Progress",
                                    error = painterResource(R.drawable.property_1_finish),
                                    placeholder = painterResource(R.drawable.property_1_finish)
                                )
                            }
                        }
                    }

                    // Show playing overlay only for the currently playing task
                    if (isCurrentlyPlaying) {
                        AsyncImage(
                            model = (R.drawable.playing),
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.width(imageSize).height(imageSize),
                            contentDescription = "Playing",
                            error = painterResource(R.drawable.property_1_finish), // Fallback
                            placeholder = painterResource(R.drawable.property_1_finish)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        modifier = Modifier.padding(start = 16.dp),
                        text = task.title,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    // Dynamic description
                    val displayDescription = when {
                        task.progress in 0..26 -> "Starting AI audio engine..."
                        task.progress in 27..99 -> {
                            val queueCount = task.queueCount ?: (Random.nextInt(15, 25) * 1000)
                            "${(queueCount / 1000f).toInt()}.${(queueCount % 1000) / 100}K users in queue skip"
                        }
                        else -> task.originalDescription
                    }

                    if (task.progress in 27..99 && displayDescription.contains("skip")) {
                        val annotatedString = buildAnnotatedString {
                            val text = displayDescription
                            val skipIndex = text.indexOf("skip")

                            append(text.substring(0, skipIndex))
                            withStyle(
                                style = SpanStyle(
                                    textDecoration = TextDecoration.Underline,
                                    color = Color.Gray
                                )
                            ) {
                                append("skip")
                            }
                            append(text.substring(skipIndex + 4))
                        }

                        Text(
                            text = annotatedString,
                            modifier = Modifier.padding(start = 16.dp),
                            fontSize = 14.sp,
                            maxLines = 1,
                            color = Color.Gray,
                            overflow = TextOverflow.Ellipsis
                        )
                    } else {
                        Text(
                            modifier = Modifier.padding(start = 16.dp),
                            text = displayDescription,
                            color = Color.Gray,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // More options
                if (isCompleted) {
                    IconButton(onClick = { /* Show options */ }) {
                        Text("•••", color = Color.Gray)
                    }
                } else {
                    Spacer(modifier = Modifier.width(48.dp))
                }
            }
        }

    }
}
@Composable
fun DirectAnimatedBorder(
    progress: Int,
    imageSize: Dp,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "border")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "angle"
    )

    val animatedProgress by animateIntAsState(
        targetValue = progress,
        animationSpec = tween(1800, delayMillis = 1, easing = LinearEasing),
        label = "progress"
    )

    val borderAlpha = (animatedProgress / 100f).coerceIn(0f, 1f)
    val borderColors = if (borderAlpha > 0f) {
        listOf(
            Color(0xFFFF6200).copy(alpha = borderAlpha),
            Color(0x80AA00FF).copy(alpha = borderAlpha),
            Color(0x00000000)
        )
    } else {
        listOf(Color.Transparent)
    }

    val brush = Brush.sweepGradient(borderColors)

    Surface(
        modifier = Modifier.size(imageSize),
        shape = RoundedCornerShape(16.dp)
    ) {
        Surface(
            modifier = Modifier
                .clipToBounds()
                .fillMaxSize()
                .padding(2.dp)
                .drawWithContent {
                    if (borderAlpha > 0f) {
                        rotate(angle) {
                            drawCircle(
                                brush = brush,
                                radius = size.width,
                                blendMode = BlendMode.SrcIn,
                            )
                        }
                    }
                    drawContent()
                },
            color = Color.Transparent,
            shape = RoundedCornerShape(14.dp)
        ) {
            content()
        }
    }
}

@Composable
fun ImageWithAnimatedBorder(
    progress: Int,
    imageSize: Dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // Infinite rotation animation
    val infiniteTransition = rememberInfiniteTransition(label = "borderAnimation")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotationAngle"
    )

    // Animate progress for border visibility
    val animatedProgress by animateIntAsState(
        targetValue = progress,
        animationSpec = tween(
            durationMillis = 1800,
            delayMillis = 1,
            easing = LinearEasing
        ),
        label = "borderProgress"
    )

    // Your 3 colors with animated alpha
    val borderAlpha = (animatedProgress / 100f).coerceIn(0f, 1f)
    val borderColors = listOf(
        Color(0xFFFF6200), // Orange
        Color(0x80AA00FF), // Purple with alpha
        Color(0x00000000), // Transparent
        Color(0x00000000), // Transparent
        Color(0xFFFF6200), // Faded orange
    )

    val brush = if (borderAlpha >= 0f) {
        Brush.sweepGradient(borderColors)
    } else {
        Brush.sweepGradient(listOf(Color.Transparent, Color.Transparent))
    }

    // Outer surface for border
    Surface(
        modifier = modifier.size(imageSize + 1.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        // Inner surface with animated border
        Surface(
            modifier = Modifier
                .clipToBounds()
                .fillMaxSize()
                .padding(1.dp) // Border thickness
                .drawWithContent {
                    if (borderAlpha >= 0f) {
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
            shape = RoundedCornerShape(16.dp)
        ) {
            content()
        }
    }
}
@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun TaskCardVariousStatesPreview() {
    val tasks = listOf(
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
            progress = 45,
            audioUrl = R.raw.sample1,
            image = R.drawable.random_3,
            queueCount = 12300
        ),
        GenerationTask(
            id = "4",
            title = "Electronic dance music",
            originalDescription = "Electronic dance music with heavy bass drops",
            progress = 75,
            audioUrl = R.raw.sample2,
            image = R.drawable.random_1,
            queueCount = 8700
        ),
        GenerationTask(
            id = "5",
            title = "Ambient space sounds",
            originalDescription = "Ambient space sounds for meditation and focus",
            progress = 0,
            audioUrl = R.raw.sample1,
            image = R.drawable.random_2
        ),
    )

    MaterialTheme {
        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(tasks) { index, task ->
                TaskCard(
                    task = task,
                    isCurrentlyPlaying = index == 1,
                    onSkipClick = { /* Preview */ },
                    onClick = { /* Preview */ }
                )
            }
        }
    }
}