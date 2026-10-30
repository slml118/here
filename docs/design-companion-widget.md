# 伴生模块设计：影迹倒计时（桌面小组件）

## 1. 定位

主 App 解决「管理」，伴生模块只解决「抬头就能看见」：把**下一部待映影片的倒计时**放到桌面。

约束来自作业要求：

- **独立可运行**：单独安装、单独打开也能用完整流程 → 所以必须有 Launcher Activity，不能只有一个 Widget。
- **小而完整**：2 个界面 + 1 个 Widget，1 学分体量。
- **与主 App 关联**：真实读取主 App 的看单，而不是各存一份。

## 2. 组件清单

| 组件 | 类型 | 职责 |
|---|---|---|
| `CompanionHomeActivity` | Activity（Launcher） | 小组件预览、数据源状态、手动刷新、跳转主 App |
| `CountdownWidgetConfigureActivity` | Activity | 从桌面添加组件时选择「追踪哪一部」，按 `appWidgetId` 存储 |
| `CountdownWidgetProvider` | `AppWidgetProvider` | 渲染 RemoteViews；处理 `onUpdate` / `onDeleted` / 点击 |
| `WidgetRefreshWorker` | `WorkManager` Worker | 每小时对齐刷新；跨天重算倒计时 |
| `BootReceiver` | `BroadcastReceiver` | 开机、日期变更后请求刷新 |
| `WatchlistClient` | 数据层 | 先查主 App ContentProvider，失败降级本地缓存 |
| `WidgetPreferences` | DataStore | `appWidgetId → movieId` 映射、主题色 |

## 3. 数据流

```
主 App Room ──(ContentProvider, 只读)──▶ WatchlistClient ──▶ WidgetState
                                              │
                                    主 App 未安装 / 查询失败
                                              ▼
                                    本地 Room 缓存（上次同步快照）
                                              ▼
                                    内置 seed 样例（首装可用）
```

**降级顺序**是设计重点：任何一层失败都不能让小组件变成空白或 `Problem loading widget`。

## 4. 刷新策略

| 触发 | 机制 | 说明 |
|---|---|---|
| 周期刷新 | `WorkManager` 每小时 | `updatePeriodMillis` 最小 30 分钟且不可靠，仅作兜底 |
| 跨天 | `ACTION_DATE_CHANGED` | 倒计时天数必须零点变化 |
| 开机 | `ACTION_BOOT_COMPLETED` | 重新入队 Worker |
| 用户手动 | 组件右上角刷新按钮 → `PendingIntent.getBroadcast` | 演示时可控 |
| 数据变更 | 主 App 写库后 `contentResolver.notifyChange` | 可选，W8 再做 |

倒计时文案规则：`D-30` / `D-3` / `今天上映` / `已上映 12 天`。

## 5. 交互

- 点击组件主体 → 主 App 深链 `cinetrack://movie/{id}`；主 App 未安装 → 打开 `CompanionHomeActivity`。
- 长按 → 系统组件设置，进入 `CountdownWidgetConfigureActivity` 重新选片。
- 多实例：桌面放多个组件，各自 `appWidgetId` 对应不同影片。

## 6. RemoteViews 注意点

1. **不能用 Compose 布局**，必须用 RemoteViews 支持的控件（`TextView` / `ImageView` / `ProgressBar` / `LinearLayout`）。配置页仍可用 Compose。
2. 海报图不能在组件里发起网络请求：由 Worker 预下载到 `filesDir/posters/`，`onUpdate` 只读本地文件并 `setImageViewBitmap`；文件不存在就显示占位色块。
3. Android 12+ 圆角：根布局用 `@android:id/background`，并声明 `targetCellWidth` / `targetCellHeight`。
4. 组件尺寸变化（`onAppWidgetOptionsChanged`）时按最小宽度切换紧凑 / 完整布局。

## 7. 权限与降级

本模块**不申请任何运行时权限**，这是刻意的设计：提醒权限的复杂度留在主 App 讲。

| 场景 | 表现 |
|---|---|
| 主 App 未安装 | 显示内置样例 + 顶部提示「安装影迹后可同步看单」 |
| 主 App 已装但看单为空 | 显示「去看单里加一部」+ 跳转按钮 |
| 主 App 查询被拒 / 版本不匹配 | 走本地缓存快照，状态页显示「离线数据」 |
| 海报下载失败 | 显示类型色块 + 片名首字 |

## 8. 验收清单（现场可逐条演示）

1. 单独安装伴生 App（不装主 App）→ 打开有界面、桌面组件能添加、显示样例数据。
2. 安装主 App 并加入 3 部待映影片 → 组件自动变成真实数据。
3. 卸载主 App → 组件回落到缓存，不崩溃、不空白。
4. 放 2 个组件实例，分别选不同影片 → 各自独立。
5. 改系统日期到次日 → 组件天数自动 -1（`ACTION_DATE_CHANGED`）。
6. 点击组件 → 正确跳转主 App 详情页 / 伴生首页。

## 9. 与主 App 的契约

主 App 暴露只读 Provider：

```
content://com.wzbx.cinetrack.provider/watchlist
列：movie_id, title, poster_path, release_date, status, updated_at
```

只实现 `query()`，其余方法抛 `UnsupportedOperationException`。`android:exported="true"`，数据非敏感，文档中说明取舍（对比 signature 级权限的利弊，答辩可讲）。

主 App 侧深链：`cinetrack://movie/{movieId}`，`intent-filter` 中 `autoVerify` 不需要。
