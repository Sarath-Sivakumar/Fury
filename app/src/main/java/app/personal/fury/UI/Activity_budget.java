package app.personal.fury.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import app.personal.MVVM.Viewmodel.mainViewModel;
import app.personal.Utls.Commons;
import app.personal.Utls.Constants;
import app.personal.fury.R;

public class Activity_budget extends Fragment {

    private FloatingActionButton fltBtn;

    private TextView MyBudget, CurrentBal,TotalExp;

    private RecyclerView.ViewHolder ViewHolder;

    private mainViewModel vm;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void init(View v) {

        fltBtn = v.findViewById(R.id.setBud);

        MyBudget = v.findViewById(R.id.bgtAmt);

        CurrentBal = v.findViewById(R.id.B_Balance);

        TotalExp = v.findViewById(R.id.T_exp);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_budget, container, false);
        init(v);
        return v;
    }
}