package com.lalas.musicgpt.presentation.components
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import coil.size.Dimension
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
    val imageSize= remember { Dimensions.imageSizeLarge }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(imageSize+16.dp)
            .then(
                if (task.progress == 100 && !isCurrentlyPlaying) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier // No click modifier for incomplete tasks
                }
            ),
        shape = RoundedCornerShape(Dimensions.cornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentlyPlaying) Color(0x8D2C2C2C) else AppBackground
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
                modifier = Modifier.padding( vertical = 8.dp).padding(start = 8.dp)
            ) {
                // Progress indicator with percentage images or album cover
                Box(
                    modifier = Modifier
                        .size(imageSize),
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
                        AsyncImage(
                            model = (iconResource),
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.width(imageSize).height(imageSize),
                            contentDescription = "Playing",
                            error = painterResource(R.drawable.property_1_finish), // Fallback
                            placeholder = painterResource(R.drawable.property_1_finish)
                        )
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


@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun TaskCardVariousStatesPreview() {
    val tasks = listOf(
        GenerationTask(
            id = "1",
            title = "Create a funky house",
            originalDescription = "Create a funky house song with upbeat rhythm",
            progress = 100,
            audioUrl =  R.raw.sample1,
            image = R.drawable.random_1

        ),
        GenerationTask(
            id = "2",
            title = "Lo-fi hip hop",
            originalDescription = "Lo-fi hip hop beats for studying and relaxation",
            progress = 100,
            audioUrl =R.raw.sample2,
            image = R.drawable.random_2
        ),
        GenerationTask(
            id = "3",
            title = "Classical piano composition",
            originalDescription = "Classical piano composition in the style of Chopin",
            progress = 100,
            audioUrl = R.raw.sample1,
            image = R.drawable.random_3
        ),
        GenerationTask(
            id = "4",
            title = "Electronic dance music",
            originalDescription = "Electronic dance music with heavy bass drops",
            progress = 45,
            audioUrl = R.raw.sample2,
            image = R.drawable.random_1,
            queueCount = 12300
        ),
        GenerationTask(
            id = "5",
            title = "Ambient space sounds",
            originalDescription = "Ambient space sounds for meditation and focus",
            progress = 0,
            audioUrl=R.raw.sample1,
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
                    isCurrentlyPlaying = index == 1, // Second item is playing
                    onSkipClick = { /* Preview */ },
                    onClick = { /* Preview */ }
                )
            }
        }
    }
}