package com.example.clipshift

import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.test.rule.GrantPermissionRule
import org.junit.Rule
import org.junit.Test

/**
 * UI Tests to test functions
 */
class DownloadTest {
    @get:Rule
    var composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val grantRule: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.POST_NOTIFICATIONS,
        android.Manifest.permission.READ_MEDIA_AUDIO,
        android.Manifest.permission.READ_MEDIA_VIDEO,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )


    /**
     * Tests the dropown menu, URL input and download function by selecting MP3 and converting a video
     */
    @Test
    fun test1(){
        composeTestRule.onNodeWithText("MP4").assertIsDisplayed()
        composeTestRule.onNodeWithText("MP4").performClick()
        composeTestRule.onNodeWithTag("MP3").isDisplayed()
        composeTestRule.onNodeWithText("MP3").performClick()
        composeTestRule.onNodeWithTag("UrlInput").performTextInput("https://www.youtube.com/watch?v=NVVvAH6cd6k")
        composeTestRule.onNodeWithText("OK").performClick()
        composeTestRule.waitUntil(15000) {
            composeTestRule
                .onNodeWithTag("TextOutput")
                .fetchSemanticsNode()
                .config.any { it.value.toString().contains("Fertig") }
        }
        composeTestRule.onNodeWithTag("TextOutput").assertTextEquals("✅ Fertig!")
    }

    /**
     * Checks the functionality of the Dark mode button
     */
    @Test
    fun test2(){

        composeTestRule.onNodeWithTag("MainContent")
            .assertContentDescriptionEquals("light")

        composeTestRule.onNodeWithTag("DarkModeButton").performClick()
        composeTestRule.onNodeWithTag("MainContent")
            .assertContentDescriptionEquals("dark")

        composeTestRule.onNodeWithTag("DarkModeButton").performClick()
        composeTestRule.onNodeWithTag("MainContent")
            .assertContentDescriptionEquals("light")

        composeTestRule.onNodeWithTag("DarkModeButton").performClick()
        composeTestRule.onNodeWithTag("MainContent")
            .assertContentDescriptionEquals("dark")
    }

    /**
     * Checks the functionality of the info button
     */
    @Test
    fun test3(){

        composeTestRule.onNodeWithTag("InfoButton").performClick()
        composeTestRule.onNodeWithText("Über die App").assertIsDisplayed()
        composeTestRule.onNodeWithText("Schließen").performClick()
        composeTestRule.onNodeWithTag("MainContent").isDisplayed()

    }

    /**
     * Tests the Expert mode by selecting MP4, selecting a resolution and converting a video
     */
    @Test
    fun test4(){
        composeTestRule.onNodeWithTag("ExpertModus").performClick()
        composeTestRule.waitUntil(2000) {
            composeTestRule.onAllNodes(hasText("Auflösung:")).fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("SelectableVideo").performScrollToNode(hasText("144p"))
        composeTestRule.onNodeWithText("144p").performClick()
        composeTestRule.onNodeWithTag("UrlInput").performTextInput("https://www.youtube.com/watch?v=44KP0vp2Wvg")
        composeTestRule.onNodeWithText("OK").performClick()
        composeTestRule.waitUntil(15000) {
            composeTestRule
                .onNodeWithTag("TextOutput")
                .fetchSemanticsNode()
                .config.any { it.value.toString().contains("Fertig") }
        }
        composeTestRule.onNodeWithTag("TextOutput").assertTextEquals("✅ Fertig!")
    }
}