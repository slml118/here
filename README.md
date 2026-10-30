# CineTrack 影迹

Android 原生「影视追踪」课程项目。主 App 管看单、上映提醒、年度报告；伴生模块是桌面倒计时小组件。

> 当前进度：**阶段 A 完成** —— 工程可编译、可安装，三个 tab 能切换，加载/空/错误三态组件已就位。

## 一、怎么运行

1. 打开 Android Studio → `File > Open` → 选 `E:\SK work area\cinetrack`。
2. 首次打开会同步依赖（要联网，约几分钟，正常现象）。
3. 右上角选设备：模拟器 `Pixel_7_Pro`，或用 USB 连真机（需开 USB 调试）。
4. 点绿色 ▶ Run。

命令行构建（可选）：

```powershell
$env:JAVA_HOME = 'E:\app\study\AN ST\jbr'
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :algorithm:test
```

产物：`app\build\outputs\apk\debug\app-debug.apk`（约 11.5 MB）

## 二、目录结构

```
cinetrack/
├── app/                     主 App
│   └── src/main/java/com/wzbx/cinetrack/
│       ├── MainActivity.kt          唯一 Activity
│       └── ui/
│           ├── navigation/          底部导航 + NavHost
│           ├── screens/             发现 / 看单 / 报告
│           ├── components/          三态组件、页头
│           ├── model/               界面用的影片模型 + 示例数据
│           └── theme/               配色与主题
├── algorithm/               纯 Kotlin 算法模块（排序 / 画像 / 推荐 / 年报）
├── docs/                    设计文档
└── gradle/libs.versions.toml 版本目录
```

## 三、阶段 A 做了什么

| 内容 | 位置 |
|---|---|
| 单 Activity + Compose | `MainActivity.kt` |
| 底部导航三个 tab | `ui/navigation/CineTrackApp.kt` |
| 发现页（列表样板，6 条示例数据） | `ui/screens/DiscoverScreen.kt` + `MovieCard.kt` |
| 看单页（空态 + 引导按钮） | `ui/screens/WatchlistScreen.kt` |
| 报告页（空态） | `ui/screens/ReportScreen.kt` |
| 加载 / 空 / 错误 三态 | `ui/components/UiState.kt` |
| 深浅色主题 | `ui/theme/Theme.kt` |

设计取舍（答辩可讲）：

- **单 Activity**：页面切换交给 Compose Navigation，转场快、状态共享简单，Manifest 里不用堆 Activity。
- **三态用 sealed interface**：如果用「`isLoading` 布尔 + `error` 字符串」，会出现「既在加载又报错」的非法状态；sealed 类型从编译期就排除了这种组合。
- **示例数据独立成 `SampleMovies`**：阶段 C 换成网络数据时，页面代码只认 `MovieUi`，不用改。
- **空态不写"暂无数据"**：给一句说明 + 一个下一步动作（去看单页点「去发现页看看」）。

## 四、版本基线（本机已验证）

| 项 | 版本 |
|---|---|
| Gradle | 9.6.0 |
| Android Gradle Plugin | 9.4.0（内置 Kotlin，**不要再加 kotlin-android 插件**，会冲突） |
| Kotlin / Compose 编译器插件 | 2.2.10 |
| compileSdk / targetSdk / minSdk | 37 / 37 / 26 |
| Compose BOM | 2026.09.00 |
| Navigation Compose | 2.10.2 |

## 五、本机环境备注

- Android Studio 实际路径：`E:\app\study\AN ST`（桌面快捷方式还指向旧的 `E:\app\学习\...`，建议重建快捷方式）。
- SDK：`E:\app\study\SDK`（platforms `android-37.0`，build-tools `36.0.0`）。
- Git：尚未安装，提交前需要先装。
- 若用命令行构建，Gradle 缓存目录设为 `.gradle-home`（已被 `.gitignore` 忽略），避免写到用户目录。

## 六、下一步（阶段 B）

1. 设计 Room 表：`movies`、`watch_entries`。
2. 加入看单 / 改状态 / 打分 / 删除。
3. 看单页从数据库读数据，替换现在的空态。
