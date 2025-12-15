package com.neon.connectsort.ui.audio

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val TAG = "AnalyticsTracker"

class AnalyticsTracker(
    analyticsEnabledFlow: Flow<Boolean>
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val enabled = MutableStateFlow(true)

    init {
        scope.launch {
            analyticsEnabledFlow.collect { enabled.value = it }
        }
    }

    fun logEvent(event: String, properties: Map<String, Any?> = emptyMap()) {
        if (!enabled.value) return
        Log.i(TAG, "event=$event props=$properties")
    }

    fun logCrash(throwable: Throwable) {
        if (!enabled.value) return
        Log.e(TAG, "crash", throwable)
    }

    fun dispose() {
        scope.cancel()
    }
}
