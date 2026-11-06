package com.wzbx.cinetrack

import android.app.Application
import com.wzbx.cinetrack.data.WatchRepository
import com.wzbx.cinetrack.data.local.CineTrackDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * 进程级容器。
 *
 * 用 Application 持有数据库和仓库，不引第三方 DI 框架——
 * 课程项目里多一个框架就多一层要解释的东西，这里手写足够。
 */
class CineTrackApplication : Application() {

    val repository: WatchRepository by lazy {
        WatchRepository(CineTrackDatabase.get(this).watchDao())
    }

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        // 首次启动播种，放 IO 线程，不阻塞冷启动。
        appScope.launch { repository.ensureSeeded() }
    }
}
