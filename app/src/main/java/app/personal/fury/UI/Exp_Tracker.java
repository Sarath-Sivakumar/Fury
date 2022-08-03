package app.personal.fury.UI;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;
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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FloatingActionButton fltBtn;
    private ProgressBar limiter;
    private mainViewModel vm;
    private RecyclerView recyclerView;
    private expAdapter adapter;
    private float finalBalance;
    private EditText expenseName, expenseAmt;
    private TextView balanceView, dateView, expView;
    salaryEntity Entity = new salaryEntity();
    private LiveData<balanceEntity> getBalance;
    private LiveData<List<expEntity>> getExp;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Exp_Tracker() {
    }

    public static Exp_Tracker newInstance(String param1, String param2) {
        Exp_Tracker fragment = new Exp_Tracker();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private void init(View v) {
        fltBtn = v.findViewById(R.id.exp_actionBtn);
        recyclerView = v.findViewById(R.id.exp_list);
        balanceView = v.findViewById(R.id.expBalance);
        dateView = v.findViewById(R.id.exp_trac_date);
        limiter = v.findViewById(R.id.progress);
        expView = v.findViewById(R.id.todayExp);
        limiter.setMax(Constants.LIMITER_MAX);
        touchHelper();
        adapter = new expAdapter();
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

        vm.getSalary().observe(requireActivity(), entity -> {
            if (entity!=null){
                Entity = entity;
            }else{
                //Redirect to salary planner here..
                Entity.setSalary(1000);
                finalBalance = 1000;
            }
        });
        vm.getBalance().observe(requireActivity(), entity -> {
            //Balance updates here..
            if (entity != null) {
                balanceView.setText(Constants.RUPEE + entity.getBalance());
                limiter.setProgress(setProgress(adapter.getTotalExp(), Entity.getSalary()), true);
            } else {
                vm.InsertBalance(new balanceEntity(Entity.getSalary()));
                Commons.SnackBar(getView(), "Please update your current balance..");
            }
        });

        vm.getExp().observe(requireActivity(), entity -> {
            //Exp updates here
            if (entity != null) {
                dateView.setText(getDate());
                adapter.setExp(entity, true);
                expView.setText(Constants.RUPEE  + adapter.getTotalExp());
            }
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void callPopupWindow(int layout) {
        //Value of layout in Constants
        PopupWindow popupWindow = new PopupWindow(getContext());
        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        if (layout == Constants.itemDelete) {
            View view = inflater.inflate(R.layout.delete_exp_item, null);
            popupWindow.setContentView(view);
            //Code here...
            popupWindow.dismiss();
        } else if (layout == Constants.itemAdd) {
            View view = inflater.inflate(R.layout.add_exp_item, null);
            popupWindow.setContentView(view);
            Button cancel = view.findViewById(R.id.add_no);
            Button add = view.findViewById(R.id.add_yes);
            expenseName = view.findViewById(R.id.expName);
            expenseAmt = view.findViewById(R.id.expAmt);
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
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                expEntity entity = adapter.getExpAt(viewHolder.getAdapterPosition());
                float amt = entity.getExpenseAmt();
                finalBalance = finalBalance + amt;
                balanceEntity entity1 = new balanceEntity();
                entity1.setBalance(finalBalance);
                vm.DeleteBalance(entity1);
                vm.DeleteExp(adapter.getExpAt(viewHolder.getAdapterPosition()));
                adapter.clear();
                adapter.notifyDataSetChanged();
                Snackbar.make(recyclerView, "Expense data deleted", Snackbar.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

//        adapter.setOnItemClickListener(exp -> {
//            Intent intent = new Intent(requireActivity(), exp_details.class);
//            intent.putExtra(EXP_NAME, exp.getExpenseName());
//            intent.putExtra(EXP_AMT, exp.getExpenseAmt());
//            intent.putExtra(EXP_DATE, exp.getDate());
//            intent.putExtra(EXP_TIME, exp.getTime());
//            startActivity(intent);
//        });
    }
}