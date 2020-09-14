package ai.sterling.kchat.tasks

import org.gradle.api.DefaultTask

open class ProjectLintTask : DefaultTask() {

    init {
        group = "ci"
        description = "Runs AndroidLint for all modules"
    }
}
