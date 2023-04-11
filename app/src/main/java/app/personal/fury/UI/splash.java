package app.personal.fury.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;
import androidx.core.view.ViewPropertyAnimatorListener;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.database.FirebaseDatabase;

import app.personal.MVVM.Entity.LaunchChecker;
import app.personal.MVVM.Viewmodel.AppUtilViewModel;
import app.personal.MVVM.Viewmodel.userInitViewModel;
import app.personal.Utls.Constants;
import app.personal.fury.R;
import app.personal.fury.UI.User_Init.Landing;

public class splash extends AppCompatActivity {

    private ViewGroup container;
    private boolean animationStarted = false;
    private userInitViewModel uvm;
    private AppUtilViewModel appVm;
    //----Anim variables----------------------------
    private final int animScale = 2, delay = 1000, duration = 100;

    //-----------------------------------------------
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uvm = new ViewModelProvider(this).get(userInitViewModel.class);
        uvm.checkForUser();
        appVm = new ViewModelProvider(this).get(AppUtilViewModel.class);
        appVm.getCheckerData().observe(this, launchChecker -> {
            try {
                if (launchChecker.getTimesLaunched() > 0) {
                    appVm.UpdateLaunchChecker(new LaunchChecker(launchChecker.getId(),
                            launchChecker.getTimesLaunched() + 1));
                    appVm.getCheckerData().removeObservers(this);
                }
            } catch (Exception ignored) {
                appVm.InsertLaunchChecker(new LaunchChecker(0));
            }
        });
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.init_splash);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        container = findViewById(R.id.container);
        if (hasFocus || !animationStarted) {
            animate();
            super.onWindowFocusChanged(hasFocus);
        }
    }

    private void animate() {
        for (int i = 0; i < container.getChildCount(); i++) {
            View v = container.getChildAt(i);
            ViewPropertyAnimatorCompat viewAnimator;
            viewAnimator = ViewCompat.animate(v)
                    .scaleX(animScale).scaleY(animScale)
                    .setStartDelay(delay)
                    .setDuration(duration);
            viewAnimator.setInterpolator(new DecelerateInterpolator()).start();
            animationStarted = true;
            viewAnimator.setListener(new ViewPropertyAnimatorListener() {
                @Override
                public void onAnimationStart(@NonNull View view) {

                }

                @Override
                public void onAnimationEnd(@NonNull View view) {
                    dataFetch();
                }

                @Override
                public void onAnimationCancel(@NonNull View view) {
                    dataFetch();
                }
            });
        }
    }

    private void dataFetch() {
        uvm.getUserId().observe(this, firebaseUser -> {
            if (firebaseUser == null) {
                appVm.getCheckerData().observe(this, launchChecker -> {
                    try {
                        if (launchChecker.getTimesLaunched() > 0) {
                            startActivity(new Intent(this, Landing.class));
                            finish();
                        }else{
                            startActivity(new Intent(this, splashTutorialSlider.class));
                            finish();
                        }
                    } catch (Exception ignored) {
                    }
                });
            } else {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });
    }
}