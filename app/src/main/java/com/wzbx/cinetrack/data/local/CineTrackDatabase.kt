package com.wzbx.cinetrack.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [MovieEntity::class, WatchEntryEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class CineTrackDatabase : RoomDatabase() {

    abstract fun watchDao(): WatchDao

    companion object {
        @Volatile
        private var instance: CineTrackDatabase? = null

        /**
         * 双检锁单例：数据库连接很贵，整个进程只应该有一份；
         * 用 applicationContext 避免持有 Activity 导致内存泄漏。
         */
        fun get(context: Context): CineTrackDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    CineTrackDatabase::class.java,
                    "cinetrack.db"
                ).build().also { instance = it }
            }
    }
}
