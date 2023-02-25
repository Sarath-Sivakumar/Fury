package app.personal.fury.UI.Drawer;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import app.personal.fury.R;

public class About_Activity extends AppCompatActivity {
    private TextView policy,terms,about_app,navtitle;
    private ImageButton back;
    WebView webView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_activity_about);
        init();
    }

    private void init() {
        findView();
        OnClick();
    }

    private void findView(){
        back = findViewById(R.id.nBack);
        navtitle = findViewById(R.id.title);
        policy = findViewById(R.id.p_policies);
        terms = findViewById(R.id.t_c);
        about_app = findViewById(R.id.app_info);
        webView = findViewById(R.id.myWebView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setBuiltInZoomControls(false);
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new Callback());
    }

    private class Callback extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        if(webView != null && webView.canGoBack()){
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }


    private void OnClick(){
        back.setOnClickListener(v -> finish());
        policy.setOnClickListener(v -> {
            policy.setVisibility(View.GONE);
            terms.setVisibility(View.GONE);
            about_app.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
            navtitle.setText(R.string.privacy_policy);
            webView.loadUrl("file:///android_asset/web_resources/privacy_policy.html");
        });

        terms.setOnClickListener(v -> {
            policy.setVisibility(View.GONE);
            terms.setVisibility(View.GONE);
            about_app.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
            navtitle.setText(R.string.terms_conditions);
            webView.loadUrl("file:///android_asset/web_resources/t_c.html");
        });

        about_app.setOnClickListener(v -> {
            policy.setVisibility(View.GONE);
            terms.setVisibility(View.GONE);
            about_app.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
            navtitle.setText(R.string.about_us);
            webView.loadUrl("file:///android_asset/web_resources/about_app.html");
        });
    }
}

