package app.personal.fury.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import app.personal.MVVM.Viewmodel.LoggedInUserViewModel;
import app.personal.MVVM.Viewmodel.userInitViewModel;
import app.personal.Utls.Constants;
import app.personal.Utls.ViewPager.viewPager;
import app.personal.fury.R;
import app.personal.fury.UI.Drawer.Notification_Activity;
import app.personal.fury.UI.Drawer.Settings_Activity;
import app.personal.fury.UI.Drawer.help_Activity;
import app.personal.fury.UI.User_Init.Landing;
import app.personal.fury.ViewPagerAdapter.vpAdapter;

public class MainActivity extends AppCompatActivity {

    private static viewPager vp;
    private userInitViewModel uvm;
    private LoggedInUserViewModel luvm;
    private TabLayout tl;
    private DrawerLayout dl;
    private Toolbar tb;

    private TextView userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUserViewModel();
        init();
        setNav();

        if (savedInstanceState==null){
            vp.setCurrentItem(2,true);
        }
    }

    private void init() {
        //init AD here..
        findView();
        initViewPager();
    }

    private void setUserViewModel(){
        uvm = new ViewModelProvider(this).get(userInitViewModel.class);
        luvm = new ViewModelProvider(this).get(LoggedInUserViewModel.class);
        luvm.getIsLoggedOut().observe(this, Boolean->{
            if (Boolean){
                startActivity(new Intent(this, Landing.class));
                finish();
            }
        });
        uvm.getUserId().observe(this, firebaseUser -> {
            if (firebaseUser==null){
                startActivity(new Intent(this, Landing.class));
                finish();
            }
        });
    }

    private void setNav(){
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                dl, tb,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        dl.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);

        NavigationView navView = findViewById(R.id.navView);
        navView.inflateHeaderView(R.layout.nav_header);
        navView.inflateMenu(R.menu.nav_menu);
        navView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.notification:
                    startActivity(new Intent(MainActivity.this, Notification_Activity.class));
                    break;
                case R.id.help:
                    startActivity(new Intent(MainActivity.this, help_Activity.class));
                    break;
                case R.id.settings:
                    startActivity(new Intent(MainActivity.this, Settings_Activity.class));
                    break;
                case R.id.about:
                    break;
                case R.id.logout:
                    luvm.LogOut();
                    break;
                default:
                    Log.e("NavView", "Default");
                    break;
            }
            dl.closeDrawer(GravityCompat.START);
            return true;
        });
    }

//    private void profileinfo() {
//    }

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
        adapter.addFragment(new Exp_Tracker());
        adapter.addFragment(new BudgetFragment());
        adapter.addFragment(new fragment_main());
        adapter.addFragment(new Salary_Planner());
        adapter.addFragment(new Dues_Debt());


        vp.setAdapter(adapter);
        vp.setPagingEnabled(false);
        tl.setupWithViewPager(vp, true);
        Objects.requireNonNull(tl.getTabAt(0)).setIcon(R.drawable.expense);
        Objects.requireNonNull(tl.getTabAt(1)).setIcon(R.drawable.budget);
        Objects.requireNonNull(tl.getTabAt(2)).setIcon(R.drawable.home);
        Objects.requireNonNull(tl.getTabAt(3)).setIcon(R.drawable.rupee);
        Objects.requireNonNull(tl.getTabAt(4)).setIcon(R.drawable.invoice);
        tl.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vp.setCurrentItem(tab.getPosition());
                switch(tab.getPosition()){
                    case 0:
                        tb.setTitle(Constants.Exp);
                        break;
                    case 1:
                        tb.setTitle(Constants.Budget);
                        break;
                    case 2:
                        tb.setTitle(Constants.main);
                        break;
                    case 3:
                        tb.setTitle(Constants.Earnings);
                        break;
                    case 4:
                        tb.setTitle(Constants.Dues);
                        break;
                }
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