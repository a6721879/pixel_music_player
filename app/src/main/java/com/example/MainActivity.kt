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

    override fun onDestroy() {
        super.onDestroy()
        // Release MediaPlayer resource safely when activity is destroyed
        MusicPlayerController.release()
    }
}

