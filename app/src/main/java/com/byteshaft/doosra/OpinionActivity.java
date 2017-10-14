package com.byteshaft.doosra;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.byteshaft.doosra.utils.AppGlobals;

public class OpinionActivity extends Activity implements View.OnClickListener {

    private Button diagnosisButton;
    private Button treatmentButton;
    private Button surgeryButton;

    private TextView motoLineOne;
    private TextView motoLineTwo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opinion);
        diagnosisButton = findViewById(R.id.button_diagnosis);
        treatmentButton = findViewById(R.id.button_treatment);
        surgeryButton = findViewById(R.id.button_surgery);

        motoLineOne = findViewById(R.id.text_one);
        motoLineTwo = findViewById(R.id.text_two);

        // set typeface
        diagnosisButton.setTypeface(AppGlobals.typeface);
        treatmentButton.setTypeface(AppGlobals.typeface);
        surgeryButton.setTypeface(AppGlobals.typeface);
        motoLineOne.setTypeface(AppGlobals.typeface);
        motoLineTwo.setTypeface(AppGlobals.typeface);

        diagnosisButton.setOnClickListener(this);
        treatmentButton.setOnClickListener(this);
        surgeryButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_diagnosis:
                startActivity(new Intent(OpinionActivity.this, UserProfile.class));
                break;
            case R.id.button_treatment:
                startActivity(new Intent(OpinionActivity.this, UserProfile.class));
                break;
            case R.id.button_surgery:
                startActivity(new Intent(OpinionActivity.this, UserProfile.class));
                break;
        }
    }
}
