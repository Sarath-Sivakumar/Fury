package app.personal.fury.UI.User_Init.walkthroughFrag;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import app.personal.fury.R;
import app.personal.fury.UI.User_Init.Landing;
import app.personal.fury.UI.splashTutorialSlider;

public class frag1 extends Fragment {

    private ImageButton next;
    private Button skip;

    public frag1() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.item_walkthrough_exp, container, false);
        findView(v);
        onClick();
        return v;
    }
    private void findView(View v){
        next = v.findViewById(R.id.next);
        skip = v.findViewById(R.id.skip);
    }

    private void onClick(){
        next.setOnClickListener(v -> splashTutorialSlider.setPage(1));
        skip.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), Landing.class));
            requireActivity().finish();
        });
    }
}