package app.personal.fury.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;

import java.util.Objects;
import java.util.Set;

import app.personal.Utls.Constants;
import app.personal.Utls.ViewPager.viewPager;
import app.personal.fury.R;
import app.personal.fury.ViewPagerAdapter.vpAdapter;

public class MainActivity extends AppCompatActivity {

    private viewPager vp;
    private TabLayout tl;
    private ImageView alert,help;
    private TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
//        Utils();
    }

    private void init() {
        //init AD here..
        findView();
        initViewPager();
    }

    private void findView() {
        vp = findViewById(R.id.viewPager);
        tl = findViewById(R.id.tabLayout);
//        alert = findViewById(R.id.B_Notification);
//        help = findViewById(R.id.B_help);
//        toolbarTitle = findViewById(R.id.ab_title);
    }

//    private void Utils() {
//        alert.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this,Notification_Activity.class);
//                startActivity(intent);
//            }
//        });
//        help.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this,help_Activity.class);
//                startActivity(intent);
//            }
//        });
//    }
    private void initViewPager() {
        vpAdapter adapter = new vpAdapter(getSupportFragmentManager());

        //Add fragments here
        adapter.addFragment(new fragment_main(), Constants.main);
        adapter.addFragment(new Exp_Tracker(), Constants.Exp);
        adapter.addFragment(new Salary_Planner(), Constants.Salary);
        adapter.addFragment(new Dues_Debt(), Constants.Dues);

        vp.setAdapter(adapter);
        vp.setPagingEnabled(false);
        tl.setupWithViewPager(vp, true);
//        toolbarTitle.setText(Objects.requireNonNull(tl.getTabAt(0)).getText());
        tl.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vp.setCurrentItem(tab.getPosition());
//                toolbarTitle.setText(tab.getText());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
//                toolbarTitle.setText(tab.getText());
            }
        });
    }
    public void redirectTo(int i){
        vp.setCurrentItem(i);
    }
}