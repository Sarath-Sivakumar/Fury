package app.personal.fury.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import app.personal.MVVM.Viewmodel.mainViewModel;
import app.personal.Utls.Commons;
import app.personal.Utls.Constants;
import app.personal.fury.R;
import app.personal.fury.UI.Adapters.mainLists.categoryAdapter;
import app.personal.fury.UI.Adapters.mainLists.duesAdapter;

public class fragment_main extends Fragment {
    //Color contains 6 usable colors...

    private CircularProgressIndicator mainProgressBar;
    private TextView mainProgressText, dAvg, expView, budgetView;
    private mainViewModel vm;
    private duesAdapter dAdapter;
    private categoryAdapter cAdapter;
    private float salary = 0, expense = 0;
    private int progress = 0;
    private AdView ad;

    public fragment_main() {}

    private void requestAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        ad.loadAd(adRequest);
    }

    public void findView(View v) {
        mainProgressBar = v.findViewById(R.id.indicator);
        mainProgressText = v.findViewById(R.id.mainText);
        expView = v.findViewById(R.id.expText);
        dAvg = v.findViewById(R.id.daily_avg);
        budgetView = v.findViewById(R.id.budgetText);
        mainProgressBar.setMax(Constants.LIMITER_MAX);
        ad = v.findViewById(R.id.adView);
        RecyclerView dueList = v.findViewById(R.id.dueList);
        RecyclerView catList = v.findViewById(R.id.catList);
        dueList.setHasFixedSize(true);
        dueList.setAdapter(dAdapter);
        catList.setHasFixedSize(true);
        catList.setAdapter(cAdapter);
        requestAd();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobileAds.initialize(requireContext());
        cAdapter = new categoryAdapter();
        dAdapter = new duesAdapter();
        vm = new ViewModelProvider(requireActivity()).get(mainViewModel.class);
        //initViewModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        findView(v);
        initViewModel();
        budgetView.setText("Set a budget.");
        budgetView.setTextSize(12);
        return v;
    }

    private void initViewModel() {

        getSal();
//        vm.getBudget().observe(requireActivity(), budgetEntities -> {
//            if (budgetEntities!=null){
//                budgetView.setText(budgetEntities.);
//            }
//        });

        getExp();
        getDebt();
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

    private void getExp() {
        vm.getExp().observe(requireActivity(), expEntities -> {
            expense = 0;
            for (int i = 0; i < expEntities.size(); i++) {
                expense = expense + expEntities.get(i).getExpenseAmt();
                progress = Commons.setProgress(expense, salary);
            }
            cAdapter.clear();
            cAdapter.setExpes(expEntities, salary);
            try {
                setMain(progress);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (Commons.getAvg(expEntities).equals(Constants.dAvgNoData)) {
                dAvg.setTextSize(12);
            }
            dAvg.setText(Commons.getAvg(expEntities));
//            dAvg.setText(Commons.getDailyAvg(7000));
        });
    }

    private void getDebt() {
        vm.getDebt().observe(requireActivity(), debtEntities -> {
            if (debtEntities != null) {
                dAdapter.setDues(debtEntities);
                dAdapter.notifyDataSetChanged();
            }
        });
    }


    private void setMain(int progress) {
        mainProgressBar.setProgress(progress, true);
        String prg;
        if (progress > 0 && progress <= 100) {
            prg = progress + "%\nTotal";
        } else {
            prg = 0 + "%\nTotal";
        }
        mainProgressText.setText(prg);
        String p = Constants.RUPEE + (int)expense;
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
        try{
            initViewModel();
            setMain(progress);
        }catch(Exception e){
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
        try{
            initViewModel();
            setMain(progress);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}