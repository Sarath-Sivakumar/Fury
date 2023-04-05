package app.personal.fury.UI.Fragments;

import android.content.Context;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import app.personal.MVVM.Entity.LaunchChecker;
import app.personal.MVVM.Entity.budgetEntity;
import app.personal.MVVM.Viewmodel.AppUtilViewModel;
import app.personal.MVVM.Viewmodel.mainViewModel;
import app.personal.Utls.Commons;
import app.personal.Utls.Constants;
import app.personal.Utls.TutorialUtil;
import app.personal.fury.R;
import app.personal.fury.UI.Adapters.budgetList.budgetAdapter;
import app.personal.fury.UI.MainActivity;
import app.personal.fury.UI.Adapters.infoGraphicAdapter.infoGraphicsAdapter;

public class BudgetFragment extends Fragment {

    private FloatingActionButton addBudget;
    private TextView BudgetAmt, Balance, Expense, DailyLimitAllowed, CurrentDailyLimit;
    private mainViewModel vm;
    private int totalSalary = 0, totalExp = 0;
    private budgetAdapter adapter;
    private boolean isView = false, deletedOnce = false;
    private final int[] FragmentList = new int[]{R.drawable.info_1, R.drawable.info_2, R.drawable.info_3};
//    private AdView ad;
//    private LinearLayout adLayout;
//    private AdRequest adRequest;
    private int prevType = 3, prevAmt = 0;
    private String prevDate = "0", Currency = "";
    private TutorialUtil util;
    private InterstitialAd interstitial;


    public BudgetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        adapter = new budgetAdapter();
        util = new TutorialUtil(requireActivity(), requireContext(), requireActivity(), requireActivity());
//        MobileAds.initialize(requireContext());
//        adRequest = new AdRequest.Builder().build();
    }

    private void init() {
        vm = new ViewModelProvider(requireActivity()).get(mainViewModel.class);
        vm.getRupee().observe(requireActivity(), String->{
            if (String!=null||!String.getCurrency().equals("")){
                Currency = String.getCurrency();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.main_fragment_budget, container, false);
        findView(v);
        if (savedInstanceState == null) {
//            requestAd();
        }
        if (savedInstanceState == null && Commons.isConnectedToInternet(requireContext())) {
            initAd();
        }
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initItems();
        isView = true;
    }

//    private void requestAd() {
//        ad.loadAd(adRequest);
//        ad.setAdListener(new AdListener() {
//            @Override
//            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                super.onAdFailedToLoad(loadAdError);
//                adLayout.setVisibility(View.GONE);
//                ad.loadAd(adRequest);
//            }
//
//            @Override
//            public void onAdLoaded() {
//                super.onAdLoaded();
//                adLayout.setVisibility(View.VISIBLE);
//            }
//        });
//    }

    private void findView(View v) {
        addBudget = v.findViewById(R.id.setBud);
        BudgetAmt = v.findViewById(R.id.bgtAmt);
        Balance = v.findViewById(R.id.B_Balance);
        Expense = v.findViewById(R.id.T_exp);
//        ad = v.findViewById(R.id.adView);
//        adLayout = v.findViewById(R.id.adLayout);
        RecyclerView topExp = v.findViewById(R.id.top_exp);
        topExp.setLayoutManager(new LinearLayoutManager(requireContext()));
        topExp.setHasFixedSize(true);
        topExp.setAdapter(adapter);
        DailyLimitAllowed = v.findViewById(R.id.ID_avg);
        CurrentDailyLimit = v.findViewById(R.id.C_avg);
        ViewPager ig_vp = v.findViewById(R.id.infoGraphics_vp2);
        TabLayout ig_tl = v.findViewById(R.id.infoGraphics_tab2);

        infoGraphicsAdapter igAdapter = new infoGraphicsAdapter(requireContext(), FragmentList);
        ig_vp.setAdapter(igAdapter);
        ig_tl.setupWithViewPager(ig_vp, true);
        Commons.timedSliderInit(ig_vp, FragmentList, 5);

        addBudget.setOnClickListener(v1 -> callAddBudgetPopup());
    }

    private void initItems() {
        vm.getBudget().observe(getViewLifecycleOwner(), budgetEntities -> {
            try {
                prevType = budgetEntities.getRefreshPeriod();
                prevDate = budgetEntities.getCreationDate();
                prevAmt = budgetEntities.getAmount();
                String s = Currency + budgetEntities.getAmount();
                BudgetAmt.setText(s);
                String s1 = Currency + budgetEntities.getBal();
                Balance.setText(s1);
                String s2;
                adapter.setBudgetInfo(budgetEntities.getCreationDate(), budgetEntities.getRefreshPeriod());
                if (!Commons.budgetValidityChecker(budgetEntities.getRefreshPeriod(),
                        budgetEntities.getCreationDate()) && !deletedOnce) {
                    vm.DeleteBudget();
                    deletedOnce = true;
                }
                if (prevType == Constants.BUDGET_MONTHLY) {
                    s2 = Currency + (budgetEntities.getAmount() / Commons.getDays(Calendar.MONTH)) + " /day";
                } else if (prevType == Constants.BUDGET_WEEKLY) {
                    s2 = Currency + (budgetEntities.getAmount() / 7) + " /day";
                } else {
                    s2 = "-";
                }
                DailyLimitAllowed.setText(s2);

            } catch (Exception e) {
                try {
                    String s = Currency + "0";
                    BudgetAmt.setText(s);
                    String s1 = Currency + "0";
                    Balance.setText(s1);
                    String s2 = "-";
                    DailyLimitAllowed.setText(s2);
                } catch (Exception ignored) {
                }
            }
        });

        vm.getExp().observe(getViewLifecycleOwner(), expEntities -> {
            int total = 0;
            adapter.clear();
            if (expEntities != null && !expEntities.isEmpty()) {
                adapter.setExp(expEntities, Currency);
                for (int i = 0; i < expEntities.size(); i++) {
                    total = total + expEntities.get(i).getExpenseAmt();
                }
                try {
                    String s = Commons.getAvg(expEntities, true, Currency);
                    CurrentDailyLimit.setText(s);
                    if (Objects.equals(s, "Collecting data!")) {
                        CurrentDailyLimit.setTextSize(12);
                    }
                    String s1 = Currency + total;
                    Expense.setText(s1);
                } catch (Exception ignored) {
                }
                adapter.notifyDataSetChanged();
            } else {
                try {
                    String s = "No data";
                    CurrentDailyLimit.setText(s);
                    CurrentDailyLimit.setTextSize(14);
                    String s1 = Currency + total;
                    Expense.setText(s1);
                } catch (Exception ignored) {
                }
            }
            totalExp = total;
        });

        vm.getSalary().observe(getViewLifecycleOwner(), salaryEntities -> {
            totalSalary = 0;
            if (salaryEntities != null && !salaryEntities.isEmpty()) {
                for (int i = 0; i < salaryEntities.size(); i++) {
                    totalSalary = totalSalary + salaryEntities.get(i).getSalary();
                }
            }
        });
    }

    private void callAddBudgetPopup() {
        PopupWindow popupWindow = new PopupWindow(getContext());
        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.budget_popup_create, null);
        popupWindow.setContentView(view);

        Button yes = view.findViewById(R.id.continue_btn);
        Button no = view.findViewById(R.id.No_btn);
        Button cancel = view.findViewById(R.id.cancel_btn);
        RadioGroup budTypeGrp = view.findViewById(R.id.budType);
        cancel.setOnClickListener(v -> popupWindow.dismiss());
        CalendarView cal = view.findViewById(R.id.nd_date);
        AtomicReference<String> startDate = new AtomicReference<>(Commons.getDate());

        cal.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            startDate.set(dayOfMonth + "/" + (month + 1) + "/" + year);
        });
        if (prevDate != null) {
            if (!prevDate.equals("0")) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                    Date d = sdf.parse(prevDate);
                    assert d != null;
                    cal.setDate(d.getTime());
                } catch (Exception ignored) {
                }
            }
        }

        if (prevType == Constants.BUDGET_MONTHLY) {
            budTypeGrp.check(R.id.monthly);
        } else {
            budTypeGrp.check(R.id.weekly);
        }

        yes.setOnClickListener(v -> {
            popupWindow.dismiss();
            if (budTypeGrp.getCheckedRadioButtonId() == R.id.weekly) {
                Commons.fakeLoadingScreen(requireContext(), totalSalary, totalExp,
                        Constants.BUDGET_WEEKLY, vm, addBudget, startDate.get());
                onBudgetSetTutorial(getView(), "Great we've set a budget..");
                loadNextPhase();
            } else {
                Commons.fakeLoadingScreen(requireContext(), totalSalary, totalExp,
                        Constants.BUDGET_MONTHLY, vm, addBudget, startDate.get());
                onBudgetSetTutorial(getView(), "Great we've set a budget..");
            }
            showInterstitial();
        });

        no.setOnClickListener(v -> {
            popupWindow.dismiss();
            callManualPopup();
            showInterstitial();
        });

        popupWindow.setFocusable(true);
        popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setBackgroundDrawable(null);
        popupWindow.setElevation(6);
        popupWindow.showAsDropDown(addBudget);

        onBudgetSetTutorial(yes, "Select a budget refresh type and tap continue or set budget manually..");

    }
    private void initAd() {
        MobileAds.initialize(requireContext());
        String TestAdId = "ca-app-pub-8620335196955785/9643633645";
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(
                requireContext(),
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
                    }
                });
    }

    private void showInterstitial() {
        if (interstitial != null) {
            interstitial.show(requireActivity());
        }
    }

    private void loadNextPhase() {
        util.setPhaseStatus(2);
        util.isPhaseStatus().observe(requireActivity(), integer -> {
            if (integer == 2) {
                MainActivity.initTutorialPhase5();
                util.isPhaseStatus().removeObservers(requireActivity());
            }
        });
    }

    private void callManualPopup() {
        PopupWindow popupWindow = new PopupWindow(getContext());
        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.budget_popup_manual, null);
        popupWindow.setContentView(view);

        Button yes = view.findViewById(R.id.yes_btn);
        EditText Amt = view.findViewById(R.id.budget);
        TextView income = view.findViewById(R.id.income);
        RadioGroup grp = view.findViewById(R.id.budType);
        CalendarView cal = view.findViewById(R.id.nd_date);
        AtomicReference<String> startDate = new AtomicReference<>(Commons.getDate());

        cal.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            startDate.set(dayOfMonth + "/" + (month + 1) + "/" + year);
        });

        if (!prevDate.equals("0")) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                Date d = sdf.parse(prevDate);
                assert d != null;
                cal.setDate(d.getTime());
            } catch (Exception ignored) {
            }
        }

        if (prevAmt != 0) {
            Amt.setText(String.valueOf(prevAmt));
        }

        if (prevType == Constants.BUDGET_MONTHLY) {
            grp.check(R.id.monthly);
        } else {
            grp.check(R.id.weekly);
        }

        String s = Currency + totalSalary;
        income.setText(s);

        yes.setOnClickListener(v -> {
            int type;
            if (grp.getCheckedRadioButtonId() == R.id.monthly) {
                type = Constants.BUDGET_MONTHLY;
            } else if (grp.getCheckedRadioButtonId() == R.id.weekly) {
                type = Constants.BUDGET_WEEKLY;
            } else {
                type = 3;
                Commons.SnackBar(yes, "Select a budget type.");
            }
            if (!Amt.getText().toString().trim().isEmpty() &&
                    (Integer.parseInt(Amt.getText().toString().trim()) <
                            Integer.parseInt(String.valueOf(totalSalary)))) {
                if (type != 3) {
                    budgetEntity budget = new budgetEntity();
                    budget.setAmount(Integer.parseInt(Amt.getText().toString()));
                    budget.setBal(Integer.parseInt(Amt.getText().toString()) - totalExp);
                    budget.setRefreshPeriod(type);
                    budget.setCreationDate(startDate.get());
                    vm.DeleteBudget();
                    vm.InsertBudget(budget);
                    popupWindow.dismiss();
                    onBudgetSetTutorial(yes, "Great we've set a budget..");
                    loadNextPhase();
                }
            } else if (!Amt.getText().toString().trim().isEmpty() && (Integer.parseInt(Amt.getText().toString().trim()) > Integer.parseInt(String.valueOf(totalSalary)))) {
                Commons.SnackBar(yes, "Budget Amount should be less than total earnings.");
            } else {
                Commons.SnackBar(yes, "Set a budget.");
            }
        });

        popupWindow.setFocusable(true);
        popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setBackgroundDrawable(null);
        popupWindow.setElevation(6);
        popupWindow.showAsDropDown(addBudget);
    }

    private void onBudgetSetTutorial(View v, String s) {
        AppUtilViewModel appAM = new ViewModelProvider(requireActivity()).get(AppUtilViewModel.class);
        appAM.getCheckerData().observe(requireActivity(), launchChecker -> {
            try {
                if (launchChecker.getTimesLaunched() == 0) {
                    Commons.SnackBar(v, s);
                }
            } catch (Exception ignored) {
                appAM.InsertLaunchChecker(new LaunchChecker(0));
            }
        });
    }

    private void InitTutorialPhase4() {
        ArrayList<View> Targets = new ArrayList<>();
        ArrayList<String> PrimaryTexts = new ArrayList<>();
        ArrayList<String> SecondaryTexts = new ArrayList<>();
//      Budget
        Targets.add(addBudget);
        PrimaryTexts.add("Budgeting");
        SecondaryTexts.add("1. Fury automates your budget with some special sets of calculations considering your earings.Manual budgeting is also available for those who wants to create their own budget\n\n2. Ideal daily limit shows the maximum amount you can spend in a day as per your budget and current average shows your current spending rate\n\n3. Weekly or monthly budget can be created as per user and they repeat after previous budget period ends\n\nUSE THIS PEN TO CREATE A SAMPLE");
        //write about ideal and current
//      ----
        util.TutorialPhase4(Targets, PrimaryTexts, SecondaryTexts);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isView) {
            try {
                InitTutorialPhase4();
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }
}