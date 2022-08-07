package app.personal.fury.UI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;

import app.personal.MVVM.Entity.balanceEntity;
import app.personal.MVVM.Entity.expEntity;
import app.personal.MVVM.Viewmodel.mainViewModel;
import app.personal.Utls.Commons;
import app.personal.Utls.Constants;
import app.personal.fury.R;
import app.personal.fury.UI.Adapters.expList.expAdapter;

public class Exp_Tracker extends Fragment {

    private FloatingActionButton fltBtn;
    private ProgressBar limiter;
    private mainViewModel vm;
    private RecyclerView recyclerView;
    private expAdapter adapter;
    private float finalBalance;
    private EditText expenseName, expenseAmt;
    private TextView balanceView, dateView, expView;
    private RecyclerView.ViewHolder ViewHolder;

    public Exp_Tracker() {}

    public static Exp_Tracker newInstance() {
        return new Exp_Tracker();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void init(View v) {
        fltBtn = v.findViewById(R.id.exp_actionBtn);
        recyclerView = v.findViewById(R.id.exp_list);
        balanceView = v.findViewById(R.id.expBalance);
        dateView = v.findViewById(R.id.exp_trac_date);
        limiter = v.findViewById(R.id.progress);
        expView = v.findViewById(R.id.todayExp);
        limiter.setMax(Constants.LIMITER_MAX);
        adapter = new expAdapter();
        touchHelper();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        fltBtn.setOnClickListener(v1 -> {
            callPopupWindow(Constants.itemAdd);
        });
    }

    private int setProgress(float exp, float sal) {
        return (int) ((exp / sal) * 100);
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initViewModel() {
        vm = new ViewModelProvider(requireActivity()).get(mainViewModel.class);

        vm.getBalance().observe(requireActivity(), entity -> {
            //Balance updates here..
            if (entity != null) {
                balanceEntity bal = new balanceEntity(entity.getId(), entity.getBalance());
                finalBalance = bal.getBalance();
                if (vm.getExp().getValue() != null) {
                    if (finalBalance == 0 && vm.getExp().getValue().isEmpty()) {
                        finalBalance = ((MainActivity) requireActivity()).getTotalSalary();
                        balanceEntity entity1 = new balanceEntity(finalBalance);
                        vm.InsertBalance(entity1);
                    }else{
                        finalBalance = ((MainActivity) requireActivity()).getTotalSalary() - adapter.getTotalExp();
                    }
                } else {
                    finalBalance = ((MainActivity) requireActivity()).getTotalSalary();
                    balanceEntity entity1 = new balanceEntity(finalBalance);
                    vm.InsertBalance(entity1);
                }
            } else {
                //Can redirect here to salary planner
                try {
                    finalBalance = ((MainActivity) requireActivity()).getTotalSalary();
                    balanceEntity entity1 = new balanceEntity(finalBalance);
                    vm.InsertBalance(entity1);
                } catch (Exception e) {
                    e.printStackTrace();
                    finalBalance = 0.0F;
                    ((MainActivity) requireActivity()).redirectTo(2);
                }
            }
            String b = Constants.RUPEE + finalBalance;
            balanceView.setText(b);
            limiter.setProgress(setProgress(adapter.getTotalExp(),
                    ((MainActivity) requireActivity()).getTotalSalary()), true);
        });

        vm.getExp().observe(requireActivity(), entity -> {
            //Exp updates here
            if (entity != null) {
                adapter.setExp(entity, true);
                expView.setText(Constants.RUPEE + adapter.getTotalExp());limiter.setProgress(setProgress(adapter.getTotalExp(),
                        ((MainActivity) requireActivity()).getTotalSalary()), true);
            } else {
                Commons.SnackBar(getView(),"No registered Expenses yet!");
            }
            //limiter.setProgress(setProgress(adapter.getTotalExp(), ),true);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("UseCompatLoadingForDrawables")
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
                finalBalance = finalBalance + amt;
                balanceEntity entity1 = new balanceEntity();
                entity1.setBalance(finalBalance);
                balanceView.setText(String.valueOf(finalBalance));
                vm.DeleteBalance(entity1);
                vm.DeleteExp(adapter.getExpAt(ViewHolder.getAdapterPosition()));
                adapter.clear();
                adapter.notifyDataSetChanged();
                expView.setText(Constants.RUPEE + adapter.getTotalExp());
                limiter.setProgress(setProgress(adapter.getTotalExp(),
                        ((MainActivity) requireActivity()).getTotalSalary()), true);
                popupWindow.dismiss();
                Commons.SnackBar(getView(), "Expense data deleted");
            });
            cancel.setOnClickListener(v -> popupWindow.dismiss());

        } else if (layout == Constants.itemAdd) {
            View view = inflater.inflate(R.layout.add_exp_item, null);
            popupWindow.setContentView(view);
            Button cancel = view.findViewById(R.id.add_no);
            Button add = view.findViewById(R.id.add_yes);
            expenseName = view.findViewById(R.id.expName);
            expenseAmt = view.findViewById(R.id.expAmt);
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    private void addExp(String expName, String expAmt) {
        //VM setter here..
        if (finalBalance > 0) {
            if (expAmt != null && expName != null) {
                if (!expName.trim().isEmpty() && !expAmt.trim().isEmpty()) {
                    adapter.clear();
                    expEntity entity = new expEntity();
                    entity.setExpenseName(expName);
                    entity.setExpenseAmt(Float.parseFloat(expAmt));
                    entity.setTime(getTime());
                    entity.setDate(getDate());
                    vm.InsertExp(entity);
                    balanceEntity bal = new balanceEntity();
                    bal.setBalance(finalBalance - Float.parseFloat(expAmt));
                    vm.InsertBalance(bal);
                    adapter.notifyDataSetChanged();
                    expView.setText(Constants.RUPEE + adapter.getTotalExp());
                    expenseName.setText("");
                    expenseAmt.setText("");
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

    public static String getDate() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(new Date());
    }

    private String getTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        return sdf.format(new Date());
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