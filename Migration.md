# ARouter 到 Router 插件迁移说明

## 1. 简介

为了把 `README_CN.md` 中“六、ARouter 迁移到 Router 的架构迁移方式”落地为可执行能力，项目新增了一个独立插件模块 `router-migration-plugin`。

这个插件的目标是：

- 通过 Gradle 任务批量迁移 ARouter 代码到 Router
- 在迁移过程中输出清晰日志
- 在迁移完成后生成迁移报告
- 支持先预演、再正式执行
- 支持插件发布到本地仓库和远程仓库，便于在业务工程中集成

## 2. 插件能力

当前插件已经支持以下迁移内容：

- 替换 ARouter 注解和相关类的导包
- 替换 `Postcard -> Sorter`
- 替换 `IProvider -> ProviderService`
- 替换 `ARouter.getInstance() -> Router`
- 替换 `navigation(SomeClass::class.java) -> getInstance(SomeClass::class.java)`
- 替换 `navigation() as Xxx -> getInstance() as Xxx`
- 替换 `build.gradle` / `build.gradle.kts` 中的 ARouter 依赖
- 将 `kapt` 或 `annotationProcessor` 的 `arouter-compiler` 替换为 `ksp`
- 在需要时自动补充 `com.google.devtools.ksp` 插件声明

默认扫描以下文件：

- `*.kt`
- `*.java`
- `*.gradle`
- `*.gradle.kts`

默认忽略以下目录：

- `.git`
- `.gradle`
- `.idea`
- `bin`
- `build`
- `out`

## 3. 插件模块位置

插件模块位于：

- `router-migration-plugin`

插件 ID：

- `com.hearthappy.router.migration`

任务名称：

- `previewArouterToRouterMigration`
- `migrateArouterToRouter`

## 4. 在当前仓库中开发和验证

在当前仓库中可以直接编译插件模块：

```bash
./gradlew :router-migration-plugin:build
```

预演迁移：

```bash
./gradlew previewArouterToRouterMigration
```

正式执行迁移：

```bash
./gradlew migrateArouterToRouter
```

## 5. 日志与报告

插件运行时会输出三类信息：

- 开始日志：包含扫描目录和是否为预演模式
- 过程日志：逐个输出发生迁移的文件和命中的规则
- 完成日志：输出扫描文件数、变更文件数、替换次数和报告路径

默认报告位置：

```text
router-migration-plugin/build/reports/router-migration/report.txt
```

如果是预演任务，默认会生成：

```text
router-migration-plugin/build/reports/router-migration/preview-report.txt
```

## 6. 在业务工程中集成插件

### 6.1 在 `settings.gradle` 中配置插件仓库

如果插件发布在本地 Maven 仓库：

```groovy
pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
```

如果插件发布在自定义 Maven 仓库：

```groovy
pluginManagement {
    repositories {
        maven {
            url = uri("你的插件仓库地址")
        }
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
```

### 6.2 在根工程中应用插件

```groovy
plugins {
    id 'com.hearthappy.router.migration' version '1.0.2'
}

routerMigration {
    scanDir = rootDir
    dryRun = false
    reportFile = file("$buildDir/reports/router-migration/report.txt")
    routerCoreDependency = "io.github.hearthappy:router-core:1.0.2"
    routerCompilerDependency = "io.github.hearthappy:router-compiler:1.0.2"
    kspPluginVersion = "2.0.10-1.0.24"
}
```

### 6.3 可选配置说明

- `scanDir`：扫描根目录，默认是当前项目目录
- `dryRun`：是否只预演不写入文件，默认 `false`
- `reportFile`：迁移结果报告输出文件
- `includeFileSuffixes`：需要扫描的文件后缀
- `excludeDirectoryNames`：忽略的目录名
- `enableGradleDependencyMigration`：是否迁移 Gradle 依赖，默认 `true`
- `routerCoreDependency`：替换后的 `router-core` 依赖坐标
- `routerCompilerDependency`：替换后的 `router-compiler` 依赖坐标
- `kspPluginVersion`：自动补充时使用的 KSP 插件版本

## 7. 推荐迁移流程

推荐按下面步骤进行：

1. 先发布插件到本地仓库
2. 在目标工程中接入插件
3. 先执行 `previewArouterToRouterMigration`
4. 查看控制台日志和报告文件
5. 确认结果后执行 `migrateArouterToRouter`
6. 最后执行一次项目编译并人工检查少量特殊代码

## 8. 发布插件到本地仓库

### 8.1 发布到 Maven Local

```bash
./gradlew :router-migration-plugin:publishToMavenLocal
```

发布后，其他工程只要在 `pluginManagement.repositories` 中声明 `mavenLocal()`，就可以通过插件 ID 直接使用。

### 8.2 发布到项目内本地测试仓库

```bash
./gradlew :router-migration-plugin:publishAllPublicationsToProjectLocalRepository
```

发布目录：

```text
router-migration-plugin/build/repo
```

同时可以执行：

```bash
./gradlew :router-migration-plugin:zipRepo
```

该任务会把本地仓库内容打包到：

```text
router-migration-plugin/build/router-migration-plugin.zip
```

## 9. 发布插件到远程仓库

插件模块已经支持远程发布，默认按 Sonatype OSSRH 地址配置：

- Release：`https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/`
- Snapshot：`https://s01.oss.sonatype.org/content/repositories/snapshots/`

你可以通过 `gradle.properties` 或环境变量提供远程发布凭据。

### 9.1 推荐的 `gradle.properties` 配置

```properties
ossrhUsername=your_username
ossrhPassword=your_password

routerReleaseRepoUrl=https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/
routerSnapshotRepoUrl=https://s01.oss.sonatype.org/content/repositories/snapshots/
```

也支持环境变量：

```bash
export OSSRH_USERNAME=your_username
export OSSRH_PASSWORD=your_password
```

### 9.2 发布命令

发布正式版：

```bash
./gradlew :router-migration-plugin:publishAllPublicationsToRemoteRepository
```

如果版本号以 `SNAPSHOT` 结尾，会自动发布到 Snapshot 仓库；否则发布到 Release 仓库。

## 10. 签名说明

插件模块已经接入 `signing`：

- 发布非 `SNAPSHOT` 版本时会自动签名
- `publishToMavenLocal` 和本地测试仓库发布可不依赖远程仓库认证

当前项目已经存在以下签名配置：

```properties
signing.keyId=你的 keyId
signing.password=你的 password
signing.secretKeyRingFile=你的密钥文件
```

如果后续你希望使用内存密钥，也可以切换为 `useInMemoryPgpKeys(...)` 方案。

## 11. 注意事项

- 当前迁移规则以文本替换和正则替换为主，适合绝大多数标准 ARouter 写法
- 如果业务代码存在高度自定义封装，建议先执行预演任务
- 迁移完成后，仍建议执行一次全量编译和人工回归
- Gradle 依赖迁移只处理常见写法，个别复杂脚本场景可能需要手动调整

## 12. 后续可扩展方向

后续可以继续增强以下能力：

- 增加更多 README 中未覆盖的迁移规则
- 输出更细粒度的差异报告
- 提供白名单/黑名单目录配置
- 提供按模块迁移能力
- 提供自动备份与回滚能力
