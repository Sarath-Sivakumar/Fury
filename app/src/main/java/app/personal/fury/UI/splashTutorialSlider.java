package app.personal.fury.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import app.personal.Utls.ViewPager.viewPager;
import app.personal.fury.R;
import app.personal.fury.UI.Adapters.ViewPagerAdapter.vpAdapter;
import app.personal.fury.UI.User_Init.walkthroughFrag.frag1;
import app.personal.fury.UI.User_Init.walkthroughFrag.frag2;
import app.personal.fury.UI.User_Init.walkthroughFrag.frag3;
import app.personal.fury.UI.User_Init.walkthroughFrag.frag4;

public class splashTutorialSlider extends AppCompatActivity {

    private static viewPager vp;
    private TabLayout tl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_tutorial_slider);
        findView();
        setVp();
    }

    private void findView(){
        vp = findViewById(R.id.sliderVP);
        tl = findViewById(R.id.sliderTab);
    }

    private void setVp(){
        vpAdapter adapter = new vpAdapter(getSupportFragmentManager());

        adapter.addFragment(new frag1());
        adapter.addFragment(new frag2());
        adapter.addFragment(new frag3());
        adapter.addFragment(new frag4());
        vp.setAdapter(adapter);
        vp.setPagingEnabled(false);
        tl.setupWithViewPager(vp, true);
    }

    public static void setPage(int Index){
        vp.setCurrentItem(Index);
    }
}