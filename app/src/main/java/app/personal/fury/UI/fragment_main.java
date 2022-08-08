package app.personal.fury.UI;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.progressindicator.CircularProgressIndicator;

import app.personal.MVVM.Viewmodel.mainViewModel;
import app.personal.Utls.Commons;
import app.personal.Utls.Constants;
import app.personal.fury.R;

public class fragment_main extends Fragment {

    private CircularProgressIndicator mainProgressBar;
    private TextView mainProgressText;
    private mainViewModel vm;
    private TextView expView;
    private float salary = 0, expense = 0;
    private int progress;

    public fragment_main() {}

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void findView(View v) {
        mainProgressBar = v.findViewById(R.id.indicator);
        mainProgressText = v.findViewById(R.id.mainText);
        expView = v.findViewById(R.id.expText);
        mainProgressBar.setMax(100);
        setMain(progress);
    }

    public static fragment_main newInstance() {
        return new fragment_main();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        initViewModel();
        findView(v);
        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initViewModel() {
        vm = new ViewModelProvider(requireActivity()).get(mainViewModel.class);
        vm.getSalary().observe(requireActivity(), salaryEntity -> {
            salary = 0;
            int size = salaryEntity.size();
            for (int i = 0; i < size; i++) {
                salary = salary + salaryEntity.get(i).getSalary();
            }
        });
        vm.getExp().observe(requireActivity(), expEntities -> {
            expense = 0;
            int size = expEntities.size();
            for (int i = 0; i < size; i++) {
                expense = expense + expEntities.get(i).getExpenseAmt();
            }
            progress = Commons.setProgress(expense, salary);
            setMain(progress);
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
}