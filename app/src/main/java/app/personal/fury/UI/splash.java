package app.personal.fury.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;
import androidx.lifecycle.ViewModelProvider;

import app.personal.MVVM.Viewmodel.userInitViewModel;
import app.personal.fury.R;
import app.personal.fury.UI.User_Init.Landing;

public class splash extends AppCompatActivity {

    private ViewGroup container;

    private boolean animationStarted = false;
    private userInitViewModel uvm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uvm = new ViewModelProvider(this).get(userInitViewModel.class);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        setContentView(R.layout.init_splash);
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
                    .scaleX(5).scaleY(5)
                    .setStartDelay(0)
                    .setDuration(100);
            viewAnimator.setInterpolator(new DecelerateInterpolator()).start();
            animationStarted = true;
        }
//        startActivity(new Intent(this, MainActivity.class));
//        finish();

        uvm.getUserId().observe(this, firebaseUser -> {
            if (firebaseUser != null) {
                startActivity(new Intent(this, MainActivity.class));
                Log.e("Splash", firebaseUser.getUid());
                finish();
            } else {
                startActivity(new Intent(splash.this, Landing.class));
                Log.e("Splash", "User Init..");
                finish();
            }
        });
    }
}