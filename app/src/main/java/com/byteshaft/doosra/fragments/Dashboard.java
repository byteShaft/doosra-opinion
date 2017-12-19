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
import android.widget.TextView;

import com.byteshaft.doosra.AboutUs;
import com.byteshaft.doosra.BOD;
import com.byteshaft.doosra.HowItWorks;
import com.byteshaft.doosra.MainActivity;
import com.byteshaft.doosra.OpinionActivity;
import com.byteshaft.doosra.R;
import com.byteshaft.doosra.utils.AppGlobals;

public class Dashboard extends Fragment implements View.OnClickListener {

    private View mBaseView;
    private Button buttonHowItWorks;
    private Button buttonAboutUs;
    private Button buttonBoD;
    private Button buttonGetSecOpinion;

    private TextView lineOne;
    private TextView appName;
    private TextView lineTwo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle(getResources().getString(R.string.home));
        MainActivity.getInstance().updateProfilePic();
        lineOne = mBaseView.findViewById(R.id.line_part_one);
        lineTwo = mBaseView.findViewById(R.id.line_part_two);
        appName = mBaseView.findViewById(R.id.app_name_doosra);

        lineOne.setTypeface(AppGlobals.typefaceSecondary);
        lineTwo.setTypeface(AppGlobals.typefaceSecondary);
        appName.setTypeface(AppGlobals.typefaceSecondary);

        buttonHowItWorks = mBaseView.findViewById(R.id.button_how_it_works);
        buttonAboutUs = mBaseView.findViewById(R.id.button_about_us);
        buttonBoD = mBaseView.findViewById(R.id.button_board_of_doctors);
        buttonGetSecOpinion = mBaseView.findViewById(R.id.button_get_a_second_opinion);

        buttonHowItWorks.setTypeface(AppGlobals.typefaceSecondary);
        buttonAboutUs.setTypeface(AppGlobals.typefaceSecondary);
        buttonBoD.setTypeface(AppGlobals.typefaceSecondary);
        buttonGetSecOpinion.setTypeface(AppGlobals.typefaceSecondary);

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
                startActivity(new Intent(getActivity(), HowItWorks.class));
                break;
            case R.id.button_about_us:
                startActivity(new Intent(getActivity(), AboutUs.class));
                break;
            case R.id.button_board_of_doctors:
                startActivity(new Intent(getActivity(), BOD.class));
                break;
            case R.id.button_get_a_second_opinion:
                startActivity(new Intent(getActivity(), OpinionActivity.class));
                break;
        }
    }
}
