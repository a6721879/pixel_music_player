package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.Song
import com.example.ui.MusicViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerOverlay(
    viewModel: MusicViewModel,
    modifier: Modifier = Modifier
) {
    val currentSong by viewModel.currentSong.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val currentPosition by viewModel.currentPosition.collectAsStateWithLifecycle()
    val duration by viewModel.duration.collectAsStateWithLifecycle()
    val isPlayerLoading by viewModel.isPlayerLoading.collectAsStateWithLifecycle()
    val playerError by viewModel.playerError.collectAsStateWithLifecycle()
    val favoriteSongs by viewModel.favoriteSongs.collectAsStateWithLifecycle()

    var showFullPlayer by remember { mutableStateOf(false) }

    if (currentSong == null) return

    val activeSong = currentSong!!
    val isFav = favoriteSongs.any { it.newId == activeSong.newId }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Mini Player Bar
        MiniPlayerBar(
            song = activeSong,
            isPlaying = isPlaying,
            isLoading = isPlayerLoading,
            progress = if (duration > 0) currentPosition.toFloat() / duration else 0f,
            onPlayPauseToggle = { viewModel.togglePlayPause() },
            onNextClick = { viewModel.next() },
            onBarClick = { showFullPlayer = true }
        )

        // Full Player Modal Bottom Sheet
        if (showFullPlayer) {
            ModalBottomSheet(
                onDismissRequest = { showFullPlayer = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = MaterialTheme.colorScheme.background,
                dragHandle = { BottomSheetDefaults.DragHandle() },
                modifier = Modifier
                    .fillMaxHeight(0.95f)
                    .testTag("full_player_sheet")
            ) {
                FullPlayerSheetContent(
                    song = activeSong,
                    isPlaying = isPlaying,
                    isLoading = isPlayerLoading,
                    currentPosition = currentPosition,
                    duration = duration,
                    isFavorite = isFav,
                    errorMsg = playerError,
                    onPlayPauseToggle = { viewModel.togglePlayPause() },
                    onNextClick = { viewModel.next() },
                    onPrevClick = { viewModel.previous() },
                    onSeek = { viewModel.seekTo(it) },
                    onFavoriteToggle = { viewModel.toggleFavorite(activeSong) },
                    onDismiss = { showFullPlayer = false }
                )
            }
        }
    }
}

@Composable
fun MiniPlayerBar(
    song: Song,
    isPlaying: Boolean,
    isLoading: Boolean,
    progress: Float,
    onPlayPauseToggle: () -> Unit,
    onNextClick: () -> Unit,
    onBarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(68.dp)
            .testTag("mini_player_bar")
    ) {
        // Pixel Drop Shadow
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = 4.dp, y = 4.dp)
                .background(
                    color = MaterialTheme.colorScheme.onBackground,
                    shape = RoundedCornerShape(4.dp)
                )
        )

        // Main Bar
        Card(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onBarClick() },
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.onBackground),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Retro Square Cover with border
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .border(1.5.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(2.dp))
                            .clip(RoundedCornerShape(2.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!song.cover.isNullOrEmpty()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(song.cover)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Cover",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Rounded.MusicNote,
                                contentDescription = "Song cover",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Titles
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = song.name,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = song.artistName,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Play / Pause Button
                    if (isLoading) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .size(20.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        IconButton(
                            onClick = onPlayPauseToggle,
                            modifier = Modifier
                                .size(36.dp)
                                .border(1.5.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                .testTag("mini_play_pause")
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Next Button
                    IconButton(
                        onClick = onNextClick,
                        modifier = Modifier
                            .size(36.dp)
                            .border(1.5.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                            .testTag("mini_next")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.SkipNext,
                            contentDescription = "Next",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Progress line (Pixel look)
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f)
                )
            }
        }
    }
}

@Composable
fun FullPlayerSheetContent(
    song: Song,
    isPlaying: Boolean,
    isLoading: Boolean,
    currentPosition: Int,
    duration: Int,
    isFavorite: Boolean,
    errorMsg: String?,
    onPlayPauseToggle: () -> Unit,
    onNextClick: () -> Unit,
    onPrevClick: () -> Unit,
    onSeek: (Int) -> Unit,
    onFavoriteToggle: () -> Unit,
    onDismiss: () -> Unit
) {
    // Infinite transition for spinning cassette reels
    val infiniteTransition = rememberInfiniteTransition(label = "TapeRotation")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "TapeRotationAngle"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Retro Cassette Deck Area
        RetroCassettePlayer(
            song = song,
            isPlaying = isPlaying,
            rotationAngle = rotationAngle,
            modifier = Modifier
                .weight(1.1f)
                .fillMaxWidth()
        )

        // Song Title, Artist and Favorite Button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.4f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Retro Square Favorite Button
                IconButton(
                    onClick = onFavoriteToggle,
                    modifier = Modifier
                        .size(44.dp)
                        .border(2.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(4.dp))
                        .testTag("full_player_favorite")
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color(0xFFFF4D4D) else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = song.name,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = song.artistName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Decorative Share Icon in a pixel box
                IconButton(
                    onClick = { /* Omitted unrequested feature */ },
                    modifier = Modifier
                        .size(44.dp)
                        .border(2.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(4.dp))
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Share,
                        contentDescription = "Share info",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Error Message (If any)
            if (!errorMsg.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMsg,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 2,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Seek Bar (Sleek retro linear style slider)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.4f),
            verticalArrangement = Arrangement.Center
        ) {
            var sliderDraggingValue by remember { mutableStateOf<Float?>(null) }
            val sliderValue = sliderDraggingValue ?: currentPosition.toFloat()

            Slider(
                value = sliderValue.coerceIn(0f, duration.toFloat().coerceAtLeast(1f)),
                onValueChange = { sliderDraggingValue = it },
                onValueChangeFinished = {
                    sliderDraggingValue?.let {
                        onSeek(it.roundToInt())
                    }
                    sliderDraggingValue = null
                },
                valueRange = 0f..duration.toFloat().coerceAtLeast(1f),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.onBackground,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("player_progress_slider")
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTime(sliderValue.toInt()),
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatTime(duration),
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Arcade Controls Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous Button (Retro Square)
            IconButton(
                onClick = onPrevClick,
                modifier = Modifier
                    .size(54.dp)
                    .border(2.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(4.dp))
                    .testTag("full_player_prev")
            ) {
                Icon(
                    imageVector = Icons.Rounded.SkipPrevious,
                    contentDescription = "Previous Song",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(30.dp)
                )
            }

            // Big Arcade Play/Pause box button
            Box(
                modifier = Modifier
                    .size(width = 96.dp, height = 64.dp)
            ) {
                // Drop shadow
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .offset(x = 4.dp, y = 4.dp)
                        .background(MaterialTheme.colorScheme.onBackground, RoundedCornerShape(6.dp))
                )
                // Button body
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(2.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(6.dp))
                        .clickable { onPlayPauseToggle() }
                        .testTag("full_player_play_pause"),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Icon(
                            imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            contentDescription = "Play/Pause Toggle",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }

            // Next Button (Retro Square)
            IconButton(
                onClick = onNextClick,
                modifier = Modifier
                    .size(54.dp)
                    .border(2.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(4.dp))
                    .testTag("full_player_next")
            ) {
                Icon(
                    imageVector = Icons.Rounded.SkipNext,
                    contentDescription = "Next Song",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}

@Composable
fun RetroCassettePlayer(
    song: Song,
    isPlaying: Boolean,
    rotationAngle: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        // Pixel Drop Shadow
        Box(
            modifier = Modifier
                .width(280.dp)
                .height(180.dp)
                .offset(6.dp, 6.dp)
                .background(MaterialTheme.colorScheme.onBackground, RoundedCornerShape(8.dp))
        )

        // Cassette main body
        Column(
            modifier = Modifier
                .width(280.dp)
                .height(180.dp)
                .border(3.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(8.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Label sticker area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(2.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(8.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top stripes (Retro chiptune theme colors)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                ) {
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().background(MaterialTheme.colorScheme.primary))
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().background(MaterialTheme.colorScheme.tertiary))
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color(0xFFF1C40F))) // Classic retro yellow
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Song Name Label (Pixelated text label)
                Text(
                    text = song.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )

                // Window showing the cassette reels
                Row(
                    modifier = Modifier
                        .width(130.dp)
                        .height(38.dp)
                        .border(1.5.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(2.dp))
                        .background(Color(0xFF2C3E50)), // Retro cassette window tint
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left reel
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .rotate(if (isPlaying) rotationAngle else 0f),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .border(2.dp, Color.White, CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(24.dp)
                                .background(Color.White)
                        )
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height(2.dp)
                                .background(Color.White)
                        )
                    }

                    // Tape roll indicator (middle ribbon)
                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .height(4.dp)
                            .background(Color(0xFF111111))
                    )

                    // Right reel
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .rotate(if (isPlaying) rotationAngle else 0f),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .border(2.dp, Color.White, CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(24.dp)
                                .background(Color.White)
                        )
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height(2.dp)
                                .background(Color.White)
                        )
                    }
                }
            }

            // Cassette tape bottom trapezoid details
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .width(180.dp)
                    .height(20.dp)
                    .border(2.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 4.dp, bottomEnd = 4.dp))
                    .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f)),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(6.dp).background(MaterialTheme.colorScheme.onBackground, CircleShape))
                Box(modifier = Modifier.size(6.dp).background(MaterialTheme.colorScheme.onBackground, CircleShape))
            }
        }
    }
}

private fun formatTime(ms: Int): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}
