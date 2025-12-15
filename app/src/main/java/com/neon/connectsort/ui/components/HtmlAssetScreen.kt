package com.neon.connectsort.ui.components

import android.util.Log
import android.content.Context
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

private const val TAG = "HtmlAssetScreen"

private class ManagedWebView(context: Context) : WebView(context) {
    override fun onDetachedFromWindow() {
        try {
            stopLoading()
            clearHistory()
            clearCache(true)
            removeAllViews()
            destroy()
        } catch (_: Throwable) {
            // Defensive cleanup; ignore errors during destruction.
        }
        super.onDetachedFromWindow()
    }
}

/** Renders an HTML asset inside a WebView and logs once the page finishes loading. */
@Composable
fun HtmlAssetScreen(
    assetPath: String,
    modifier: Modifier = Modifier,
    enableJavaScript: Boolean = true,
    enableDomStorage: Boolean = true,
    webAppInterface: Any? = null,
    bridge: HtmlBridge? = null
) {
    val context = LocalContext.current
    val assetUrl = "file:///android_asset/$assetPath"
    val assetName = assetPath.substringAfterLast('/')

    AndroidView(
        modifier = modifier,
        factory = {
            ManagedWebView(context).apply {
                settings.javaScriptEnabled = enableJavaScript
                settings.domStorageEnabled = enableJavaScript || enableDomStorage
                webAppInterface?.let { addJavascriptInterface(it, "Android") }
                bridge?.let {
                    addJavascriptInterface(it, "NeonBridge")
                    it.attachWebView(this)
                }
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        Log.i(TAG, "HTML_LOADED:$assetPath")
                        Log.i(TAG, "HTML_ACTIVE:$assetName")
                    }
                }
                loadUrl(assetUrl)
            }
        },
        update = { webView ->
            bridge?.attachWebView(webView)
            if (webView.url != assetUrl) {
                webView.loadUrl(assetUrl)
            }
        }
    )
}
