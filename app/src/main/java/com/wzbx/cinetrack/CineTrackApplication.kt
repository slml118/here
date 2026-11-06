package com.wzbx.cinetrack

import android.app.Application
import com.wzbx.cinetrack.data.WatchRepository
import com.wzbx.cinetrack.data.local.CineTrackDatabase

/**
 * 进程级容器：数据库和仓库只创建一次。
 *
 * 没有引第三方 DI 框架，课程项目里手写足够，也少一层要解释的东西。
 */
class CineTrackApplication : Application() {

    val repository: WatchRepository by lazy {
        WatchRepository(CineTrackDatabase.get(this).watchDao())
    }
}
