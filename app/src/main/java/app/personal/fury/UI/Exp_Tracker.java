package app.personal.fury.UI;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import app.personal.Utls.Commons;
import app.personal.fury.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Exp_Tracker#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Exp_Tracker extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FloatingActionButton fltBtn;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Exp_Tracker() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Exp_Tracker.
     */
    // TODO: Rename and change types and number of parameters
    public static Exp_Tracker newInstance(String param1, String param2) {
        Exp_Tracker fragment = new Exp_Tracker();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private void init(View v){
        fltBtn = v.findViewById(R.id.exp_actionBtn);
        fltBtn.setOnClickListener(v1-> Commons.SnackBar(v,"Replace with a actual method"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_exp__tracker, container, false);
        init(v);
        return v;
    }
}