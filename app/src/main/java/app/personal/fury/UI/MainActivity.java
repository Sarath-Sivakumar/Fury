package app.personal.fury.UI;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import app.personal.MVVM.Viewmodel.LoggedInUserViewModel;
import app.personal.MVVM.Viewmodel.userInitViewModel;
import app.personal.Utls.Constants;
import app.personal.Utls.ViewPager.viewPager;
import app.personal.fury.R;
import app.personal.fury.UI.Drawer.About_Activity;
import app.personal.fury.UI.Drawer.Notification_Activity;
import app.personal.fury.UI.Drawer.Settings_Activity;
import app.personal.fury.UI.Drawer.help_Activity;
import app.personal.fury.UI.Fragments.BudgetFragment;
import app.personal.fury.UI.Fragments.Dues_Debt;
import app.personal.fury.UI.Fragments.Ear_Tracker;
import app.personal.fury.UI.Fragments.Exp_Tracker;
import app.personal.fury.UI.Fragments.fragment_main;
import app.personal.fury.UI.User_Init.Landing;
import app.personal.fury.ViewPagerAdapter.vpAdapter;

public class MainActivity extends AppCompatActivity {

    private static viewPager vp;
    private userInitViewModel uvm;
    private LoggedInUserViewModel luvm;
    private TabLayout tl;
    private DrawerLayout dl;
    private Toolbar tb;
    private ImageView userDp;
    private TextView userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try{
            init();
            setNav();
            setUserViewModel();
            if (savedInstanceState == null) {
                vp.setCurrentItem(2, true);
            }
        }catch (Exception ignored){}
    }

    private void init() {
        //init AD here..
        uvm = new ViewModelProvider(this).get(userInitViewModel.class);
        luvm = new ViewModelProvider(this).get(LoggedInUserViewModel.class);
        findView();
        initViewPager();
    }

    private void setUserViewModel(){
        luvm.getIsLoggedOut().observe(this, Boolean->{
            if (Boolean){
                startActivity(new Intent(this, Landing.class));
                finish();
            }
        });

        luvm.getUserData().observe(this, userEntity -> {
            if (userEntity!=null){
                String uname = userEntity.getName();
                String[] arr= uname.split(" ");
                String fname=arr[0];
//                String lname=arr[1];
//                Log.d("First name",fname);
//                Log.d("last name",lname);
                String s = "Hello "+fname;
                userName.setText(s);
                if (userEntity.getImgUrl().equals(Constants.DEFAULT_DP)){
                    userDp.setImageResource(R.drawable.nav_icon_account);
                }else{
                    Glide.with(this).load(userEntity.getImgUrl()).circleCrop().into(userDp);
                }
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
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.common_icon_menu);

        NavigationView navView = findViewById(R.id.navView);
        navView.inflateHeaderView(R.layout.nav_header);

        View v = navView.getHeaderView(0);

        userDp = (ImageView) v.findViewById(R.id.userDP);
        userName = (TextView) v.findViewById(R.id.profileName);

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
                    startActivity(new Intent(MainActivity.this, About_Activity.class));
                    break;
                case R.id.logout:
                    luvm.LogOut();
                    finishAffinity();
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
        adapter.addFragment(new Ear_Tracker());
        adapter.addFragment(new Dues_Debt());


        vp.setAdapter(adapter);
        vp.setPagingEnabled(false);
        tl.setupWithViewPager(vp, true);
        Objects.requireNonNull(tl.getTabAt(0)).setIcon(R.drawable.frag_icon_expense);
        Objects.requireNonNull(tl.getTabAt(1)).setIcon(R.drawable.frag_icon_budget);
        Objects.requireNonNull(tl.getTabAt(2)).setIcon(R.drawable.frag_icon_home);
        Objects.requireNonNull(tl.getTabAt(3)).setIcon(R.drawable.frag_icon_earnings);
        Objects.requireNonNull(tl.getTabAt(4)).setIcon(R.drawable.frag_icon_dues);
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