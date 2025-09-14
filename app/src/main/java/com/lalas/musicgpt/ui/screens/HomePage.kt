package com.lalas.musicgpt.ui.screens
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.lalas.musicgpt.R
import com.lalas.musicgpt.data.GenerationTask
import kotlin.random.Random

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
            .background(Color.Black)
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
                containerColor = Color.Black
            )
        )

        // Task List - Now fills the entire remaining space
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
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

@Composable
fun TaskCard(
    task: GenerationTask,
    isCurrentlyPlaying: Boolean,
    onSkipClick: () -> Unit,
    onClick: () -> Unit
) {
    val isCompleted = task.progress == 100
    val isInProgress = task.progress in 1..99
    val imageSize= remember { 68.dp }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(imageSize)
            .then(
                if (task.progress == 100 && !isCurrentlyPlaying) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier // No click modifier for incomplete tasks
                }
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentlyPlaying) Color(0x8D2C2C2C) else Color(0xFF000000)
        )
    ) {
        Box {
            // Show progress status overlay
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
                modifier = Modifier.padding(4.dp)
            ) {
                // Progress indicator with percentage images or album cover
                Box(
                    modifier = Modifier
                        .size(imageSize),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        // Show random album cover for completed tasks
                        val randomAlbum = remember(task.id) {
                            listOf(R.drawable.random_1, R.drawable.random_2, R.drawable.random_3).random()
                        }

                        AsyncImage(
                            model = randomAlbum,
                            contentDescription = "Album Cover",
                            modifier = Modifier
                                .size(imageSize),
                            error = painterResource(R.drawable.property_1_finish), // Fallback
                            placeholder = painterResource(R.drawable.property_1_finish)
                        )
                    } else {
                        // Use different icons based on progress for incomplete tasks
                        val iconResource = when (task.progress) {
                            0 -> R.drawable.property_1_0
                            in 1..24 -> R.drawable.property_1_0
                            in 25..49 -> R.drawable.property_1_25
                            in 50..74 -> R.drawable.property_1_50
                            in 75..89 -> R.drawable.property_1_75
                            in 90..99 -> R.drawable.property_1_90
                            else -> R.drawable.property_1_finish
                        }

                        Icon(
                            painter = painterResource(iconResource),
                            contentDescription = "${task.progress}%",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(imageSize)
                        )
                    }

                    // Show playing overlay only for the currently playing task
                    if (isCurrentlyPlaying) {
                        Icon(
                            painter = painterResource(R.drawable.playing),
                            contentDescription = "Playing",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(imageSize)
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

                    // Dynamic description based on progress
                    val displayDescription = when {
                        task.progress in 0..26 -> "Starting AI audio engine..."
                        task.progress in 27..99 -> {
                            val queueCount = task.queueCount ?: (Random.nextInt(15, 25) * 1000)
                            "${(queueCount / 1000f).toInt()}.${(queueCount % 1000) / 100}K users in queue skip"
                        }
                        else -> task.originalDescription
                    }

                    if (task.progress in 27..99 && displayDescription.contains("skip")) {
                        // Create styled "skip" text (non-clickable)
                        val annotatedString = buildAnnotatedString {
                            val text = displayDescription
                            val skipIndex = text.indexOf("skip")

                            append(text.substring(0, skipIndex))
                            withStyle(
                                style = SpanStyle(
                                    textDecoration = TextDecoration.Underline, // keep underline
                                    color = Color.Gray
                                )
                            ) {
                                append("skip")
                            }
                            append(text.substring(skipIndex + 4)) // rest of text after "skip"
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

                // More options - only show for completed tasks
                if (isCompleted) {
                    IconButton(onClick = { /* Show options */ }) {
                        Text("•••", color = Color.Gray)
                    }
                } else {
                    // Add some padding to maintain layout consistency
                    Spacer(modifier = Modifier.width(48.dp))
                }
            }
        }
    }
}

@Composable
fun ClickableText(
    text: androidx.compose.ui.text.AnnotatedString,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    onClick: (Int) -> Unit
) {
    androidx.compose.foundation.text.ClickableText(
        text = text,
        modifier = modifier,
        style = style,
        onClick = onClick
    )
}

// Enhanced version with functional input - Keep this for external use
@Composable
fun CreateSongInputEnhanced(
    onShowInputChange: (Boolean) -> Unit,
) {
            Surface(
                onClick = { onShowInputChange(true) },
                shape = RoundedCornerShape(25.dp),
                contentColor = Color.White,
                color = Color(0xE6000000),
                shadowElevation = 16.dp
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(color = Color(0x1AFFFFFF))
                        .padding(
                        top = 8.dp,
                        bottom = 8.dp,
                        start = 16.dp,
                        end = 18.dp
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_star),
                        contentDescription = null,
                        tint = Color.Unspecified
                    )
                    Text(
                        "Create",
                        fontSize = 16.sp
                    )
                }
    }
}