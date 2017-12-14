package com.byteshaft.doosra;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.byteshaft.doosra.utils.AppGlobals;
import com.byteshaft.doosra.utils.Helpers;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {

    private static UserProfile sInstance;

    public static UserProfile getInstance() {
        return sInstance;
    }

    private EditText shortHistory;
    private EditText existingDisease;
    private EditText concern;
    private CircleImageView userImage;
    private TextView fullName;
    private TextView age;
    private TextView weight;
    private TextView height;
    private Button buttonNext;
    private int opinionTypeID;
    private TextView opinionByType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sInstance = this;

        setContentView(R.layout.activity_user_profile);
        opinionTypeID = getIntent().getIntExtra("id", 0);
        String opinionName = getIntent().getStringExtra("name");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        userImage = findViewById(R.id.user_image);
        shortHistory = findViewById(R.id.short_history_edit_text);
        existingDisease = findViewById(R.id.disease_details_edit_text);
        concern = findViewById(R.id.concerns_details_edit_text);
        buttonNext = findViewById(R.id.button_next);
        fullName = findViewById(R.id.tv_name);
        age = findViewById(R.id.tv_age);
        weight = findViewById(R.id.tv_weight);
        height = findViewById(R.id.tv_height);
        opinionByType = findViewById(R.id.text_by_opinion_type);
        opinionByType.setText(getTextByOpinion(opinionName));

        fullName.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_FIRST_NAME) + " " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_LAST_NAME));

        age.setText("Age: " + AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_USER_AGE) + " Years");
        weight.setText("Weight:" + AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_WEIGHT) + " kg");
        height.setText("Height:" + AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_HEIGHT) + " ft");

        concern.setTypeface(AppGlobals.typeface);
        existingDisease.setTypeface(AppGlobals.typeface);
        shortHistory.setTypeface(AppGlobals.typeface);
        opinionByType.setTypeface(AppGlobals.typeface);
        fullName.setTypeface(AppGlobals.typeface);
        age.setTypeface(AppGlobals.typeface);
        weight.setTypeface(AppGlobals.typeface);
        height.setTypeface(AppGlobals.typeface);
        buttonNext.setTypeface(AppGlobals.typeface);

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shortHistoryString = shortHistory.getText().toString();
                String existingDiseaseString = existingDisease.getText().toString();
                String concernString = concern.getText().toString();

                Intent intent = new Intent(UserProfile.this, MedicalReports.class);
                intent.putExtra("id", opinionTypeID);
                intent.putExtra("short_history", shortHistoryString);
                intent.putExtra("existing_disease", existingDiseaseString);
                intent.putExtra("concern", concernString);
                startActivity(intent);
            }
        });

        if (AppGlobals.isLogin() && AppGlobals.getStringFromSharedPreferences(
                AppGlobals.KEY_SERVER_IMAGE) != null) {
            String url = AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_SERVER_IMAGE);
            Helpers.getBitMap(url, userImage);
        }
    }

    private String getTextByOpinion(String name) {
        switch (name) {
            case "Surgical opinion":
                return getResources().getString(R.string.string_for_surgical);
            case "Treatment opinion":
                return getResources().getString(R.string.string_for_treatment);
            case "Diagnostic opinion":
                return getResources().getString(R.string.string_for_diagnostic);
            default:
                return "";
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
