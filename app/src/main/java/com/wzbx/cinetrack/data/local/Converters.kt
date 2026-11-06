package com.wzbx.cinetrack.data.local

import androidx.room.TypeConverter

/** 枚举以字符串入库，比存 ordinal 稳——以后往枚举中间插值不会把旧数据读错。 */
class Converters {
    @TypeConverter
    fun toStatus(value: String): WatchStatus = WatchStatus.valueOf(value)

    @TypeConverter
    fun fromStatus(status: WatchStatus): String = status.name
}
