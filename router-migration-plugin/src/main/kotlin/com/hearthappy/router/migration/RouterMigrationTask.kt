package com.hearthappy.router.migration

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

open class RouterMigrationTask : DefaultTask() {

    companion object {
        const val TASK_NAME = "migrateArouterToRouter"
        const val DRY_RUN_TASK_NAME = "previewArouterToRouterMigration"
        private val REPORT_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        private val ROOT_KSP_CLASSPATH_REGEX = Regex(
            """(?m)^(\s*)classpath\s*\(?\s*["']com\.google\.devtools\.ksp:symbol-processing-gradle-plugin:[^"']+["']\s*\)?"""
        )
        private val ROOT_KSP_PLUGIN_REGEX = Regex("""(?m)^(\s*)(id\s*\(?\s*["']com\.google\.devtools\.ksp["']\)?|alias\s*\(\s*libs\.plugins\.[^)]+ksp[^)]*\))""")
        private val MODULE_KSP_PLUGIN_REGEX = Regex("""(?m)^(\s*)id\s*\(?\s*["']com\.google\.devtools\.ksp["']\)?""")
        private val MODULE_KSP_APPLY_PLUGIN_REGEX = Regex("""(?m)^(\s*)apply\s+plugin:\s*["']com\.google\.devtools\.ksp["']\s*$""")
        private val MODULE_KAPT_APPLY_PLUGIN_REGEX = Regex("""(?m)^(\s*)apply\s+plugin:\s*["']kotlin-kapt["']\s*\r?\n?""")
        private val GROOVY_AROUTER_API_REGEX = Regex(
            """(?m)^(\s*)(implementation|api)\s*\(?\s*["']com\.alibaba:arouter-api:[^"']+["']\s*\)?(\s*(?://.*)?)$"""
        )
        private val KTS_AROUTER_API_REGEX = Regex(
            """(?m)^(\s*)(implementation|api)\s*\(?\s*["']com\.alibaba:arouter-api:[^"']+["']\s*\)?(\s*(?://.*)?)$"""
        )
        private val GROOVY_AROUTER_COMPILER_REGEX = Regex(
            """(?m)^(\s*)(kapt|annotationProcessor)\s*\(?\s*["']com\.alibaba:arouter-compiler:[^"']+["']\s*\)?(\s*(?://.*)?)$"""
        )
        private val KTS_AROUTER_COMPILER_REGEX = Regex(
            """(?m)^(\s*)(kapt|annotationProcessor)\s*\(?\s*["']com\.alibaba:arouter-compiler:[^"']+["']\s*\)?(\s*(?://.*)?)$"""
        )
    }

    @get:Internal
    lateinit var extension: RouterMigrationExtension

    @get:Internal
    var dryRunOverride: Boolean? = null

    @TaskAction
    fun migrate() {
        val scanRoot = extension.scanDir
        val reportFile = resolveReportFile()
        val isDryRun = dryRunOverride ?: extension.dryRun
        val rules = baseRules()
        val requiresKspSupport = if (extension.enableGradleDependencyMigration) detectKspSupportRequirement(scanRoot) else false

        if (!scanRoot.exists() || !scanRoot.isDirectory) {
            throw IllegalArgumentException("routerMigration.scanDir does not exist or is not a directory: ${scanRoot.absolutePath}")
        }

        logger.lifecycle("Router migration started. scanDir=${scanRoot.absolutePath}, dryRun=$isDryRun")

        val scannedFiles = mutableListOf<File>()
        val fileResults = mutableListOf<FileMigrationResult>()

        scanRoot.walkTopDown()
            .onEnter { directory -> shouldEnter(directory, scanRoot) }
            .filter { candidate -> candidate.isFile && shouldMigrate(candidate) }
            .forEach { file ->
                scannedFiles += file
                migrateFile(file, scanRoot, rules, requiresKspSupport)?.let { result ->
                    fileResults += result
                    val relativePath = result.relativePath
                    val details = result.ruleHits.joinToString { "${it.ruleId}=${it.count}" }
                    val actionLabel = if (isDryRun) "preview" else "updated"
                    logger.lifecycle("Router migration $actionLabel: $relativePath ($details)")
                }
            }

        val summary = MigrationSummary(
            scannedFileCount = scannedFiles.size,
            changedFileCount = fileResults.size,
            replacementCount = fileResults.sumOf { result -> result.ruleHits.sumOf { it.count } }
        )
        writeReport(reportFile, scanRoot, isDryRun, fileResults, summary)

        logger.lifecycle(
            "Router migration finished. scanned=${summary.scannedFileCount}, changed=${summary.changedFileCount}, replacements=${summary.replacementCount}"
        )
        logger.lifecycle("Router migration report: ${reportFile.absolutePath}")
    }

    private fun migrateFile(
        file: File,
        scanRoot: File,
        rules: List<RouterMigrationRule>,
        requiresKspSupport: Boolean
    ): FileMigrationResult? {
        val originalText = file.readText(StandardCharsets.UTF_8)
        var migratedText = originalText
        val ruleHits = mutableListOf<RuleHit>()

        rules.filter { rule -> matchesTarget(file, rule.target) }
            .forEach { rule ->
                val result = applyRule(migratedText, rule)
                if (result.count > 0) {
                    migratedText = result.updatedText
                    ruleHits += RuleHit(rule.id, result.count, rule.description)
                }
            }

        if (isGradleFile(file) && extension.enableGradleDependencyMigration) {
            val gradleResults = applyGradleMigration(file, migratedText, scanRoot, requiresKspSupport)
            migratedText = gradleResults.updatedText
            ruleHits += gradleResults.ruleHits
        }

        if (migratedText == originalText) {
            return null
        }

        val dryRun = dryRunOverride ?: extension.dryRun
        if (!dryRun) {
            file.writeText(migratedText, StandardCharsets.UTF_8)
        }

        return FileMigrationResult(
            file = file,
            relativePath = file.relativeTo(scanRoot).invariantSeparatorsPath,
            ruleHits = ruleHits
        )
    }

    private fun applyGradleMigration(
        file: File,
        content: String,
        scanRoot: File,
        requiresKspSupport: Boolean
    ): GradleMigrationResult {
        var updatedText = content
        val ruleHits = mutableListOf<RuleHit>()
        val isKotlinDsl = file.name.endsWith(".gradle.kts")
        val resolvedKspVersion = if (requiresKspSupport) resolveKspPluginVersion(scanRoot) else null

        val apiRule = if (isKotlinDsl) {
            KTS_AROUTER_API_REGEX to "$1$2(\"${extension.routerCoreDependency}\")$3"
        } else {
            GROOVY_AROUTER_API_REGEX to "$1$2('${extension.routerCoreDependency}')$3"
        }
        val apiCount = apiRule.first.findAll(updatedText).count()
        if (apiCount > 0) {
            updatedText = apiRule.first.replace(updatedText, apiRule.second)
            ruleHits += RuleHit(
                ruleId = "gradle-router-core",
                count = apiCount,
                description = "Replace ARouter API dependency with Router Core dependency"
            )
        }

        val compilerRule = if (isKotlinDsl) {
            KTS_AROUTER_COMPILER_REGEX to "$1ksp(\"${extension.routerCompilerDependency}\")$3"
        } else {
            GROOVY_AROUTER_COMPILER_REGEX to "$1ksp('${extension.routerCompilerDependency}')$3"
        }
        val compilerCount = compilerRule.first.findAll(updatedText).count()
        if (compilerCount > 0) {
            updatedText = compilerRule.first.replace(updatedText, compilerRule.second)
            ruleHits += RuleHit(
                ruleId = "gradle-router-compiler",
                count = compilerCount,
                description = "Replace ARouter compiler dependency with Router compiler dependency"
            )
        }

        val pluginInsertion = insertKspPluginIfNeeded(
            file = file,
            content = updatedText,
            scanRoot = scanRoot,
            isKotlinDsl = isKotlinDsl,
            requiresKspSupport = requiresKspSupport,
            kspPluginVersion = resolvedKspVersion
        )
        if (pluginInsertion.count > 0) {
            updatedText = pluginInsertion.updatedText
            ruleHits += RuleHit(
                ruleId = pluginInsertion.ruleId,
                count = pluginInsertion.count,
                description = pluginInsertion.description
            )
        }

        return GradleMigrationResult(updatedText, ruleHits)
    }

    private fun insertKspPluginIfNeeded(
        file: File,
        content: String,
        scanRoot: File,
        isKotlinDsl: Boolean,
        requiresKspSupport: Boolean,
        kspPluginVersion: String?
    ): TextReplacementResult {
        if (!requiresKspSupport) {
            return TextReplacementResult.noChanges(content)
        }

        val isRootBuildFile = file.parentFile == scanRoot && file.name.startsWith("build.gradle")
        if (isRootBuildFile) {
            if (ROOT_KSP_PLUGIN_REGEX.containsMatchIn(content) ||
                ROOT_KSP_CLASSPATH_REGEX.containsMatchIn(content) ||
                content.contains("devtools.ksp")
            ) {
                return TextReplacementResult.noChanges(content)
            }

            if (content.contains("plugins")) {
                val insertion = if (isKotlinDsl) {
                    """    id("com.google.devtools.ksp") version "$kspPluginVersion" apply false"""
                } else {
                    """    id 'com.google.devtools.ksp' version '$kspPluginVersion' apply false"""
                }
                return insertIntoPluginsBlock(
                    content = content,
                    insertionLine = insertion,
                    ruleId = "gradle-root-ksp-plugin",
                    description = "Add KSP plugin version declaration to root build script"
                )
            }

            if (!isKotlinDsl && content.contains("buildscript")) {
                return insertIntoBuildscriptDependencies(
                    content = content,
                    insertionLine = "        classpath \"com.google.devtools.ksp:symbol-processing-gradle-plugin:$kspPluginVersion\"",
                    ruleId = "gradle-root-ksp-classpath",
                    description = "Add KSP classpath dependency to root buildscript"
                )
            }

            return TextReplacementResult.noChanges(content)
        }

        if (MODULE_KSP_PLUGIN_REGEX.containsMatchIn(content) || MODULE_KSP_APPLY_PLUGIN_REGEX.containsMatchIn(content)) {
            return TextReplacementResult.noChanges(content)
        }

        val needsKspPlugin = content.contains("ksp(") ||
            content.contains("ksp '") ||
            content.contains("ksp(\"") ||
            content.contains("ksp") && content.contains("router-compiler") ||
            content.contains("com.alibaba:arouter-compiler")

        if (!needsKspPlugin) {
            return TextReplacementResult.noChanges(content)
        }

        if (content.contains("plugins")) {
            val insertion = if (isKotlinDsl) {
                """    id("com.google.devtools.ksp")"""
            } else {
                """    id 'com.google.devtools.ksp'"""
            }
            return insertIntoPluginsBlock(
                content = content,
                insertionLine = insertion,
                ruleId = "gradle-module-ksp-plugin",
                description = "Add KSP plugin to module build script"
            )
        }

        if (!isKotlinDsl && content.contains("apply plugin:")) {
            return insertAfterLastApplyPlugin(
                content = content,
                insertionLine = "apply plugin: 'com.google.devtools.ksp'",
                ruleId = "gradle-module-ksp-apply-plugin",
                description = "Add KSP apply plugin to legacy module build script"
            )
        }

        return TextReplacementResult.noChanges(content)
    }

    private fun detectKspSupportRequirement(scanRoot: File): Boolean {
        return scanRoot.walkTopDown()
            .onEnter { directory -> shouldEnter(directory, scanRoot) }
            .filter { candidate -> candidate.isFile && isGradleFile(candidate) }
            .any { file ->
                val content = file.readText(StandardCharsets.UTF_8)
                GROOVY_AROUTER_COMPILER_REGEX.containsMatchIn(content) ||
                    KTS_AROUTER_COMPILER_REGEX.containsMatchIn(content) ||
                    content.contains("ksp(") ||
                    content.contains("ksp '") ||
                    content.contains("ksp(\"") ||
                    content.contains(extension.routerCompilerDependency)
            }
    }

    private fun resolveKspPluginVersion(scanRoot: File): String {
        return extension.kspPluginVersion?.trim()?.takeIf { it.isNotEmpty() }
            ?: throw IllegalStateException(
                "KSP migration requires routerMigration.kspPluginVersion to be set explicitly. " +
                    "The KSP version must match the Kotlin version used by the target project. " +
                    "Example: Kotlin 1.9.24 -> KSP 1.9.24-1.0.20; Kotlin 2.0.10 -> KSP 2.0.10-1.0.24."
            )
    }

    private fun insertIntoBuildscriptDependencies(
        content: String,
        insertionLine: String,
        ruleId: String,
        description: String
    ): TextReplacementResult {
        val dependenciesRegex = Regex("""buildscript\s*\{[\s\S]*?dependencies\s*\{""")
        val match = dependenciesRegex.find(content) ?: return TextReplacementResult.noChanges(content)
        val insertionIndex = match.range.last + 1
        val updatedText = buildString {
            append(content.substring(0, insertionIndex))
            append(System.lineSeparator())
            append(insertionLine)
            append(content.substring(insertionIndex))
        }
        return TextReplacementResult(
            updatedText = updatedText,
            count = 1,
            ruleId = ruleId,
            description = description
        )
    }

    private fun insertAfterLastApplyPlugin(
        content: String,
        insertionLine: String,
        ruleId: String,
        description: String
    ): TextReplacementResult {
        val applyPluginRegex = Regex("""(?m)^(\s*)apply\s+plugin:\s*["'][^"']+["']\s*$""")
        val matches = applyPluginRegex.findAll(content).toList()
        val lastMatch = matches.lastOrNull() ?: return TextReplacementResult.noChanges(content)
        val insertionIndex = lastMatch.range.last + 1
        val updatedText = buildString {
            append(content.substring(0, insertionIndex))
            append(System.lineSeparator())
            append(insertionLine)
            append(content.substring(insertionIndex))
        }
        return TextReplacementResult(
            updatedText = updatedText,
            count = 1,
            ruleId = ruleId,
            description = description
        )
    }

    private fun insertIntoPluginsBlock(
        content: String,
        insertionLine: String,
        ruleId: String,
        description: String
    ): TextReplacementResult {
        val pluginsRegex = Regex("""plugins\s*\{""")
        val match = pluginsRegex.find(content) ?: return TextReplacementResult.noChanges(content)
        val insertionIndex = match.range.last + 1
        val updatedText = buildString {
            append(content.substring(0, insertionIndex))
            append(System.lineSeparator())
            append(insertionLine)
            append(content.substring(insertionIndex))
        }
        return TextReplacementResult(
            updatedText = updatedText,
            count = 1,
            ruleId = ruleId,
            description = description
        )
    }

    private fun applyRule(content: String, rule: RouterMigrationRule): TextReplacementResult {
        return when (rule.type) {
            RouterMigrationRule.RuleType.LITERAL -> {
                val count = countOccurrences(content, rule.oldValue)
                if (count == 0) {
                    TextReplacementResult.noChanges(content)
                } else {
                    TextReplacementResult(
                        updatedText = content.replace(rule.oldValue, rule.newValue),
                        count = count,
                        ruleId = rule.id,
                        description = rule.description
                    )
                }
            }

            RouterMigrationRule.RuleType.REGEX -> {
                val regex = Regex(rule.oldValue)
                val count = regex.findAll(content).count()
                if (count == 0) {
                    TextReplacementResult.noChanges(content)
                } else {
                    TextReplacementResult(
                        updatedText = regex.replace(content, rule.newValue),
                        count = count,
                        ruleId = rule.id,
                        description = rule.description
                    )
                }
            }
        }
    }

    private fun baseRules(): List<RouterMigrationRule> = listOf(
        RouterMigrationRule(
            id = "import-router",
            description = "Replace ARouter launcher import",
            type = RouterMigrationRule.RuleType.LITERAL,
            oldValue = "com.alibaba.android.arouter.launcher.ARouter",
            newValue = "com.hearthappy.router.launcher.Router"
        ),
        RouterMigrationRule(
            id = "import-route",
            description = "Replace Route annotation import",
            type = RouterMigrationRule.RuleType.LITERAL,
            oldValue = "com.alibaba.android.arouter.facade.annotation.Route",
            newValue = "com.hearthappy.router.annotations.Route"
        ),
        RouterMigrationRule(
            id = "import-interceptor",
            description = "Replace Interceptor annotation import",
            type = RouterMigrationRule.RuleType.LITERAL,
            oldValue = "com.alibaba.android.arouter.facade.annotation.Interceptor",
            newValue = "com.hearthappy.router.annotations.Interceptor"
        ),
        RouterMigrationRule(
            id = "import-autowired",
            description = "Replace Autowired annotation import",
            type = RouterMigrationRule.RuleType.LITERAL,
            oldValue = "com.alibaba.android.arouter.facade.annotation.Autowired",
            newValue = "com.hearthappy.router.annotations.Autowired"
        ),
        RouterMigrationRule(
            id = "import-provider-service",
            description = "Replace provider service import",
            type = RouterMigrationRule.RuleType.LITERAL,
            oldValue = "com.alibaba.android.arouter.facade.template.IProvider",
            newValue = "com.hearthappy.router.service.ProviderService"
        ),
        RouterMigrationRule(
            id = "import-path-replace",
            description = "Replace path replace service import",
            type = RouterMigrationRule.RuleType.LITERAL,
            oldValue = "com.alibaba.android.arouter.facade.service.PathReplaceService",
            newValue = "com.hearthappy.router.service.PathReplaceService"
        ),
        RouterMigrationRule(
            id = "import-serialization",
            description = "Replace serialization service import",
            type = RouterMigrationRule.RuleType.LITERAL,
            oldValue = "com.alibaba.android.arouter.facade.service.SerializationService",
            newValue = "com.hearthappy.router.service.SerializationService"
        ),
        RouterMigrationRule(
            id = "import-sorter",
            description = "Replace Postcard import",
            type = RouterMigrationRule.RuleType.LITERAL,
            oldValue = "com.alibaba.android.arouter.facade.Postcard",
            newValue = "com.hearthappy.router.launcher.Sorter"
        ),
        RouterMigrationRule(
            id = "import-navigation-callback",
            description = "Replace navigation callback import",
            type = RouterMigrationRule.RuleType.LITERAL,
            oldValue = "com.alibaba.android.arouter.facade.callback.NavigationCallback",
            newValue = "com.hearthappy.router.interfaces.NavigationCallback"
        ),
        RouterMigrationRule(
            id = "import-interceptor-callback",
            description = "Replace interceptor callback import",
            type = RouterMigrationRule.RuleType.LITERAL,
            oldValue = "com.alibaba.android.arouter.facade.callback.InterceptorCallback",
            newValue = "com.hearthappy.router.interfaces.InterceptorCallback"
        ),
        RouterMigrationRule(
            id = "import-iinterceptor",
            description = "Replace interceptor interface import",
            type = RouterMigrationRule.RuleType.LITERAL,
            oldValue = "com.alibaba.android.arouter.facade.template.IInterceptor",
            newValue = "com.hearthappy.router.interfaces.IInterceptor"
        ),
        RouterMigrationRule(
            id = "remove-arouter-init",
            description = "Remove ARouter.init(...) initialization call",
            type = RouterMigrationRule.RuleType.REGEX,
            target = RouterMigrationRule.TargetType.SOURCE,
            oldValue = """(?m)^[ \t]*ARouter\.init\(\s*[^)\r\n]+\s*\)\s*;?\s*\r?\n?""",
            newValue = ""
        ),
        RouterMigrationRule(
            id = "remove-arouter-open-debug",
            description = "Remove ARouter.openDebug() call",
            type = RouterMigrationRule.RuleType.REGEX,
            target = RouterMigrationRule.TargetType.SOURCE,
            oldValue = """(?m)^[ \t]*ARouter\.openDebug\(\s*\)\s*;?\s*\r?\n?""",
            newValue = ""
        ),
        RouterMigrationRule(
            id = "replace-arouter-open-log",
            description = "Replace ARouter.openLog() with Router.openLog()",
            type = RouterMigrationRule.RuleType.REGEX,
            target = RouterMigrationRule.TargetType.SOURCE,
            oldValue = """\bARouter\.openLog\(\s*\)""",
            newValue = "Router.openLog()"
        ),
        RouterMigrationRule(
            id = "class-sorter",
            description = "Rename Postcard to Sorter",
            type = RouterMigrationRule.RuleType.REGEX,
            target = RouterMigrationRule.TargetType.SOURCE,
            oldValue = """\bPostcard\b""",
            newValue = "Sorter"
        ),
        RouterMigrationRule(
            id = "class-provider-service",
            description = "Rename IProvider to ProviderService",
            type = RouterMigrationRule.RuleType.REGEX,
            target = RouterMigrationRule.TargetType.SOURCE,
            oldValue = """\bIProvider\b""",
            newValue = "ProviderService"
        ),
        RouterMigrationRule(
            id = "java-instance-by-class",
            description = "Replace Java ARouter navigation(Class) with Router.getInstance(Class)",
            type = RouterMigrationRule.RuleType.REGEX,
            target = RouterMigrationRule.TargetType.SOURCE,
            oldValue = """\bARouter\.getInstance\(\)\.navigation\(\s*([a-zA-Z0-9_.$]+)\s*\.class\s*\)""",
            newValue = "Router.getInstance($1.class)"
        ),
        RouterMigrationRule(
            id = "java-instance-cast-from-arouter-build",
            description = "Replace Java casted ARouter build(...).navigation() with Router.build(...).getInstance()",
            type = RouterMigrationRule.RuleType.REGEX,
            target = RouterMigrationRule.TargetType.SOURCE,
            oldValue = """\(\s*([a-zA-Z0-9_.$<>?,\s]+)\s*\)\s*ARouter\.getInstance\(\)\.build\((.+?)\)\.navigation\(\s*\)""",
            newValue = "($1) Router.build($2).getInstance()"
        ),
        RouterMigrationRule(
            id = "api-router-instance",
            description = "Replace ARouter.getInstance() with Router",
            type = RouterMigrationRule.RuleType.REGEX,
            target = RouterMigrationRule.TargetType.SOURCE,
            oldValue = """\bARouter\.getInstance\(\)""",
            newValue = "Router"
        ),
        RouterMigrationRule(
            id = "instance-by-class",
            description = "Replace navigation(Class) with getInstance(Class)",
            type = RouterMigrationRule.RuleType.REGEX,
            target = RouterMigrationRule.TargetType.SOURCE,
            oldValue = """navigation\(\s*([a-zA-Z0-9_.$]+)\s*::\s*class\s*\.\s*java\s*\)""",
            newValue = "getInstance($1::class.java)"
        ),
        RouterMigrationRule(
            id = "java-instance-cast",
            description = "Replace Java casted Router.build(...).navigation() with Router.build(...).getInstance()",
            type = RouterMigrationRule.RuleType.REGEX,
            target = RouterMigrationRule.TargetType.SOURCE,
            oldValue = """\(\s*([a-zA-Z0-9_.$<>?,\s]+)\s*\)\s*Router\.build\((.+?)\)\.navigation\(\s*\)""",
            newValue = "($1) Router.build($2).getInstance()"
        ),
        RouterMigrationRule(
            id = "instance-cast",
            description = "Replace navigation() cast with getInstance() cast",
            type = RouterMigrationRule.RuleType.REGEX,
            target = RouterMigrationRule.TargetType.SOURCE,
            oldValue = """navigation\(\s*\)\s*as\s+(\w+)""",
            newValue = "getInstance() as $1"
        )
    )

    private fun shouldEnter(directory: File, scanRoot: File): Boolean {
        if (directory == scanRoot) {
            return true
        }
        return !extension.excludeDirectoryNames.contains(directory.name)
    }

    private fun shouldMigrate(file: File): Boolean {
        return extension.includeFileSuffixes.any { suffix -> file.name.endsWith(suffix) }
    }

    private fun matchesTarget(file: File, target: RouterMigrationRule.TargetType): Boolean {
        return when (target) {
            RouterMigrationRule.TargetType.ANY -> true
            RouterMigrationRule.TargetType.SOURCE -> !isGradleFile(file)
            RouterMigrationRule.TargetType.GRADLE -> isGradleFile(file)
        }
    }

    private fun isGradleFile(file: File): Boolean {
        return file.name.endsWith(".gradle") || file.name.endsWith(".gradle.kts")
    }

    private fun resolveReportFile(): File {
        val originalReportFile = extension.reportFile
        if (dryRunOverride == true && originalReportFile.nameWithoutExtension == "report") {
            return originalReportFile.toPath()
                .resolveSibling("preview-report.${originalReportFile.extension.ifBlank { "txt" }}")
                .toFile()
        }
        return originalReportFile
    }

    private fun writeReport(
        reportFile: File,
        scanRoot: File,
        dryRun: Boolean,
        fileResults: List<FileMigrationResult>,
        summary: MigrationSummary
    ) {
        reportFile.parentFile?.mkdirs()

        val reportContent = buildString {
            appendLine("Router Migration Report")
            appendLine("time=${LocalDateTime.now().format(REPORT_TIME_FORMATTER)}")
            appendLine("scanDir=${scanRoot.absolutePath}")
            appendLine("dryRun=$dryRun")
            appendLine("scannedFiles=${summary.scannedFileCount}")
            appendLine("changedFiles=${summary.changedFileCount}")
            appendLine("replacements=${summary.replacementCount}")
            appendLine()
            if (fileResults.isEmpty()) {
                appendLine("No files required migration.")
            } else {
                fileResults.forEach { result ->
                    appendLine("[${result.relativePath}]")
                    result.ruleHits.forEach { hit ->
                        appendLine("- ${hit.ruleId}: ${hit.count} (${hit.description})")
                    }
                    appendLine()
                }
            }
        }

        reportFile.writeText(reportContent, StandardCharsets.UTF_8)
    }

    private fun countOccurrences(content: String, token: String): Int {
        if (content.isEmpty() || token.isEmpty()) {
            return 0
        }

        var count = 0
        var startIndex = 0
        while (true) {
            val index = content.indexOf(token, startIndex)
            if (index < 0) {
                return count
            }
            count++
            startIndex = index + token.length
        }
    }

    data class FileMigrationResult(
        val file: File,
        val relativePath: String,
        val ruleHits: List<RuleHit>
    )

    data class RuleHit(
        val ruleId: String,
        val count: Int,
        val description: String
    )

    data class MigrationSummary(
        val scannedFileCount: Int,
        val changedFileCount: Int,
        val replacementCount: Int
    )

    data class GradleMigrationResult(
        val updatedText: String,
        val ruleHits: List<RuleHit>
    )

    data class TextReplacementResult(
        val updatedText: String,
        val count: Int,
        val ruleId: String,
        val description: String
    ) {
        companion object {
            fun noChanges(content: String): TextReplacementResult {
                return TextReplacementResult(
                    updatedText = content,
                    count = 0,
                    ruleId = "",
                    description = ""
                )
            }
        }
    }
}
