package com.byteshaft.doosra;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.byteshaft.doosra.utils.AppGlobals;

public class OpinionDetail extends AppCompatActivity implements View.OnClickListener {

    private TextView tvName;
    //    private TextView tvType;
    private TextView tvW;
    private TextView tvWeight;
    private TextView tvH;
    private TextView tvHeight;
    private TextView tvConcern;
    private TextView tvConcernText;
    private TextView tvShortHistory;
    private TextView tvShortHistoryText;
    private TextView tvDisease;
    private TextView tvDiseaseText;


    private String opinionType;
    private String userName;
    private String concern;
    private String disease;
    private String shortHistory;
    private String height;
    private String weight;
    private String xrayFile;
    private String otherFile;
    private String labFile;
    private String medicalFile;

    private Button medicalFileButton;
    private Button labFileButton;
    private Button xrayFileButton;
    private Button otherFileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opinion_detail);

        opinionType = getIntent().getStringExtra("type_name");
        setTitle(opinionType);

        userName = getIntent().getStringExtra("full_name");
        concern = getIntent().getStringExtra("concern");
        disease = getIntent().getStringExtra("disease");
        shortHistory = getIntent().getStringExtra("short_history");
        height = getIntent().getStringExtra("height");
        weight = getIntent().getStringExtra("weight");

        xrayFile = getIntent().getStringExtra("xray_file");
        otherFile = getIntent().getStringExtra("other_file");
        labFile = getIntent().getStringExtra("lab_file");
        medicalFile = getIntent().getStringExtra("medical_file");


        medicalFileButton = (Button) findViewById(R.id.medical_summary);
        labFileButton = (Button) findViewById(R.id.lab_result_button);
        xrayFileButton = (Button) findViewById(R.id.xray_button);
        otherFileButton = (Button) findViewById(R.id.other_button);

        if (xrayFile.contains("null")) {
            xrayFileButton.setVisibility(View.GONE);
        }

        if (otherFile.contains("null")) {
            otherFileButton.setVisibility(View.GONE);
        }
        if (labFile.contains("null")) {
            labFileButton.setVisibility(View.GONE);
        }
        if (medicalFile.contains("null")) {
            medicalFileButton.setVisibility(View.GONE);
        }
        medicalFileButton.setOnClickListener(this);
        labFileButton.setOnClickListener(this);
        xrayFileButton.setOnClickListener(this);
        otherFileButton.setOnClickListener(this);

        tvName = (TextView) findViewById(R.id.tv_name);
//        tvType = (TextView) findViewById(R.id.tv_type);
        tvW = (TextView) findViewById(R.id.tv_w);
        tvWeight = (TextView) findViewById(R.id.weigh_);
        tvH = (TextView) findViewById(R.id.tv_h);
        tvHeight = (TextView) findViewById(R.id.height_);
        tvConcern = (TextView) findViewById(R.id.tv_concern);
        tvConcernText = (TextView) findViewById(R.id.concern_);
        tvShortHistory = (TextView) findViewById(R.id.tv_history);
        tvShortHistoryText = (TextView) findViewById(R.id.history_);
        tvDisease = (TextView) findViewById(R.id.tv_disease);
        tvDiseaseText = (TextView) findViewById(R.id.disease_);

        tvName.setText(userName);
//        tvType.setText(opinionType);
        tvConcernText.setText(concern);
        tvShortHistoryText.setText(shortHistory);
        tvDiseaseText.setText(disease);
        tvWeight.setText(weight);
        tvHeight.setText(height);

        tvName.setTypeface(AppGlobals.typeface);
//        tvType.setTypeface(AppGlobals.typeface);
        tvW.setTypeface(AppGlobals.typeface);
        tvWeight.setTypeface(AppGlobals.typeface);
        tvH.setTypeface(AppGlobals.typeface);
        tvHeight.setTypeface(AppGlobals.typeface);
        tvConcern.setTypeface(AppGlobals.typeface);
        tvConcernText.setTypeface(AppGlobals.typeface);
        tvShortHistory.setTypeface(AppGlobals.typeface);
        tvShortHistoryText.setTypeface(AppGlobals.typeface);
        tvDisease.setTypeface(AppGlobals.typeface);
        tvDiseaseText.setTypeface(AppGlobals.typeface);

        medicalFileButton.setTypeface(AppGlobals.typeface);
        labFileButton.setTypeface(AppGlobals.typeface);
        xrayFileButton.setTypeface(AppGlobals.typeface);
        otherFileButton.setTypeface(AppGlobals.typeface);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.medical_summary:
                Intent medical = new Intent(OpinionDetail.this, Webview.class);
                medical.putExtra("url", medicalFile);
                startActivity(medical);
                break;
            case R.id.lab_result_button:
                Intent lab = new Intent(OpinionDetail.this, Webview.class);
                lab.putExtra("url", labFile);
                startActivity(lab);
                break;
            case R.id.xray_button:
                Intent xray = new Intent(OpinionDetail.this, Webview.class);
                xray.putExtra("url", xrayFile);
                startActivity(xray);
                break;
            case R.id.other_button:
                Intent other = new Intent(OpinionDetail.this, Webview.class);
                other.putExtra("url", otherFile);
                startActivity(other);
                break;
        }
    }
}
