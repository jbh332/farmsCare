package com.example.hong.boaaproject.firstActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.hong.boaaproject.R;


public class PagerFragment extends Fragment {

    private int position;

    public static PagerFragment getInstance(int position) {
        PagerFragment helperFragment = new PagerFragment();
        helperFragment.position = position;
        return helperFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_helper_frag, null);
        ImageView imageView = view.findViewById(R.id.position);

        switch (position){
            case 0:
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.frag1));
                return view;
            case 1:
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.frag2));
                return view;
            case 2:
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.frag3));
                return view;
            case 3:
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.frag4));
                return view;
            case 4:
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.logo2));
                return view;
        }




        return view;
    }
}