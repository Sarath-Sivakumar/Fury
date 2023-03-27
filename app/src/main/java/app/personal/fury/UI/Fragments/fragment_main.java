package app.personal.fury.UI.Fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
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

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
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
import app.personal.MVVM.Viewmodel.LoggedInUserViewModel;
import app.personal.MVVM.Viewmodel.mainViewModel;
import app.personal.Utls.Commons;
import app.personal.Utls.Constants;
import app.personal.fury.R;
import app.personal.fury.UI.Adapters.mainLists.categoryAdapter;
import app.personal.fury.UI.Adapters.mainLists.duesAdapter;
import app.personal.fury.UI.MainActivity;
import app.personal.fury.UI.Adapters.infoGraphicAdapter.infoGraphicsAdapter;

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
    private Switch statButton;
    private PopupWindow warningPopup, updatePopup;
    private mainViewModel vm;
    private LoggedInUserViewModel userVM;
    private duesAdapter dAdapter;
    private categoryAdapter cAdapter;
    private int salary = 0, expense = 0, budgetTotalAmount = 0;
    private int progress = 0;
//    private AdView ad;
//    private AdRequest adRequest;
//    private LinearLayout adLayout;
    private RecyclerView dueList;
    private LinearLayout noDues, statView, statDisabled;
    private int filter = 0;
    private boolean isViewed = false, isVisible = false, updateIsViewed = false;
    private final ArrayList<debtEntity> debtList = new ArrayList<>();
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
    private String Currency = "";
    private final int[] FragmentList = new int[]{R.drawable.infos1, R.drawable.infos2,
            R.drawable.infos3, R.drawable.infos4, R.drawable.infos5, R.drawable.infos6};

    public fragment_main() {
    }

//    private void requestAd() {
//        ad.loadAd(adRequest);
//        ad.setAdListener(new AdListener() {
//            @Override
//            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                super.onAdFailedToLoad(loadAdError);
//                adLayout.setVisibility(View.GONE);
//                ad.loadAd(adRequest);
//            }
//
//            @Override
//            public void onAdLoaded() {
//                super.onAdLoaded();
//                adLayout.setVisibility(View.VISIBLE);
//            }
//        });
//    }

    public void findView(View v) {
        initStat(v);
        mainProgressBar = v.findViewById(R.id.indicator);
        mainProgressText = v.findViewById(R.id.mainText);
        expView = v.findViewById(R.id.expText);
        dAvg = v.findViewById(R.id.daily_avg);
        budgetView = v.findViewById(R.id.budgetText);
        mainProgressBar.setMax(Constants.LIMITER_MAX);
//        ad = v.findViewById(R.id.adView);
        dueList = v.findViewById(R.id.dueList);
//        adLayout = v.findViewById(R.id.adLayout);
        statButton = v.findViewById(R.id.stat_switch);
//        statButton.setText("Inactive");

        statView = v.findViewById(R.id.stat_layout);
        statDisabled = v.findViewById(R.id.stat_disable);

        statButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                statView.setVisibility(View.VISIBLE);
                statDisabled.setVisibility(View.GONE);
//                statButton.setText("Active");
            } else {
                statView.setVisibility(View.GONE);
                statDisabled.setVisibility(View.VISIBLE);
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
        allExp.setOnClickListener(v1 -> startActivity(new Intent(getContext(), app.personal.fury.UI.allExp.class)));
        Button allDues = v.findViewById(R.id.allDues);
        allDues.setOnClickListener(v1 -> startActivity(new Intent(getContext(), app.personal.fury.UI.allDues.class)));
        Spinner catFilter = v.findViewById(R.id.catFilter);
        RecyclerView catList = v.findViewById(R.id.catList);
        dueList.setHasFixedSize(true);
        dueList.setAdapter(dAdapter);
        noDues = v.findViewById(R.id.noDues);
        catList.setHasFixedSize(true);
        catList.setAdapter(cAdapter);
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
            String cea = Currency + cashEr[0];
            cashEarn.setText(cea);
            String cex = Currency + cashEx[0];
            cashExp.setText(cex);
            String cba = Currency + cashBa[0];
            cashBal.setText(cba);
            String bea = Currency + bankEr[0];
            bankEarn.setText(bea);
            String bex = Currency + bankEx[0];
            bankExp.setText(bex);
            String bba = Currency + bankBa[0];
            bankBal.setText(bba);

            String ter = Currency + totalEr;
            totalEarn.setText(ter);
            String tex = Currency + totalEx;
            totalExp.setText(tex);
            String tba = Currency + totalBa;
            totalBal.setText(tba);
        } catch (Exception ignored) {
        }
    }

    private void initViewModel() {
        if (!updateIsViewed) {
            try{
                update();
                updateIsViewed = true;
            }catch (Exception ignored){}
        }
        getSal();
        getExp(filter);
        getDebt();
        getBud();
        setStat();
    }

    private void callWarningPopup(debtEntity debt) {
        warningPopup = new PopupWindow(getContext());
        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View v = inflater.inflate(R.layout.popup_due_warning, null);
        warningPopup.setContentView(v);

        TextView mainBody, name, date;
        Button yes, no;

        mainBody = v.findViewById(R.id.warning);
        name = v.findViewById(R.id.NameDue);
        date = v.findViewById(R.id.DateDue);
        yes = v.findViewById(R.id.yes_btn);
        no = v.findViewById(R.id.no_btn);

        String s1 = "The below due of " + Currency + debt.getAmount() + "\nis close to it's deadline";

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
            warningPopup.dismiss();
        });
        no.setOnClickListener(v1 -> warningPopup.dismiss());

        warningPopup.setFocusable(true);
        warningPopup.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        warningPopup.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        warningPopup.setBackgroundDrawable(null);
        warningPopup.setElevation(6);
        warningPopup.showAsDropDown(mainProgressText);
    }

    private void callUpdatePopup() {
        updatePopup = new PopupWindow(getContext());
        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View v = inflater.inflate(R.layout.popup_app_update, null);
        updatePopup.setContentView(v);

        Button yes, no;
        no = v.findViewById(R.id.ignore);
        yes = v.findViewById(R.id.update);

        yes.setOnClickListener(v1 -> {
            up();
            updatePopup.dismiss();
        });

        no.setOnClickListener(v1 -> updatePopup.dismiss());

        updatePopup.setFocusable(true);
        updatePopup.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        updatePopup.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        updatePopup.setBackgroundDrawable(null);
        updatePopup.setElevation(6);
        updatePopup.showAsDropDown(mainProgressText);
    }

    private void up() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + requireContext().getPackageName())));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id" + requireContext().getPackageName())));
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vm = new ViewModelProvider(requireActivity()).get(mainViewModel.class);
        userVM = new ViewModelProvider(requireActivity()).get(LoggedInUserViewModel.class);
        userVM.update();
        cAdapter = new categoryAdapter();
        dAdapter = new duesAdapter();
        progress = 0;
        salary = 0;
        expense = 0;
        vm.getRupee().observe(requireActivity(), String->{
            if (!String.equals("null")){
                Currency = String;
            }
        });
        initViewModel();
//        MobileAds.initialize(requireContext());
//        adRequest = new AdRequest.Builder().build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.main_fragment_home, container, false);
        findView(v);
        initViewModel();
        try {
            setMain(progress);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (savedInstanceState==null){
//            requestAd();
            statButton.setChecked(true);
        }
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewModel();
        if (budgetTotalAmount > 0) {
            progress = Commons.setProgress(expense, budgetTotalAmount);
            setMain(progress);
        } else {
            progress = Commons.setProgress(expense, salary);
            setMain(progress);
        }
    }


    private void getBud() {
        vm.getBudget().observe(requireActivity(), budgetEntities -> {
            try {
                String s = Currency + budgetEntities.getAmount();
                budgetView.setText(s);
                budgetTotalAmount = budgetEntities.getAmount();
            } catch (Exception e) {
                budgetTotalAmount = 0;
                try {
                    String s = "Set a budget.";
                    budgetView.setText(s);
                    budgetView.setTextSize(14);
                    budgetView.setElegantTextHeight(true);
                } catch (Exception ignored) {
                }
            }
            if (!isViewed && isVisible) {
                try {
                    debtWaring();
                } catch (Exception ignored) {
                }
                isViewed = true;
            }
            try {
                if (budgetTotalAmount > 0) {
                    progress = Commons.setProgress(expense, budgetTotalAmount);
                    setMain(progress);
                } else {
                    progress = Commons.setProgress(expense, salary);
                    setMain(progress);
                }
            } catch (Exception ignored) {
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
            setStat();
            cAdapter.clear();
            cAdapter.setExpes(expEntities, salary, filter);
            for (int i = 0; i < expEntities.size(); i++) {
                expense = expense + expEntities.get(i).getExpenseAmt();
            }
            try {
                String p = Currency + expense;
                expView.setText(p);
                if (Commons.getAvg(expEntities, true, Currency).equals(Constants.dAvgNoData)) {
                    dAvg.setTextSize(12);
                }
                if (expEntities != null && !expEntities.isEmpty()) {
                    dAvg.setText(Commons.getAvg(expEntities, true, Currency));
                } else {
                    String s = "No data to display";
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
                        }
                    }
                }
                if (isVisible) {
                    if (!debtList.isEmpty() && debtList.size() > 1) {
                        ArrayList<debtEntity> newDebtList = Commons.debtSorterProMax(debtList);
                        debtList.clear();
                        try {
                            callWarningPopup(newDebtList.get(newDebtList.size() - 1));
                        } catch (Exception ignored) {
                        }
                        newDebtList.clear();
                    } else if (debtList.size() == 1) {
                        try {
                            callWarningPopup(debtList.get(0));
                        } catch (Exception ignored) {
                        }
                    }
                }
            }
        });
    }

    private void update() {
        userVM.updateListener().observe(requireActivity(), integer -> {
            if (integer == Constants.UpdateAvailable) {
                callUpdatePopup();
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
                dAdapter.clear();
                if (debtEntities != null) {
                    dAdapter.setDues(debtEntities, Currency);
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
        if (progress >= 0 && progress <= 100) {
            prg = progress + "%";
        } else {
            prg = "NaN";
        }
        setStat();
        mainProgressText.setText(prg);
        String p = Currency + expense;
        expView.setText(p);
    }

    @Override
    public void onResume() {
        super.onResume();
        progress = 0;
        salary = 0;
        expense = 0;
        initViewModel();
        setStat();
        try {
            setMain(progress);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isVisible = isVisibleToUser;
        Log.e("isVisible", "setUserVisibleHint: " + isVisibleToUser);
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            warningPopup.dismiss();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            warningPopup.dismiss();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        progress = 0;
        salary = 0;
        expense = 0;
        initViewModel();
        try {
            setMain(progress);
        } catch (Exception ignored) {
        }
    }
}