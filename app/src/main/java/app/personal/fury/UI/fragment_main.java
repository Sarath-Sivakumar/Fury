package app.personal.fury.UI;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.Objects;

import app.personal.MVVM.Viewmodel.mainViewModel;
import app.personal.Utls.Commons;
import app.personal.Utls.Constants;
import app.personal.fury.R;
import app.personal.fury.UI.Adapters.mainLists.categoryAdapter;
import app.personal.fury.UI.Adapters.mainLists.duesAdapter;

public class fragment_main extends Fragment {
    //Color contains 6 usable colors...

    private CircularProgressIndicator mainProgressBar;
    private TextView expView;
    private TextView mainProgressText;
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void findView(View v) {
        mainProgressBar = v.findViewById(R.id.indicator);
        mainProgressText = v.findViewById(R.id.mainText);
        expView = v.findViewById(R.id.expText);
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModel();
        MobileAds.initialize(requireContext());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        findView(v);
        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initViewModel() {
        cAdapter = new categoryAdapter();
        dAdapter = new duesAdapter();
        vm = new ViewModelProvider(requireActivity()).get(mainViewModel.class);
        vm.getExp().observe(requireActivity(), expEntities -> {
            expense = 0;
            cAdapter.setExpes(expEntities, salary);
            for (int i = 0; i < expEntities.size(); i++) {
                expense = expense + expEntities.get(i).getExpenseAmt();
                progress = Commons.setProgress(expense, salary);
            }
            cAdapter.notifyDataSetChanged();
            setMain(progress);
        });
        vm.getSalary().observe(requireActivity(), salaryEntity -> {
            salary = 0;
            int size = salaryEntity.size();
            if (size>=0){
                for (int i = 0; i < size; i++) {
                    salary = salary + salaryEntity.get(i).getSalary();
                    progress = Commons.setProgress(expense, salary);
                }
            }
            setMain(progress);
        });

        vm.getDebt().observe(requireActivity(), debtEntities -> {
            if (debtEntities!=null){
                dAdapter.setDues(debtEntities);
                dAdapter.notifyDataSetChanged();
            }else{
                Log.e("Main","Due null");
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setMain(int progress){
        mainProgressBar.setProgress(progress, true);
        if (progress > 0) {
            String prg = progress + "%\nTotal";
            mainProgressText.setText(prg);
        } else {
            int pro = 0;
            String prg = pro + "%\nTotal";
            mainProgressText.setText(prg);
        }
        String p = Constants.RUPEE + expense;
        expView.setText(p);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResume() {
        super.onResume();
        setMain(progress);
    }
}