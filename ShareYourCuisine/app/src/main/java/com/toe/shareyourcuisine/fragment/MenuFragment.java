package com.toe.shareyourcuisine.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.toe.shareyourcuisine.R;
import com.toe.shareyourcuisine.activity.CreateMenuActivity;

/**
 * Created by HQu on 12/3/2016.
 */

public class MenuFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.create_menu_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateMenuActivity.class);
                startActivity(intent);
            }
        });
        return rootView;
    }
}
