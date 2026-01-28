package com.example.clipshift

import androidx.compose.ui.test.MainTestClock
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

class DownloadTest {
    @get:Rule
    var composeTestRule = createAndroidComposeRule<MainActivity>()

    //Mp3 auswählen und youtube-link eingeben, danach runterladen
    @Test
    fun test1(){
        composeTestRule.onNodeWithText("MP4").assertIsDisplayed()
        composeTestRule.onNodeWithText("MP4").performClick()
        composeTestRule.onNodeWithTag("MP3").isDisplayed()
        composeTestRule.onNodeWithText("MP3").performClick()
        composeTestRule.onNodeWithTag("UrlInput").performTextInput("https://youtu.be/dydazik2gsg?si=gIG-4b63XpEMkW5M")
        composeTestRule.onNodeWithText("OK").performClick()
        composeTestRule.waitUntil(15000) {
            composeTestRule
                .onNodeWithTag("TextOutput")
                .fetchSemanticsNode()
                .config.any { it.value.toString().contains("Fertig") }
        }
        composeTestRule.onNodeWithTag("TextOutput").assertTextEquals("✅ Fertig! (Umbenannt zu MP3)")
    }

    //Darkmode an und ausmachen und danach auf Darkmode bleiben
    @Test
    fun test2(){
        composeTestRule.onNodeWithTag("MainContent").assertContentDescriptionEquals("light")
        composeTestRule.onNodeWithTag("DarkModeButton").performClick()
        composeTestRule.onNodeWithTag("MainContent").assertContentDescriptionEquals("dark")
        composeTestRule.onNodeWithTag("DarkModeButton").performClick()
        composeTestRule.onNodeWithTag("MainContent").assertContentDescriptionEquals("light")
        composeTestRule.onNodeWithTag("DarkModeButton").performClick()
        composeTestRule.onNodeWithTag("MainContent").assertContentDescriptionEquals("dark")

    }

    //Expertenmodus testen, Auflösung wählen, audio qualität auswählen und youtube-link eingeben, danach runterladen
    @Test
    fun test3(){
        composeTestRule.onNodeWithTag("ExpertModus").performClick()
        composeTestRule.waitUntil(2000) {
            composeTestRule.onAllNodes(hasText("Auflösung:")).fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("MP3 128 kBit/s (Standard)").performScrollTo()
        composeTestRule.onNodeWithTag("ExpertModusAudioOptionen").isDisplayed()
        composeTestRule.onNodeWithText("MP3 128 kBit/s (Standard)").performClick()
        composeTestRule.onNodeWithTag("UrlInput").performTextInput("https://youtu.be/dydazik2gsg?si=gIG-4b63XpEMkW5M")
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