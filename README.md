# Maxitendo Commons

A comprehensive Android library providing common functionality for Maxitendo apps, including UI components, utilities, and shared resources.

## Overview

Maxitendo Commons is a fork of Goodwy Commons, specifically adapted for Maxitendo applications with enhanced functionality and branding updates.

## Features

### Core Components
- **BaseSimpleActivity**: Foundation activity class with common functionality
- **AboutActivity**: Standardized about screen with app information
- **SettingsActivity**: Common settings interface
- **PrivacyPolicyActivity**: Privacy policy display
- **FAQActivity**: Frequently asked questions interface

### UI Components
- **Compose Screens**: Modern Jetpack Compose UI components
- **Alert Dialogs**: Reusable dialog components
- **Theme System**: Consistent theming across apps
- **Icon Customization**: App icon selection and customization

### Utilities
- **Extensions**: Kotlin extensions for common operations
- **Helpers**: Utility classes for various tasks
- **Database**: Room database components
- **Permissions**: Permission handling utilities

### Special Features
- **Hide External Links**: Control visibility of external sections in About screen
- **App-Specific Handling**: Custom behavior for different Maxitendo apps
- **GitHub Integration**: Direct links to appropriate repositories
- **Privacy Policy Integration**: Launch activities instead of web browsers

## Integration

### Gradle Dependency

Add to your app's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.Maxitendo:Maxitendo-Commons:main-SNAPSHOT")
}
```

Add to your project's `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        maven { setUrl("https://jitpack.io") }
    }
}
```

### Basic Usage

```kotlin
class MainActivity : BaseSimpleActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Your activity code
    }
}
```

### About Screen Integration

```kotlin
// Launch About screen
startAboutActivity(
    appName = "Your App Name",
    appVersion = BuildConfig.VERSION_NAME,
    faqItems = faqList,
    repositoryName = "Your-Repository-Name"
)
```

## Configuration

### Hide External Links

Add to your app's `values/bools.xml`:

```xml
<bool name="hide_all_external_links">true</bool>
```

This will hide Rate Us, More Apps, GitHub, and Tip Jar sections in the About screen while keeping Privacy Policy and FAQ visible.

### App-Specific Behavior

The library automatically detects your app package and provides appropriate behavior:

- **com.maxitendo.contacts**: Redirects to Special-Contacts repository, launches PrivacyPolicyActivity
- **Other apps**: Uses standard repository naming and web privacy policy

## Supported Apps

- Special Contacts (`com.maxitendo.contacts`)
- Dialer (`com.maxitendo.dialer`)
- Messages (`com.maxitendo.smsmessenger`)
- Gallery (`com.maxitendo.gallery`)
- File Manager (`com.maxitendo.filemanager`)
- Voice Recorder (`com.maxitendo.voicerecorder`)
- Calendar (`com.maxitendo.calendar`)

## Requirements

- Android API 21+
- Kotlin 2.1.0+
- Android Gradle Plugin 8.9.0+
- Jetpack Compose

## License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Acknowledgments

- Based on Goodwy Commons by Goodwy
- Adapted and enhanced for Maxitendo applications
- Special thanks to the Android development community
