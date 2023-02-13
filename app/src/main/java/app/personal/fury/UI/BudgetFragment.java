package app.personal.fury.UI;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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
import app.personal.fury.UI.IG_fragment.ig;
import app.personal.fury.ViewPagerAdapter.infoGraphicsAdapter;

public class BudgetFragment extends Fragment {

    private FloatingActionButton addBudget;
    private TextView BudgetAmt, Balance, Expense;
    private mainViewModel vm;
    private List<expEntity> allExpense = new ArrayList<>();
    private int totalSalary = 0;
    private RecyclerView topExp;


    public BudgetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vm = new ViewModelProvider(requireActivity()).get(mainViewModel.class);
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
    }

    private void findView(View v) {
        addBudget = v.findViewById(R.id.setBud);
        BudgetAmt = v.findViewById(R.id.bgtAmt);
        Balance = v.findViewById(R.id.B_Balance);
        Expense = v.findViewById(R.id.T_exp);
        topExp = v.findViewById(R.id.top_exp);
        addBudget.setOnClickListener(v1 -> callAddBudgetPopup());
    }

    private void initItems() {
        vm.getExp().observe(getViewLifecycleOwner(), expEntities -> {
            if (expEntities != null && !expEntities.isEmpty()) {
                allExpense = expEntities;
                int total = 0;
                for (int i = 0; i < expEntities.size(); i++) {
                    total = total + expEntities.get(i).getExpenseAmt();
                }
                String s = Constants.RUPEE + total;
                Expense.setText(s);
            }
        });

        vm.getSalary().observe(getViewLifecycleOwner(), salaryEntities -> {
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
            } catch (Exception e) {
                Log.e("Budget", "Error: " + e.getMessage());
                String s = Constants.RUPEE + "0";
                BudgetAmt.setText(s);
                String s1 = Constants.RUPEE + "0";
                Balance.setText(s1);
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
            if (!Amt.getText().toString().trim().isEmpty()){
                budgetEntity budget = new budgetEntity();
                budget.setAmount(Integer.parseInt(Amt.getText().toString()));
                budget.setBal(Integer.parseInt(Amt.getText().toString()));
                vm.InsertBudget(budget);
                popupWindow.dismiss();
            }else{
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