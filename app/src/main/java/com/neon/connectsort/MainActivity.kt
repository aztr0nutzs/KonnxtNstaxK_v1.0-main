package com.neon.connectsort

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import com.neon.connectsort.ui.NeonGameApp
import com.neon.connectsort.ui.theme.NeonGameTheme
import com.neon.connectsort.core.AppContextHolder

@OptIn(ExperimentalAnimationApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppContextHolder.appContext = applicationContext
        
        // Enable edge-to-edge display for immersive experience
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        // Set window background to match holographic theme
        window.navigationBarColor = Color(0xFF000022).toArgb()
        window.statusBarColor = Color(0xFF000022).toArgb()
        
        setContent {
            NeonGameTheme {
                // Global composition effects
                CompositionLocalProvider(
                    
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = NeonGameTheme.colors.depthVoid
                    ) {
                        NeonGameApp()
                    }
                }
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Optional: Add immersive mode for full-screen gaming
        window.decorView.systemUiVisibility = 
            android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            android.view.View.SYSTEM_UI_FLAG_FULLSCREEN or
            android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }
}
