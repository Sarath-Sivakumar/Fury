package app.personal.fury.UI;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import app.personal.MVVM.Entity.expEntity;
import app.personal.MVVM.Viewmodel.mainViewModel;
import app.personal.Utls.Constants;
import app.personal.fury.R;

public class BudgetFragment extends Fragment {

    private FloatingActionButton addBudget;
    private TextView BudgetAmt, Balance, Expense;
    private mainViewModel vm;
    private List<expEntity> allExp = new ArrayList<>();
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
    }

    private void initItems() {
        vm.getBalance().observe(getViewLifecycleOwner(), balanceEntity -> {
            try{
                String s = Constants.RUPEE + balanceEntity.getBalance();
                Balance.setText(s);
            } catch (Exception e){
                Log.e("Bud_Bal", "Error: "+e.getMessage());
                String s = Constants.RUPEE + "0";
                Balance.setText(s);
            }
        });

        vm.getExp().observe(getViewLifecycleOwner(), expEntities -> {
            if (expEntities != null && !expEntities.isEmpty()) {
                allExp = expEntities;
                int total = 0;
                for (int i = 0; i < expEntities.size(); i++) {
                    total = total + expEntities.get(i).getExpenseAmt();
                }
                String s = Constants.RUPEE + total;
                Expense.setText(s);
            }
        });

        vm.getBudget().observe(getViewLifecycleOwner(), budgetEntities -> {
            try{
                String s = Constants.RUPEE + budgetEntities.getPercent();
                BudgetAmt.setText(s);
            }catch (Exception e){
                Log.e("Budget", "Error: "+e.getMessage());
                String s = Constants.RUPEE+"0";
                BudgetAmt.setText(s);
            }
        });
    }
}