package app.personal.fury.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;
import androidx.lifecycle.ViewModelProvider;

import app.personal.MVVM.Entity.LaunchChecker;
import app.personal.MVVM.Viewmodel.AppUtilViewModel;
import app.personal.MVVM.Viewmodel.userInitViewModel;
import app.personal.fury.R;
import app.personal.fury.ui.User_Init.Landing;

public class splash extends AppCompatActivity {

    private ViewGroup container;
    private int currLaunch = 0;
    private boolean animationStarted = false;
    private userInitViewModel uvm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uvm = new ViewModelProvider(this).get(userInitViewModel.class);
        uvm.checkForUser();
        AppUtilViewModel appVm = new ViewModelProvider(this).get(AppUtilViewModel.class);
        appVm.getCheckerData().observe(this, launchChecker -> {
            try{
                if (launchChecker.getTimesLaunched()>0){
                    appVm.UpdateLaunchChecker(new LaunchChecker(launchChecker.getId(),
                            launchChecker.getTimesLaunched()+1));
                    appVm.getCheckerData().removeObservers(this);
                }
            }catch (Exception ignored){
                appVm.InsertLaunchChecker(new LaunchChecker(0));
            }
        });
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
                    .setDuration(300);
            viewAnimator.setInterpolator(new DecelerateInterpolator()).start();
            animationStarted = true;
        }
        dataFetch();
    }

    private void dataFetch() {
        uvm.getUserId().observe(this, firebaseUser -> {
            if (firebaseUser == null) {
                startActivity(new Intent(this, Landing.class));
                finish();
            } else {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });
    }
}