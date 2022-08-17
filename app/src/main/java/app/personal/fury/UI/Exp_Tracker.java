package app.personal.fury.UI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.List;

import app.personal.MVVM.Entity.balanceEntity;
import app.personal.MVVM.Entity.expEntity;
import app.personal.MVVM.Entity.salaryEntity;
import app.personal.MVVM.Viewmodel.mainViewModel;
import app.personal.Utls.Commons;
import app.personal.Utls.Constants;
import app.personal.fury.R;
import app.personal.fury.UI.Adapters.expList.expAdapter;

public class Exp_Tracker extends Fragment {

    private int count = 0;

    private FloatingActionButton fltBtn;
    private LinearProgressIndicator limiter;
    private mainViewModel vm;
    private RecyclerView recyclerView;
    private expAdapter adapter;
    private float finalBalance;
    private EditText expenseName, expenseAmt;
    private TextView balanceView;
    private TextView expView;
    private RecyclerView.ViewHolder ViewHolder;
    private float finalTotalSalary = 0;
    private float finalTotalExpense = 0;

    public Exp_Tracker() {
    }

    public static Exp_Tracker newInstance() {
        return new Exp_Tracker();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new expAdapter();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void init(View v) {
        vm = new ViewModelProvider(requireActivity()).get(mainViewModel.class);

        fltBtn = v.findViewById(R.id.exp_actionBtn);
        recyclerView = v.findViewById(R.id.exp_list);
        balanceView = v.findViewById(R.id.expBalance);
        TextView dateView = v.findViewById(R.id.exp_trac_date);
        dateView.setText(Commons.getDate());
        limiter = v.findViewById(R.id.progress);
        expView = v.findViewById(R.id.todayExp);
        limiter.setMax(Constants.LIMITER_MAX);
        touchHelper();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        fltBtn.setOnClickListener(v1 -> callPopupWindow(Constants.itemAdd));
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initViewModel() {
        vm.getSalary().observe(requireActivity(), entities -> {
            try {
                finalTotalSalary = 0;
                int salSize = entities.size();
                List<salaryEntity> salList = new ArrayList<>(entities);
                if (salSize>=0){
                    for (int i = 0; i < salSize; i++) {
                        finalTotalSalary = finalTotalSalary + salList.get(i).getSalary();
                    }
                }
                finalBalance = Math.subtractExact((long)finalTotalSalary, (long)finalTotalExpense);
                Log.e("sal", Constants.RUPEE+finalBalance);
            } catch (Exception e) {
                e.printStackTrace();
                finalTotalSalary = 0;
                finalBalance = 0;
                finalTotalExpense = 0;
                vm.InsertBalance(new balanceEntity(finalBalance));
            }
        });

        vm.getExp().observe(requireActivity(), entity -> {
            if (entity != null) {
                adapter.setExp(entity, true);
                finalTotalExpense = adapter.getTotalExp();
                int prg = Commons.setProgress("ExpTracker",
                        finalTotalExpense, finalTotalSalary);
                if (prg>0&&prg<33.33){
                    limiter.setIndicatorColor(Color.GREEN);
                }else if(prg>33.33&&prg<66.66){
                    limiter.setIndicatorColor(Color.YELLOW);
                }else{
                    limiter.setIndicatorColor(Color.RED);
                }
                limiter.setProgress(prg, true);
                String s = Constants.RUPEE + finalTotalExpense;
                expView.setText(s);
            } else {
                int prg = Commons.setProgress("ExpTracker",
                        finalTotalExpense, finalTotalSalary);
                if (prg>0&&prg<33.33){
                    limiter.setIndicatorColor(Color.GREEN);
                }else if(prg>33.33&&prg<66.66){
                    limiter.setIndicatorColor(Color.YELLOW);
                }else{
                    limiter.setIndicatorColor(Color.RED);
                }
                limiter.setProgress(prg, true);
                String s = Constants.RUPEE + finalTotalExpense;
                expView.setText(s);
            }
        });

        vm.getBalance().observe(requireActivity(), entity -> {
            try {
                finalBalance = entity.getBalance();
                limiter.setProgress(Commons.setProgress("ExpTracker",
                        finalTotalExpense, finalTotalSalary), true);
                String s = Constants.RUPEE + finalBalance;
                balanceView.setText(s);
            } catch (Exception e) {
                e.printStackTrace();
                balanceEntity entity1 = new balanceEntity(finalBalance);
                vm.InsertBalance(entity1);
                Commons.OneTimeSnackBar(getView(), "Set Salary.", count);
                count++;
                String s = Constants.RUPEE + finalBalance;
                balanceView.setText(s);
                limiter.setProgress(Commons.setProgress("ExpTracker",
                        finalTotalExpense, finalTotalSalary), true);
            }
            String s = Constants.RUPEE + finalBalance;
            balanceView.setText(s);
        });
    }

    @SuppressLint({"UseCompatLoadingForDrawables"})
    private void callPopupWindow(int layout) {
        //Value of layout in Constants
        PopupWindow popupWindow = new PopupWindow(getContext());
        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        if (layout == Constants.itemDelete) {
            View view = inflater.inflate(R.layout.delete_exp_item, null);
            popupWindow.setContentView(view);
            Button del = view.findViewById(R.id.del_yes);
            Button cancel = view.findViewById(R.id.del_no);

            del.setOnClickListener(v -> {
                expEntity entity = adapter.getExpAt(ViewHolder.getAdapterPosition());
                float amt = entity.getExpenseAmt();
                balanceEntity entity1 = new balanceEntity();
                entity1.setBalance(finalBalance);
                vm.DeleteBalance(entity1);
                finalBalance = finalBalance + amt;
                entity1.setBalance(finalBalance);
                vm.InsertBalance(entity1);
                vm.DeleteExp(adapter.getExpAt(ViewHolder.getAdapterPosition()));
                adapter.clear();
                adapter.notifyDataSetChanged();
                popupWindow.dismiss();
                Commons.SnackBar(getView(), "Expense data deleted");
            });
            cancel.setOnClickListener(v -> {
                adapter.notifyDataSetChanged();
                popupWindow.dismiss();
            });

        } else if (layout == Constants.itemAdd) {
            View view = inflater.inflate(R.layout.add_exp_item, null);
            popupWindow.setContentView(view);
            Button cancel = view.findViewById(R.id.add_no);
            Button add = view.findViewById(R.id.add_yes);
            Spinner sp = view.findViewById(R.id.expOptions);
//            expenseName = view.findViewById(R.id.expName);
            expenseAmt = view.findViewById(R.id.expAmt);
            sp.setPrompt("Expense Name");
            sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                     parent.getSelectedItem();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            //------------------------------------------------------
            TextView v1 = view.findViewById(R.id.radioTitle);
            v1.setVisibility(View.GONE);
            RadioGroup grp = view.findViewById(R.id.RadioGroup);
            grp.setVisibility(View.GONE);
            //------------------------------------------------------

            cancel.setOnClickListener(v -> popupWindow.dismiss());
            add.setOnClickListener(v -> {
                addExp(expenseName.getText().toString(), expenseAmt.getText().toString());
                popupWindow.dismiss();
            });
        } else {
            Log.e(Constants.expFragLog, "callPopup unknown layout");
        }

        popupWindow.setFocusable(true);
        popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setBackgroundDrawable(null);
        popupWindow.setElevation(6);
        popupWindow.showAsDropDown(fltBtn);
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    private void addExp(String expName, String expAmt) {
        //VM setter here..
        if (finalBalance > 0) {
            if (expAmt != null && expName != null) {
                if (!expName.trim().isEmpty() && !expAmt.trim().isEmpty()) {
                    adapter.clear();
                    finalBalance = finalBalance - Float.parseFloat(expAmt);
                    expEntity entity = new expEntity();
                    entity.setExpenseName(expName);
                    entity.setExpenseAmt(Float.parseFloat(expAmt));
                    entity.setTime(Commons.getTime());
                    entity.setDate(Commons.getDate());
                    vm.InsertExp(entity);
                    balanceEntity bal = new balanceEntity();
                    bal.setBalance(finalBalance);
                    vm.InsertBalance(bal);
                    adapter.notifyDataSetChanged();
                    balanceView.setText(String.valueOf(vm.getBalance().getValue()));
                } else {
                    Commons.SnackBar(getView(), "Empty field(s)");
                }
            } else {
                Log.e(Constants.expFragLog, "EditText null");
            }
        } else {
            Commons.SnackBar(getView(), "No money to spend..(-_-)");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_exp__tracker, container, false);
        init(v);
        initViewModel();
        return v;
    }

    private void touchHelper() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT
                | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                ViewHolder = viewHolder;
                callPopupWindow(Constants.itemDelete);
            }

        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(exp -> {
            Intent intent = new Intent(requireActivity(), exp_details.class);
            intent.putExtra(Constants.EXP_NAME, exp.getExpenseName());
            intent.putExtra(Constants.EXP_AMT, exp.getExpenseAmt());
            intent.putExtra(Constants.EXP_DATE, exp.getDate());
            intent.putExtra(Constants.EXP_TIME, exp.getTime());
            startActivity(intent);
        });
    }
}