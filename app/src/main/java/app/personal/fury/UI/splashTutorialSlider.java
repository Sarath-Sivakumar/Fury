package app.personal.fury.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import app.personal.Utls.ViewPager.viewPager;
import app.personal.fury.R;

public class splashTutorialSlider extends AppCompatActivity {

    private viewPager vp;
    private TabLayout tl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_tutorial_slider);
        findView();
    }

    private void findView(){
        vp = findViewById(R.id.sliderVP);
        tl = findViewById(R.id.sliderTab);
    }
}