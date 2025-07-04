package com.maxitendo.commons.compose.screens

import android.app.Activity
import android.widget.TextView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.text.HtmlCompat
import com.maxitendo.commons.R
import com.maxitendo.commons.compose.extensions.MyDevices
import com.maxitendo.commons.compose.lists.SimpleColumnScaffold
import com.maxitendo.commons.compose.theme.AppThemeSurface
import com.maxitendo.commons.extensions.baseConfig
import com.maxitendo.commons.extensions.isPlayStoreInstalled

import com.maxitendo.strings.R as stringsR

@Composable
internal fun AboutScreen(
    goBack: () -> Unit,
    aboutSection: @Composable () -> Unit,
    isTopAppBarColorIcon: Boolean,
    isTopAppBarColorTitle: Boolean,
) {
    SimpleColumnScaffold(
        title = stringResource(id = R.string.about),
        goBack = goBack,
        isTopAppBarColorIcon = isTopAppBarColorIcon,
        isTopAppBarColorTitle = isTopAppBarColorTitle,
    ) {
        aboutSection()
    }
}

@MyDevices
@Composable
private fun AboutScreenPreview() {
    AppThemeSurface {
        AboutScreen(
            goBack = {},
            aboutSection = {
                AboutNewSection(
                    setupFAQ = true,
                    appName = "Common",
                    appVersion = "1.0",
                    onRateUsClick = {},
                    onMoreAppsClick = {},
                    onPrivacyPolicyClick = {},
                    onFAQClick = {},
                    onTipJarClick = {},
                    onGithubClick = {},
                    showGithub = true,
                )
            },
            isTopAppBarColorIcon = true,
            isTopAppBarColorTitle = true,
        )
    }
}

@Composable
internal fun AboutNewSection(
    setupFAQ: Boolean = true,
    appName: String,
    appVersion: String,
    onRateUsClick: () -> Unit,
    onMoreAppsClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onFAQClick: () -> Unit,
    onTipJarClick: () -> Unit,
    onGithubClick: () -> Unit,
    showGithub: Boolean = true,
) {
    Box(
        modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.about_margin))
    ) {
        val context = LocalContext.current
        val textColor = MaterialTheme.colorScheme.onSurface
        val playStoreInstalled = context.isPlayStoreInstalled()
        val ruStoreInstalled = false
        val hideAllExternalLinks = try {
            context.resources.getBoolean(R.bool.hide_all_external_links)
        } catch (e: Exception) {
            false
        }
        Column(Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 26.dp)) {
            ListItem(
                modifier = Modifier
                    .fillMaxWidth(),
                leadingContent = {
                    Box(
                        modifier = Modifier.width(72.dp).padding(bottom = 8.dp),
                        contentAlignment = Alignment.Center
                    )
                    {
                        // Get the current app icon from the activity
                        val activity = context as? Activity
                        val appIconIds = activity?.intent?.getIntegerArrayListExtra("app_icon_ids") ?: arrayListOf()
                        val currentIconId = if (appIconIds.isNotEmpty()) {
                            // Use the first icon as default, or implement logic to get current selected icon
                            appIconIds[0]
                        } else {
                            // Fallback to a generic icon from commons
                            com.maxitendo.commons.R.drawable.ic_person_vector
                        }

                        // Load icon as bitmap to handle both mipmap and drawable resources
                        val iconPainter = remember(currentIconId) {
                            val drawable = ContextCompat.getDrawable(context, currentIconId)
                            if (drawable != null) {
                                BitmapPainter(drawable.toBitmap().asImageBitmap())
                            } else {
                                // Fallback to a generic icon from commons
                                val fallbackDrawable = ContextCompat.getDrawable(context, com.maxitendo.commons.R.drawable.ic_person_vector)
                                if (fallbackDrawable != null) {
                                    BitmapPainter(fallbackDrawable.toBitmap().asImageBitmap())
                                } else {
                                    // Ultimate fallback - create a simple colored painter
                                    BitmapPainter(
                                        android.graphics.Bitmap.createBitmap(1, 1, android.graphics.Bitmap.Config.ARGB_8888)
                                            .apply { eraseColor(android.graphics.Color.GRAY) }
                                            .asImageBitmap()
                                    )
                                }
                            }
                        }

                        Icon(
                            modifier = Modifier
                                .size(68.dp)
                                .shadow(elevation = 10.dp, shape = RoundedCornerShape(34.dp), clip = true),
                            painter = iconPainter,
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                        Icon(
                            modifier = Modifier
                                .size(72.dp),
                            painter = iconPainter,
                            contentDescription = appName,
                            tint = Color.Unspecified
                        )
                    }
                },
                headlineContent = {
                    Text(
                        modifier = Modifier.padding(start = 12.dp),
                        text = appName,
                        fontSize = 18.sp,
                    )
                },
                supportingContent = {
                    Text(
                        modifier = Modifier.padding(start = 12.dp),
                        text = "Version: $appVersion",
                        color = textColor.copy(alpha = 0.5F),
                    )
                },
            )
            Spacer(modifier = Modifier.size(8.dp))
            HtmlText(stringResource(stringsR.string.about_summary), textColor = textColor)
            Spacer(modifier = Modifier.size(24.dp))
            if (!hideAllExternalLinks && (playStoreInstalled || ruStoreInstalled)) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Row (
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(onClick = onRateUsClick),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(modifier = Modifier
                            .padding(start = 16.dp, end = 8.dp)
                            .weight(1f),
                            text = stringResource(R.string.rate).toUpperCase(LocaleList.current),
                            fontSize = 14.sp,
                            lineHeight = 18.sp,
                            color = textColor,)
                        Box (modifier = Modifier
                            .padding(end = 8.dp, top = 8.dp, bottom = 8.dp)
                            .width(42.dp)) {
                            Icon(modifier = Modifier.alpha(0.2f).size(42.dp),
                                imageVector = Icons.Rounded.Circle, contentDescription = stringResource(id = R.string.rate), tint = textColor)
                            Icon(modifier = Modifier.size(42.dp).padding(8.dp),
                                imageVector = Icons.Rounded.Star, contentDescription = stringResource(id = R.string.rate), tint = textColor)
                        }
                    }
                }
                Spacer(modifier = Modifier.size(18.dp))
            }
            if (!hideAllExternalLinks) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Row (
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(onClick = onMoreAppsClick),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(modifier = Modifier
                            .padding(start = 16.dp, end = 8.dp)
                            .weight(1f),
                            text = stringResource(R.string.more_apps_from_us).toUpperCase(LocaleList.current),
                            fontSize = 14.sp,
                            lineHeight = 18.sp,
                            color = textColor,)
                        Box (modifier = Modifier
                            .padding(end = 8.dp, top = 8.dp, bottom = 8.dp)
                            .width(42.dp)) {
                            Icon(modifier = Modifier.alpha(0.2f).size(42.dp),
                                imageVector = Icons.Rounded.Circle, contentDescription = stringResource(id = R.string.more_apps_from_us), tint = textColor)
                            if (ruStoreInstalled && !context.baseConfig.useGooglePlay) {
                                Icon(modifier = Modifier
                                    .size(42.dp)
                                    .padding(9.dp),
                                    painter = painterResource(id = R.drawable.ic_rustore),
                                    contentDescription = stringResource(id = R.string.more_apps_from_us), tint = textColor)
                            }
                            else {
                                Icon(modifier = Modifier.size(42.dp).padding(start = 10.dp, end = 6.dp, top = 8.dp, bottom = 8.dp),
                                    painter = painterResource(id = R.drawable.ic_google_play_vector),
                                    contentDescription = stringResource(id = R.string.more_apps_from_us), tint = textColor)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.size(18.dp))
            }
            // Privacy Policy is always shown regardless of hideAllExternalLinks
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row (
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(onClick = onPrivacyPolicyClick),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(modifier = Modifier
                        .padding(start = 16.dp, end = 8.dp)
                        .weight(1f),
                        text = stringResource(R.string.privacy_policy).toUpperCase(LocaleList.current),
                        fontSize = 14.sp,
                        lineHeight = 18.sp,
                        color = textColor,)
                    Box (modifier = Modifier
                        .padding(end = 8.dp, top = 8.dp, bottom = 8.dp)
                        .width(42.dp)) {
                        Icon(modifier = Modifier.alpha(0.2f).size(42.dp),
                            imageVector = Icons.Rounded.Circle, contentDescription = stringResource(id = R.string.privacy_policy), tint = textColor)
                        Icon(modifier = Modifier.size(42.dp).padding(8.dp),
                            imageVector = Icons.Rounded.Policy, contentDescription = stringResource(id = R.string.privacy_policy), tint = textColor)
                    }
                }
            }
            if (setupFAQ) {
                Spacer(modifier = Modifier.size(18.dp))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(onClick = onFAQClick),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(start = 16.dp, end = 8.dp)
                                .weight(1f),
                            text = stringResource(R.string.frequently_asked_questions).toUpperCase(LocaleList.current),
                            fontSize = 14.sp,
                            lineHeight = 18.sp,
                            color = textColor,
                        )
                        Box(modifier = Modifier
                            .padding(end = 8.dp, top = 8.dp, bottom = 8.dp)
                            .width(42.dp)) {
                            Icon(
                                modifier = Modifier.alpha(0.2f).size(42.dp),
                                imageVector = Icons.Rounded.Circle,
                                contentDescription = stringResource(id = R.string.frequently_asked_questions),
                                tint = textColor
                            )
                            Icon(
                                modifier = Modifier.size(42.dp).padding(8.dp),
                                imageVector = Icons.Rounded.QuestionMark,
                                contentDescription = stringResource(id = R.string.frequently_asked_questions),
                                tint = textColor
                            )
                        }
                    }
                }
            }
            if (!hideAllExternalLinks) {
                Spacer(modifier = Modifier.size(18.dp))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Row (
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(onClick = onTipJarClick),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(modifier = Modifier
                            .padding(start = 16.dp, end = 8.dp)
                            .weight(1f),
                            text = stringResource(stringsR.string.tip_jar).toUpperCase(LocaleList.current),
                            fontSize = 14.sp,
                            lineHeight = 18.sp,
                            color = textColor,)
                        Box (modifier = Modifier
                            .padding(end = 8.dp, top = 8.dp, bottom = 8.dp)
                            .width(42.dp)) {
                            Icon(modifier = Modifier.alpha(0.2f).size(42.dp),
                                imageVector = Icons.Rounded.Circle, contentDescription = stringResource(id = stringsR.string.tip_jar), tint = textColor)
                            Icon(modifier = Modifier.size(42.dp).padding(8.dp),
                                imageVector = Icons.Rounded.Savings, contentDescription = stringResource(id = stringsR.string.tip_jar), tint = textColor)
                        }
                    }
                }
            }
            if (!hideAllExternalLinks && showGithub) {
                Spacer(modifier = Modifier.size(18.dp))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(onClick = onGithubClick),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(start = 16.dp, end = 8.dp)
                                .weight(1f),
                            text = stringResource(R.string.github).toUpperCase(LocaleList.current),
                            fontSize = 14.sp,
                            lineHeight = 18.sp,
                            color = textColor,
                        )
                        Box(
                            modifier = Modifier
                                .padding(end = 8.dp, top = 8.dp, bottom = 8.dp)
                                .width(42.dp)
                        ) {
                            Icon(
                                modifier = Modifier.alpha(0.2f).size(42.dp),
                                imageVector = Icons.Rounded.Circle, contentDescription = stringResource(id = R.string.privacy_policy), tint = textColor
                            )
                            Icon(
                                modifier = Modifier.size(42.dp).padding(8.dp),
                                painter = painterResource(id = R.drawable.ic_github_vector),
                                contentDescription = stringResource(id = R.string.privacy_policy),
                                tint = textColor
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.size(32.dp))
        }
    }
}

@Composable
fun HtmlText(html: String, modifier: Modifier = Modifier, textColor: Color = Color.Unspecified) {
    AndroidView(
        modifier = modifier.padding(horizontal = 4.dp),
        factory = { context -> TextView(context).apply {
            setTextColor(textColor.toArgb())
        } },
        update = { it.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT) }
    )
}
