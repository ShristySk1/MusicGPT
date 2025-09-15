package com.lalas.musicgpt.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lalas.musicgpt.R
import com.lalas.musicgpt.data.model.GenerationTask

@Composable
fun FloatingPlayerBar(
    currentTrack: GenerationTask?,
    isPlaying: Boolean,
    isLoading: Boolean,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (currentTrack == null) return

    // Bottom floating bar with blur effect
    Box {
    Surface(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(80.dp),
        color = Color(0xE6000000), // Semi-transparent black
        shadowElevation = 16.dp
    ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = Color(0x661D2125),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = Color(0x0DFFFFFF),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Track info and album art
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Album Art
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(currentTrack.colorStart, currentTrack.colorEnd)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            // You can replace this with actual album art
                            Text(
                                text = currentTrack.title.take(2).uppercase(),
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Track Info
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = currentTrack.title,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = currentTrack.description,
                                color = Color.Gray,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Control Buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Previous Button
                        IconButton(
                            onClick = onPrevious,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_previous),
                                contentDescription = "Previous",
                                tint = Color.White,
                            )
                        }
                        IconButton(
                            onClick = if (!isLoading) onPlayPause else { {} }, // Disable when loading
                            modifier = Modifier.size(48.dp)
                        ) {
                            when {
                                isLoading -> {
                                    // Show circular progress indicator
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                }
                                isPlaying -> {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_pause),
                                        contentDescription = "Pause",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                else -> {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_play),
                                        contentDescription = "Play",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }

                        // Next Button
                        IconButton(
                            onClick = onNext,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_next),
                                contentDescription = "Next",
                                tint = Color.White
                            )
                        }
                    }
                }
            }


        }
        // Close Button - Top Right
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .align(Alignment.TopEnd)
                .offset(x = (8).dp, y = (0).dp)
                .size(24.dp)
                .clip(CircleShape)
                .background(
                    color = Color(0xFF181B1E),
                )
                .border(
                    width = 1.dp,
                    color = Color(0x1AFFFFFF),
                    shape = CircleShape
                )
                .clickable { onClose() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_cross),
                contentDescription = "Close",
                tint = Color.White,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun FloatingPlayerBarPreview() {
    val sampleTrack = GenerationTask(
        title = "Midnight Dreams",
        originalDescription = "Ambient Electronic",
        colorStart = Color(0xFF6366F1),
        colorEnd = Color(0xFF8B5CF6),
        image = R.drawable.property_1_finish,
        id = "123"
    )

    MaterialTheme {
        FloatingPlayerBar(
            currentTrack = sampleTrack,
            isPlaying = true,
            isLoading = false,
            onPlayPause = { /* Preview */ },
            onNext = { /* Preview */ },
            onPrevious = { /* Preview */ },
            onClose = { /* Preview */ }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FloatingPlayerBarPausedPreview() {
    val sampleTrack = GenerationTask(
        title = "Ocean Waves",
        originalDescription = "Nature Sounds",
        colorStart = Color(0xFF06B6D4),
        colorEnd = Color(0xFF0284C7),
        image = R.drawable.property_1_finish,
        id = "123"
    )

    MaterialTheme {
        FloatingPlayerBar(
            currentTrack = sampleTrack,
            isPlaying = false,
            isLoading = false,
            onPlayPause = { /* Preview */ },
            onNext = { /* Preview */ },
            onPrevious = { /* Preview */ },
            onClose = { /* Preview */ }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FloatingPlayerBarLoadingPreview() {
    val sampleTrack = GenerationTask(
        title = "Loading Track",
        originalDescription = "Please wait...",
        colorStart = Color(0xFFEF4444),
        colorEnd = Color(0xFFF97316),
        image = R.drawable.property_1_finish,
        id = "123"
    )

    MaterialTheme {
        FloatingPlayerBar(
            currentTrack = sampleTrack,
            isPlaying = false,
            isLoading = true,
            onPlayPause = { /* Preview */ },
            onNext = { /* Preview */ },
            onPrevious = { /* Preview */ },
            onClose = { /* Preview */ }
        )
    }
}

