package app.personal.fury.UI.Drawer;

import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;

import java.io.IOException;

import app.personal.fury.R;

public class About_Activity extends AppCompatActivity {
    private TextView policy,terms,about_app;

    PDFView policy_pdf,terms_pdf;
    private ImageButton back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        init();
    }

    private void init() {
        findView();
        OnClick();
    }

    private void findView(){
        back = findViewById(R.id.nBack);
        policy = findViewById(R.id.p_policies);
        terms = findViewById(R.id.t_c);
        about_app = findViewById(R.id.app_info);
        policy_pdf = findViewById(R.id.policy_pdf);
    }

    private void OnClick(){
        back.setOnClickListener(v -> finish());
        policy.setOnClickListener(v -> {
            policy.setVisibility(View.GONE);
            terms.setVisibility(View.GONE);
            about_app.setVisibility(View.GONE);
            policy_pdf.setVisibility(View.VISIBLE);
            policy_pdf.fromAsset("assets/policy.pdf")
                    .pages(0, 1, 2, 3, 4, 5) // all pages are displayed by default
                    .enableSwipe(true)
                    .swipeHorizontal(false)
                    .enableDoubletap(true)
                    .defaultPage(0)
//                    .onDraw(onDrawListener)
                    .onLoad(nbPages -> {
                        Log.e("Error", ""+nbPages);
                    })
//                    .onPageChange(onPageChangeListener)
//                    .onPageScroll(onPageScrollListener)
                    .onError(t -> Log.e("Error", ""+t.getMessage()))
                    .enableAnnotationRendering(false)
                    .password(null)
                    .scrollHandle(null)
                    .load();

        });
    }
}

