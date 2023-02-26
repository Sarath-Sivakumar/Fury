package app.personal.fury.UI.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private int finalTotalDue = 0;
    private FloatingActionButton fltBtn;

    public Dues_Debt() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new dueAdapter();
        vm = new ViewModelProvider(requireActivity()).get(mainViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.main_fragment_dues, container, false);
        find(v);
        initViewModel();
        return v;
    }

    private void find(View v){
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
            View view = inflater.inflate(R.layout.add_item_due, null);
            popupWindow.setContentView(view);

            Button add, cancel;
            TextView DueDateTitle;
            EditText name, amt;
            CalendarView c;
            AtomicBoolean date = new AtomicBoolean(false);
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
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                try {
                    Date dateBefore = sdf.parse(Commons.getDate());
                    Date dateAfter = sdf.parse(currDate[0]);
                    assert dateAfter != null;
                    assert dateBefore != null;
                    long timeDiff = Math.abs(dateAfter.getTime() - dateBefore.getTime());
                    long daysDiff = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);

                    if (daysDiff >= 1 && dateAfter.getTime() > dateBefore.getTime()) {
                        DueDateTitle.setVisibility(View.VISIBLE);
                        DueDateTitle.setText(currDate[0]);
                        c.setVisibility(View.GONE);
                        date.set(true);
                    } else {
                        Commons.SnackBar(getView(), "Set a date one day ahead of " + Commons.getDate());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            add.setOnClickListener(v -> {
                if (currDate[0] != null && !name.getText().toString().trim().isEmpty()
                        && !amt.getText().toString().trim().isEmpty()) {
                    if (date.get()) {
                        debtEntity entity = new debtEntity();
                        entity.setAmount(Integer.parseInt(amt.getText().toString()));
                        entity.setDate(Commons.getDate());
                        entity.setFinalDate(currDate[0]);
                        entity.setStatus(Constants.DEBT_NOT_PAID);
                        entity.setSource(name.getText().toString());
                        vm.InsertDebt(entity);
                        adapter.clear();
                        popupWindow.dismiss();
                    } else {
                        Commons.SnackBar(getView(), "Select a valid date");
                    }
                } else {
                    Commons.SnackBar(getView(), "Set due date");
                }
            });
            cancel.setOnClickListener(v -> popupWindow.dismiss());

        } else if (Layout == Constants.itemPaid) {
            Log.e("onSwipe", "called");
            View view = inflater.inflate(R.layout.popup_action_duepaid, null);
            popupWindow.setContentView(view);

            ImageView check = view.findViewById(R.id.check);

            ViewPropertyAnimatorCompat viewAnimator;
            viewAnimator = ViewCompat.animate(check)
                    .scaleX(2).scaleY(2)
                    .setStartDelay(500)
                    .setDuration(500);
            viewAnimator.setInterpolator(new DecelerateInterpolator()).start();
            new CountDownTimer(1500, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {}

                @Override
                public void onFinish() {
                    popupWindow.dismiss();
                }
            }.start();

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
                adapter.clear();
                adapter.setDebt(entity, true);
                finalTotalDue = 0;
                finalTotalDue = adapter.getTotalDebt();
                noDues.setText(String.valueOf(adapter.getItemCount()));
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

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.RIGHT) {
                    vm.DeleteDebt(adapter.getDebtAt(viewHolder.getAdapterPosition()));
                    Commons.SnackBar(requireView(), "Debt deleted.");
                    adapter.clear();
                } else {
                    debtEntity entity = adapter.getDebtAt(viewHolder.getAdapterPosition());

                    if (!entity.getStatus().equals(Constants.DEBT_PAID)) {
                        entity.setStatus(Constants.DEBT_PAID);
                        entity.setDate(Commons.getDate());
                        vm.DeleteDebt(adapter.getDebtAt(viewHolder.getAdapterPosition()));
                        vm.InsertDebt(entity);

//                        Commons.SnackBar(recyclerView, "Debt marked as paid.");

                        callPopupWindow(Constants.itemPaid);
                        adapter.notifyDataSetChanged();
                    } else {
                        Commons.SnackBar(requireView(), "Debt marked as paid on " + entity.getDate() + ".");
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }).attachToRecyclerView(dueList);

        adapter.setOnItemClickListener(Due -> {
//            Intent intent = new Intent(requireActivity(), allExp.class);
//            intent.putExtra(Constants.DUE_SRC, Due.getSource());
//            intent.putExtra(Constants.DUE_AMT, Due.getAmount());
//            intent.putExtra(Constants.DUE_FINAL_DATE, Due.getFinalDate());
//            intent.putExtra(Constants.DUE_STATUS, Due.getStatus());
//            intent.putExtra(Constants.DUE_PAID_DATE, Due.getDate());
//            startActivity(intent);
        });
    }
}