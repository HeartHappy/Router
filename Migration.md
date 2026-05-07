# ARouter 迁移插件说明

## 1. 插件简介

`router-migration-plugin` 是一个用于将 ARouter 工程批量迁移到 Router 的 Gradle 插件。

插件 ID：

- `io.hearthappy.router.migration`

当前提供两个任务：

- `previewArouterToRouterMigration`：仅预览，并输出可迁移报告文件和日志
- `migrateArouterToRouter`：执行实际迁移并写回文件

插件支持扫描：

- Kotlin 源码：`*.kt`
- Java 源码：`*.java`
- Groovy Gradle：`*.gradle`
- Kotlin DSL Gradle：`*.gradle.kts`

默认忽略目录：

- `.git`
- `.gradle`
- `.idea`
- `bin`
- `build`
- `out`

## 2. 当前支持的迁移范围

### 2.1 源码迁移

已支持以下源码替换：

- `com.alibaba.android.arouter.launcher.ARouter` -> `com.hearthappy.router.launcher.Router`
- `com.alibaba.android.arouter.facade.annotation.Route` -> `com.hearthappy.router.annotations.Route`
- `com.alibaba.android.arouter.facade.annotation.Interceptor` -> `com.hearthappy.router.annotations.Interceptor`
- `com.alibaba.android.arouter.facade.annotation.Autowired` -> `com.hearthappy.router.annotations.Autowired`
- `com.alibaba.android.arouter.facade.template.IProvider` -> `com.hearthappy.router.service.ProviderService`
- `com.alibaba.android.arouter.facade.service.PathReplaceService` -> `com.hearthappy.router.service.PathReplaceService`
- `com.alibaba.android.arouter.facade.service.SerializationService` -> `com.hearthappy.router.service.SerializationService`
- `com.alibaba.android.arouter.facade.Postcard` -> `com.hearthappy.router.launcher.Sorter`
- `com.alibaba.android.arouter.facade.callback.NavigationCallback` -> `com.hearthappy.router.interfaces.NavigationCallback`
- `com.alibaba.android.arouter.facade.callback.InterceptorCallback` -> `com.hearthappy.router.interfaces.InterceptorCallback`
- `com.alibaba.android.arouter.facade.template.IInterceptor` -> `com.hearthappy.router.interfaces.IInterceptor`
- `Postcard` -> `Sorter`
- `IProvider` -> `ProviderService`
- `ARouter.getInstance()` -> `Router`
- 删除 `ARouter.init(...)`
- 删除 `ARouter.openDebug()`
- `ARouter.openLog()` -> `Router.openLog()`

### 2.2 Kotlin 语法迁移

当前已支持的 Kotlin 常见写法：

- `ARouter.getInstance().navigation(Service::class.java)` -> `Router.getInstance(Service::class.java)`
- `ARouter.getInstance().build("/service/demo").navigation() as DemoService` -> `Router.build("/service/demo").getInstance() as DemoService`

### 2.3 Java 语法迁移

当前已支持的 Java 常见写法：

- `ARouter.getInstance().navigation(DemoService.class)` -> `Router.getInstance(DemoService.class)`
- `(DemoService) ARouter.getInstance().build("/service/demo").navigation()` -> `(DemoService) Router.build("/service/demo").getInstance()`

### 2.4 Gradle 迁移

当前已支持以下 Gradle 替换：

- `com.alibaba:arouter-api` -> `io.github.hearthappy:router-core`
- `kapt("com.alibaba:arouter-compiler")` -> `ksp("io.github.hearthappy:router-compiler")`
- `annotationProcessor 'com.alibaba:arouter-compiler'` -> `ksp 'io.github.hearthappy:router-compiler'`
- 在根脚本缺失时自动补充 `com.google.devtools.ksp` 版本声明
- 在模块脚本缺失时自动补充 `com.google.devtools.ksp` 插件声明
- 对老式 `buildscript {}` 根脚本自动补充 `classpath "com.google.devtools.ksp:symbol-processing-gradle-plugin:..."`
- 对老式 `apply plugin:` 模块脚本自动补充 `apply plugin: 'com.google.devtools.ksp'`



## 3. 在目标工程中集成插件

### 3.1 配置插件仓库

如果使用当前项目生成的本地测试仓库，请在目标工程的 `settings.gradle` 或 `settings.gradle.kts` 中配置：

```groovy
pluginManagement {
    repositories {
        maven {
            url = uri("你的 Router 仓库路径/router-migration-plugin/build/repo")
        }
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}
```



### 3.2 使用 `plugins {}` 方式接入

Groovy DSL：

```groovy
plugins {
    id 'io.hearthappy.router.migration' version '1.0.2'
}

routerMigration {
    scanDir = rootDir
    dryRun = true
    reportFile = file("$buildDir/reports/router-migration/report.txt")
    routerCoreDependency = "io.github.hearthappy:router-core:1.0.2"
    routerCompilerDependency = "io.github.hearthappy:router-compiler:1.0.2"
    kspPluginVersion = "1.9.24-1.0.20"
}
```

Kotlin DSL：

```kotlin
plugins {
    id("io.hearthappy.router.migration") version "1.0.2"
}

routerMigration {
    scanDir = rootDir
    dryRun = true
    reportFile = file("$buildDir/reports/router-migration/report.txt")
    routerCoreDependency = "io.github.hearthappy:router-core:1.0.2"
    routerCompilerDependency = "io.github.hearthappy:router-compiler:1.0.2"
    kspPluginVersion = "1.9.24-1.0.20"
}
```

### 3.3 使用 `buildscript + apply plugin` 方式接入老项目

如果目标工程是老式 Gradle 工程，没有使用 `plugins {}`，而是使用：

```groovy
apply plugin: 'com.android.application'
apply plugin: 'kotlin-android'
```

那么仅仅在仓库里有插件包还不够，你还必须把插件加入根工程 `buildscript` 的 `classpath`。

根 `build.gradle` 示例：

```groovy
buildscript {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath "io.github.hearthappy:router-migration-plugin:1.0.2"
    }
}
```

然后在需要执行迁移任务的模块或根脚本中应用：

```groovy
apply plugin: 'io.hearthappy.router.migration'
```

说明：

- `plugins {}` 方式依赖 `pluginManagement.repositories`
- `apply plugin:` 方式依赖 `buildscript.repositories + buildscript.dependencies.classpath`
- 你看到 `~/.m2/repository/io/github/hearthappy/router-migration-plugin/1.0.2` 只能说明插件主包已经发布成功
- 如果使用 `apply plugin:`，没有把插件加入 `buildscript classpath`，Gradle 仍然会报 `Plugin with id ... not found`

## 4. 可配置项

- `scanDir`：扫描根目录，默认当前工程根目录
- `dryRun`：是否只预演不写文件，默认 `false`
- `reportFile`：报告输出文件
- `includeFileSuffixes`：扫描的文件后缀集合
- `excludeDirectoryNames`：忽略目录集合
- `enableGradleDependencyMigration`：是否迁移 Gradle 依赖，默认 `true`
- `routerCoreDependency`：替换后的 `router-core` 坐标
- `routerCompilerDependency`：替换后的 `router-compiler` 坐标
- `kspPluginVersion`：必填；必须显式指定，且必须与目标工程 Kotlin 版本严格对应

示例：

```groovy
routerMigration {
    scanDir = rootDir
    dryRun = false
    reportFile = file("$buildDir/reports/router-migration/report.txt")
    includeFileSuffixes = [".kt", ".java", ".gradle", ".gradle.kts"] as Set
    excludeDirectoryNames = [".git", ".gradle", ".idea", "build", "out"] as Set
    enableGradleDependencyMigration = true
    routerCoreDependency = "io.github.hearthappy:router-core:1.0.2"
    routerCompilerDependency = "io.github.hearthappy:router-compiler:1.0.2"
    kspPluginVersion = "1.9.24-1.0.20"
}
```

### 4.1 必须显式配置 `kspPluginVersion`

如果不配置：

- 迁移插件在处理 `arouter-compiler -> ksp(...)` 时会直接报错
- 老项目即使已经接入了迁移插件，也无法正确补全 KSP 插件与根脚本 classpath
- 这是因为 KSP 与 Kotlin 编译器版本强相关，不能随意写死为某一个版本

因此，`routerMigration.kspPluginVersion` 必须显式配置。

推荐写法：

```groovy
routerMigration {
    kspPluginVersion = "1.9.24-1.0.20"
}
```

### 4.2 重点说明：KSP 版本必须与 Kotlin 版本保持一致

你必须保证目标工程中的 Kotlin 版本与配置的 KSP 版本严格对应，否则会出现 Gradle 同步失败、插件不兼容或 `ksp(...)` 无法工作等问题。

推荐对应关系：

- Kotlin `1.9.24` -> KSP `1.9.24-1.0.20`
- Kotlin `2.0.10` -> KSP `2.0.10-1.0.24`

推荐优先使用以下版本组合：

- 推荐组合 1：Kotlin `1.9.24` + KSP `1.9.24-1.0.20`
- 推荐组合 2：Kotlin `2.0.10` + KSP `2.0.10-1.0.24`

错误示例：

- Kotlin `1.9.24` 搭配 KSP `2.0.10-1.0.24`
- Kotlin `2.0.10` 搭配 KSP `1.9.24-1.0.20`

这两类组合都不兼容，不建议使用。

## 5. 使用方式

### 5.1 先预演

建议先执行：

```bash
.\gradlew previewArouterToRouterMigration
```

预演模式不会修改源码，只会输出日志并生成报告。

### 5.2 再正式迁移

确认预演结果无误后执行：

```bash
.\gradlew migrateArouterToRouter
```

插件会直接修改匹配到的源码和 Gradle 脚本。

## 6. 报告与日志

默认报告路径：

```text
build/reports/router-migration/report.txt
```

预演任务默认生成：

```text
build/reports/router-migration/preview-report.txt
```

报告包含：

- 扫描目录
- 是否为预演模式
- 扫描文件数
- 变更文件数
- 总替换次数
- 每个文件命中的迁移规则明细

## 7. 推荐迁移流程

建议按以下顺序操作：


1. 在目标工程接入插件和 Router 依赖仓库
2. 执行 `previewArouterToRouterMigration`
3. 查看控制台和报告文件
4. 确认结果后执行 `migrateArouterToRouter`
5. 执行一次全量构建
6. 对服务获取、拦截器、参数注入和页面跳转做人工回归

## 8. 注意事项

- 插件现在不仅支持 Kotlin 老项目，也支持 Java 老项目中的常见 ARouter 写法迁移
- Java 场景重点覆盖了 `navigation(SomeService.class)` 和强转后的 `build(...).navigation()`
- 对于复杂封装代码，仍建议先预演再正式执行
- Gradle 依赖迁移只覆盖常规写法，特殊脚本请自行复核
- 迁移结束后建议执行代码格式化和无用导包清理
