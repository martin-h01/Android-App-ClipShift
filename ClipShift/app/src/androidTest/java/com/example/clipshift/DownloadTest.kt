package com.example.clipshift

import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import org.junit.Rule
import org.junit.Test

/**
 * UI Tests to test functions
 *
 * !! If youtube blocks while running the tests the provided youtube links need to be exchanged for new ones
 */
class DownloadTest {
    @get:Rule
    var composeTestRule = createAndroidComposeRule<MainActivity>()
    val context = InstrumentationRegistry
        .getInstrumentation()
        .targetContext

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
        composeTestRule.onNodeWithTag("UrlInput").performTextInput("https://www.youtube.com/watch?v=hZNfyHM12-c")
        composeTestRule.onNodeWithText("OK").performClick()
        composeTestRule.waitUntil(15000) {
            composeTestRule
                .onNodeWithTag("TextOutput")
                .fetchSemanticsNode()
                .config.any { it.value.toString().contains("Fertig") }
        }
        composeTestRule.onNodeWithTag("TextOutput").assertTextEquals(context.getString(R.string.status_done))
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
        composeTestRule.onNodeWithText(context.getString(R.string.about_app_title)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.close_button)).performClick()
        composeTestRule.onNodeWithTag("MainContent").assertIsDisplayed()

    }

    /**
     * Tests the functionality of the language button
     */
    @Test
    fun test4(){

        composeTestRule.onNodeWithTag("MainContent").assertExists()
        composeTestRule.onNodeWithText("Bereit").assertIsDisplayed()

        composeTestRule.onNodeWithText("EN").performClick()

        //warten bis auch wirklich alles aktualisiert ist
        composeTestRule.waitUntil(3_000) {
            composeTestRule
                .onAllNodesWithText("Ready")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        composeTestRule.onNodeWithText("Ready").assertIsDisplayed()

        composeTestRule.onNodeWithText("DE").performClick()
        composeTestRule.waitUntil(3_000) {
            composeTestRule
                .onAllNodesWithText("Bereit")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        composeTestRule.onNodeWithText("Bereit").assertIsDisplayed()

    }

    /**
     * Tests the Expert mode by selecting MP4, selecting a resolution and converting a video
     */
    @Test
    fun test5(){

        composeTestRule.onNodeWithTag("ExpertModus").performClick()

        val resolutionText = context.getString(R.string.resolution)
        composeTestRule.waitUntil(2_000) {
            composeTestRule
                .onAllNodesWithText(resolutionText)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithTag("ExpertModus").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("MP4").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("SelectableVideo")
            .performScrollToNode(hasText("144p"))

        composeTestRule.onNodeWithText("144p").performClick()

        composeTestRule.onNodeWithTag("UrlInput")
                .performTextInput("https://www.youtube.com/watch?v=JxPmTysx_j0")

        composeTestRule.onNodeWithText("OK").performClick()

        val doneText = context.getString(R.string.status_done)
        composeTestRule.waitUntil(15_000) {
            composeTestRule
                .onAllNodesWithTag("TextOutput")
                .fetchSemanticsNodes()
                .any { node ->
                    node.config.toString().contains(doneText)
                }
        }

        composeTestRule.onNodeWithTag("TextOutput").assertTextEquals(doneText)
    }
}