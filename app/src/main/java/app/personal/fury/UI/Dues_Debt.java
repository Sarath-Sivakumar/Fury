package app.personal.fury.UI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import app.personal.MVVM.Entity.debtEntity;
import app.personal.MVVM.Viewmodel.mainViewModel;
import app.personal.Utls.Commons;
import app.personal.Utls.Constants;
import app.personal.fury.R;
import app.personal.fury.UI.Adapters.dueList.dueAdapter;

public class Dues_Debt extends Fragment {

    private TextView totalDueDisplay;
    private RecyclerView dueList;
    private mainViewModel vm;
    private dueAdapter adapter;
    private TextView noDues;
    private RecyclerView.ViewHolder ViewHolder;
    private float finalTotalDue = 0.0F;
    private FloatingActionButton fltBtn;

    public Dues_Debt() {}

    public static Dues_Debt newInstance() {
        return new Dues_Debt();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dues__debt, container, false);
        find(v);
        initViewModel();
        return v;
    }

    private void find(View v){
        adapter = new dueAdapter();
        vm = new ViewModelProvider(requireActivity()).get(mainViewModel.class);
        dueList = v.findViewById(R.id.dueList);
        fltBtn = v.findViewById(R.id.addDue);
        fltBtn.setOnClickListener(v1 -> callPopupWindow(Constants.itemAdd));
        totalDueDisplay = v.findViewById(R.id.dueTotalText);
        noDues = v.findViewById(R.id.dueTotalNo);
        touchHelper();
        dueList.setLayoutManager(new LinearLayoutManager(requireContext()));
        dueList.setHasFixedSize(true);
        dueList.setAdapter(adapter);
    }

    @SuppressLint({"UseCompatLoadingForDrawables"})
    private void callPopupWindow(int Layout) {
        PopupWindow popupWindow = new PopupWindow(getContext());
        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        if (Layout == Constants.itemAdd) {
            View view = inflater.inflate(R.layout.add_due_item, null);
            popupWindow.setContentView(view);

            Button add, cancel;
            TextView DueDateTitle;
            EditText name, amt;
            CalendarView c;
            final String[] currDate = new String[1];
            currDate[0] = null;
            add = view.findViewById(R.id.due_add);
            name = view.findViewById(R.id.D_name);
            amt = view.findViewById(R.id.D_amt);
            cancel = view.findViewById(R.id.due_c);
            DueDateTitle = view.findViewById(R.id.Due_);
            c = view.findViewById(R.id.D_date);
            c.setVisibility(View.GONE);

            DueDateTitle.setOnClickListener(v -> {
                DueDateTitle.setVisibility(View.GONE);
                c.setVisibility(View.VISIBLE);
            });

            c.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
                currDate[0] = dayOfMonth + "/" + (month + 1) + "/" + year;
                DueDateTitle.setVisibility(View.VISIBLE);
                DueDateTitle.setText(currDate[0]);
                c.setVisibility(View.GONE);
            });

            add.setOnClickListener(v -> {
                if (currDate[0] != null && !name.getText().toString().trim().isEmpty()
                        && !amt.getText().toString().trim().isEmpty()) {
                    debtEntity entity = new debtEntity();
                    entity.setAmount(Float.parseFloat(amt.getText().toString()));
                    entity.setDate("0");
                    entity.setFinalDate(currDate[0]);
                    entity.setStatus(Constants.DEBT_NOT_PAID);
                    entity.setSource(name.getText().toString());
                    vm.InsertDebt(entity);
                    popupWindow.dismiss();
                } else {
                    Commons.SnackBar(getView(), "Set due date");
                }
            });

            cancel.setOnClickListener(v -> popupWindow.dismiss());
        }//use else if delete popup exists in future..

        popupWindow.setFocusable(true);
        popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setBackgroundDrawable(null);
        popupWindow.setElevation(6);
        popupWindow.showAsDropDown(fltBtn);
    }

    private void initViewModel() {
        vm.getDebt().observe(requireActivity(), entity -> {
            if (entity != null) {
                adapter.setDebt(entity,true);
                finalTotalDue = 0F;
                finalTotalDue = adapter.getTotalDebt();
                String size;
                try {
                    if (vm.getDebt().getValue().size()<10){
                        size = "0" + vm.getDebt().getValue().size();
                    }else{
                        size = String.valueOf(vm.getDebt().getValue().size());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    size = "00";
                }
                noDues.setText(size);
            }
            String s = Constants.RUPEE + finalTotalDue;
            totalDueDisplay.setText(s);
        });
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
                vm.DeleteDebt(adapter.getDebtAt(viewHolder.getAdapterPosition()));
            }

        }).attachToRecyclerView(dueList);

        adapter.setOnItemClickListener(Due -> {
            Intent intent = new Intent(requireActivity(), exp_details.class);
            intent.putExtra(Constants.DUE_SRC, Due.getSource());
            intent.putExtra(Constants.DUE_AMT, Due.getAmount());
            intent.putExtra(Constants.DUE_FINAL_DATE, Due.getFinalDate());
            intent.putExtra(Constants.DUE_STATUS, Due.getStatus());
            intent.putExtra(Constants.DUE_PAID_DATE, Due.getDate());

            startActivity(intent);
        });
    }
}