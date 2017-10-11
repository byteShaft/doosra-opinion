package com.byteshaft.doosra.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.byteshaft.doosra.MedicalReports;
import com.byteshaft.doosra.OpinionActivity;
import com.byteshaft.doosra.R;
import com.byteshaft.doosra.utils.AppGlobals;

public class Dashboard extends Fragment implements View.OnClickListener {

    private View mBaseView;
    private Button buttonHowItWorks;
    private Button buttonAboutUs;
    private Button buttonBoD;
    private Button buttonGetSecOpinion;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle(getResources().getString(R.string.home));

        buttonHowItWorks = mBaseView.findViewById(R.id.button_how_it_works);
        buttonAboutUs = mBaseView.findViewById(R.id.button_about_us);
        buttonBoD = mBaseView.findViewById(R.id.button_board_of_doctors);
        buttonGetSecOpinion = mBaseView.findViewById(R.id.button_get_a_second_opinion);

        buttonHowItWorks.setTypeface(AppGlobals.typeface);
        buttonAboutUs.setTypeface(AppGlobals.typeface);
        buttonBoD.setTypeface(AppGlobals.typeface);
        buttonGetSecOpinion.setTypeface(AppGlobals.typeface);

        buttonHowItWorks.setOnClickListener(this);
        buttonAboutUs.setOnClickListener(this);
        buttonBoD.setOnClickListener(this);
        buttonGetSecOpinion.setOnClickListener(this);

        return mBaseView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_how_it_works:
                startActivity(new Intent(getActivity(), MedicalReports.class));
                break;
            case R.id.button_about_us:
                System.out.println("About Us");
                break;
            case R.id.button_board_of_doctors:
                break;
            case R.id.button_get_a_second_opinion:
                startActivity(new Intent(getActivity(), OpinionActivity.class));
                break;
        }
    }
}
