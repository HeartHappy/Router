package com.hearthappy.router.migration

import org.gradle.api.Project
import java.io.File

open class RouterMigrationExtension(project: Project) {

    companion object {
        const val EXTENSION_NAME = "routerMigration"
    }

    var scanDir: File = project.projectDir
    var includeFileSuffixes: Set<String> = linkedSetOf(".kt", ".java", ".gradle", ".gradle.kts")
    var excludeDirectoryNames: Set<String> = linkedSetOf(".git", ".gradle", ".idea", "bin", "build", "out")
    var reportFile: File = project.layout.buildDirectory.file("reports/router-migration/report.txt").get().asFile
    var dryRun: Boolean = false
    var enableGradleDependencyMigration: Boolean = true
    var routerCoreDependency: String = "io.github.hearthappy:router-core:1.0.2"
    var routerCompilerDependency: String = "io.github.hearthappy:router-compiler:1.0.2"
    var kspPluginVersion: String = "2.0.10-1.0.24"
}
