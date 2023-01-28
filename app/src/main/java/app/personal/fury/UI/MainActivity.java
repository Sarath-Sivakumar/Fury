package app.personal.fury.UI;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import app.personal.Utls.Constants;
import app.personal.Utls.ViewPager.viewPager;
import app.personal.fury.R;
import app.personal.fury.ViewPagerAdapter.vpAdapter;

public class MainActivity extends AppCompatActivity {

    private static viewPager vp;
    private TabLayout tl;
    private DrawerLayout dl;
    private Toolbar tb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
//        Utils();
        setNav();

        if (savedInstanceState==null){
            vp.setCurrentItem(0,true);
        }
    }

    private void init() {
        //init AD here..
        findView();
        initViewPager();
    }

    private void setNav(){
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                dl, tb,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        dl.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navView = findViewById(R.id.navView);
        navView.inflateHeaderView(R.layout.nav_header);
        navView.inflateMenu(R.menu.nav_menu);
        navView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {

                default:
                    Log.e("NavView", "Fu*k");
                    break;
            }
            dl.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void findView() {
        vp = findViewById(R.id.viewPager);
        tl = findViewById(R.id.tabLayout);
        tb = findViewById(R.id.action_bar);
        dl = findViewById(R.id.drawerLayout);
        setSupportActionBar(tb);
    }

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
        Objects.requireNonNull(tl.getTabAt(0)).setIcon(R.drawable.rent);
        Objects.requireNonNull(tl.getTabAt(1)).setIcon(R.drawable.bill);
        Objects.requireNonNull(tl.getTabAt(2)).setIcon(R.drawable.fees);
        Objects.requireNonNull(tl.getTabAt(3)).setIcon(R.drawable.subscription);
        tl.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vp.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        if (vp.getCurrentItem()!=0){
//            redirectTo(0);
//        }else {
//            finishAffinity();
//        }
//    }

    public static void redirectTo(int i){
        vp.setCurrentItem(i);
    }
}