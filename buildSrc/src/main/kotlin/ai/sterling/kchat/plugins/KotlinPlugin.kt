package ai.sterling.kchat.plugins

import Libs
import ai.sterling.kchat.plugins.base.BasePlugin
import org.gradle.api.Project

open class KotlinPlugin : BasePlugin() {
    override fun apply(project: Project) {
        project.pluginManager.apply("kotlin")

        super.apply(project)

        project.dependencies.add("implementation", Libs.javaxInject)
        project.dependencies.add("implementation", Libs.androidAnnotations)
    }
}
