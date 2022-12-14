package app.personal.fury.UI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import app.personal.MVVM.Entity.balanceEntity;
import app.personal.MVVM.Entity.expEntity;
import app.personal.MVVM.Entity.salaryEntity;
import app.personal.MVVM.Viewmodel.mainViewModel;
import app.personal.Utls.Commons;
import app.personal.Utls.Constants;
import app.personal.fury.R;
import app.personal.fury.UI.Adapters.salaryList.salaryAdapter;

public class Salary_Planner extends Fragment {
    //Daily = 1, Monthly = 0, Hourly = -1, oneTime = ?(To be implemented in a future update).

    private RecyclerView salSplitList;
    private FloatingActionButton addSal;
    private mainViewModel vm;
    private salaryAdapter adapter;
    private TextView salAmt;

    public Salary_Planner() {}

    public static Salary_Planner newInstance() {
        return new Salary_Planner();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vm = new ViewModelProvider(requireActivity()).get(mainViewModel.class);
        adapter = new salaryAdapter();
        //Comes before onCreateView
        //initialise methods that don't require activity or context
    }

    private void initAd() {
//        Init ad here if necessary else delete method.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_salary__planner, container, false);
        initAd();
        findView(v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        salAmt.setText(Constants.RUPEE + getSalary());
    }

    private void findView(View v) {
        salAmt = v.findViewById(R.id.salAmt);
        salSplitList = v.findViewById(R.id.salList);
        addSal = v.findViewById(R.id.addSal);
        addSal.setOnClickListener(v1 -> callPopUpWindow());
        salSplitList.setLayoutManager(new LinearLayoutManager(requireContext()));
        salSplitList.setHasFixedSize(true);
        salSplitList.setAdapter(adapter);
    }

    @SuppressLint("SetTextI18n")
    private void callPopUpWindow() {
        int oldSal = getSalary();
        PopupWindow popupWindow = new PopupWindow(getContext());
        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.add_exp_item, null);
        popupWindow.setContentView(view);

        TextView title = view.findViewById(R.id.title);

        @SuppressLint("CutPasteId") EditText name = view.findViewById(R.id.salSrc);
        @SuppressLint("CutPasteId") EditText amt = view.findViewById(R.id.expAmt);
        Button yes = view.findViewById(R.id.add_yes);
        Button no = view.findViewById(R.id.add_no);
        RadioGroup grp = view.findViewById(R.id.RadioGroup);

        grp.clearCheck();

        title.setText("New Income");
        name.setHint("Income Name");
        amt.setHint("Income Amount");

        FrameLayout frm = view.findViewById(R.id.expNameTitle);
        frm.setVisibility(View.GONE);

        no.setOnClickListener(v -> popupWindow.dismiss());
        yes.setOnClickListener(v -> {
            if (!name.getText().toString().trim().isEmpty() && !amt.getText().toString().trim().isEmpty()) {
                adapter.clear();
                salaryEntity entity = new salaryEntity();
                entity.setIncName(name.getText().toString());
                entity.setSalary(Integer.parseInt(amt.getText().toString()));
                if (grp.getCheckedRadioButtonId() == R.id.daily) {
                    entity.setIncType(Constants.daily);
                    vm.InsertSalary(entity);
                    popupWindow.dismiss();
                } else if (grp.getCheckedRadioButtonId() == R.id.monthly) {
                    entity.setIncType(Constants.monthly);
                    vm.InsertSalary(entity);
                    popupWindow.dismiss();
                } else if (grp.getCheckedRadioButtonId() == R.id.hourly){
                    entity.setIncType(Constants.hourly);
                    vm.InsertSalary(entity);
                    popupWindow.dismiss();
                }else if (grp.getCheckedRadioButtonId() == R.id.oneTime){
                    entity.setIncType(Constants.oneTime);
                    vm.InsertSalary(entity);
                    popupWindow.dismiss();
                }else {
                    Commons.SnackBar(view, "Select salary type");
                    return;
                }

                int size = Objects.requireNonNull(vm.getExp().getValue()).size();
                int expense = 0;
                for (int i = 0; i < size; i++) {
                    expEntity exp = vm.getExp().getValue().get(i);
                    if (exp != null && exp.getDate().equals(Commons.getDate())) {
                        expense = expense + exp.getExpenseAmt();
                    }
                }

                int Sal;
                if (getSalary()!=oldSal+Integer.parseInt(amt.getText().toString())) {
                    Sal = Integer.parseInt(amt.getText().toString())+oldSal;
                }else{
                    Sal = getSalary();
                }
                vm.DeleteBalance();
                vm.InsertBalance(new balanceEntity(Sal - expense));
            }else{
                adapter.clear();
            }
        });

        popupWindow.setFocusable(true);
        popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setBackgroundDrawable(null);
        popupWindow.setElevation(6);
        popupWindow.showAsDropDown(addSal);
    }

    private int getSalary() {
        AtomicInteger finalTotalSalary = new AtomicInteger();
        vm.getSalary().observe(requireActivity(), entity -> {
            if (entity != null) {
                adapter.setSal(entity);
                int total = 0;
                for (int i = 0; i < entity.size(); i++) {
                    total = total + entity.get(i).getSalary();
                }
                finalTotalSalary.set(total);
                salAmt.setText(Constants.RUPEE + finalTotalSalary.get());
            }
        });
        return finalTotalSalary.get();
    }
}