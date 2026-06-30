package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.MusicNote
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.ArtistInfo
import com.example.data.model.Song

import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun PixelEqualizer(
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(2.dp))
            .padding(horizontal = 4.dp, vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "Equalizer")
        
        val bar1Height by infiniteTransition.animateFloat(
            initialValue = 4f,
            targetValue = 16f,
            animationSpec = infiniteRepeatable(
                animation = tween(400, easing = FastOutLinearInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "Bar1"
        )
        val bar2Height by infiniteTransition.animateFloat(
            initialValue = 6f,
            targetValue = 18f,
            animationSpec = infiniteRepeatable(
                animation = tween(300, easing = LinearOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "Bar2"
        )
        val bar3Height by infiniteTransition.animateFloat(
            initialValue = 3f,
            targetValue = 14f,
            animationSpec = infiniteRepeatable(
                animation = tween(500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "Bar3"
        )

        val activeColor = MaterialTheme.colorScheme.primary

        Box(
            modifier = Modifier
                .width(2.dp)
                .height(if (isPlaying) bar1Height.dp else 4.dp)
                .background(activeColor)
        )
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(if (isPlaying) bar2Height.dp else 6.dp)
                .background(activeColor)
        )
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(if (isPlaying) bar3Height.dp else 3.dp)
                .background(activeColor)
        )
    }
}

@Composable
fun SongRowItem(
    song: Song,
    isCurrent: Boolean,
    isPlaying: Boolean,
    isFavorite: Boolean,
    onPlayClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Pulsing/Breathing scale animation instead of rotation if playing
    val pulseTransition = rememberInfiniteTransition(label = "Pulse")
    val pulseScale by pulseTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("song_item_${song.newId}")
    ) {
        // Pixel Drop Shadow (3D block shadow)
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = 4.dp, y = 4.dp)
                .background(
                    color = MaterialTheme.colorScheme.onBackground,
                    shape = RoundedCornerShape(4.dp)
                )
        )

        // Pixel Frame Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.onBackground),
            colors = CardDefaults.cardColors(
                containerColor = if (isCurrent) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surface
                }
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .clickable { onPlayClick() }
                    .padding(12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Song Cover with Retro Pixel Border
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .border(2.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(2.dp))
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
                            contentDescription = "Cover for ${song.name}",
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    if (isCurrent && isPlaying) {
                                        scaleX = pulseScale
                                        scaleY = pulseScale
                                    }
                                },
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.MusicNote,
                            contentDescription = "No cover",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(24.dp)
                                .graphicsLayer {
                                    if (isCurrent && isPlaying) {
                                        scaleX = pulseScale
                                        scaleY = pulseScale
                                    }
                                }
                        )
                    }

                    // Overlay pixel equalizer if currently selected/playing
                    if (isCurrent) {
                        PixelEqualizer(
                            isPlaying = isPlaying,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Song Info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = song.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${song.artistName} | ${song.albumName ?: "未知"}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Favorite Button (Styled as retro square button)
                IconButton(
                    onClick = onFavoriteToggle,
                    modifier = Modifier
                        .size(36.dp)
                        .border(1.5.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(4.dp))
                        .background(
                            if (isFavorite) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            else Color.Transparent,
                            RoundedCornerShape(4.dp)
                        )
                        .testTag("favorite_button_${song.newId}")
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (isFavorite) "取消收藏" else "收藏",
                        tint = if (isFavorite) Color(0xFFFF4D4D) else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ArtistCard(
    artist: ArtistInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(96.dp)
            .clickable { onClick() }
            .padding(4.dp)
            .testTag("artist_card_${artist.name}"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Styled Pixel Avatar Frame
        Box(
            modifier = Modifier.size(68.dp)
        ) {
            // Drop shadow
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = 3.dp, y = 3.dp)
                    .background(MaterialTheme.colorScheme.onBackground, RoundedCornerShape(4.dp))
            )
            // Foreground Image
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(2.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(4.dp))
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                if (!artist.pic.isNullOrEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(artist.pic)
                            .crossfade(true)
                            .build(),
                        contentDescription = artist.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = artist.name.take(1),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = artist.name,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun EmptyState(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(24.dp)
    ) {
        // Drop shadow
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = 4.dp, y = 4.dp)
                .background(MaterialTheme.colorScheme.onBackground, RoundedCornerShape(8.dp))
        )

        // Main Dialog Frame
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .border(2.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.MusicNote,
                    contentDescription = "Empty music",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            )
        }
    }
}

@Composable
fun CutePixelMascot(
    modifier: Modifier = Modifier,
    pixelSizeDp: Int = 3
) {
    // 0 = transparent, 1 = body, 2 = headphones, 3 = eyes/mouth, 4 = blush
    val frame1 = arrayOf(
        intArrayOf(0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0),
        intArrayOf(0, 1, 1, 0, 0, 2, 2, 0, 0, 1, 1, 0),
        intArrayOf(2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2),
        intArrayOf(2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2),
        intArrayOf(2, 1, 3, 1, 1, 1, 1, 1, 3, 1, 1, 2),
        intArrayOf(0, 1, 1, 1, 1, 3, 3, 1, 1, 1, 1, 0),
        intArrayOf(0, 1, 4, 1, 1, 1, 1, 1, 1, 4, 1, 0),
        intArrayOf(0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0),
        intArrayOf(0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0),
        intArrayOf(0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0),
        intArrayOf(0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0),
        intArrayOf(0, 0, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0)
    )

    val frame2 = arrayOf(
        intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        intArrayOf(0, 0, 1, 0, 0, 2, 2, 0, 0, 1, 0, 0),
        intArrayOf(2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2),
        intArrayOf(2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2),
        intArrayOf(2, 1, 3, 1, 1, 1, 1, 1, 3, 1, 1, 2),
        intArrayOf(0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0),
        intArrayOf(0, 1, 4, 1, 1, 3, 3, 1, 1, 4, 1, 0),
        intArrayOf(0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0),
        intArrayOf(0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0),
        intArrayOf(0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0),
        intArrayOf(0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0),
        intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
    )

    val infiniteTransition = rememberInfiniteTransition(label = "MascotAnim")
    val frameIndex by infiniteTransition.animateValue(
        initialValue = 0,
        targetValue = 2,
        typeConverter = Int.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "MascotFrame"
    )

    val activeFrame = if (frameIndex == 0) frame1 else frame2

    val bodyColor = MaterialTheme.colorScheme.primary
    val headphoneColor = MaterialTheme.colorScheme.secondary
    val eyeColor = MaterialTheme.colorScheme.onBackground
    val blushColor = Color(0xFFFF7675)

    androidx.compose.foundation.Canvas(
        modifier = modifier
            .size((12 * pixelSizeDp).dp)
    ) {
        val pxWidth = size.width / 12
        val pxHeight = size.height / 12

        for (row in 0 until 12) {
            for (col in 0 until 12) {
                val colorVal = activeFrame[row][col]
                if (colorVal != 0) {
                    val color = when (colorVal) {
                        1 -> bodyColor
                        2 -> headphoneColor
                        3 -> eyeColor
                        4 -> blushColor
                        else -> Color.Transparent
                    }
                    drawRect(
                        color = color,
                        topLeft = androidx.compose.ui.geometry.Offset(col * pxWidth, row * pxHeight),
                        size = androidx.compose.ui.geometry.Size(pxWidth, pxHeight)
                    )
                }
            }
        }
    }
}
