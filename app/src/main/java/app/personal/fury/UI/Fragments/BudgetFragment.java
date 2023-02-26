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

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import app.personal.MVVM.Entity.budgetEntity;
import app.personal.MVVM.Viewmodel.mainViewModel;
import app.personal.Utls.Commons;
import app.personal.Utls.Constants;
import app.personal.fury.R;
import app.personal.fury.UI.Adapters.budgetList.budgetAdapter;
import app.personal.fury.ViewPagerAdapter.infoGraphicsAdapter;
public class BudgetFragment extends Fragment {

    private FloatingActionButton addBudget;
    private TextView BudgetAmt, Balance, Expense , DailyLimitAllowed, CurrentDailyLimit;
    private mainViewModel vm;
    private int totalSalary = 0, totalExp = 0;
    private budgetAdapter adapter;
    private final int[] FragmentList = new int[]{R.drawable.info_1, R.drawable.info_2, R.drawable.info_3};

    private AdView ad;


    public BudgetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        adapter = new budgetAdapter();
        MobileAds.initialize(requireContext());
    }

    private void init(){
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
                }catch (Exception ignored){}
                adapter.notifyDataSetChanged();
            }else{
                try {
                    String s = "No data to process";
                    CurrentDailyLimit.setText(s);
                    CurrentDailyLimit.setTextSize(14);
                    String s1 = Constants.RUPEE + total;
                    Expense.setText(s1);
                }catch (Exception ignored){}
            }
            totalExp = total;
        });

        vm.getSalary().observe(getViewLifecycleOwner(), salaryEntities -> {
            totalSalary = 0;
            if (salaryEntities!=null&&!salaryEntities.isEmpty()){
                for (int i = 0;i<salaryEntities.size();i++){
                    totalSalary = totalSalary + salaryEntities.get(i).getSalary();
                }
            }
        });

        vm.getBudget().observe(getViewLifecycleOwner(), budgetEntities -> {
            try {
                String s = Constants.RUPEE + budgetEntities.getAmount();
                BudgetAmt.setText(s);
                String s1 = Constants.RUPEE + budgetEntities.getBal();
                Balance.setText(s1);
                String s2 = Constants.RUPEE + (budgetEntities.getAmount() / Commons.getDays(Calendar.MONTH)) + " /day";
                DailyLimitAllowed.setText(s2);
            } catch (Exception e) {
                try{
                    String s = Constants.RUPEE + "0";
                    BudgetAmt.setText(s);
                    String s1 = Constants.RUPEE + "0";
                    Balance.setText(s1);
                }catch(Exception ignored){}
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
        cancel.setOnClickListener(v -> popupWindow.dismiss());

        yes.setOnClickListener(v -> {
            new CountDownTimer(2000, 1000) {
                final PopupWindow fakeScrn = new PopupWindow(getContext());
                @Override
                public void onTick(long millisUntilFinished) {
                    popupWindow.dismiss();
                    LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view = inflater.inflate(R.layout.popup_budget_fake_loading, null);
                    TextView t = view.findViewById(R.id.loadingText);
                    String s = "Analyzing Your Earnings..";
                    t.setText(s);
                    fakeScrn.setContentView(view);
                    fakeScrn.setFocusable(true);
                    fakeScrn.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
                    fakeScrn.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
                    fakeScrn.setBackgroundDrawable(null);
                    fakeScrn.setElevation(6);
                    fakeScrn.showAsDropDown(addBudget);
                }

                @Override
                public void onFinish() {
                    fakeScrn.dismiss();
                    new CountDownTimer(2000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View view = inflater.inflate(R.layout.popup_budget_fake_loading, null);
                            TextView t = view.findViewById(R.id.loadingText);
                            String s = "Crafting a ideal budget..";
                            t.setText(s);
                            fakeScrn.setContentView(view);
                            fakeScrn.setFocusable(true);
                            fakeScrn.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
                            fakeScrn.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
                            fakeScrn.setBackgroundDrawable(null);
                            fakeScrn.setElevation(6);
                            fakeScrn.showAsDropDown(addBudget);
                        }
                        @Override
                        public void onFinish() {
                            Commons.setDefaultBudget(vm, totalSalary, totalExp);
                            fakeScrn.dismiss();
                        }
                    }.start();
                }
            }.start();
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
    }

    private void callManualPopup() {
        PopupWindow popupWindow = new PopupWindow(getContext());
        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.budget_popup_manual, null);
        popupWindow.setContentView(view);

        Button yes = view.findViewById(R.id.yes_btn);
        EditText Amt = view.findViewById(R.id.budget);
        TextView income = view.findViewById(R.id.income);

        String s = Constants.RUPEE+totalSalary;
        income.setText(s);

        yes.setOnClickListener(v -> {
            if (!Amt.getText().toString().trim().isEmpty() && (Integer.parseInt(Amt.getText().toString().trim())<Integer.parseInt(String.valueOf(totalSalary)))){
                budgetEntity budget = new budgetEntity();
                budget.setAmount(Integer.parseInt(Amt.getText().toString()));
                budget.setBal(Integer.parseInt(Amt.getText().toString()));
                vm.DeleteBudget();
                vm.InsertBudget(budget);
                popupWindow.dismiss();
            }
            else if(!Amt.getText().toString().trim().isEmpty() && (Integer.parseInt(Amt.getText().toString().trim())>Integer.parseInt(String.valueOf(totalSalary)))){
                Commons.SnackBar(getView(),"Budget Amount should be less than total earnings.");
            }
            else {
                Commons.SnackBar(getView(),"Set a budget.");
            }
        });

        popupWindow.setFocusable(true);
        popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setBackgroundDrawable(null);
        popupWindow.setElevation(6);
        popupWindow.showAsDropDown(addBudget);
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }
}