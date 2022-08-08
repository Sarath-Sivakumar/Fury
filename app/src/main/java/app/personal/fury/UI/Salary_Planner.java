package app.personal.fury.UI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import app.personal.MVVM.Entity.balanceEntity;
import app.personal.MVVM.Entity.expEntity;
import app.personal.MVVM.Entity.salaryEntity;
import app.personal.MVVM.Viewmodel.mainViewModel;
import app.personal.Utls.Commons;
import app.personal.Utls.Constants;
import app.personal.fury.R;
import app.personal.fury.UI.Adapters.salaryList.salaryAdapter;

public class Salary_Planner extends Fragment {
    //Daily = 1, Monthly = 0, Hourly = -1(To be implemented in a future update).
    private static int count = 0;

    private TextView salAmt;
    private RecyclerView salSplitList;
    private FloatingActionButton addSal;
    private mainViewModel vm;
    private salaryAdapter adapter;

    public Salary_Planner() {
    }

    public static Salary_Planner newInstance(String param1, String param2) {
        Salary_Planner fragment = new Salary_Planner();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Comes before onCreateView
        initAd();
//        Use if necessary
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    private void initAd() {
//        Init ad here if necessary else delete method.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_salary__planner, container, false);
        findView(v);
        initViewModel();
        return v;
    }

    private void findView(View v) {
        salAmt = v.findViewById(R.id.salAmt);
        salSplitList = v.findViewById(R.id.salList);
        addSal = v.findViewById(R.id.addSal);
        addSal.setOnClickListener(v1 -> callPopUpWindow());
        adapter = new salaryAdapter();
        salSplitList.setLayoutManager(new LinearLayoutManager(requireContext()));
        salSplitList.setHasFixedSize(true);
        salSplitList.setAdapter(adapter);
    }

    @SuppressLint("SetTextI18n")
    private void callPopUpWindow() {
        PopupWindow popupWindow = new PopupWindow(getContext());
        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.add_exp_item, null);
        popupWindow.setContentView(view);

        TextView title = view.findViewById(R.id.title);
        @SuppressLint("CutPasteId") EditText name = view.findViewById(R.id.expName);
        @SuppressLint("CutPasteId") EditText amt = view.findViewById(R.id.expAmt);
        Button yes = view.findViewById(R.id.add_yes);
        Button no = view.findViewById(R.id.add_no);
        RadioGroup grp = view.findViewById(R.id.RadioGroup);
        grp.clearCheck();

        title.setText("New Income");
        name.setHint("Income Name");
        amt.setHint("Income Amount");
        no.setOnClickListener(v -> popupWindow.dismiss());
        yes.setOnClickListener(v -> {
            if (!name.getText().toString().trim().isEmpty() && !amt.getText().toString().trim().isEmpty()) {
                adapter.clear();
                salaryEntity entity = new salaryEntity();
                entity.setIncName(name.getText().toString());
                entity.setSalary(Float.parseFloat(amt.getText().toString()));
                if (grp.getCheckedRadioButtonId() == R.id.daily) {
                    entity.setIncType(Constants.daily);
                } else if (grp.getCheckedRadioButtonId() == R.id.monthly) {
                    entity.setIncType(Constants.monthly);
                } else {
                    entity.setIncType(Constants.hourly);
                }
                vm.InsertSalary(entity);

                int size = Objects.requireNonNull(vm.getExp().getValue()).size();
                float expense = 0;
                for (int i = 0;i<size;i++){
                    expEntity exp = vm.getExp().getValue().get(i);
                    if (exp != null) {
                        expense = expense + exp.getExpenseAmt();
                    }
                }
                vm.InsertBalance(new balanceEntity(entity.getSalary()-expense));
                popupWindow.dismiss();
            }
        });

        popupWindow.setFocusable(true);
        popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setBackgroundDrawable(null);
        popupWindow.setElevation(6);
        popupWindow.showAsDropDown(addSal);
    }

    @SuppressLint("SetTextI18n")
    private void initViewModel() {
        vm = new ViewModelProvider(requireActivity()).get(mainViewModel.class);

        vm.getSalary().observe(requireActivity(), entity -> {
            if (entity != null) {
               if (!entity.isEmpty()){
                   adapter.setSal(entity);
                   salAmt.setText(Constants.RUPEE + adapter.getTotalSal());
                }else {
                   salAmt.setText(Constants.RUPEE + 0.0);
                   if (Commons.OneTimeSnackBar(getView(), "Set Salary.", count)==0) {
                       count++;
                       addSal.callOnClick();
                   }
               }
            } else {
                salAmt.setText(Constants.RUPEE + 0.0);
                if (Commons.OneTimeSnackBar(getView(), "Set Salary.", count)==0) {
                    count++;
                    addSal.callOnClick();
                }
            }
        });
    }
}