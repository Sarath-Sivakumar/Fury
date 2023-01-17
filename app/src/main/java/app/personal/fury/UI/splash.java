package app.personal.fury.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;

import app.personal.fury.R;

public class splash extends AppCompatActivity {

    private ViewGroup container;

    private boolean animationStarted = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        setContentView(R.layout.splash);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        container = findViewById(R.id.container);
        if (!hasFocus || animationStarted) {
            return;
        }
        animate();
        super.onWindowFocusChanged(hasFocus);
    }

    private void animate() {
        for (int i = 0; i < container.getChildCount(); i++) {
            View v = container.getChildAt(i);
            ViewPropertyAnimatorCompat viewAnimator;
            viewAnimator = ViewCompat.animate(v)
                    .scaleX(25).scaleY(25)
                    .setStartDelay(0)
                    .setDuration(10000);
            viewAnimator.setInterpolator(new DecelerateInterpolator()).start();
            animationStarted=true;
        }
        startActivity(new Intent(splash.this, MainActivity.class));
        finish();
    }
}