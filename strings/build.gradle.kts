plugins {
    alias(libs.plugins.library)
    alias(libs.plugins.kotlinAndroid)
    `maven-publish`
}

android {
    namespace = "com.maxitendo.strings"
    compileSdk = libs.versions.app.build.compileSDKVersion.get().toInt()

    lint {
        abortOnError = false
        warningsAsErrors = false
    }
}

publishing.publications {
    create<MavenPublication>("release") {
        afterEvaluate {
            from(components["release"])
        }
    }
}
