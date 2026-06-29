package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.example.data.model.Song
import com.example.ui.components.CutePixelMascot
import com.example.ui.components.EmptyState
import com.example.ui.components.SongRowItem
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class AppScreenshotTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun mascot_animated_screenshot() {
        composeTestRule.setContent {
            MyApplicationTheme {
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    CutePixelMascot(pixelSizeDp = 10)
                }
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().captureRoboImage(filePath = "../assets/mascot_animated.png")
    }

    @Test
    fun song_row_item_screenshot() {
        val fakeSong = Song(
            newId = "test_song_1",
            name = "七里香 (Common Jasmine Orange)",
            cover = "",
            artistName = "周杰伦 (Jay Chou)",
            albumName = "七里香"
        )
        composeTestRule.setContent {
            MyApplicationTheme {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    SongRowItem(
                        song = fakeSong,
                        isCurrent = true,
                        isPlaying = true,
                        isFavorite = true,
                        onPlayClick = {},
                        onFavoriteToggle = {}
                    )
                }
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().captureRoboImage(filePath = "../assets/song_row_item.png")
    }

    @Test
    fun empty_state_screenshot() {
        composeTestRule.setContent {
            MyApplicationTheme {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyState(
                        message = "★ 像素电玩城音乐搜索 ★\n试着搜搜「周杰伦」或「许嵩」吧！"
                    )
                }
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().captureRoboImage(filePath = "../assets/empty_state.png")
    }
}
