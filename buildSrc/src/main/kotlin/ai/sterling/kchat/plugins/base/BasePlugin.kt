package ai.sterling.kchat.plugins.base

import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

open class BasePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            plugins.apply("kotlin-kapt")

            repositories.google()
            repositories.gradlePluginPortal()

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

            dependencies.add("implementation", "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
            dependencies.add("implementation", "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
//            dependencies.add("implementation", "com.google.dagger:hilt-core:$daggerVersion")
//            dependencies.add("kapt","androidx.hilt:hilt-compiler:1.0.0-alpha03")

            dependencies.add("testImplementation", "junit:junit:4.12")
            dependencies.add("testImplementation", "com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
            dependencies.add("testImplementation", "org.mockito:mockito-core:3.1.0")
            dependencies.add("testImplementation", "org.assertj:assertj-core:3.13.2")
            dependencies.add("testImplementation", "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.3.5")

            tasks.withType(KotlinCompile::class.java).all {
                it.kotlinOptions.freeCompilerArgs += arrayOf(
                    "-Xuse-experimental=kotlin.time.ExperimentalTime",
                    "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi",
                    "-Xuse-experimental=kotlinx.coroutines.ObsoleteCoroutinesApi",
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true"
                )
                it.kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
            }
        }
    }

    companion object {
        private const val kotlinVersion = "1.5.21"
        private const val daggerVersion = "2.38.1"
        private const val coroutinesVersion = "1.5.0"
    }
}
