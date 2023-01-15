package app.personal.fury.UI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.concurrent.atomic.AtomicInteger;

import app.personal.MVVM.Entity.balanceEntity;
import app.personal.MVVM.Entity.expEntity;
import app.personal.MVVM.Viewmodel.mainViewModel;
import app.personal.Utls.Commons;
import app.personal.Utls.Constants;
import app.personal.fury.R;
import app.personal.fury.UI.Adapters.expList.expAdapter;

public class Exp_Tracker extends Fragment {

    private FloatingActionButton fltBtn;
    private LinearProgressIndicator limiter;
    private mainViewModel vm;
    private RecyclerView recyclerView;
    private expAdapter adapter;
    private TextView balanceView, expView;
    private RecyclerView.ViewHolder ViewHolder;
    private int finalTotalSalary = 0;
    private int finalTotalExpense = 0;

    public Exp_Tracker() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModel();
    }

    private void init(View v) {
        fltBtn = v.findViewById(R.id.exp_actionBtn);
        recyclerView = v.findViewById(R.id.exp_list);
        balanceView = v.findViewById(R.id.expBalance);
        TextView dateView = v.findViewById(R.id.exp_trac_date);
        dateView.setText(Commons.getDisplayDay(Commons.getDay())+" | "+Commons.getDate());
        limiter = v.findViewById(R.id.progress);
        expView = v.findViewById(R.id.todayExp);
        limiter.setMax(Constants.LIMITER_MAX);
        touchHelper();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        fltBtn.setOnClickListener(v1 -> callPopupWindow(Constants.itemAdd));
        String s = Constants.RUPEE + getBalance();
        Log.e("Exp sal", "Sal: " + s);
        balanceView.setText(s);
    }

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

    private void initViewModel() {
        adapter = new expAdapter();
        vm = new ViewModelProvider(requireActivity()).get(mainViewModel.class);
        getSal();
        getExp();

    }

    private void getSal() {
        vm.getSalary().observe(requireActivity(), entities -> {
            finalTotalSalary = 0;
            if (entities != null) {
                for (int i = 0; i < entities.size(); i++) {
                    finalTotalSalary = finalTotalSalary + entities.get(i).getSalary();
                }
            }
        });
    }

    private void getExp() {
        vm.getExp().observe(requireActivity(), entity -> {
            finalTotalExpense = 0;
            adapter.clear();
            adapter.setExp(entity, true);
            finalTotalExpense = adapter.getTotalExpInt();
            try {
                expView.setText(adapter.getTotalExpStr());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private int getBalance() {
        AtomicInteger Balance = new AtomicInteger();
        vm.getBalance().observe(requireActivity(), entity -> {
            if (entity != null) {
                Balance.set(entity.getBalance());
                try {
                    limiter.setProgress(Commons.setProgress(finalTotalExpense, finalTotalSalary), true);
                    setColor();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            String s = Constants.RUPEE + Balance.get();
            balanceView.setText(s);
        });
        return Balance.get();
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
                int amt = entity.getExpenseAmt();
                int oldBal = getBalance();
                vm.DeleteBalance();
                balanceEntity entity1 = new balanceEntity();
                entity1.setBalance(oldBal + amt);
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
                entity.setExpenseAmt(Integer.parseInt(expAmt.getText().toString()));
                entity.setTime(Commons.getTime());
                entity.setDay(Commons.getDay());
                entity.setDate(Commons.getDate());
                vm.InsertExp(entity);

                //Balance
                int oldBal = getBalance();
                vm.DeleteBalance();
                balanceEntity bal = new balanceEntity();
                bal.setBalance(oldBal - Integer.parseInt(expAmt.getText().toString()));
                vm.InsertBalance(bal);
                expView.setText(adapter.getTotalExpStr());
//                finalBalance = Objects.requireNonNull(vm.getBalance().getValue()).getBalance();
                String s = Constants.RUPEE + getBalance();
                balanceView.setText(s);
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
                Commons.SnackBar(getView(), "Please set salary");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_exp__tracker, container, false);
        init(v);
        getBalance();
        getSal();
        getExp();
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

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                ViewHolder = viewHolder;
                callPopupWindow(Constants.itemDelete);
            }

        }).attachToRecyclerView(recyclerView);

        //            Intent intent = new Intent(requireActivity(), allExp.class);
        //            intent.putExtra(Constants.EXP_NAME, exp.getExpenseName());
        //            intent.putExtra(Constants.EXP_AMT, exp.getExpenseAmt());
        //            intent.putExtra(Constants.EXP_DAY, exp.getDay());
        //            intent.putExtra(Constants.EXP_DATE, exp.getDate());
        //            intent.putExtra(Constants.EXP_TIME, exp.getTime());
        //            startActivity(intent);
        adapter.setOnItemClickListener(this::expDetailPopup);
    }

    private void expDetailPopup(expEntity exp){
        PopupWindow popupWindow = new PopupWindow(getContext());
        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.exp_detail_layout, null);
        popupWindow.setContentView(view);

        ImageButton close = view.findViewById(R.id.close);
        close.setOnClickListener(v -> popupWindow.dismiss());
        TextView cat, amt, time, date, day;
        cat = view.findViewById(R.id.cat);
        amt = view.findViewById(R.id.amt);
        date = view.findViewById(R.id.date);
        day = view.findViewById(R.id.day);
        time = view.findViewById(R.id.time);

        cat.setText(exp.getExpenseName());
        amt.setText(Constants.RUPEE+exp.getExpenseAmt());
        date.setText(exp.getDate());
        day.setText(Commons.getDisplayDay(exp.getDay()));
        time.setText(exp.getTime());

        popupWindow.setFocusable(true);
        popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setBackgroundDrawable(null);
        popupWindow.setElevation(6);
        popupWindow.showAsDropDown(recyclerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        getBalance();
        getSal();
        getExp();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getBalance();
        getSal();
        getExp();
    }

    @Override
    public void onStart() {
        super.onStart();
        getBalance();
        getSal();
        getExp();
    }
}