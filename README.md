<div align="center">
</br>
</div>

<div align="center">

# **Maxitendo Commons**

</div>

</br>

<p align="center">
  <img alt="API" src="https://img.shields.io/badge/Android-50f270?logo=android&logoColor=black&style=for-the-badge"/></a>
  <img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-a503fc?logo=kotlin&logoColor=white&style=for-the-badge"/></a>
  <img alt="material" src="https://custom-icon-badges.demolab.com/badge/material%20you-lightblue?style=for-the-badge&logoColor=333&logo=material-you"/></a>
  </br>
</p>

<p align="center">
 <img src="https://tokei.rs/b1/github/Maxitendo1/Maxitendo-Commons?category=code&style=for-the-badge&color=aeff4d&logo=data%3Aimage%2Fsvg%2Bxml%3Bbase64%2CPHN2ZyB2aWV3Qm94PSIwIDAgMjQgMjQiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI%2BCiAgICA8cGF0aCBkPSJNMTIuODksM0wxNC44NSwzLjRMMTEuMTEsMjFMOS4xNSwyMC42TDEyLjg5LDNNMTkuNTksMTJMMTYsOC40MVY1LjU4TDIyLjQyLDEyTDE2LDE4LjQxVjE1LjU4TDE5LjU5LDEyTTEuNTgsMTJMOCw1LjU4VjguNDFMNC40MSwxMkw4LDE1LjU4VjE4LjQxTDEuNTgsMTJaIgogICAgICAgIGZpbGw9IndoaXRlIiAvPgo8L3N2Zz4%3D&label=Lines%20of%20code&labelColor=4b731a"/>
 <a href="https://jitpack.io/#Maxitendo1/Maxitendo-Commons">
  <img src="https://img.shields.io/badge/JitPack-Available-orange?style=for-the-badge&logo=gradle&logoColor=white&labelColor=700f63"/>
<img src="https://img.shields.io/github/repo-size/Maxitendo1/Maxitendo-Commons?style=for-the-badge&color=8ce2ff&logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCI+PHRpdGxlPndlaWdodDwvdGl0bGU+PHBhdGggZD0iTTEyLDNBNCw0IDAgMCwxIDE2LDdDMTYsNy43MyAxNS44MSw4LjQxIDE1LjQ2LDlIMThDMTguOTUsOSAxOS43NSw5LjY3IDE5Ljk1LDEwLjU2QzIxLjk2LDE4LjU3IDIyLDE4Ljc4IDIyLDE5QTIsMiAwIDAsMSAyMCwyMUg0QTIsMiAwIDAsMSAyLDE5QzIsMTguNzggMi4wNCwxOC41NyA0LjA1LDEwLjU2QzQuMjUsOS42NyA1LjA1LDkgNiw5SDguNTRDOC4xOSw4LjQxIDgsNy43MyA4LDdBNCw0IDAgMCwxIDEyLDNNMTIsNUEyLDIgMCAwLDAgMTAsN0EyLDIgMCAwLDAgMTIsOUEyLDIgMCAwLDAgMTQsN0EyLDIgMCAwLDAgMTIsNVoiIGZpbGw9IndoaXRlIiAvPjwvc3ZnPg==&labelColor=0782ab">
</br>

</a>

<div align="center">

Maxitendo Commons is a comprehensive Android library providing common functionality for Special Contacts, including UI components, utilities, and shared resources.



# ✨ Features

</div>

## **🏗️ Core Architecture**
- **BaseSimpleActivity**: Foundation activity with lifecycle management, theming, and common functionality
- **Activity Suite**: Pre-built activities (About, Settings, Privacy Policy, FAQ, Customization)
- **Configuration System**: Centralized app configuration with SharedPreferences integration
- **Permission Manager**: Streamlined runtime permission handling with callbacks
- **File System Integration**: Advanced file operations with SAF (Storage Access Framework) support

## **🎨 UI & Theming**
- **Material You Integration**: Dynamic theming with system color extraction
- **Custom Theme Engine**: 24 app icon variations with color customization
- **Dialog Components**: Rich dialog library (IconList, RadioGroup, Confirmation, etc.)
- **FastScroller**: High-performance list scrolling with alphabet indexing
- **Gesture Views**: Advanced touch handling for images and interactive content
- **Compose Integration**: Modern Jetpack Compose components with Material 3

## **📱 User Experience**
- **Biometric Authentication**: Fingerprint and face unlock with fallback patterns
- **Pattern Lock**: Custom pattern-based security with visual feedback
- **Accessibility Support**: Full screen reader and navigation support
- **RTL Language Support**: Complete right-to-left language compatibility
- **Font Scaling**: Adaptive text sizing for accessibility needs
- **Dark/Light Themes**: System-aware theme switching with custom variants

## **🔧 Developer Tools**
- **Kotlin Extensions**: 200+ extension functions for common operations
- **Database Utilities**: Room database helpers with migration support
- **Image Processing**: Glide integration with caching and transformations
- **Network Helpers**: HTTP utilities with error handling
- **Logging System**: Structured logging with debug/release configurations
- **Testing Utilities**: Mock helpers and test fixtures

## **📦 Integrated Libraries**
- **Locally Integrated**: No external dependencies for core functionality
- **IndicatorFastScroll**: Enhanced RecyclerView scrolling performance
- **SwipeActionView**: Swipe gesture actions with Material animations
- **GestureViews**: Zoom, pan, and rotation for images and content
- **Pattern Lock View**: Secure pattern-based authentication
- **Reprint**: Biometric authentication with comprehensive fallback support

<div align="center">

# 💻 Integration

</div>

## **Gradle Dependency**

### Add JitPack Repository

Add to your project's `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
    }
}
```

### Add Dependencies

Add to your app's `build.gradle.kts`:

```kotlin
dependencies {
    // Maxitendo Commons (All UI libraries integrated locally)
    implementation("com.github.Maxitendo1.Maxitendo-Commons:commons:d8bb080")
    implementation("com.github.Maxitendo1.Maxitendo-Commons:strings:d8bb080")
}
```

### Version Catalog (Recommended)

Add to your `gradle/libs.versions.toml`:

```toml
[versions]
maxitendo-commons = "d8bb080"

[libraries]
maxitendo-commons = { module = "com.github.Maxitendo1.Maxitendo-Commons:commons", version.ref = "maxitendo-commons" }
maxitendo-strings = { module = "com.github.Maxitendo1.Maxitendo-Commons:strings", version.ref = "maxitendo-commons" }
```

Then in your `build.gradle.kts`:

```kotlin
dependencies {
    implementation(libs.maxitendo.commons)
    implementation(libs.maxitendo.strings)
}
```

<div align="center">

# 📚 Tech Stack & Requirements

</div>

## **Requirements**
- **Minimum SDK**: Android API 21+
- **Kotlin**: 2.1.0+
- **Android Gradle Plugin**: 8.5.2+
- **Jetpack Compose**: Latest stable
- **Material Design 3**: Full support

## **Dependencies**
- **AndroidX Libraries**: Core, AppCompat, Lifecycle, Room
- **Jetpack Compose**: UI toolkit with Material 3
- **Kotlin Coroutines**: Asynchronous programming
- **Material Components**: Material Design components
- **Glide**: Image loading and caching
- **Room Database**: Local data persistence
- **Biometric**: Fingerprint and face authentication

</div>

<div align="center">

# 🌟 Contributing

</div>

We welcome contributions to Maxitendo Commons! Here's how you can help:

## Reporting an issue or suggesting a feature

1.  This repository is for bugs and suggestions that affect Maxitendo Commons or **multiple** apps. For issues affecting only one app, use its separate repository.
2.  If you are reporting a bug, provide clear **steps to reproduce** it. Be sure to mention your app version, device model, and OS version.
3.  Ensure you're using the **latest app version** and have read the in-app FAQ before reporting.
4.  **Search first!** Check if the issue has already been reported by searching with relevant keywords.
5.  If a feature request already exists, add a "👍" to show your support. Avoid comments like `+1` or `I need this too!`, as they do not add useful information.
6.  If a bug is already reported and has the `needs triage` or `device/software specific` labels, feel free to add a comment with your device model and OS version.
7.  Please write all reports in **English**. Reports in other languages will be closed.

## **Development Setup**
```bash
git clone https://github.com/Maxitendo1/Maxitendo-Commons.git
cd Maxitendo-Commons
./gradlew commons:build
```

## Contributing code

1. **Issue first**

   - Before starting work, confirm there is an open issue for the task and that it is not tagged `needs triage`.
   - **Exceptions:** Critical, unclassified production-blocking bugs. Trivial changes, such as typos or broken links.

2. **Code style & formatting**

   - Always format code and optimize imports.
   - Follow existing naming and style conventions. Don't introduce a new style.
   - Prefix any new icon with `ic_`, use white vector drawables.
   - Always use braces, even for one-line `if`, `return`, or `continue` statements.

3. **Commit messages**

   - Use clear, descriptive commit messages.
   - Prefer [conventional commits](https://www.conventionalcommits.org/en/v1.0.0/#specification).

4. **Theming & accessibility testing**

   - Test UI changes on all themes (Light, Dark, Black & White, System default on Android 12+) and with the largest system font size.

5. **CI and quality gates**

   - Ensure **all** CI checks pass (build, tests, lint, detekt, etc.) on your pull request.

6. **Completeness & readiness**

   - PRs must be well-researched, thoroughly tested, and production-ready.
   - No bare-bones or speculative PRs.

7. **Build configuration**

   - Changes to build configuration, dependencies, or target SDK versions require explicit prior approval.

**Note:** Most pull requests will be **squash merged** unless they contain atomic changes worth preserving.

## Contributing translations

The best and preferred way to contribute translations is via Weblate.

- **Contribute on Weblate: [https://hosted.weblate.org/projects/maxitendo/](https://hosted.weblate.org/projects/maxitendo/)**

If you prefer to work directly on GitHub, you can follow the instructions below.

<details>
<summary><b>Click for GitHub translation instructions</b></summary>

### Editing an existing language file
1.  Log in to GitHub and navigate to the target language file (e.g., `app/src/main/res/values-es/strings.xml`).
2.  Click the pencil icon to edit the file.
3.  Modify only the text between `>` and `</string>`. Do **not** change the `name="..."` attribute or any comments.
4.  Escape any apostrophes with a backslash (`\'`).
5.  Add a clear commit message (e.g., "Update Spanish strings").
6.  Click **Propose file change**, then **Create pull request**.

### Adding a new language file
1.  Log in to GitHub and navigate to `app/src/main/res` in the app's repository.
2.  Click **Create new file**.
3.  In the path box, type the folder path `values-<language_code>/` (e.g., `values-de/` for German).
4.  Name the file `strings.xml`.
5.  Copy the entire contents of the default `app/src/main/res/values/strings.xml` into your new file.
6.  Translate the strings, making sure to escape apostrophes (`\'`).
7.  Add a clear commit message (e.g., "Add German translations").
8.  Press **Propose new file**, then **Create pull request**.

</details>

<div align="center">

# 👏 Credits

</div>

[Goodwy Commons](https://github.com/Goodwy/Goodwy-Commons)
</div>

<div align="center">

# ⚖️ License

```xml
Licensed under the GNU General Public License, Version 3.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

https://www.gnu.org/licenses/gpl-3.0.en.html

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
License for the specific language governing permissions and
limitations under the License.
```

</div>