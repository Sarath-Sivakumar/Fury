package app.personal.fury.UI.Drawer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import app.personal.fury.R;

public class help_Activity extends AppCompatActivity {

    private ImageButton back;
    WebView web;
    private TextView contact,faq,feedback,navtitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.nav_activity_help);
        init();
    }

    private void init() {
        findView();
        OnClick();
    }

        private void findView(){
            back = findViewById(R.id.nBack);
            navtitle = findViewById(R.id.title);
            contact = findViewById(R.id.Contact_us);
            faq = findViewById(R.id.faq);
            feedback = findViewById(R.id.feedback);
            web = findViewById(R.id.helpweb);
            WebSettings webSettings = web.getSettings();
            webSettings.setBuiltInZoomControls(false);
            webSettings.setJavaScriptEnabled(true);
            web.setWebViewClient(new Callback());
        }

    public void onBackPressed() {
        if(web != null && web.canGoBack()){
            web.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private class Callback extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
    }

        private void OnClick(){
            back.setOnClickListener(v -> finish());
            contact.setOnClickListener(v -> {
                contact.setVisibility(View.GONE);
                faq.setVisibility(View.GONE);
                feedback.setVisibility(View.GONE);
                web.setVisibility(View.VISIBLE);
                navtitle.setText(R.string.contact_us);
                web.loadUrl("file:///android_asset/web_resources/contact_us.html");
            });

            faq.setOnClickListener(v -> {
                navtitle.setText(R.string.faq);
            });

             feedback.setOnClickListener(v -> {
                navtitle.setText(R.string.report_a_problem);
            });
        }
    }
