package com.neon.connectsort.ui.components

import android.webkit.JavascriptInterface
import android.webkit.WebView
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

sealed interface HtmlBridgeAction {
    data class FindMatch(val destination: String?) : HtmlBridgeAction
    data class PurchaseItem(val itemId: String?) : HtmlBridgeAction
}

class HtmlBridge(
    private val onAction: (HtmlBridgeAction) -> Unit
) {
    private val coinBalance = AtomicInteger(0)
    private val playerName = AtomicReference("NEON")
    private var attachedWebView: WebView? = null

    @JavascriptInterface
    fun getCoinBalance(): Int = coinBalance.get()

    @JavascriptInterface
    fun getPlayerName(): String = playerName.get()

    @JavascriptInterface
    fun performAction(action: String, payload: String?) {
        val normalized = action.trim().lowercase()
        when (normalized) {
            "findmatch" -> onAction(HtmlBridgeAction.FindMatch(payload))
            "purchase" -> onAction(HtmlBridgeAction.PurchaseItem(payload))
        }
    }

    fun updateCoinBalance(value: Int) {
        coinBalance.set(value)
        attachedWebView?.post {
            attachedWebView?.evaluateJavascript("window.neonUpdateCoins($value)", null)
        }
    }

    fun updatePlayerName(name: String) {
        playerName.set(name)
        val sanitized = name.replace("'", "\\'")
        attachedWebView?.post {
            attachedWebView?.evaluateJavascript("window.neonUpdatePlayer('$sanitized')", null)
        }
    }

    internal fun attachWebView(webView: WebView) {
        attachedWebView = webView
    }
}
