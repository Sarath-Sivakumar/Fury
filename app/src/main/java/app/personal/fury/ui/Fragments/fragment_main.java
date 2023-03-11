package app.personal.fury.ui.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import app.personal.MVVM.Entity.debtEntity;
import app.personal.MVVM.Viewmodel.mainViewModel;
import app.personal.Utls.Commons;
import app.personal.Utls.Constants;
import app.personal.fury.R;
import app.personal.fury.ui.Adapters.mainLists.categoryAdapter;
import app.personal.fury.ui.Adapters.mainLists.duesAdapter;
import app.personal.fury.ui.MainActivity;
import app.personal.fury.ViewPagerAdapter.infoGraphicsAdapter;

public class fragment_main extends Fragment {
    private CircularProgressIndicator mainProgressBar;
    private TextView mainProgressText, dAvg, expView, budgetView;
    private TextView cashEarn, cashExp, cashBal,
            bankEarn, bankExp, bankBal,
            totalEarn, totalExp, totalBal;

    private final int[] cashEr = {0};
    private final int[] cashEx = {0};
    private final int[] cashBa = {0};
    private final int[] bankEr = {0};
    private final int[] bankEx = {0};
    private final int[] bankBa = {0};
    private int totalEr = 0;
    private int totalEx = 0;
    private int totalBa = 0;

    private mainViewModel vm;
    private duesAdapter dAdapter;
    private categoryAdapter cAdapter;
    private int salary = 0, expense = 0, budgetTotalAmount = 0;
    private int progress = 0;
    private AdView ad;
    private RecyclerView dueList;
    private LinearLayout noDues,statview,statdisabled;
    private Switch statButton;
    private int filter = 0;
    private final ArrayList<debtEntity> debtList = new ArrayList<>();
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
    private final int[] FragmentList = new int[]{R.drawable.infos1, R.drawable.infos2, R.drawable.infos3, R.drawable.infos4, R.drawable.infos5, R.drawable.infos6};

    public fragment_main() {
    }

    private void requestAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        ad.loadAd(adRequest);
    }

    @SuppressLint("SetTextI18n")
    public void findView(View v) {
        initStat(v);
        mainProgressBar = v.findViewById(R.id.indicator);
        mainProgressText = v.findViewById(R.id.mainText);
        expView = v.findViewById(R.id.expText);
        dAvg = v.findViewById(R.id.daily_avg);
        budgetView = v.findViewById(R.id.budgetText);
        mainProgressBar.setMax(Constants.LIMITER_MAX);
        ad = v.findViewById(R.id.adView);
        dueList = v.findViewById(R.id.dueList);
        statButton = v.findViewById(R.id.stat_switch);
//        statButton.setText("Inactive");

        statview = v.findViewById(R.id.stat_layout);
        statdisabled = v.findViewById(R.id.stat_disable);

        statButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                statview.setVisibility(View.VISIBLE);
                statdisabled.setVisibility(View.GONE);
//                statButton.setText("Active");
            }
            else {
                statview.setVisibility(View.GONE);
                statdisabled.setVisibility(View.VISIBLE);
//                statButton.setText("Inactive");
            }
        });


        ViewPager ig_vp = v.findViewById(R.id.infoGraphics_vp);
        TabLayout ig_tl = v.findViewById(R.id.infoGraphics_tab);

        infoGraphicsAdapter igAdapter = new infoGraphicsAdapter(requireContext(), FragmentList);
        ig_vp.setAdapter(igAdapter);
        ig_tl.setupWithViewPager(ig_vp, true);
        Commons.timedSliderInit(ig_vp, FragmentList, 5);

        Button allExp = v.findViewById(R.id.allExp);
        allExp.setOnClickListener(v1 -> startActivity(new Intent(getContext(), app.personal.fury.ui.allExp.class)));
        Button allDues = v.findViewById(R.id.allDues);
        allDues.setOnClickListener(v1 -> startActivity(new Intent(getContext(), app.personal.fury.ui.allDues.class)));
        Spinner catFilter = v.findViewById(R.id.catFilter);
        RecyclerView catList = v.findViewById(R.id.catList);
        dueList.setHasFixedSize(true);
        dueList.setAdapter(dAdapter);
        noDues = v.findViewById(R.id.noDues);
        catList.setHasFixedSize(true);
        catList.setAdapter(cAdapter);
        requestAd();
        catFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //            Total 3 pos available , 0-today, 1-month, 2-year.
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filter = position;
                getExp(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        budgetView.setOnClickListener(v1 -> {
            if (budgetView.getText().toString().equals("Set a budget.")) {
                MainActivity.redirectTo(1);
            }
        });
    }

    private void initStat(View v) {
        cashEarn = v.findViewById(R.id.cashEarnings);
        cashExp = v.findViewById(R.id.cashExpense);
        cashBal = v.findViewById(R.id.cashBalance);
        bankEarn = v.findViewById(R.id.bankEarnings);
        bankExp = v.findViewById(R.id.bankExpense);
        bankBal = v.findViewById(R.id.bankBalance);
        totalEarn = v.findViewById(R.id.totalEarnings);
        totalExp = v.findViewById(R.id.totalExpense);
        totalBal = v.findViewById(R.id.totalBalance);
    }

    private void setStat() {
        vm.getSalary().observe(requireActivity(), salaryEntityList -> {
            try {
                bankEr[0] = 0;
                cashEr[0] = 0;
                for (int i = 0; i < salaryEntityList.size(); i++) {
                    if (Objects.equals(salaryEntityList.get(i).getCreationDate(), Commons.getDate())) {
                        if (salaryEntityList.get(i).getSalMode() == Constants.SAL_MODE_ACC) {
                            bankEr[0] = bankEr[0] + salaryEntityList.get(i).getSalary();
                        } else if (salaryEntityList.get(i).getSalMode() == Constants.SAL_MODE_CASH) {
                            cashEr[0] = cashEr[0] + salaryEntityList.get(i).getSalary();
                        } else {
                            Log.e("Stat", "Earnings");
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        });
        totalEr = bankEr[0] + cashEr[0];
        vm.getExp().observe(requireActivity(), expEntities -> {
            try {
                bankEx[0] = 0;
                cashEx[0] = 0;
                for (int i = 0; i < expEntities.size(); i++) {
                    if (expEntities.get(i).getDate().equals(Commons.getDate())) {
                        if (expEntities.get(i).getExpMode() == Constants.SAL_MODE_ACC) {
                            bankEx[0] = bankEx[0] + expEntities.get(i).getExpenseAmt();
                        } else if (expEntities.get(i).getExpMode() == Constants.SAL_MODE_CASH) {
                            cashEx[0] = cashEx[0] + expEntities.get(i).getExpenseAmt();
                        } else {
                            Log.e("Stat", "Expense");
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        });
        totalEx = bankEx[0] + cashEx[0];

        bankBa[0] = bankEr[0] - bankEx[0];

        cashBa[0] = cashEr[0] - cashEx[0];

        totalBa = bankBa[0] + cashBa[0];

        try {
            String cea = Constants.RUPEE + cashEr[0];
            cashEarn.setText(cea);
            String cex = Constants.RUPEE + cashEx[0];
            cashExp.setText(cex);
            String cba = Constants.RUPEE + cashBa[0];
            cashBal.setText(cba);
            String bea = Constants.RUPEE + bankEr[0];
            bankEarn.setText(bea);
            String bex = Constants.RUPEE + bankEx[0];
            bankExp.setText(bex);
            String bba = Constants.RUPEE + bankBa[0];
            bankBal.setText(bba);

            String ter = Constants.RUPEE + totalEr;
            totalEarn.setText(ter);
            String tex = Constants.RUPEE + totalEx;
            totalExp.setText(tex);
            String tba = Constants.RUPEE + totalBa;
            totalBal.setText(tba);
        } catch (Exception ignored) {
        }
    }

    private void callWarningPopup(debtEntity debt) {
        PopupWindow popupWindow = new PopupWindow(getContext());
        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View v = inflater.inflate(R.layout.popup_due_warning, null);
        popupWindow.setContentView(v);

        TextView mainBody, name, date;
        Button yes, no;

        mainBody = v.findViewById(R.id.warning);
        name = v.findViewById(R.id.NameDue);
        date = v.findViewById(R.id.DateDue);
        yes = v.findViewById(R.id.yes_btn);
        no = v.findViewById(R.id.no_btn);

        String s1 = "Attention!\n Your due " + debt.getSource() + " of \n" + Constants.RUPEE + debt.getAmount() + " is nearing its deadline.";
        mainBody.setText(s1);
        String s2 = debt.getSource();
        name.setText(s2);
        String s4 = debt.getFinalDate();
        date.setText(s4);
        yes.setOnClickListener(v1 -> {
            if (debt.getIsRepeat() == Constants.NON_REPEATING_DUE) {
                debt.setStatus(Constants.DEBT_PAID);
                vm.UpdateDebt(debt);
            } else {
                debt.setStatus(Constants.DEBT_NOT_PAID);
                debt.setDate(Commons.getDate());
                String[] Date = debt.getFinalDate().split("/");
                String month = String.valueOf(Integer.parseInt(Date[1]) + 1);
                String[] curDate = Commons.getDate().split("/");
                String year = curDate[2];
                debt.setFinalDate(Date[0] + "/" + month + "/" + year);
                vm.UpdateDebt(debt);
            }
            popupWindow.dismiss();
        });
        no.setOnClickListener(v1 -> popupWindow.dismiss());

        popupWindow.setFocusable(true);
        popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setBackgroundDrawable(null);
        popupWindow.setElevation(6);
        popupWindow.showAsDropDown(mainProgressText);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vm = new ViewModelProvider(requireActivity()).get(mainViewModel.class);
        cAdapter = new categoryAdapter();
        dAdapter = new duesAdapter();
        progress = 0;
        salary = 0;
        expense = 0;
        cAdapter.clear();
        dAdapter.clear();
        initViewModel();
        MobileAds.initialize(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.main_fragment_home, container, false);
        findView(v);
        getExp(filter);
        try {
            setMain(progress);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewModel();
        try {
            progress = Commons.setProgress(expense, budgetTotalAmount);
            setMain(progress);
        } catch (Exception e) {
//          No budget set yet...
            progress = Commons.setProgress(expense, salary);
            setMain(progress);
        }
    }

    private void initViewModel() {
        getExp(filter);
        getSal();
        getDebt();
        getBud();
        setStat();
    }

    private void getBud() {
        vm.getBudget().observe(requireActivity(), budgetEntities -> {
            try {
                String s = Constants.RUPEE + budgetEntities.getAmount();
                budgetView.setText(s);
                budgetTotalAmount = budgetEntities.getAmount();
                try {
                    progress = Commons.setProgress(expense, budgetTotalAmount);
                    setMain(progress);
                } catch (Exception e) {
//                    No budget set yet...
                    progress = Commons.setProgress(expense, salary);
                    setMain(progress);
                }
            } catch (Exception e) {
                budgetTotalAmount = 0;
                try {
                    String s = "Set a budget.";
                    budgetView.setText(s);
                    budgetView.setTextSize(13);
                    budgetView.setElegantTextHeight(true);
                } catch (Exception ignored) {
                }
            }
            setStat();
        });
    }

    private void getSal() {
        vm.getSalary().observe(requireActivity(), salaryEntity -> {
            salary = 0;
            setStat();
            if (!salaryEntity.isEmpty()) {
                for (int i = 0; i < salaryEntity.size(); i++) {
                    salary = salary + salaryEntity.get(i).getSalary();
                }
            }
        });
    }

    private void getExp(int filter) {
        vm.getExp().observe(requireActivity(), expEntities -> {
            expense = 0;
            cAdapter.clear();
            setStat();
            for (int i = 0; i < expEntities.size(); i++) {
                expense = expense + expEntities.get(i).getExpenseAmt();
            }
            cAdapter.setExpes(expEntities, salary, filter);

            try {
                if (Commons.getAvg(expEntities, true).equals(Constants.dAvgNoData)) {
                    dAvg.setTextSize(12);
                }
                if (expEntities != null && !expEntities.isEmpty()) {
                    dAvg.setText(Commons.getAvg(expEntities, true));
                } else {
                    String s = "No data to process.";
                    dAvg.setText(s);
                }
                if (dAvg.getText().equals(Constants.dAvgNoData)) {
                    dAvg.setOnClickListener(v -> dAvgPopup());
                }
            } catch (Exception ignored) {
            }
        });
    }

    private void debtWaring() {
        vm.getDebt().observe(requireActivity(), debtEntities -> {
            debtList.clear();
//          FinalDate
            if (debtEntities != null && !debtEntities.isEmpty()) {
                for (int i = 0; i < debtEntities.size(); i++) {
                    if (debtEntities.get(i).getStatus().equals(Constants.DEBT_NOT_PAID)) {
                        try {
                            Date dateBefore = sdf.parse(Commons.getDate());
                            Date dateAfter = sdf.parse(debtEntities.get(i).getFinalDate());
                            assert dateAfter != null;
                            assert dateBefore != null;
                            long timeDiff = Math.abs(dateAfter.getTime() - dateBefore.getTime());
                            long daysDiff = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);
//                      Warning filter..
                            if (daysDiff <= Constants.SHOW_WARNING_BY_IN_APP) {
                                debtList.add(debtEntities.get(i));
                            }
                        } catch (Exception ignored) {
                            Log.e("App", "Daddy!! Noo stop!");
                        }
                    }
                }
                if (!debtList.isEmpty() && debtList.size() > 1) {
                    ArrayList<debtEntity> newDebtList = Commons.debtSorterProMax(debtList);
                    debtList.clear();
                    callWarningPopup(newDebtList.get(newDebtList.size() - 1));
                    newDebtList.clear();
                } else if (debtList.size() == 1) {
                    callWarningPopup(debtList.get(0));
                }
            }
        });
    }

    private void dAvgPopup() {
        PopupWindow popupWindow = new PopupWindow(getContext());
        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.popup_text_dailyavg, null);
        popupWindow.setContentView(view);
        popupWindow.setFocusable(true);
        //setting width in dp units-------------
        final float scale = requireContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (200 * scale + 0.5f);
        //---------------------------------------
        popupWindow.setWidth(pixels);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(null);
        popupWindow.setElevation(6);
        popupWindow.setOverlapAnchor(true);
        popupWindow.showAsDropDown(dAvg);
    }

    private void getDebt() {
        vm.getDebt().observe(requireActivity(), debtEntities -> {
            try {
                setStat();
                if (debtEntities != null) {
                    dAdapter.setDues(debtEntities);
                    if (dAdapter.getItemCount() <= 0) {
                        dueList.setVisibility(View.GONE);
                        noDues.setVisibility(View.VISIBLE);
                    } else if (dAdapter.getItemCount() <= 0 && debtEntities.isEmpty()) {
                        dueList.setVisibility(View.GONE);
                        noDues.setVisibility(View.VISIBLE);
                    } else {
                        dueList.setVisibility(View.VISIBLE);
                        noDues.setVisibility(View.GONE);
                    }
                    dAdapter.notifyDataSetChanged();
                }
            } catch (Exception ignored) {
            }
        });
    }

    private void setMain(int progress) {
        mainProgressBar.setProgress(progress, true);
        String prg;
        if (progress > 0 && progress <= 100) {
            prg = progress + "%";
        } else {
            prg = 0 + "%";
        }
        setStat();
        mainProgressText.setText(prg);
        String p = Constants.RUPEE + expense;
        expView.setText(p);
    }

    @Override
    public void onResume() {
        super.onResume();
        progress = 0;
        salary = 0;
        expense = 0;
        cAdapter.clear();
        dAdapter.clear();
        try {
            initViewModel();
            setStat();
            setMain(progress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            try {
                debtWaring();
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        progress = 0;
        salary = 0;
        expense = 0;
        cAdapter.clear();
        dAdapter.clear();
        try {
            initViewModel();
            setMain(progress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}