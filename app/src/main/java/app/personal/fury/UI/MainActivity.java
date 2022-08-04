package app.personal.fury.UI;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import app.personal.Utls.Constants;
import app.personal.Utls.ViewPager.viewPager;
import app.personal.fury.R;
import app.personal.fury.ViewPagerAdapter.vpAdapter;

public class MainActivity extends AppCompatActivity {

    private viewPager vp;
    private TabLayout tl;
    private TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        //init AD here..
        findView();
        initViewPager();
    }

    private void findView() {
        vp = findViewById(R.id.viewPager);
        tl = findViewById(R.id.tabLayout);
        toolbarTitle = findViewById(R.id.ab_title);
    }

    private void initViewPager() {
        vpAdapter adapter = new vpAdapter(getSupportFragmentManager());

        //Add fragments here
        adapter.addFragment(new Exp_Tracker(), Constants.Exp);
        adapter.addFragment(new Salary_Planner(), Constants.Salary);
        adapter.addFragment(new Dues_Debt(), Constants.Dues);

        vp.setAdapter(adapter);
        vp.setPagingEnabled(false);
        tl.setupWithViewPager(vp, true);
        toolbarTitle.setText(Objects.requireNonNull(tl.getTabAt(0)).getText());
        tl.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //vp.setCurrentItem(tab.getPosition());
                toolbarTitle.setText(tab.getText());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                toolbarTitle.setText(tab.getText());
            }
        });

//        Setting icons for items in tabLayout
        for (int i = 0; i < tl.getTabCount(); i++) {
//            Use after all icons available
            CharSequence text = Objects.requireNonNull(tl.getTabAt(i)).getText();
            if (text != null) {
                if (Constants.Exp.contentEquals(text)) {
                    Objects.requireNonNull(tl.getTabAt(i)).setIcon(R.drawable.ic_home);
                } else if (Constants.Dues.contentEquals(text)) {
                    Objects.requireNonNull(tl.getTabAt(i)).setIcon(R.drawable.ic_add);
                } else if (Constants.Salary.contentEquals(text)) {
                    Objects.requireNonNull(tl.getTabAt(i)).setIcon(R.drawable.ic_warning);
                }
            }else{
                Log.e(Constants.mActivityLog,"Tab text null");
            }
        }
    }
}