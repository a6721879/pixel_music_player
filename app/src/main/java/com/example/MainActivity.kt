package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.player.MusicPlayerController
import com.example.ui.MusicViewModel
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.PlayerOverlay
import com.example.ui.screens.SearchScreen
import com.example.ui.theme.MyApplicationTheme
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.ui.components.CutePixelMascot
import kotlinx.coroutines.delay

enum class MusicScreen(val title: String, val icon: ImageVector, val tag: String) {
    EXPLORE("发现", Icons.Rounded.Explore, "nav_explore"),
    SEARCH("搜索", Icons.Rounded.Search, "nav_search"),
    FAVORITES("我的", Icons.Rounded.Favorite, "nav_favorites")
}

class MainActivity : ComponentActivity() {

    private val viewModel: MusicViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                var showSplashScreen by remember { mutableStateOf(true) }

                if (showSplashScreen) {
                    SplashScreen(onDismiss = { showSplashScreen = false })
                } else {
                    var currentScreen by remember { mutableStateOf(MusicScreen.EXPLORE) }

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            NavigationBar(
                                modifier = Modifier.testTag("bottom_nav_bar"),
                                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
                            ) {
                                listOf(MusicScreen.EXPLORE, MusicScreen.SEARCH, MusicScreen.FAVORITES).forEach { screen ->
                                    NavigationBarItem(
                                        selected = currentScreen == screen,
                                        onClick = { currentScreen = screen },
                                        icon = { Icon(imageVector = screen.icon, contentDescription = screen.title) },
                                        label = { Text(screen.title) },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = MaterialTheme.colorScheme.primary,
                                            selectedTextColor = MaterialTheme.colorScheme.primary,
                                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                        ),
                                        modifier = Modifier.testTag(screen.tag)
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            // Display active screen
                            when (currentScreen) {
                                MusicScreen.EXPLORE -> {
                                    ExploreScreen(
                                        viewModel = viewModel,
                                        onArtistClick = { artistName ->
                                            viewModel.onSearchQueryChanged(artistName)
                                            viewModel.performSearch()
                                            currentScreen = MusicScreen.SEARCH
                                        },
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                MusicScreen.SEARCH -> {
                                    SearchScreen(
                                        viewModel = viewModel,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                MusicScreen.FAVORITES -> {
                                    FavoritesScreen(
                                        viewModel = viewModel,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }

                            // Floating Persistent Player Bar overlay
                            PlayerOverlay(
                                viewModel = viewModel,
                                modifier = Modifier.align(Alignment.BottomCenter)
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release MediaPlayer resource safely when activity is destroyed
        MusicPlayerController.release()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplashScreen(onDismiss: () -> Unit) {
    val loadingPhrases = remember {
        listOf(
            "👾 LOADING SYSTEM DATA...",
            "🎵 CONFIGURE SOUND CHIP...",
            "🎮 LOADING PIXEL WORLD...",
            "✨ READY PLAYER ONE!"
        )
    }
    var currentPhraseIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(650)
            currentPhraseIndex = (currentPhraseIndex + 1) % loadingPhrases.size
        }
    }

    LaunchedEffect(Unit) {
        delay(2600)
        onDismiss()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "SplashBounce")
    val bounceOffset by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "MascotBounce"
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Pixel Mascot with Floating effect
                Box(
                    modifier = Modifier
                        .padding(bottom = 32.dp)
                        .offset(y = bounceOffset.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CutePixelMascot(pixelSizeDp = 12)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // App Title in retro gameboy style
                Text(
                    text = "🎮 像素音乐盒",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "PIXEL MUSIC BOX",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 2.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Loading simulation Text
                Text(
                    text = loadingPhrases[currentPhraseIndex],
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Retro pixelated looking dots indicator
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val pulseTransition = rememberInfiniteTransition(label = "DotsPulse")
                    val pulseAlpha by pulseTransition.animateFloat(
                        initialValue = 0.2f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(600, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "DotsAlpha"
                    )

                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = pulseAlpha))
                    )
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            }

            // Bottom brand credits
            Text(
                text = "© 2026 Retro Pixel Studio",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
            )
        }
    }
}

