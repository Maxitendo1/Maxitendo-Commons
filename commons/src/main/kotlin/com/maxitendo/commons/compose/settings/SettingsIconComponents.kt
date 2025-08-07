package com.maxitendo.commons.compose.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

val preferenceOrange = Color(0xFFFF9500)
val preferenceBlue = Color(0xFF007BFF)
val preferenceGreen = Color(0xFF34C85A)
val preferencePurple = Color(0xFF5955D6)
val preferenceCyan = Color(0xFF31ADE7)
val preferenceRed = Color(0xFFFF3A2E)
val preferenceYellow = Color(0xFFFFCC01)
val preferenceGrey = Color(0xFF8F8E95)

/**
 * Base settings icon component that creates a rounded square background with an icon
 * @param icon The Material Design icon to display
 * @param backgroundColor The background color for the icon
 * @param modifier Optional modifier for customization
 */

@Composable
fun SettingsIcon(
    icon: ImageVector,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(32.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
    }
}

// COMMON SETTINGS ICONS - Available to all apps

/**
 * Appearance/Theme settings icon
 */
@Composable
fun AppearanceSettingsIcon(modifier: Modifier = Modifier) {
    SettingsIcon(
        icon = Icons.Outlined.Palette,
        backgroundColor = preferenceOrange,
        modifier = modifier
    )
}

/**
 * General settings icon
 */
@Composable
fun GeneralSettingsIcon(modifier: Modifier = Modifier) {
    SettingsIcon(
        icon = Icons.Filled.Settings,
        backgroundColor = preferenceBlue,
        modifier = modifier
    )
}

/**
 * Advanced settings icon
 */
@Composable
fun AdvancedSettingsIcon(modifier: Modifier = Modifier) {
    SettingsIcon(
        icon = Icons.Rounded.SettingsSuggest,
        backgroundColor = preferenceYellow,
        modifier = modifier
    )
}

/**
 * Notifications settings icon
 */
@Composable
fun NotificationsSettingsIcon(modifier: Modifier = Modifier) {
    SettingsIcon(
        icon = Icons.Filled.Notifications,
        backgroundColor = preferenceRed,
        modifier = modifier
    )
}

/**
 * Tip Jar/Support icon
 */
@Composable
fun TipJarSettingsIcon(modifier: Modifier = Modifier) {
    SettingsIcon(
        icon = Icons.Rounded.Savings,
        backgroundColor = preferenceOrange,
        modifier = modifier
    )
}

/**
 * About settings icon
 */
@Composable
fun AboutSettingsIcon(modifier: Modifier = Modifier) {
    SettingsIcon(
        icon = Icons.Outlined.Info,
        backgroundColor = preferenceGrey,
        modifier = modifier
    )
}

// COMMUNICATION APP SPECIFIC ICONS - For apps with communication features

/**
 * Phone/Calls settings icon - for dialer/phone apps
 */
@Composable
fun CallsSettingsIcon(modifier: Modifier = Modifier) {
    SettingsIcon(
        icon = Icons.Filled.Phone,
        backgroundColor = preferenceGreen,
        modifier = modifier
    )
}

/**
 * Messages/SMS settings icon - for messaging apps
 */
@Composable
fun MessagesSettingsIcon(modifier: Modifier = Modifier) {
    SettingsIcon(
        icon = Icons.Filled.Message,
        backgroundColor = preferenceBlue,
        modifier = modifier
    )
}

// UI/UX SPECIFIC ICONS - For apps with customizable interfaces

/**
 * Tabs settings icon - for apps with tab interfaces
 */
@Composable
fun TabsSettingsIcon(modifier: Modifier = Modifier) {
    SettingsIcon(
        icon = Icons.Outlined.Tab,
        backgroundColor = preferenceGreen,
        modifier = modifier
    )
}

/**
 * Swipe gestures settings icon - for apps with gesture controls
 */
@Composable
fun SwipeGesturesSettingsIcon(modifier: Modifier = Modifier) {
    SettingsIcon(
        icon = Icons.Filled.Gesture,
        backgroundColor = preferencePurple,
        modifier = modifier
    )
}

/**
 * List view settings icon - for apps with list/grid view options
 */
@Composable
fun ListViewSettingsIcon(modifier: Modifier = Modifier) {
    SettingsIcon(
        icon = Icons.Outlined.ViewList,
        backgroundColor = preferenceCyan,
        modifier = modifier
    )
}

// UTILITY FUNCTIONS

/**
 * Creates a custom settings icon with specified parameters
 * @param icon The Material Design icon to use
 * @param backgroundColor The background color
 * @param modifier Optional modifier
 */
@Composable
fun CustomSettingsIcon(
    icon: ImageVector,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    SettingsIcon(
        icon = icon,
        backgroundColor = backgroundColor,
        modifier = modifier
    )
}
