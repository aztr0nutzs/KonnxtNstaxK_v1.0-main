package com.neon.connectsort.ui.components

import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

private const val TAG = "HtmlAssetScreen"

/** Renders an HTML asset inside a WebView and logs once the page finishes loading. */
@Composable
fun HtmlAssetScreen(
    assetPath: String,
    modifier: Modifier = Modifier,
    enableJavaScript: Boolean = false,
) {
    val context = LocalContext.current
    val assetUrl = "file:///android_asset/$assetPath"

    AndroidView(
        modifier = modifier,
        factory = {
            WebView(context).apply {
                settings.javaScriptEnabled = enableJavaScript
                settings.domStorageEnabled = enableJavaScript
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        Log.i(TAG, "HTML_LOADED:$assetPath")
                    }
                }
                loadUrl(assetUrl)
            }
        },
        update = { webView ->
            if (webView.url != assetUrl) {
                webView.loadUrl(assetUrl)
            }
        }
    )
}
