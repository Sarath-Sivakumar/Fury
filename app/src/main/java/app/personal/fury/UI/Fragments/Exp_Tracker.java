package app.personal.fury.UI.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.util.TypedValue;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import app.personal.MVVM.Entity.balanceEntity;
import app.personal.MVVM.Entity.budgetEntity;
import app.personal.MVVM.Entity.expEntity;
import app.personal.MVVM.Entity.inHandBalEntity;
import app.personal.MVVM.Viewmodel.AppUtilViewModel;
import app.personal.MVVM.Viewmodel.LoggedInUserViewModel;
import app.personal.MVVM.Viewmodel.mainViewModel;
import app.personal.Utls.Commons;
import app.personal.Utls.Constants;
import app.personal.Utls.TutorialUtil;
import app.personal.fury.R;
import app.personal.fury.UI.Adapters.expList.expAdapter;
import app.personal.fury.UI.MainActivity;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class Exp_Tracker extends Fragment {

    private FloatingActionButton fltBtn;
    private mainViewModel vm;
    private RecyclerView recyclerView;
    private expAdapter adapter;
    private TextView balanceView, expView, inHandExp, accountExp, inHandCount, accountCount, dLimit;
    private RecyclerView.ViewHolder ViewHolder;
    private int accBal = 0, inHandBal = 0, cashAmt, cashCount, accAmt, accCount, cDAvg, s2;
    private String userName = "";
    private TutorialUtil util;
    private AppUtilViewModel appVM;

    public Exp_Tracker() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModel();
        accBal = getBalance();
        inHandBal = getInHandBalance();
        if (savedInstanceState==null){
            appVM = new ViewModelProvider(requireActivity()).get(AppUtilViewModel.class);
            util = new TutorialUtil(requireActivity(), requireContext(), requireActivity(), requireActivity());
        }
    }

    private void init(View v) {
        fltBtn = v.findViewById(R.id.exp_actionBtn);
        recyclerView = v.findViewById(R.id.exp_list);
        balanceView = v.findViewById(R.id.expBalance);
        inHandExp = v.findViewById(R.id.inhand_Amt);
        inHandCount = v.findViewById(R.id.inhand_count);
        accountExp = v.findViewById(R.id.account_amt);
        accountCount = v.findViewById(R.id.account_count);
        dLimit = v.findViewById(R.id.dLimit);
        expView = v.findViewById(R.id.todayExp);
        touchHelper();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        fltBtn.setOnClickListener(v1 -> callPopupWindow(Constants.itemAdd));
        String s1 = Constants.RUPEE + (getBalance() + getInHandBalance());
        balanceView.setText(s1);
    }

    private void initViewModel() {
        LoggedInUserViewModel userVm = new ViewModelProvider(requireActivity()).get(LoggedInUserViewModel.class);
        userVm.getUserData().observe(requireActivity(), userEntity -> {
            String[] c = userEntity.getName().split(" ");
            userName = String.valueOf(c[0]);
        });
        vm = new ViewModelProvider(requireActivity()).get(mainViewModel.class);
    }

    private void getExp() {
        vm.getExp().observe(requireActivity(), e -> {
            adapter.clear();
            adapter.setExp(e, true);
            try {
                expView.setText(adapter.getTotalExpStr());
                cashAmt = 0;
                cashCount = 0;
                accAmt = 0;
                accCount = 0;
                if (e != null) {
                    if (!e.isEmpty()) {
                        dLimit.setText(Commons.getAvg(e, true));
                        cDAvg = Integer.parseInt(Commons.getAvg(e, false));
                    } else {
                        String s = "No data to process";
                        dLimit.setTextSize(14);
                        dLimit.setText(s);
                    }
                    for (int i = 0; i < e.size(); i++) {
                        if (e.get(i).getExpMode() == Constants.SAL_MODE_ACC) {
                            accCount = accCount + 1;
                            accAmt = accAmt + e.get(i).getExpenseAmt();
                        } else {
                            cashCount = cashCount + 1;
                            cashAmt = cashAmt + e.get(i).getExpenseAmt();
                        }
                    }
                    try {
                        String s1 = Constants.RUPEE + cashAmt, s2 = Constants.RUPEE + accAmt;
                        inHandExp.setText(s1);
                        accountExp.setText(s2);
                        inHandCount.setText(String.valueOf(cashCount));
                        accountCount.setText(String.valueOf(accCount));
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            } catch (Exception ignored) {
            }
        });
    }

    private budgetEntity getBudget() {
        AtomicReference<budgetEntity> entity = new AtomicReference<>(new budgetEntity());
        vm.getBudget().observe(requireActivity(), budgetEntity -> {
            try {
                entity.set(budgetEntity);
            } catch (Exception e) {
                e.printStackTrace();
                budgetEntity e1 = new budgetEntity();
                e1.setBal(0);
                if (budgetEntity.getAmount() != 0) {
                    e1.setAmount(budgetEntity.getAmount());
                } else {
                    e1.setAmount(0);
                }
                entity.set(e1);
            }
        });
        try {
            if (entity.get().getBal()>0){
                if (entity.get().getRefreshPeriod() == Constants.BUDGET_MONTHLY) {
                    s2 = entity.get().getAmount() / Commons.getDays(Calendar.MONTH);
                } else {
                    s2 = entity.get().getAmount() / 7;
                }
            }
        }
        catch (Exception e){
            Log.e("Popup_debug", "Error: "+e.getMessage());
        }
        return entity.get();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            if (cDAvg > s2) {
                showWarningPopup();
            }
            ExpTutorial();
        }
    }

    private void ExpTutorial(){
        ArrayList<View> Targets = new ArrayList<>();
        ArrayList<String> PrimaryTexts = new ArrayList<>(), SecondaryTexts = new ArrayList<>();
        Targets.add(fltBtn);
        PrimaryTexts.add("Expense Tracker");
        SecondaryTexts.add("This is the area where new expenses are added, Tap here to add one.");
        util.TutorialPhase6(Targets, PrimaryTexts, SecondaryTexts);
    }

    private int getBalance() {
        AtomicReference<balanceEntity> Balance = new AtomicReference<>(new balanceEntity());
        vm.getBalance().observe(requireActivity(), entity -> {
            if (entity != null) {
                Balance.set(entity);
                accBal = entity.getBalance();
            }
            try {
                String s = Constants.RUPEE + (accBal + inHandBal);
                balanceView.setText(s);
            } catch (Exception ignored) {
            }
        });
        return Balance.get().getBalance();
    }

    private int getInHandBalance() {
        AtomicReference<inHandBalEntity> Balance = new AtomicReference<>(new inHandBalEntity());
        vm.getInHandBalance().observe(requireActivity(), entity -> {
            if (entity != null) {
                Balance.set(entity);
                inHandBal = entity.getBalance();
            }
            try {
                String s = Constants.RUPEE + (accBal + inHandBal);
                balanceView.setText(s);
            } catch (Exception ignored) {
            }
        });
        return Balance.get().getBalance();
    }

    private void showWarningPopup() {
        Log.e("Popup_debug", "Popup init");
        PopupWindow popupWindow = new PopupWindow(getContext());
        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.popup_alert_highexp, null);
        popupWindow.setContentView(view);

        View bg1, bg2;
        TextView warningTitle1, dAvg;
        bg1 = view.findViewById(R.id.bgView1);
        bg2 = view.findViewById(R.id.bgView2);
        bg1.setOnClickListener(v -> popupWindow.dismiss());
        bg2.setOnClickListener(v -> popupWindow.dismiss());
        warningTitle1 = view.findViewById(R.id.expWarning);
        if (userName != null) {
            if (!userName.trim().equals("")) {
                String title1 = userName + warningTitle1.getText().toString();
                warningTitle1.setText(title1);
            }
        } else {
            popupWindow.dismiss();
            new CountDownTimer(2000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    showWarningPopup();
                }
            };
        }
        dAvg = view.findViewById(R.id.expDAvg);
        String s = cDAvg + " /Day";
        dAvg.setText(s);

        popupWindow.setFocusable(true);
        popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setBackgroundDrawable(null);
        popupWindow.setElevation(6);
        popupWindow.showAsDropDown(recyclerView);
    }

    @SuppressLint({"UseCompatLoadingForDrawables"})
    private void callPopupWindow(int layout) {
        //Value of layout in Constants
        accBal = getBalance();
        inHandBal = getInHandBalance();
        PopupWindow popupWindow = new PopupWindow(getContext());
        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        if (layout == Constants.itemDelete) {
            View view = inflater.inflate(R.layout.popup_action_expdelete, null);
            popupWindow.setContentView(view);
            Button del = view.findViewById(R.id.yes_btn);
            Button cancel = view.findViewById(R.id.no_btn);

            del.setOnClickListener(v -> {
                expEntity entity = adapter.getExpAt(ViewHolder.getAdapterPosition());
                int amt = entity.getExpenseAmt();

                if (entity.getExpMode() == Constants.SAL_MODE_ACC) {
                    int oldBal = accBal;
                    vm.DeleteBalance();
                    balanceEntity entity1 = new balanceEntity();
                    entity1.setBalance(oldBal + amt);
                    vm.InsertBalance(entity1);
                } else {
                    int oldBal = inHandBal;
                    vm.DeleteInHandBalance();
                    inHandBalEntity entity1 = new inHandBalEntity();
                    entity1.setBalance(oldBal + amt);
                    vm.InsertInHandBalance(entity1);
                }

                budgetEntity oldBudget = getBudget();
                budgetEntity bud = oldBudget;
                vm.DeleteBudget();
                bud.setBal(oldBudget.getBal() + amt);
                vm.InsertBudget(bud);

                vm.DeleteExp(entity);
                adapter.clear();
                expView.setText(adapter.getTotalExpStr());
                String s = Constants.RUPEE + (accBal + inHandBal);
                balanceView.setText(s);
                adapter.notifyDataSetChanged();
                popupWindow.dismiss();
                Commons.SnackBar(getView(), "Expense data deleted");
            });
            cancel.setOnClickListener(v -> {
                adapter.notifyDataSetChanged();
                popupWindow.dismiss();
            });

        } else if (layout == Constants.itemAdd) {
            View view = inflater.inflate(R.layout.add_item_exp, null);
            popupWindow.setContentView(view);
            Button cancel = view.findViewById(R.id.add_no);
            Button add = view.findViewById(R.id.add_yes);
            Spinner sp = view.findViewById(R.id.expOptions);
            TextView expTitle = view.findViewById(R.id.expTitle);
            TextView expMode = view.findViewById(R.id.radioTitle2);
            TextView cashAmt = view.findViewById(R.id.cashAmt);
            TextView accAmt = view.findViewById(R.id.accAmt);

            String s1 = Constants.RUPEE + inHandBal;
            cashAmt.setText(s1);
            String s2 = Constants.RUPEE + accBal;
            accAmt.setText(s2);

            String s = "Debit mode";
            expMode.setText(s);
            RadioGroup rdGrp = view.findViewById(R.id.RadioGroup2);
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
            EditText salDate = view.findViewById(R.id.salDate);
            salDate.setVisibility(View.GONE);
            TextView v1 = view.findViewById(R.id.radioTitle);
            v1.setVisibility(View.GONE);
            RadioGroup grp = view.findViewById(R.id.RadioGroup);
            grp.setVisibility(View.GONE);
            //------------------------------------------------------

            cancel.setOnClickListener(v -> popupWindow.dismiss());
            add.setOnClickListener(v -> {
                addExp(expName[0], expenseAmt, rdGrp);
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

    private void addExp(String expName, EditText expAmt, RadioGroup rdGrp) {
        if (expName != null && !expAmt.getText().toString().trim().isEmpty()) {
            //Exp
            expEntity entity = new expEntity();
            entity.setExpenseName(expName);
            entity.setExpenseAmt(Integer.parseInt(expAmt.getText().toString()));
            entity.setTime(Commons.getTime());
            entity.setDay(Commons.getDay());
            entity.setDate(Commons.getDate());
            int fromCash = 0;
            int fromAcc = 0;


            switch (rdGrp.getCheckedRadioButtonId()) {
                case R.id.inHand:
                    if (getInHandBalance() >= entity.getExpenseAmt()) {
                        entity.setExpMode(Constants.SAL_MODE_CASH);
                        vm.InsertExp(entity);
                        updateVals(entity, expAmt);
                    } else if (fromAcc == 0) {
                        Commons.SnackBar(getView(), "Not enough money to spend as cash.\nTry bank account instead.");
                        fromCash = 1;
                    } else {
                        Commons.SnackBar(getView(), "Not enough money to spend.");
                    }
                    break;

                case R.id.account:
                    if (getBalance() >= entity.getExpenseAmt()) {
                        entity.setExpMode(Constants.SAL_MODE_ACC);
                        vm.InsertExp(entity);
                        updateVals(entity, expAmt);
                    } else if (fromCash == 0) {
                        Commons.SnackBar(getView(), "Not enough money to spend as cash.\nTry bank account instead.");
                        fromAcc = 1;
                    } else {
                        Commons.SnackBar(getView(), "Not enough money to spend.");
                    }
                    break;
                default:
                    Commons.SnackBar(getView(), "Select a Debit method.");
                    break;
            }
            adapter.notifyDataSetChanged();
            addChecker();
        } else {
            Commons.SnackBar(getView(), "Please fill all field(s)");
        }
    }

    private void addChecker(){
        appVM.getCheckerData().observe(requireActivity(), launchChecker -> {
            try{
                if (launchChecker.getTimesLaunched()==0){
                    MainActivity.redirectTo(4);
                    Commons.SnackBar(getView(),"Let's explore Dues and Debt..");
                }
            }catch (Exception ignored){}
        });

    }

    private void updateVals(expEntity entity, EditText expAmt) {
        if (entity.getExpMode() == Constants.SAL_MODE_ACC) {
            //Balance
            int oldBal = accBal;
            vm.DeleteBalance();
            balanceEntity bal = new balanceEntity();
            int v = Integer.parseInt(expAmt.getText().toString());
            bal.setBalance(oldBal - v);
            vm.InsertBalance(bal);
        } else {
            int oldBal = inHandBal;
            vm.DeleteInHandBalance();
            inHandBalEntity bal = new inHandBalEntity();
            bal.setBalance(oldBal - Integer.parseInt(expAmt.getText().toString()));
            vm.InsertInHandBalance(bal);
        }

        budgetEntity oldBudget = getBudget();
        budgetEntity bud = oldBudget;
        vm.DeleteBudget();
        bud.setBal(oldBudget.getBal() - Integer.parseInt(expAmt.getText().toString()));
        vm.InsertBudget(bud);

        expView.setText(adapter.getTotalExpStr());
        String s = Constants.RUPEE + (accBal + inHandBal);
        balanceView.setText(s);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.main_fragment_expensetracker, container, false);
        adapter = new expAdapter();
        init(v);
        getBalance();
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

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.theme_red))
                        .addActionIcon(R.drawable.common_icon_trash)
                        .addSwipeLeftLabel("Delete")
                        .setSwipeLeftLabelColor(ContextCompat.getColor(requireActivity(), R.color.full_white))
                        .setSwipeLeftLabelTextSize(TypedValue.COMPLEX_UNIT_SP, 12)
                        .addCornerRadius(TypedValue.COMPLEX_UNIT_SP, 15)
                        .addSwipeRightLabel("Delete")
                        .setSwipeRightLabelColor(ContextCompat.getColor(requireActivity(), R.color.full_white))
                        .setSwipeRightLabelTextSize(TypedValue.COMPLEX_UNIT_SP, 12)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(this::expDetailPopup);
    }

    private void expDetailPopup(expEntity exp) {
        PopupWindow popupWindow = new PopupWindow(getContext());
        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.popup_details_expense, null);
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
        String s = Constants.RUPEE + exp.getExpenseAmt();
        amt.setText(s);
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        initViewModel();
    }

    @Override
    public void onResume() {
        super.onResume();
        initViewModel();
        getBalance();
//        getExp();
        getBudget();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getExp();
        accBal = getBalance();
        inHandBal = getInHandBalance();
    }

    @Override
    public void onStart() {
        super.onStart();
        getBalance();
        getExp();
    }
}