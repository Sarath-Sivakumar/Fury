package app.personal.fury.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import app.personal.MVVM.Entity.balanceEntity;
import app.personal.MVVM.Entity.debtEntity;
import app.personal.MVVM.Entity.inHandBalEntity;
import app.personal.MVVM.Entity.salaryEntity;
import app.personal.MVVM.Viewmodel.LoggedInUserViewModel;
import app.personal.MVVM.Viewmodel.mainViewModel;
import app.personal.MVVM.Viewmodel.userInitViewModel;
import app.personal.Utls.Commons;
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
    private mainViewModel vm;
    private LoggedInUserViewModel luvm;
    private TabLayout tl;
    private DrawerLayout dl;
    private Toolbar tb;
    private ImageView userDp;
    private TextView userName;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try{
            init();
            setNav();
            setUserViewModel();
            vm.getSalary().observe(this, salaryEntityList -> {
                if (salaryEntityList!=null){
                    processSalary(salaryEntityList);
                }
            });
        }catch (Exception e){
            Log.e("Main", "onCreateError: "+e.getMessage());
        }
        if (savedInstanceState == null) {
            vp.setCurrentItem(2, true);
        }
    }

    private void processSalary(@NonNull List<salaryEntity> salList) {
        Log.e("Local Repository", "processSalary: size: " + salList.size());
        for (int i = 0; i <= salList.size() - 1; i++) {
            Log.e("Local Repository", "processSalary: i: " + i);
            salaryEntity sal = salList.get(i);

            if (!sal.getCreationDate().equals(Commons.getDate())) {
                if (sal.getIncType() == Constants.monthly) {
                    monthlyCalculations(sal);
                } else if (sal.getIncType() == Constants.daily) {
                    dailyCalculations(sal);
                } else if (sal.getIncType() == Constants.oneTime) {
                    Log.e("Local Repository", "processSalary: onTime");
                } else {
                    Log.e("Local Repository", "processSalary: " + sal.getIncType() + " well well! what might this be?!");
                }
            }

        }
    }
    private void dailyCalculations(salaryEntity sal) {
        try {
            String creationDate = sal.getCreationDate();
            Date dateBefore = sdf.parse(creationDate);
            Date dateAfter = sdf.parse(Commons.getDate());
            if (dateBefore != null && dateAfter != null) {
                long timeDiff = Math.abs(dateAfter.getTime() - dateBefore.getTime());
                long daysDiff = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);
                Log.e("Local Repository", "processSalary: daysDiff: " + daysDiff);
                for (int i = 0; i <= daysDiff; i++) {
                    Log.e("Local Repository", "inside processSalary: daysDiff: " + i);
                    Log.e("Local Repository", "inside processSalary: sal mode: " + sal.getSalMode());
                    Log.e("Local Repository", "inside processSalary: salary: " + sal.getSalary());
                    if (!sal.getCreationDate().equals(Commons.getDate())) {
                        if (sal.getSalMode() == Constants.SAL_MODE_CASH) {
                            int inHandBal = getInHandBal();
                            Log.e("Local Repository", "inside processSalary: inHand before: " + inHandBal);
                            vm.DeleteInHandBalance();
                            vm.InsertInHandBalance(new inHandBalEntity(inHandBal + sal.getSalary()));
                            Log.e("Local Repository", "inside processSalary: inHand after: " + getInHandBal());
                            sal.setCreationDate(Commons.getDate());
                            vm.UpdateSalary(sal);
                        } else if (sal.getSalMode() == Constants.SAL_MODE_ACC) {
                            int bal = getBal();
                            Log.e("Local Repository", "inside processSalary: acc before: " + bal);
                            vm.DeleteBalance();
                            vm.InsertBalance(new balanceEntity(bal + sal.getSalary()));
                            Log.e("Local Repository", "inside processSalary: acc after: " + getBal());
                            sal.setCreationDate(Commons.getDate());
                            vm.UpdateSalary(sal);
                        } else {
                            Log.e("Local Repository", "Huh?!");
                        }
                    }
                }
            } else {
                Log.e("Local Repository", "Dates are null good job!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Local Repository", "Error: " + e.getMessage());
        }
    }

    private void monthlyCalculations(salaryEntity sal) {
        String creationDate = sal.getCreationDate();
        String currentDate = Commons.getDate();
        String[] splitCreationDate = creationDate.split("/");
        String[] splitCurrentDate = currentDate.split("/");

        if (splitCurrentDate[2] == splitCreationDate[2]) {
            //Will work for current year.
            if (splitCurrentDate[0] == splitCreationDate[0] &&
                    Integer.parseInt(splitCurrentDate[1]) > Integer.parseInt(splitCreationDate[1])) {
                int mode = sal.getSalMode();
                if (mode == Constants.SAL_MODE_CASH) {
                    int inHandBal = getInHandBal();
                    vm.DeleteInHandBalance();
                    vm.InsertInHandBalance(new inHandBalEntity(inHandBal + sal.getSalary()));
                } else {
                    int bal = getBal();
                    vm.DeleteBalance();
                    vm.InsertBalance(new balanceEntity(bal + sal.getSalary()));
                }
                sal.setCreationDate(Commons.getDate());
                vm.UpdateSalary(sal);
            }
        } else {
            //Next Year.
            int mode = sal.getSalMode();
            if (mode == Constants.SAL_MODE_CASH) {
                int inHandBal = getInHandBal();
                vm.DeleteInHandBalance();
                vm.InsertInHandBalance(new inHandBalEntity(inHandBal + sal.getSalary()));
            } else {
                int bal = 0;
                try {
                    bal = getBal();
                } catch (Exception ignored) {
                }
                vm.DeleteBalance();
                vm.InsertBalance(new balanceEntity(bal + sal.getSalary()));
            }
            sal.setCreationDate(Commons.getDate());
            vm.UpdateSalary(sal);
        }
    }
    private void init() {
        //init AD here..
        uvm = new ViewModelProvider(this).get(userInitViewModel.class);
        luvm = new ViewModelProvider(this).get(LoggedInUserViewModel.class);
        vm = new ViewModelProvider(this).get(mainViewModel.class);
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
//                Put this in a try block
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

    private int getInHandBal(){
        AtomicInteger bal = new AtomicInteger();
        vm.getInHandBalance().observe(this, inHandBalEntity -> {
            if (inHandBalEntity != null) {
                bal.set(inHandBalEntity.getBalance());
            }else{
                bal.set(0);
            }
        });
        return bal.get();
    }

    private int getBal(){
        AtomicInteger bal = new AtomicInteger();
        vm.getBalance().observe(this, balanceEntity -> {
            if (balanceEntity!=null){
                bal.set(balanceEntity.getBalance());
            }else {
                bal.set(0);
            }
        });
        return bal.get();
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
//                        tb.setTitleTextColor();
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