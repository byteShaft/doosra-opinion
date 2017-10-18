package com.byteshaft.doosra;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class UserProfile extends AppCompatActivity {

    private EditText shortHistory;
    private EditText existingDisease;
    private EditText concern;

    private Button buttonNext;

    private int opinionTypeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        opinionTypeID = getIntent().getIntExtra("id", 0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        shortHistory = (EditText) findViewById(R.id.short_history_edit_text);
        existingDisease = (EditText) findViewById(R.id.disease_details_edit_text);
        concern = (EditText) findViewById(R.id.concerns_details_edit_text);
        buttonNext = (Button) findViewById(R.id.button_next);
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

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
