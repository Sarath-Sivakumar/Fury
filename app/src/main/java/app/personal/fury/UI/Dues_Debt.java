package app.personal.fury.UI;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.personal.fury.R;

public class Dues_Debt extends Fragment {

    private TextView totalDueDisplay;

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
        return v;
    }

    private void find(View v){

    }
}