package app.personal.fury.UI.Adapters.IG_fragment;

import android.os.Bundle;

import androidx.annotation.DrawableRes;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import app.personal.fury.R;

public class ig extends Fragment {

    private static final String ARG_PARAM1 = "param1";

    @DrawableRes
    private int drawable;

    public ig() {
        // Required empty public constructor
    }

    public static ig newInstance(@DrawableRes int Drawable) {
        ig fragment = new ig();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, Drawable);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            drawable = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_info_graphics, container, false);
        ImageView i = v.findViewById(R.id.infoGraphics);
        i.setImageResource(drawable);
        return v;
    }
}