package app.personal.fury.UI.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.icu.util.Calendar;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
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
    private int cashAmt, cashCount, accAmt, accCount, cDAvg, s2;
    private float accBal = 0, inHandBal = 0;
    private String userName = "", Currency = "";
    private AppUtilViewModel appVM;
    private AdView ad;
    private AdRequest adRequest;
    private LinearLayout adLayout;
    private boolean loaded = false;
    private final MutableLiveData<Boolean> isVisible = new MutableLiveData<>();
    private boolean isViewed = false;

    public Exp_Tracker() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            initViewModel();
            MobileAds.initialize(requireContext());
            adRequest = new AdRequest.Builder().build();
        }
        accBal = getBalance();
        inHandBal = getInHandBalance();


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
        adLayout = v.findViewById(R.id.adLayout);
        ad = v.findViewById(R.id.adView);
        touchHelper();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        fltBtn.setOnClickListener(v1 -> callPopupWindow(Constants.itemAdd));
        String s1 = Currency + (getBalance() + getInHandBalance());
        balanceView.setText(s1);
    }

    private void initcard(View v){
        CardView card1 = v.findViewById(R.id.card1);
        CardView card2 = v.findViewById(R.id.card2);
        TextView card1txt = v.findViewById(R.id.card1txt);
        TextView card2txt = v.findViewById(R.id.card2txt);
        ImageView card1share = v.findViewById(R.id.card1share);
        ImageView card2share = v.findViewById(R.id.card2share);
        Button showmore = v.findViewById(R.id.explore);
        showmore.setOnClickListener(v1 ->{
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://furyfinance.blogspot.com/")));
        });

        String c1 = "Adopting these 14 ways will save your money";
        String c2 = "What are the different types of expense report?";
        card1txt.setText(c1);
        card2txt.setText(c2);

        card1.setOnClickListener(v1 -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://furyfinance.blogspot.com/2023/04/10-ways-you-can-manage-your-expense.html")));
        });
        card2.setOnClickListener(v1 -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://furyfinance.blogspot.com/2023/04/what-are-different-types-of-expense.html")));
        });

        card1share.setOnClickListener(v12 -> {
            Intent sentIntent = new Intent();
            sentIntent.setAction(Intent.ACTION_SEND);
            sentIntent.putExtra(Intent.EXTRA_TEXT, c1 + "https://furyfinance.blogspot.com/2023/04/what-is-credit-score-and-why-is-it.html");
            sentIntent.setType("text/plain");
            requireContext().startActivity(sentIntent);
        });
        card2share.setOnClickListener(v12 -> {
            Intent sentIntent = new Intent();
            sentIntent.setAction(Intent.ACTION_SEND);
            sentIntent.putExtra(Intent.EXTRA_TEXT, c2 + "https://furyfinance.blogspot.com/2023/04/blog-post.html");
            sentIntent.setType("text/plain");
            requireContext().startActivity(sentIntent);
        });

    }


    private void initViewModel() {
        LoggedInUserViewModel userVm = new ViewModelProvider(requireActivity()).get(LoggedInUserViewModel.class);
        userVm.getUserData().observe(requireActivity(), userEntity -> {
            String[] c = userEntity.getName().split(" ");
            userName = String.valueOf(c[0]);
        });
        vm = new ViewModelProvider(requireActivity()).get(mainViewModel.class);
        vm.getRupee().observe(requireActivity(), String -> {
            if (String != null || !String.getCurrency().equals("")) {
                Currency = String.getCurrency();
            }
        });
        appVM = new ViewModelProvider(requireActivity()).get(AppUtilViewModel.class);
    }

    private void getExp() {
        vm.getExp().observe(requireActivity(), e -> {
            adapter.clear();
            try {
                adapter.setExp(e, true, Currency);
                expView.setText(adapter.getTotalExpStr());
                cashAmt = 0;
                cashCount = 0;
                accAmt = 0;
                accCount = 0;
                if (e != null) {
                    if (!e.isEmpty()) {
                        String s = Commons.getAvg(e, true, Currency);
                        if (s.equals("Processing")) {
                            dLimit.setTextSize(14);
                        }
                        dLimit.setText(s);
                        cDAvg = Integer.parseInt(Commons.getAvg(e, false, Currency));
                        Log.e("Daily avg", "Value: " + Commons.getAvg(e, false, Currency));
                    } else {
                        String s = "No data";
                        dLimit.setTextSize(14);
                        dLimit.setText(s);
                        cDAvg = 0;
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
                        String s1 = Currency + cashAmt, s2 = Currency + accAmt;
                        inHandExp.setText(s1);
                        accountExp.setText(s2);
                        inHandCount.setText(String.valueOf(cashCount));
                        accountCount.setText(String.valueOf(accCount));
                    } catch (Exception ignored) {
                    }
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
    }

    private void requestAd() {
        try {
            ad.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    adLayout.setVisibility(View.GONE);
                    new CountDownTimer(5000, 1000) {

                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            isVisible.observe(requireActivity(), Boolean -> {
                                if (Boolean) {
                                    loadAd();
                                }
                            });
                        }
                    };
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    adLayout.setVisibility(View.VISIBLE);
                }
            });
        } catch (Exception ignored) {
        }
    }

    private void loadAd() {
        if (!loaded) {
            ad.loadAd(adRequest);
            adLayout.setVisibility(View.GONE);
            loaded = true;
        }
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
            if (entity.get().getBal() > 0) {
                if (entity.get().getRefreshPeriod() == Constants.BUDGET_MONTHLY) {
                    s2 = entity.get().getAmount() / Commons.getDays(Calendar.MONTH);
                } else {
                    s2 = entity.get().getAmount() / 7;
                }
                if (cDAvg > s2 && !isViewed) {
                    showWarningPopup();
                    isViewed = true;
                }
            }
        } catch (Exception e) {
            Log.e("Popup_debug", "Error: " + e.getMessage());
        }
        return entity.get();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isVisible.postValue(isVisibleToUser);
        if (isVisibleToUser) {
            try {
                ExpTutorial();
            } catch (Exception ignored) {
            }
        }
    }

    private void ExpTutorial() {
        TutorialUtil util = new TutorialUtil(requireActivity(), requireContext(), requireActivity(), requireActivity());
        ArrayList<View> Targets = new ArrayList<>();
        ArrayList<String> PrimaryTexts = new ArrayList<>(), SecondaryTexts = new ArrayList<>();
        Targets.add(fltBtn);
        PrimaryTexts.add("Expense Tracker");
        SecondaryTexts.add("1. Our expense tracker offer the most simplified and convenient way to track expenses\n\n2. Cash expense and bank expense can be registered separately as how you made the payment\n\n3. Balance get updated as soon as new logs created\n\n4. We also provide realtime tips and warnings as per your spending\n\n5. Swipe right or left on the expense item to delete them and click on each to view details\n\nTAP ON THE ICON TO ADD A SAMPLE");
        util.TutorialPhase6(Targets, PrimaryTexts, SecondaryTexts);
    }

    private float getBalance() {
        AtomicReference<balanceEntity> Balance = new AtomicReference<>(new balanceEntity(0));
        vm.getBalance().observe(requireActivity(), entity -> {
            try {
                Balance.set(entity);
                accBal = (float) entity.getBalance();
                String s = Currency + (accBal + inHandBal);
                balanceView.setText(s);
            } catch (Exception ignored) {
                accBal = Float.parseFloat("0");
            }
        });
        try {
            return (float) Balance.get().getBalance();
        } catch (Exception ignored) {
            return Float.parseFloat("0");
        }
    }

    private float getInHandBalance() {
        AtomicReference<inHandBalEntity> Balance = new AtomicReference<>(new inHandBalEntity(0));
        vm.getInHandBalance().observe(requireActivity(), entity -> {
            try {
                Balance.set(entity);
                inHandBal = (float) entity.getBalance();
                String s = Currency + (accBal + inHandBal);
                balanceView.setText(s);
            } catch (Exception ignored) {
                inHandBal = Float.parseFloat("0");
            }
        });
        try {
            return (float) Balance.get().getBalance();
        } catch (Exception ignored) {
            return Float.parseFloat("0");
        }
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
                String war = "\nHigh expense rate noticed";
                String title1;
                if (Commons.isConnectedToInternet(requireContext())) {
                    title1 = "Attention," + userName + "!" + war;
                } else {
                    title1 = "Attention!" + war;
                }
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
        String s = Currency + cDAvg + " /Day";
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
        LayoutInflater inflater = (LayoutInflater) requireContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                    float oldBal = accBal;
                    vm.DeleteBalance();
                    balanceEntity entity1 = new balanceEntity();
                    entity1.setBalance((int) (oldBal + amt));
                    vm.InsertBalance(entity1);
                } else {
                    float oldBal = inHandBal;
                    vm.DeleteInHandBalance();
                    inHandBalEntity entity1 = new inHandBalEntity();
                    entity1.setBalance((int) (oldBal + amt));
                    vm.InsertInHandBalance(entity1);
                }

                try {
                    if (Commons.isAfterDate(getBudget().getCreationDate())) {
                        budgetEntity oldBudget = getBudget();
                        budgetEntity bud = oldBudget;
                        vm.DeleteBudget();
                        bud.setBal(oldBudget.getBal() + amt);
                        vm.InsertBudget(bud);
                    }
                } catch (Exception ignored) {
                }

                vm.DeleteExp(entity);
                adapter.clear();
                expView.setText(adapter.getTotalExpStr());
                String s = Currency + (accBal + inHandBal);
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
            TextView unwanted = view.findViewById(R.id.expAmtDisp);
            unwanted.setVisibility(View.GONE);

            String s1 = Currency + inHandBal;
            cashAmt.setText(s1);
            String s2 = Currency + accBal;
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
                        Commons.SnackBar(getView(), "Cash balance seems insufficient.\ncheck transaction records again.");
                        fromCash = 1;
                    } else {
                        Commons.SnackBar(getView(), "The balance is insufficient.");
                    }
                    break;

                case R.id.account:
                    if (getBalance() >= entity.getExpenseAmt()) {
                        entity.setExpMode(Constants.SAL_MODE_ACC);
                        vm.InsertExp(entity);
                        updateVals(entity, expAmt);
                    } else if (fromCash == 0) {
                        Commons.SnackBar(getView(), "Bank balance seems insufficient.\ncheck transaction records again.");
                        fromAcc = 1;
                    } else {
                        Commons.SnackBar(getView(), "The balance is insufficient");
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

    private void addChecker() {
        appVM.getCheckerData().observe(requireActivity(), launchChecker -> {
            try {
                if (launchChecker.getTimesLaunched() == 0) {
                    MainActivity.redirectTo(4);
                    Commons.SnackBar(getView(), "Let's explore Dues and Debt..");
                }
            } catch (Exception ignored) {
            }
        });

    }

    private void updateVals(expEntity entity, EditText expAmt) {
        if (entity.getExpMode() == Constants.SAL_MODE_ACC) {
            //Balance
            float oldBal = accBal;
            vm.DeleteBalance();
            balanceEntity bal = new balanceEntity();
            float v = Float.parseFloat(expAmt.getText().toString());
            bal.setBalance((int) (oldBal - v));
            vm.InsertBalance(bal);
        } else {
            float oldBal = inHandBal;
            vm.DeleteInHandBalance();
            inHandBalEntity bal = new inHandBalEntity();
            bal.setBalance((int)(oldBal - Float.parseFloat(expAmt.getText().toString())));
            vm.InsertInHandBalance(bal);
        }

        try {
            budgetEntity oldBudget;
            if (Commons.isAfterDate(getBudget().getCreationDate())) {
                oldBudget = getBudget();
                budgetEntity bud = oldBudget;
                vm.DeleteBudget();
                bud.setBal(oldBudget.getBal() - Integer.parseInt(expAmt.getText().toString()));
                vm.InsertBudget(bud);
            }
        } catch (Exception ignored) {
        }

        expView.setText(adapter.getTotalExpStr());
        String s = Currency + (accBal + inHandBal);
        balanceView.setText(s);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.main_fragment_expensetracker, container, false);
        adapter = new expAdapter();
        init(v);
        initcard(v);
        getBalance();
        try{
            getExp();
        }catch (Exception ignored){}
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
        String s = Currency + exp.getExpenseAmt();
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
        getBudget();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try{
            getExp();
        }catch (Exception ignored){}
        accBal = getBalance();
        inHandBal = getInHandBalance();
        isVisible.observe(requireActivity(), Boolean -> {
            if (Boolean) {
                if (savedInstanceState == null) {
                    loadAd();
                    requestAd();
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        loaded = false;
    }

    @Override
    public void onStart() {
        super.onStart();
        getBalance();
        try{
            getExp();
        }catch (Exception ignored){}
    }
}