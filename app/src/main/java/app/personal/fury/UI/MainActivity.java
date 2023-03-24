package app.personal.fury.UI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;

import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
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
import app.personal.fury.UI.Drawer.Notification_Activity;
import app.personal.fury.UI.Drawer.Settings_Activity;
import app.personal.fury.UI.Fragments.BudgetFragment;
import app.personal.fury.UI.Fragments.Dues_Debt;
import app.personal.fury.UI.Fragments.Ear_Tracker;
import app.personal.fury.UI.Fragments.Exp_Tracker;
import app.personal.fury.UI.Fragments.fragment_main;
import app.personal.fury.UI.User_Init.Landing;
import app.personal.fury.UI.Adapters.ViewPagerAdapter.vpAdapter;

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
    private InterstitialAd interstitial;
    @ColorInt
    private int accent;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        final TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String code;
        if (!tm.getSimCountryIso().isEmpty()) {
            code = tm.getSimCountryIso();
            Log.e("Currency", "Sim code: " + code);
            vm.setCountryCode(code);
            vm.initCurrency();
            vm.getRupee().observe(this, String -> {
//                new Currency().setCurrency(String);
                Constants.setRUPEE(String);
                Log.e("Currency", "Symbol code: "+Constants.getRUPEE());
            });
        } else {
            code = tm.getNetworkCountryIso();
            vm.setCountryCode(code);
            Log.e("Currency", "Network code: " + code);
            vm.initCurrency();
            vm.getRupee().observe(this, String -> {
//                new Currency().setCurrency(String);
                Constants.setRUPEE(String);
                Log.e("Currency", "Symbol code: "+Constants.getRUPEE());
            });
        }
        setNav();
        setUserViewModel();
        try {
            vm.getSalary().observe(this, salaryEntityList -> {
                if (salaryEntityList != null) {
                    processSalary(salaryEntityList);
                }
            });
        } catch (Exception ignored) {}
        if (savedInstanceState == null) {
            tb.setTitle(Constants.main);
            vp.setCurrentItem(2, true);
            initAd();
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
        vm.initFirebaseMessagingService(Constants.PromoFirebaseService);
        vm.initFirebaseMessagingService(Constants.UpdateFirebaseService);

        util = new TutorialUtil(this, this, this, this);
        findView();
        initViewPager();
        isStoragePermissionGranted();
        initTutorialPhase1();
    }

    private void initAd() {
        MobileAds.initialize(this);
        String TestAdId = "ca-app-pub-8620335196955785/6964591880";
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(
                this,
                TestAdId,
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        interstitial = interstitialAd;
                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        interstitial = null;
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                        // Called when fullscreen content failed to show.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        interstitial = null;
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                    }
                                });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        interstitial = null;
                        Commons.SnackBar(tb, "No Internet connection available");
                    }
                });
    }

    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (interstitial != null) {
            interstitial.show(this);
        }
    }

    public static void initTutorialPhase3() {
        ArrayList<View> Targets = new ArrayList<>();
        ArrayList<String> PrimaryTexts = new ArrayList<>();
        ArrayList<String> SecondaryTexts = new ArrayList<>();
        redirectTo(2);
//        Home
        Targets.add(Objects.requireNonNull(tl.getTabAt(1)).view);
        PrimaryTexts.add("This is the Budgeting Zone");
        SecondaryTexts.add("Tap here to add a sample budget\n(This sample will be cleared after the tutorial)");
//        ----
        util.TutorialPhase3(Targets, PrimaryTexts, SecondaryTexts);
    }

    public static void initTutorialPhase5() {
        ArrayList<View> Targets = new ArrayList<>();
        ArrayList<String> PrimaryTexts = new ArrayList<>();
        ArrayList<String> SecondaryTexts = new ArrayList<>();
//        Home
        Targets.add(Objects.requireNonNull(tl.getTabAt(0)).view);
        PrimaryTexts.add("The Expense Tracker");
        SecondaryTexts.add("Lets add a sample expense\n(This sample will be cleared after the tutorial)");
//        ----
        util.TutorialPhase5(Targets, PrimaryTexts, SecondaryTexts);
    }

    private void initTutorialPhase1() {
        TutorialUtil util = new TutorialUtil(this, this, this, this);
        util.setPhaseStatus(0);
        ArrayList<View> Targets = new ArrayList<>();
        ArrayList<String> PrimaryTexts = new ArrayList<>();
        ArrayList<String> SecondaryTexts = new ArrayList<>();
//        Home
        Targets.add(Objects.requireNonNull(tl.getTabAt(2)).view);
        PrimaryTexts.add("Welcome To Fury");
        SecondaryTexts.add("Follow this tutorial to see how fury works\n\n(DISCLAIMER all data collected during this phase is taken as sample and will be cleared after completing the tutorial)\n\n");
//        ----
//        Earnings
        Targets.add(Objects.requireNonNull(tl.getTabAt(3)).view);
        PrimaryTexts.add("This is the Earnings Tracker!");
        SecondaryTexts.add("Tap here to add a sample of your earnings\n(This sample will be cleared after tutorial)\n");

        util.TutorialPhase1(Targets, PrimaryTexts, SecondaryTexts);
    }

    private void setUserViewModel() {
        userVM.getIsLoggedOut().observe(this, Boolean -> {
            if (Boolean) {
                startActivity(new Intent(this, Landing.class));
                finish();
            }
        });

        userVM.getUserData().observe(this, userEntity -> {
            if (userEntity != null) {
                String uname = userEntity.getName();
                String[] arr = uname.split(" ");
                String fName = arr[0];
                String s = "Hello " + fName;
                userName.setText(s);
                if (userEntity.getImgUrl().equals(Constants.DEFAULT_DP)) {
                    userDp.setImageResource(R.drawable.nav_icon_account);
                } else {
                    Glide.with(this).load(userEntity.getImgUrl()).circleCrop().into(userDp);
                }
            }
        });

        uvm.getUserId().observe(this, firebaseUser -> {
            if (firebaseUser == null) {
                startActivity(new Intent(this, Landing.class));
                finish();
            }
        });
    }

    private int getInHandBal() {
        AtomicInteger bal = new AtomicInteger();
        vm.getInHandBalance().observe(this, inHandBalEntity -> {
            if (inHandBalEntity != null) {
                bal.set(inHandBalEntity.getBalance());
            } else {
                bal.set(0);
            }
        });
        return bal.get();
    }

    private int getBal() {
        AtomicInteger bal = new AtomicInteger();
        vm.getBalance().observe(this, balanceEntity -> {
            if (balanceEntity != null) {
                bal.set(balanceEntity.getBalance());
            } else {
                bal.set(0);
            }
        });
        return bal.get();
    }

    private void setNav() {
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

        userDp = v.findViewById(R.id.userDP);
        userName = v.findViewById(R.id.profileName);

        navView.inflateMenu(R.menu.nav_menu);

        navView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.notification) {
                startActivity(new Intent(MainActivity.this, Notification_Activity.class));
            } else if (item.getItemId() == R.id.settings) {
                startActivity(new Intent(MainActivity.this, Settings_Activity.class));
            } else if (item.getItemId() == R.id.logout) {
                logout(navView);
            } else if (item.getItemId() == R.id.rateus) {
                rate();
            } else if (item.getItemId() == R.id.donate) {
                donate(navView);
            } else if (item.getItemId() == R.id.invite) {
                share(MainActivity.this);
            }

            dl.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void rate() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id" + getPackageName())));
        }

    }

    private void share(Context context) {
        Intent sentIntent = new Intent();
        sentIntent.setAction(Intent.ACTION_SEND);
        sentIntent.putExtra(Intent.EXTRA_TEXT, "Get Fury now on : https://play.google.com/store/apps/details?id=" + getPackageName());
        sentIntent.setType("text/plain");
        context.startActivity(sentIntent);
    }

    @SuppressLint("SetTextI18n")
    private void donate(View navView) {
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
            showInterstitial();
            popupWindow.dismiss();
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

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getTheme();
        theme.resolveAttribute(com.google.android.material.R.attr.colorAccent, typedValue, true);
        accent = typedValue.data;

        //Add fragments here
        adapter.addFragment(new Exp_Tracker());
        adapter.addFragment(new BudgetFragment());
        adapter.addFragment(new fragment_main());
        adapter.addFragment(new Ear_Tracker());
        adapter.addFragment(new Dues_Debt());

        vp.setAdapter(adapter);
        vp.setPagingEnabled(false);
        tl.setupWithViewPager(vp, true);
        Objects.requireNonNull(tl.getTabAt(0)).setIcon(R.drawable.frag_icon_expense).setText("Expense");
        Objects.requireNonNull(tl.getTabAt(1)).setIcon(R.drawable.frag_icon_budget).setText("Budget");
        Objects.requireNonNull(tl.getTabAt(2)).setIcon(R.drawable.frag_icon_home).setText("Home");
        Objects.requireNonNull(tl.getTabAt(3)).setIcon(R.drawable.frag_icon_earnings).setText("Earnings");
        Objects.requireNonNull(tl.getTabAt(4)).setIcon(R.drawable.frag_icon_dues).setText("Dues");
        tl.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vp.setCurrentItem(tab.getPosition());
                switch (tab.getPosition()) {
                    case 0:
                        tb.setTitle(Constants.Exp);
                        tab.getIcon().setColorFilter(accent, PorterDuff.Mode.SRC_IN);
//                        tl.setTabTextColors();
                        break;
                    case 1:
                        tb.setTitle(Constants.Budget);
                        tab.getIcon().setColorFilter(accent, PorterDuff.Mode.SRC_IN);
                        break;
                    case 2:
                        tb.setTitle(Constants.main);
                        tab.getIcon().setColorFilter(accent, PorterDuff.Mode.SRC_IN);
                        break;
                    case 3:
                        tb.setTitle(Constants.Earnings);
                        tab.getIcon().setColorFilter(accent, PorterDuff.Mode.SRC_IN);
                        break;
                    case 4:
                        tb.setTitle(Constants.Dues);
                        tab.getIcon().setColorFilter(accent, PorterDuff.Mode.SRC_IN);
                        break;
                    default:
                        tb.setTitle("Noiccee!");
                        break;
                }
                tb.setTitleTextColor(Color.WHITE);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //for removing the color of tab icon when switched to next tab (.clearColorFilter() instead of setColorFilter.)
                for (int i = 0; i <= 4; i++) {
                    Objects.requireNonNull(Objects.requireNonNull(tl.getTabAt(i)).getIcon())
                            .setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 2) {
                    tb.setTitle(Constants.main);
                }
            }
        });
    }

    private void logout(View navView) {
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

    public static void redirectTo(int i) {
        int item = vp.getCurrentItem();
        try {
            vp.setCurrentItem(i);
        } catch (Exception e) {
            vp.setCurrentItem(item);
        }
    }

    public static int getTab() {
        return vp.getCurrentItem();
    }

    public void isStoragePermissionGranted() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Log.v("permission", "Permission is granted");
        } else {
            Log.v("permission", "Permission is revoked");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v("permission", "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
        }
    }
}