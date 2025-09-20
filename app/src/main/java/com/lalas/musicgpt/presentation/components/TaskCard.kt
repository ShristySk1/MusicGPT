package com.lalas.musicgpt.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.lalas.musicgpt.R
import com.lalas.musicgpt.data.model.GenerationTask
import com.lalas.musicgpt.theme.AppBackground
import com.lalas.musicgpt.theme.Dimensions
import kotlin.math.sin
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

    // Subtle breathing animation for in-progress items
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathingScale"
    )

    // Gentle glow animation for progress
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.0f,
        targetValue = 0.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )


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
                        // Crossfade between icons based on progress key
                        val progressKey = when (task.progress) {
                            0 -> "0"
                            in 1..24 -> "0"
                            in 25..49 -> "25"
                            in 50..74 -> "50"
                            in 75..89 ->"75"
                            in 90..99 -> "90"
                            100 -> "100"
                            else -> "complete"
                        }

                        Crossfade(
                            targetState = progressKey,
                            animationSpec = tween(
                                durationMillis = 900,
                                easing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
                            ),
                            label = "iconCrossfade"
                        ) { key ->
                            val iconRes = when (key) {
                                "0" -> R.drawable.property_1_0
                                "25" -> R.drawable.property_1_25
                                "50" -> R.drawable.property_1_50
                                "75" -> R.drawable.property_1_75
                                "90" -> R.drawable.property_1_90
                                "finish" -> R.drawable.property_1_finish
                                else -> R.drawable.property_1_finish
                            }

                            AsyncImage(
                                model = iconRes,
                                contentScale = ContentScale.FillBounds,
                                modifier = Modifier
                                    .width(imageSize)
                                    .height(imageSize)
                                    .graphicsLayer {
                                        // Breathing animation only on the image itself
                                        scaleX = breathingScale
                                        scaleY = breathingScale
                                        shadowElevation = if (isInProgress) 4.dp.toPx() else 0f
                                    },
                                contentDescription = "Progress",
                                error = painterResource(R.drawable.property_1_finish),
                                placeholder = painterResource(R.drawable.property_1_finish)
                            )
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