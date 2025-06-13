// Plugins are managed by the parent project

tasks.register<Delete>("clean") {
    delete {
        layout.buildDirectory.asFile
    }
}
