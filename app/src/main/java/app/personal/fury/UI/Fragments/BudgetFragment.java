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

import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;

import app.personal.MVVM.Entity.budgetEntity;
import app.personal.MVVM.Entity.expEntity;
import app.personal.MVVM.Viewmodel.mainViewModel;
import app.personal.Utls.Commons;
import app.personal.Utls.Constants;
import app.personal.fury.R;
import app.personal.fury.UI.Adapters.budgetList.budgetAdapter;
import app.personal.fury.UI.IG_fragment.ig;
import app.personal.fury.ViewPagerAdapter.infoGraphicsAdapter;

public class BudgetFragment extends Fragment {

    private FloatingActionButton addBudget;
    private TextView BudgetAmt, Balance, Expense , Dailylimitallowed ,CurrentDailylimit;
    private mainViewModel vm;
    private List<expEntity> allExpense = new ArrayList<>();
    private int totalSalary = 0;
    private RecyclerView topExp;
    private budgetAdapter adapter;
    private infoGraphicsAdapter igAdapter;
    private ViewPager ig_vp;
    private TabLayout ig_tl;
    private ArrayList<Fragment> FragmentList;

    private AdView ad;


    public BudgetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vm = new ViewModelProvider(requireActivity()).get(mainViewModel.class);
        adapter = new budgetAdapter();
        MobileAds.initialize(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_budget, container, false);
        findView(v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initItems();
        setIG_VP();
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
        topExp = v.findViewById(R.id.top_exp);
        topExp.setLayoutManager(new LinearLayoutManager(requireContext()));
        topExp.setHasFixedSize(true);
        topExp.setAdapter(adapter);
        Dailylimitallowed = v.findViewById(R.id.ID_avg);
        CurrentDailylimit = v.findViewById(R.id.C_avg);
        ig_vp = v.findViewById(R.id.infoGraphics_vp2);
        ig_tl = v.findViewById(R.id.infoGraphics_tab2);
        igAdapter = new infoGraphicsAdapter(getParentFragmentManager());
        FragmentList = new ArrayList<>();
        ad = v.findViewById(R.id.adView2);
        addBudget.setOnClickListener(v1 -> callAddBudgetPopup());
        requestAd();
    }
    private void setIG_VP(){
        FragmentList.add(ig.newInstance(R.drawable.info_1));
        FragmentList.add(ig.newInstance(R.drawable.info_2));
        FragmentList.add(ig.newInstance(R.drawable.info_3));
        igAdapter.setInfoGraphics(FragmentList);
        ig_vp.setAdapter(igAdapter);
        ig_tl.setupWithViewPager(ig_vp, true);
    }

    private void initItems() {
        vm.getExp().observe(getViewLifecycleOwner(), expEntities -> {
            int total = 0;
            if (expEntities != null && !expEntities.isEmpty()) {
                adapter.clear();
                adapter.setExp(expEntities);
                for (int i = 0; i < expEntities.size(); i++) {
                    total = total + expEntities.get(i).getExpenseAmt();
                }
                adapter.notifyDataSetChanged();
            }
//            else{
//                No Expenses..
//            }
            String s = Constants.RUPEE + total;
            Expense.setText(s);
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
                Dailylimitallowed.setText(s2);
            } catch (Exception e) {
                Log.e("Budget", "Error: " + e.getMessage());
                String s = Constants.RUPEE + "0";
                BudgetAmt.setText(s);
                String s1 = Constants.RUPEE + "0";
                Balance.setText(s1);
//                Commons.SnackBar(getView(), "Create a budget..");
            }
        });

    }


    private void callAddBudgetPopup() {
        PopupWindow popupWindow = new PopupWindow(getContext());
        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.set_budget_popup, null);
        popupWindow.setContentView(view);

        Button yes = view.findViewById(R.id.continue_btn);
        Button no = view.findViewById(R.id.No_btn);

        yes.setOnClickListener(v -> {
            popupWindow.dismiss();
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
        View view = inflater.inflate(R.layout.manual_budget_popup, null);
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
}