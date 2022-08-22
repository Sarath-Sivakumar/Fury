package app.personal.fury.UI;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.personal.MVVM.Viewmodel.mainViewModel;
import app.personal.Utls.Constants;
import app.personal.fury.R;
import app.personal.fury.UI.Adapters.dueList.dueAdapter;

public class Dues_Debt extends Fragment {

    private TextView totalDueDisplay;
    private RecyclerView dueList;
    private mainViewModel vm;
    private dueAdapter adapter;
    private float finalTotalDue = 0.0F;

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
        totalDueDisplay = v.findViewById(R.id.dueTotalText);
        dueList.setAdapter(adapter);

    }

    private void initViewModel() {
        vm.getDebt().observe(requireActivity(), entity -> {
            if (entity != null) {
                adapter.setDebt(entity,true);
                finalTotalDue = 0F;
                finalTotalDue = adapter.getTotalDebt();
            }
            String s = Constants.RUPEE+finalTotalDue;
            totalDueDisplay.setText(s);
        });
    }
}