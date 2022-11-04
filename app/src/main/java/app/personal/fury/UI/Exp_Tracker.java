package app.personal.fury.UI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
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

import java.util.Objects;

import app.personal.MVVM.Entity.balanceEntity;
import app.personal.MVVM.Entity.expEntity;
import app.personal.MVVM.Viewmodel.mainViewModel;
import app.personal.Utls.Commons;
import app.personal.Utls.Constants;
import app.personal.fury.R;
import app.personal.fury.UI.Adapters.expList.expAdapter;

public class Exp_Tracker extends Fragment {

    private final int count = 0;

    private FloatingActionButton fltBtn;
    private LinearProgressIndicator limiter;
    private mainViewModel vm;
    private RecyclerView recyclerView;
    private expAdapter adapter;
    private TextView balanceView, expView;
    private RecyclerView.ViewHolder ViewHolder;
    private float finalBalance;
    private float finalTotalSalary = 0F;
    private float finalTotalExpense = 0F;

    public Exp_Tracker() {
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModel();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void init(View v) {
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
    private void setColor(){
        int progress = Commons.setProgress(finalTotalExpense, finalTotalSalary);
        limiter.setProgress(progress, true);
        if (progress<34){
            limiter.setIndicatorColor(Color.GREEN);
        }else if (progress<67){
            limiter.setIndicatorColor(Color.YELLOW);
        }else{
            limiter.setIndicatorColor(Color.RED);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initViewModel() {
        adapter = new expAdapter();
        vm = new ViewModelProvider(requireActivity()).get(mainViewModel.class);
        getSal();
        getExp();
        getBalance();
    }

    private void getSal() {
        vm.getSalary().observe(requireActivity(), entities -> {
            finalTotalSalary = 0F;
            if (entities != null) {
                for (int i = 0; i < entities.size(); i++) {
                    finalTotalSalary = finalTotalSalary + entities.get(i).getSalary();
                }
            }
        });
    }

    private void getExp() {
        vm.getExp().observe(requireActivity(), entity -> {
            finalTotalExpense = 0F;
            adapter.clear();
            adapter.setExp(entity, true);
            finalTotalExpense = adapter.getTotalExpFloat();
            try {
                expView.setText(adapter.getTotalExpStr());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getBalance() {
        vm.getBalance().observe(requireActivity(), entity -> {
            finalBalance = 0F;
            if (entity != null) {
                finalBalance = entity.getBalance();
                try {
                    limiter.setProgress(Commons.setProgress(finalTotalExpense, finalTotalSalary), true);
                    setColor();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            String s = Constants.RUPEE + finalBalance;
            try {
                balanceView.setText(s);
            } catch (Exception e) {
                e.printStackTrace();
            }

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
            TextView expTitle = view.findViewById(R.id.expTitle);
            expTitle.setOnClickListener(v -> {
                sp.setVisibility(View.VISIBLE);
                sp.performClick();
            });

            EditText expenseAmt = view.findViewById(R.id.expAmt);

            final String[] expName = {null};
            sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    expName[0] = parent.getSelectedItem().toString();
                    expTitle.setVisibility(View.GONE);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    sp.setVisibility(View.GONE);
                    expTitle.setVisibility(View.VISIBLE);
                }
            });
            //------------------------------------------------------
            EditText salNm = view.findViewById(R.id.salSrc);
            salNm.setVisibility(View.GONE);
            TextView v1 = view.findViewById(R.id.radioTitle);
            v1.setVisibility(View.GONE);
            RadioGroup grp = view.findViewById(R.id.RadioGroup);
            grp.setVisibility(View.GONE);
            //------------------------------------------------------

            cancel.setOnClickListener(v -> {
                popupWindow.dismiss();
            });
            add.setOnClickListener(v -> {
                addExp(expName[0], expenseAmt);
                popupWindow.dismiss();
            });
        }

        popupWindow.setFocusable(true);
        popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setBackgroundDrawable(null);
        popupWindow.setElevation(6);
        popupWindow.showAsDropDown(fltBtn);
    }

    private void addExp(String expName, EditText expAmt) {
        if (expName != null && !expAmt.getText().toString().trim().isEmpty()) {
            try {
                //Exp
                expEntity entity = new expEntity();
                entity.setExpenseName(expName);
                entity.setExpenseAmt(Float.parseFloat(expAmt.getText().toString()));
                entity.setTime(Commons.getTime());
                entity.setDate(Commons.getDate());
                vm.InsertExp(entity);
                //Balance
                try {
                    vm.DeleteBalance(new balanceEntity(Objects.requireNonNull(vm.getBalance().getValue()).getBalance()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                balanceEntity bal = new balanceEntity();
                bal.setBalance(finalBalance - Float.parseFloat(expAmt.getText().toString()));
                vm.InsertBalance(bal);
                expView.setText(adapter.getTotalExpStr());
                finalBalance = Objects.requireNonNull(vm.getBalance().getValue()).getBalance();
                String s = Constants.RUPEE + finalBalance;
                balanceView.setText(s);
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
                Commons.SnackBar(getView(), "Please set salary");
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_exp__tracker, container, false);
        init(v);
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResume() {
        super.onResume();
        String s = Constants.RUPEE + finalBalance;
        balanceView.setText(s);
        expView.setText(adapter.getTotalExpStr());
        try {
            setColor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onStart() {
        super.onStart();
        getBalance();
        getSal();
        getExp();
    }
}