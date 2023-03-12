package app.personal.fury.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
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
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import app.personal.MVVM.Entity.balanceEntity;
import app.personal.MVVM.Entity.inHandBalEntity;
import app.personal.MVVM.Entity.salaryEntity;
import app.personal.MVVM.Viewmodel.LoggedInUserViewModel;
import app.personal.MVVM.Viewmodel.mainViewModel;
import app.personal.MVVM.Viewmodel.userInitViewModel;
import app.personal.Utls.Commons;
import app.personal.Utls.Constants;
import app.personal.Utls.TutorialUtil;
import app.personal.Utls.ViewPager.viewPager;

import app.personal.fury.R;
import app.personal.fury.ui.Drawer.Notification_Activity;
import app.personal.fury.ui.Drawer.Settings_Activity;
import app.personal.fury.ui.Fragments.BudgetFragment;
import app.personal.fury.ui.Fragments.Dues_Debt;
import app.personal.fury.ui.Fragments.Ear_Tracker;
import app.personal.fury.ui.Fragments.Exp_Tracker;
import app.personal.fury.ui.Fragments.fragment_main;
import app.personal.fury.ui.User_Init.Landing;
import app.personal.fury.ViewPagerAdapter.vpAdapter;

public class MainActivity extends AppCompatActivity {

    private static viewPager vp;
    private userInitViewModel uvm;
    private mainViewModel vm;
    private LoggedInUserViewModel userVM;
    private static TabLayout tl;
    private DrawerLayout dl;
    private Toolbar tb;
    private ImageView userDp;
    private TextView userName;
    private static TutorialUtil util;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        setNav();
        setUserViewModel();
        try{
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
        for (int i = 0; i <= salList.size() - 1; i++) {
            salaryEntity sal = salList.get(i);

            if (!sal.getCreationDate().equals(Commons.getDate())) {
                if (sal.getIncType() == Constants.monthly) {
                    monthlyCalculations(sal);
                } else if (sal.getIncType() == Constants.daily) {
                    dailyCalculations(sal);
                } else if (sal.getIncType() == Constants.oneTime) {
                    Log.e("Local Repository", "processSalary: oneTime");
                } else {
                    Log.e("App", "processSalary: " + sal.getIncType() + " well well! what might this be?!");
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
                for (int i = 0; i <= daysDiff; i++) {
                    if (!sal.getCreationDate().equals(Commons.getDate())) {
                        if (sal.getSalMode() == Constants.SAL_MODE_CASH) {
                            int inHandBal = getInHandBal();
                            vm.DeleteInHandBalance();
                            vm.InsertInHandBalance(new inHandBalEntity(inHandBal + sal.getSalary()));
                            sal.setCreationDate(Commons.getDate());
                            vm.UpdateSalary(sal);
                        } else if (sal.getSalMode() == Constants.SAL_MODE_ACC) {
                            int bal = getBal();
                            vm.DeleteBalance();
                            vm.InsertBalance(new balanceEntity(bal + sal.getSalary()));
                            sal.setCreationDate(Commons.getDate());
                            vm.UpdateSalary(sal);
                        } else {
                            Log.e("App", "Huh?! Please stop! uwwu!");
                        }
                    }
                }
            } else {
                Log.e("App", "Dates are null good job!");
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

        if (Objects.equals(splitCurrentDate[2], splitCreationDate[2])) {
            //Will work for current year.
            if (Objects.equals(splitCurrentDate[0], splitCreationDate[0]) &&
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
        userVM = new ViewModelProvider(this).get(LoggedInUserViewModel.class);
        vm = new ViewModelProvider(this).get(mainViewModel.class);

        //Firebase Topics to be implemented here..
        vm.initFirebaseMessagingService(Constants.ExpFirebaseService);
        util = new TutorialUtil(this,this, this,this);
        findView();
        initViewPager();
        isStoragePermissionGranted();
//        redirectTo(1);
        initTutorialPhase1();
    }

    public static void initTutorialPhase3(){
        ArrayList<View> Targets = new ArrayList<>();
        ArrayList<String> PrimaryTexts = new ArrayList<>();
        ArrayList<String> SecondaryTexts = new ArrayList<>();
        redirectTo(2);
//        Home
        Targets.add(Objects.requireNonNull(tl.getTabAt(1)).view);
        PrimaryTexts.add("Budget");
        SecondaryTexts.add("Lets set your budget!");
//        ----
        util.TutorialPhase3(Targets, PrimaryTexts, SecondaryTexts);
    }

    public static void initTutorialPhase5(){
        ArrayList<View> Targets = new ArrayList<>();
        ArrayList<String> PrimaryTexts = new ArrayList<>();
        ArrayList<String> SecondaryTexts = new ArrayList<>();
//        Home
        Targets.add(Objects.requireNonNull(tl.getTabAt(0)).view);
        PrimaryTexts.add("Expenses");
        SecondaryTexts.add("Lets explore the Expense Tracker!");
//        ----
        util.TutorialPhase5(Targets, PrimaryTexts, SecondaryTexts);
    }

    private void initTutorialPhase1(){
        TutorialUtil util = new TutorialUtil(this,this, this,this);
        util.setPhaseStatus(0);
        ArrayList<View> Targets = new ArrayList<>();
        ArrayList<String> PrimaryTexts = new ArrayList<>();
        ArrayList<String> SecondaryTexts = new ArrayList<>();
//        Home
        Targets.add(Objects.requireNonNull(tl.getTabAt(2)).view);
        PrimaryTexts.add("Welcome To Fury home");
        SecondaryTexts.add("This is where most of your activity in Fury gets summarized");
//        ----
//        Earnings
        Targets.add(Objects.requireNonNull(tl.getTabAt(3)).view);
        PrimaryTexts.add("Let's track your earnings first!");
        SecondaryTexts.add("Tap here to manage your earnings");

        util.TutorialPhase1(Targets, PrimaryTexts, SecondaryTexts);
    }

    private void setUserViewModel(){
        userVM.getIsLoggedOut().observe(this, Boolean->{
            if (Boolean){
                startActivity(new Intent(this, Landing.class));
                finish();
            }
        });

        userVM.getUserData().observe(this, userEntity -> {
            if (userEntity!=null){
                String uname = userEntity.getName();
                String[] arr= uname.split(" ");
                String fName=arr[0];
                String s = "Hello "+fName;
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
        navView.inflateHeaderView(R.layout.nav_main_header);

        View v = navView.getHeaderView(0);

        userDp = (ImageView) v.findViewById(R.id.userDP);
        userName = (TextView) v.findViewById(R.id.profileName);

        navView.inflateMenu(R.menu.nav_menu);

        navView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId()==R.id.notification){
                startActivity(new Intent(MainActivity.this, Notification_Activity.class));
            } else if (item.getItemId()==R.id.settings) {
                startActivity(new Intent(MainActivity.this, Settings_Activity.class));
            } else if (item.getItemId()==R.id.logout) {
                logout(navView);
            } else if (item.getItemId()==R.id.rateus) {
                rate();
            } else if (item.getItemId()==R.id.donate) {
                donate(navView);
            } else if (item.getItemId()==R.id.invite) {
                share(MainActivity.this);
            }else{
                Log.e("App", "Daddy stop! Please aaaah!");
            }

            dl.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void rate(){
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
        }
        catch (ActivityNotFoundException e){
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id" + getPackageName())));
        }

    }

    private  void share(Context context){
        Intent sentIntent = new Intent();
        sentIntent.setAction(Intent.ACTION_SEND);
        sentIntent.putExtra(Intent.EXTRA_TEXT,"Get Fury now on : https://play.google.com/store/apps/details?id="+getPackageName());
        sentIntent.setType("text/plain");
        context.startActivity(sentIntent);
    }

    @SuppressLint("SetTextI18n")
    private void donate(View navView){
        PopupWindow popupWindow = new PopupWindow(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.popup_action_acceptads, null);
        popupWindow.setContentView(view);
        popupWindow.setFocusable(true);


        Button yes = view.findViewById(R.id.dyes_btn);
        Button no = view.findViewById(R.id.dno_btn);
        no.setOnClickListener(v -> popupWindow.dismiss());
        yes.setOnClickListener(v -> {
            popupWindow.dismiss();
            Commons.SnackBar(navView, "Oops! something went wrong :(");
        });

        popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setBackgroundDrawable(null);
        popupWindow.setElevation(6);
        popupWindow.setOverlapAnchor(true);
        popupWindow.showAsDropDown(navView);
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
                    default:
                        tb.setTitle("Noiccee!");
                        break;
                }
                tb.setTitleTextColor(Color.WHITE);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void logout(View navView){
        PopupWindow popupWindow = new PopupWindow(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.popup_user_logout, null);
        popupWindow.setContentView(view);
        popupWindow.setFocusable(true);

        Button yes = view.findViewById(R.id.yes_btn);
        Button no = view.findViewById(R.id.no_btn);
        no.setOnClickListener(v -> popupWindow.dismiss());
        yes.setOnClickListener(v -> {
            userVM.LogOut();
            finishAffinity();
        });

        popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setBackgroundDrawable(null);
        popupWindow.setElevation(6);
        popupWindow.setOverlapAnchor(true);
        popupWindow.showAsDropDown(navView);
    }

//    @Override //To be implemented later..//Todo
//    public void onBackPressed() {
//        super.onBackPressed();
//        if (vp.getCurrentItem()!=0){
//            redirectTo(0);
//        }else {
//            finishAffinity();
//        }
//    }

    public static void redirectTo(int i){
        int item = vp.getCurrentItem();
        try{
            vp.setCurrentItem(i);
        }catch (Exception e){
            vp.setCurrentItem(item);
            Log.e("App","Stop it daddy! uwwu!");
        }
    }

    public static int getTab(){
        return vp.getCurrentItem();
    }
    public void isStoragePermissionGranted() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Log.v("permission","Permission is granted");
        } else {
            Log.v("permission","Permission is revoked");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Log.v("permission","Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }
}