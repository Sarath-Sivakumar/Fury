package app.personal.Utls;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import app.personal.fury.UI.Drawer.About_Activity;

public class WebViewUtil{

    private String url;
    private final WebView webView;

    public WebViewUtil(WebView webView) {
        this.webView = webView;
        init();
    }

    public void Load(String Url){
        this.url = Url;
        webView.loadUrl(url);
    }

    private void init(){
        WebSettings webSettings = webView.getSettings();
        webSettings.setBuiltInZoomControls(false);
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new Callback());
    }

    private static class Callback extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
    }
}