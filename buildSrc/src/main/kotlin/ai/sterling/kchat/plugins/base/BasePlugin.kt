package ai.sterling.kchat.plugins.base

import Libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KaptExtension

open class BasePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            plugins.apply("kotlin-kapt")

            repositories.google()
            repositories.jcenter()

            extensions.configure(KaptExtension::class.java) {
                it.correctErrorTypes = true
                it.useBuildCache = true
                it.arguments {
                    arg("dagger.formatGeneratedSource", "disabled")
                    //arg("dagger.gradle.incremental")
                }
                it.javacOptions {
                    option("-Xmaxerrs", 1000)
                }
            }

            dependencies.add("implementation", Libs.kotlinStdLib)
            dependencies.add("implementation", Libs.coroutinesCore)
            dependencies.add("testImplementation", "junit:junit:4.12")
            dependencies.add("testImplementation", "com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
            dependencies.add("testImplementation", "org.mockito:mockito-core:3.1.0")
            dependencies.add("testImplementation", "org.assertj:assertj-core:3.13.2")
            dependencies.add("testImplementation", "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.3.5")
            dependencies.add("implementation", Libs.dagger)
            dependencies.add("kapt", Libs.Kapt.daggerCompiler)
        }
    }
}
