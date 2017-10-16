package com.byteshaft.doosra;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MedicalReports extends AppCompatActivity {

    private int opinionTypeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_reports);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        opinionTypeID = getIntent().getIntExtra("id", 0);
        System.out.println(opinionTypeID);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
