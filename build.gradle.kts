// Top-level build file where you can add configuration options common to all sub-projects/modules.

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

tasks.register("projectLint")
tasks.register("projectCodestyle")
tasks.register("projectTest")
