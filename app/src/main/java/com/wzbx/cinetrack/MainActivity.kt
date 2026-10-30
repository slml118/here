package com.wzbx.cinetrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.wzbx.cinetrack.ui.navigation.CineTrackApp
import com.wzbx.cinetrack.ui.theme.CineTrackTheme

/**
 * 全 App 唯一的 Activity。
 *
 * 页面切换全部交给 Compose Navigation 在同一个 Activity 内完成，
 * 好处是转场快、状态共享简单，也不用在 Manifest 里维护一堆 Activity。
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CineTrackTheme {
                CineTrackApp()
            }
        }
    }
}
