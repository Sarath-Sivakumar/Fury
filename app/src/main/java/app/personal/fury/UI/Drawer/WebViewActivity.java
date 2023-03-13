package app.personal.fury.UI.Drawer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import app.personal.Utls.Constants;
import app.personal.fury.R;

public class WebViewActivity extends AppCompatActivity {

    private WebView webView;
    private String Title, Url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        init();
        findView();
        setWebView();
    }
    private void init(){
        Intent i = getIntent();
        Title = i.getStringExtra(Constants.WEB_VIEW_ACTIVITY_TITLE);
        Url = i.getStringExtra(Constants.WEB_VIEW_ACTIVITY_URL);
    }
    private void findView(){
        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(v -> finish());
        TextView title = findViewById(R.id.title);
        title.setText(Title);
        webView = findViewById(R.id.webView);
    }
    private void setWebView(){
        webView.setWebViewClient(new Callback());
        WebSettings webSettings = webView.getSettings();
        webSettings.setBuiltInZoomControls(false);
        webSettings.setJavaScriptEnabled(true);
        webView.requestFocus();
        webView.loadUrl(Url);
    }
    private static class Callback extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
    }
}