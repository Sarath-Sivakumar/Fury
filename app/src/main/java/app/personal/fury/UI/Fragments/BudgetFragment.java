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
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

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
import app.personal.fury.ViewPagerAdapter.infoGraphicsAdapter;

public class BudgetFragment extends Fragment {

    private FloatingActionButton addBudget;
    private TextView BudgetAmt, Balance, Expense, DailyLimitAllowed, CurrentDailyLimit;
    private mainViewModel vm;
    private int totalSalary = 0, totalExp = 0;
    private budgetAdapter adapter;
    private boolean isView = false;
    private final int[] FragmentList = new int[]{R.drawable.info_1, R.drawable.info_2, R.drawable.info_3};
    private AdView ad;
    private int prevType = 3;
    private TutorialUtil util;

    public BudgetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        adapter = new budgetAdapter();
        util = new TutorialUtil(requireActivity(),requireContext(), requireActivity(),requireActivity());
        MobileAds.initialize(requireContext());
    }

    private void init() {
        vm = new ViewModelProvider(requireActivity()).get(mainViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.main_fragment_budget, container, false);
        findView(v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initItems();
        isView = true;
    }

    private void requestAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        ad.loadAd(adRequest);
    }

    private void findView(View v) {
        addBudget = v.findViewById(R.id.setBud);
        BudgetAmt = v.findViewById(R.id.bgtAmt);
        Balance = v.findViewById(R.id.B_Balance);
        Expense = v.findViewById(R.id.T_exp);
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

        ad = v.findViewById(R.id.adView2);
        addBudget.setOnClickListener(v1 -> callAddBudgetPopup());
        requestAd();
    }

    private void initItems() {
        vm.getExp().observe(getViewLifecycleOwner(), expEntities -> {
            int total = 0;
            adapter.clear();
            if (expEntities != null && !expEntities.isEmpty()) {
                adapter.setExp(expEntities);
                for (int i = 0; i < expEntities.size(); i++) {
                    total = total + expEntities.get(i).getExpenseAmt();
                }
                try {
                    CurrentDailyLimit.setText(Commons.getAvg(expEntities, true));
                    String s1 = Constants.RUPEE + total;
                    Expense.setText(s1);
                } catch (Exception ignored) {
                }
                adapter.notifyDataSetChanged();
            } else {
                try {
                    String s = "No data to process";
                    CurrentDailyLimit.setText(s);
                    CurrentDailyLimit.setTextSize(14);
                    String s1 = Constants.RUPEE + total;
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

        vm.getBudget().observe(getViewLifecycleOwner(), budgetEntities -> {
            try {
                prevType = budgetEntities.getRefreshPeriod();
                String s = Constants.RUPEE + budgetEntities.getAmount();
                BudgetAmt.setText(s);
                String s1 = Constants.RUPEE + budgetEntities.getBal();
                Balance.setText(s1);
                String s2;
                if (prevType == Constants.BUDGET_MONTHLY) {
                    s2 = Constants.RUPEE + (budgetEntities.getAmount() / Commons.getDays(Calendar.MONTH)) + " /day";
                } else {
                    s2 = Constants.RUPEE + (budgetEntities.getAmount() / 7) + " /day";
                }
                DailyLimitAllowed.setText(s2);

            } catch (Exception e) {
                try {
                    String s = Constants.RUPEE + "0";
                    BudgetAmt.setText(s);
                    String s1 = Constants.RUPEE + "0";
                    Balance.setText(s1);
                } catch (Exception ignored) {
                }
//                Commons.SnackBar(getView(), "Create a budget..");
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

        if (prevType == Constants.BUDGET_MONTHLY) {
            budTypeGrp.check(R.id.monthly);
        } else {
            budTypeGrp.check(R.id.weekly);
        }

        yes.setOnClickListener(v -> {
            popupWindow.dismiss();
            if (budTypeGrp.getCheckedRadioButtonId() == R.id.weekly) {
                Commons.fakeLoadingScreen(requireContext(), totalSalary, totalExp,
                        Constants.BUDGET_WEEKLY, vm, addBudget);
                onBudgetSetTutorial(getView(),"Great we've set a budget..");
                loadNextPhase();
            } else {
                Commons.fakeLoadingScreen(requireContext(), totalSalary, totalExp,
                        Constants.BUDGET_MONTHLY, vm, addBudget);
                onBudgetSetTutorial(getView(), "Great we've set a budget..");
                loadNextPhase();
            }
        });

        no.setOnClickListener(v -> {
            popupWindow.dismiss();
            callManualPopup();
        });

        popupWindow.setFocusable(true);
        popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setBackgroundDrawable(null);
        popupWindow.setElevation(6);
        popupWindow.showAsDropDown(addBudget);

        onBudgetSetTutorial(yes, "Select a budget refresh type and tap continue or set budget manually..");
    }

    private void loadNextPhase(){
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

        String s = Constants.RUPEE + totalSalary;
        income.setText(s);

        yes.setOnClickListener(v -> {
            int type = 3;
            if (grp.getCheckedRadioButtonId() == R.id.monthly) {
                type = Constants.BUDGET_MONTHLY;
            } else if (grp.getCheckedRadioButtonId() == R.id.weekly) {
                type = Constants.BUDGET_WEEKLY;
            } else {
                type = 3;
                Commons.SnackBar(getView(), "Select a budget type.");
            }
            if (!Amt.getText().toString().trim().isEmpty() &&
                    (Integer.parseInt(Amt.getText().toString().trim()) <
                            Integer.parseInt(String.valueOf(totalSalary)))) {
                if (type != 3) {
                    budgetEntity budget = new budgetEntity();
                    budget.setAmount(Integer.parseInt(Amt.getText().toString()));
                    budget.setBal(Integer.parseInt(Amt.getText().toString()) - totalExp);
                    budget.setRefreshPeriod(type);
                    vm.DeleteBudget();
                    vm.InsertBudget(budget);
                    popupWindow.dismiss();
                    onBudgetSetTutorial(yes, "Great we've set a budget..");
                    loadNextPhase();
                }
            } else if (!Amt.getText().toString().trim().isEmpty() && (Integer.parseInt(Amt.getText().toString().trim()) > Integer.parseInt(String.valueOf(totalSalary)))) {
                Commons.SnackBar(getView(), "Budget Amount should be less than total earnings.");
            } else {
                Commons.SnackBar(getView(), "Set a budget.");
            }
        });

        popupWindow.setFocusable(true);
        popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setBackgroundDrawable(null);
        popupWindow.setElevation(6);
        popupWindow.showAsDropDown(addBudget);
    }

    private void onBudgetSetTutorial(View v, String s){
        AppUtilViewModel appAM = new ViewModelProvider(requireActivity()).get(AppUtilViewModel.class);
        appAM.getCheckerData().observe(requireActivity(), launchChecker -> {
            try {
                if (launchChecker.getTimesLaunched()==0){
                    Commons.SnackBar(v, s);
                }
            }catch (Exception ignored){
                appAM.InsertLaunchChecker(new LaunchChecker(0));
            }
        });
    }

    private void InitTutorialPhase4(){
        ArrayList<View> Targets = new ArrayList<>();
        ArrayList<String> PrimaryTexts = new ArrayList<>();
        ArrayList<String> SecondaryTexts = new ArrayList<>();
        //Budget
        Targets.add(addBudget);
        PrimaryTexts.add("This is where you set your budget");
        SecondaryTexts.add("Tap here to create a budget, you can set it automatically or manually.");
//        ----

        util.TutorialPhase4(Targets, PrimaryTexts, SecondaryTexts);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isView){
            InitTutorialPhase4();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }
}