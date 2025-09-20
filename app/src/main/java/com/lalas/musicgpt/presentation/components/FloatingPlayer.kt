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
import coil.compose.AsyncImage
import coil.size.Dimension
import com.lalas.musicgpt.R
import com.lalas.musicgpt.data.model.GenerationTask
import com.lalas.musicgpt.theme.Dimensions
import com.lalas.musicgpt.theme.Dimensions.imageSize
import com.lalas.musicgpt.theme.PlayerBackground
import com.lalas.musicgpt.theme.PlayerBorder

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
            .wrapContentHeight(),
        color = Color(0xE6000000), // Semi-transparent black
        shape = RoundedCornerShape(Dimensions.cornerRadius),
        shadowElevation = 16.dp
    ) {
            Column {
                Row(
                    modifier = Modifier
                        .wrapContentSize()
                        .background(
                            color = PlayerBackground,
                            shape = RoundedCornerShape(Dimensions.cornerRadius)
                        )
                        .border(
                            width = 1.dp,
                            color = PlayerBorder,
                            shape = RoundedCornerShape(Dimensions.cornerRadius)
                        )
                        .padding( vertical = 8.dp)
                        .padding(start = 8.dp, end = 16.dp),
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
                                .size(imageSize)
                                .clip(RoundedCornerShape(Dimensions.cornerRadius)),
                            contentAlignment = Alignment.Center
                        )  {
                            AsyncImage(
                                model = currentTrack.image,
                                contentDescription = "Album Cover",
                                modifier = Modifier
                                    .size(imageSize),
                                error = painterResource(R.drawable.property_1_finish), // Fallback
                                placeholder = painterResource(R.drawable.property_1_finish)
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
                                modifier = Modifier.size(Dimensions.iconSize)
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
                                        modifier = Modifier.size(Dimensions.iconSize)
                                    )
                                }
                                else -> {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_play),
                                        contentDescription = "Play",
                                        tint = Color.White,
                                        modifier = Modifier.size(Dimensions.iconSize)
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
                                tint = Color.White,
                                modifier = Modifier.size(Dimensions.iconSize)
                            )
                        }
                    }
                }
            }


        }
    }
}
@Preview(showBackground = true)
@Composable
fun FloatingPlayerBarPreview() {
    val sampleTrack = GenerationTask(
        title = "Midnight Dreams",
        originalDescription = "Ambient Electronic",
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

