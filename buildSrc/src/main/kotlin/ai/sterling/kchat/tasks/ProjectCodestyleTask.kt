package ai.sterling.kchat.tasks

import org.gradle.api.DefaultTask

open class ProjectCodestyleTask : DefaultTask() {

    init {
        group = "ci"
        description = "Runs codestyle check for all modules"
    }
}
