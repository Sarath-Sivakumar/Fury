package app.personal.fury.UI;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import app.personal.MVVM.Entity.budgetEntity;
import app.personal.MVVM.Viewmodel.mainViewModel;
import app.personal.Utls.Commons;
import app.personal.Utls.Constants;
import app.personal.fury.R;
import app.personal.fury.UI.IG_fragment.ig;
import app.personal.fury.UI.Adapters.mainLists.categoryAdapter;
import app.personal.fury.UI.Adapters.mainLists.duesAdapter;
import app.personal.fury.ViewPagerAdapter.infoGraphicsAdapter;

public class fragment_main extends Fragment {
    //Color contains 6 usable colors...

    private CircularProgressIndicator mainProgressBar;
    private TextView mainProgressText, dAvg, expView, budgetView;
    private mainViewModel vm;
    private duesAdapter dAdapter;
    private categoryAdapter cAdapter;
    private int salary = 0, expense = 0;
    private int progress = 0;
    private AdView ad;
    private RecyclerView dueList;
    private LinearLayout noDues;
    private int filter = 0;
    private ViewPager ig_vp;
    private TabLayout ig_tl;
    private ImageButton avgInfo;
    private infoGraphicsAdapter igAdapter;
    private ArrayList<Fragment> FragmentList;

    public fragment_main() {
    }

    private void requestAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        ad.loadAd(adRequest);
    }

    public void findView(View v) {
        mainProgressBar = v.findViewById(R.id.indicator);
        mainProgressText = v.findViewById(R.id.mainText);
        expView = v.findViewById(R.id.expText);
        dAvg = v.findViewById(R.id.daily_avg);
        avgInfo = v.findViewById(R.id.info);
        budgetView = v.findViewById(R.id.budgetText);
        mainProgressBar.setMax(Constants.LIMITER_MAX);
        ad = v.findViewById(R.id.adView);
        dueList = v.findViewById(R.id.dueList);
        ig_vp = v.findViewById(R.id.infoGraphics_vp);
        ig_tl = v.findViewById(R.id.infoGraphics_tab);
        igAdapter = new infoGraphicsAdapter(getParentFragmentManager());
        FragmentList = new ArrayList<>();
        budgetView.setOnClickListener(v1 -> {
            if (budgetView.getText().toString().equals("Set a budget.")) {
                MainActivity.redirectTo(1);
            }
        });
        Button allExp = v.findViewById(R.id.allExp);
        allExp.setOnClickListener(v1 -> startActivity(new Intent(getContext(), allExp.class)));
        Button allDues = v.findViewById(R.id.allDues);
        allDues.setOnClickListener(v1 -> startActivity(new Intent(getContext(), allDues.class)));
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
    }

    private void setIG_VP() {
        FragmentList.add(ig.newInstance(R.drawable.furybanner_1));
        FragmentList.add(ig.newInstance(R.drawable.budget_ig));
        FragmentList.add(ig.newInstance(R.drawable.info_graph2));
        igAdapter.setInfoGraphics(FragmentList);
        ig_vp.setAdapter(igAdapter);
        ig_tl.setupWithViewPager(ig_vp, true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobileAds.initialize(requireContext());
        cAdapter = new categoryAdapter();
        dAdapter = new duesAdapter();
        vm = new ViewModelProvider(requireActivity()).get(mainViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        findView(v);
        getExp(filter);
        initViewModel();
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setIG_VP();
        if (savedInstanceState==null){
            budgetEntity entity = getBud();
            if (entity.getAmount()>0&&expense>0){
                entity.setBal(entity.getAmount() - expense);
                Log.e("Budget main", "Exp: " + expense);
                Log.e("Budget main", "Amt: " + entity.getAmount());
                Log.e("Budget main", "Bal: " + entity.getBal());
                vm.DeleteBudget();
                vm.InsertBudget(entity);
            }
        }
    }

    private void initViewModel() {
        getSal();
        getDebt();
    }
    private budgetEntity getBud(){
        AtomicReference<budgetEntity> bud = new AtomicReference<>(new budgetEntity());
        vm.getBudget().observe(requireActivity(), budgetEntities -> {
            try {
                String s = Constants.RUPEE + budgetEntities.getAmount();
                budgetView.setText(s);
            } catch (Exception e) {
                String s = "Set a budget.";
                budgetView.setText(s);
                budgetView.setTextSize(13);
                budgetView.setElegantTextHeight(true);
                TypedValue typedValue = new TypedValue();
                Resources.Theme theme = requireActivity().getTheme();
                theme.resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true);
                budgetView.setTextColor(typedValue.data);
                Log.e("Budget", "Error: " + e.getMessage());
            }
            if (budgetEntities!=null){
                bud.set(budgetEntities);
            }
        });
        return bud.get();
    }

    private void getSal() {
        vm.getSalary().observe(requireActivity(), salaryEntity -> {
            salary = 0;
            if (!salaryEntity.isEmpty()) {
                for (int i = 0; i < salaryEntity.size(); i++) {
                    salary = salary + salaryEntity.get(i).getSalary();
                    progress = Commons.setProgress(expense, salary);
                }
            }
            try {
                setMain(progress);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void getExp(int filter) {
        vm.getExp().observe(requireActivity(), expEntities -> {
            expense = 0;
            cAdapter.clear();
            for (int i = 0; i < expEntities.size(); i++) {
                expense = expense + expEntities.get(i).getExpenseAmt();
            }
            progress = Commons.setProgress(expense, salary);
            cAdapter.setExpes(expEntities, salary, filter);
            try {
                setMain(progress);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (Commons.getAvg(expEntities, true).equals(Constants.dAvgNoData)) {
                dAvg.setTextSize(12);
            }
            dAvg.setText(Commons.getAvg(expEntities, true));
            if (dAvg.getText().equals(Constants.dAvgNoData)) {
                avgInfo.setVisibility(View.VISIBLE);
                avgInfo.setOnClickListener(v -> dAvgPopup());
                TypedValue typedValue = new TypedValue();
                Resources.Theme theme = requireActivity().getTheme();
                theme.resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true);
//                @ColorInt int colorGreen = typedValue.data;
                dAvg.setTextColor(typedValue.data);
            }

//            Call after setting budget
//            dAvg.setText(Commons.getDailyAvg(7000));
        });
    }

    private void dAvgPopup() {
        PopupWindow popupWindow = new PopupWindow(getContext());
        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.d_avg_popup, null);
        popupWindow.setContentView(view);
        popupWindow.setFocusable(true);
        //setting height in dp units-------------
        final float scale = requireContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (150 * scale + 0.5f);
        //---------------------------------------
        popupWindow.setWidth(pixels);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(null);
        popupWindow.setElevation(6);
        popupWindow.setOverlapAnchor(true);
        popupWindow.showAsDropDown(avgInfo);

    }

    private void getDebt() {
        vm.getDebt().observe(requireActivity(), debtEntities -> {
            if (debtEntities != null) {
                dAdapter.setDues(debtEntities);
                if (dAdapter.getItemCount() <= 0) {
                    dueList.setVisibility(View.GONE);
                    noDues.setVisibility(View.VISIBLE);
                } else if (dAdapter.getItemCount() <= 0 && debtEntities.isEmpty()) {
                    dueList.setVisibility(View.GONE);
                    noDues.setVisibility(View.VISIBLE);
//                    noDues;
                } else {
                    dueList.setVisibility(View.VISIBLE);
                    noDues.setVisibility(View.GONE);
                }
                dAdapter.notifyDataSetChanged();
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
            setMain(progress);
        } catch (Exception e) {
            e.printStackTrace();
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