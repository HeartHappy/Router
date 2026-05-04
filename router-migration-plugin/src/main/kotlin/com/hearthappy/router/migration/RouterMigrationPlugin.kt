package com.hearthappy.router.migration

import org.gradle.api.Plugin
import org.gradle.api.Project

class RouterMigrationPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create(
            RouterMigrationExtension.EXTENSION_NAME,
            RouterMigrationExtension::class.java,
            project
        )

        project.tasks.register(
            RouterMigrationTask.TASK_NAME,
            RouterMigrationTask::class.java
        ) { task ->
            task.group = "router migration"
            task.description = "Migrate ARouter source code and Gradle scripts to Router."
            task.extension = extension
        }

        project.tasks.register(
            RouterMigrationTask.DRY_RUN_TASK_NAME,
            RouterMigrationTask::class.java
        ) { task ->
            task.group = "router migration"
            task.description = "Preview ARouter to Router migration without writing changes."
            task.extension = extension
            task.dryRunOverride = true
        }
    }
}
