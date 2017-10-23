package com.byteshaft.doosra;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.byteshaft.doosra.utils.AppGlobals;
import com.byteshaft.doosra.utils.Helpers;
import com.byteshaft.requests.FormData;
import com.byteshaft.requests.HttpRequest;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.os.Build.VERSION_CODES.M;

public class MedicalReports extends AppCompatActivity implements View.OnClickListener,
        HttpRequest.OnReadyStateChangeListener, HttpRequest.OnErrorListener {

    private static final int MEDICAL_CODE = 1;
    private static final int LAB_CODE = 2;
    private static final int REPORT_CODE = 3;
    private static final int OTHER_CODE = 4;

    private Button buttonMedical;
    private Button buttonLabResult;
    private Button buttonReport;
    private Button buttonOthers;
    private Button buttonSubmit;

    private HttpRequest request;

    private String medicalFileUri;
    private String labResultFileUri;
    private String reportFileUri;
    private String otherFileUri;


    private int opinionTypeID;
    private String name = AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_FIRST_NAME)
            + " " + AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_LAST_NAME);
    String age = AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_USER_AGE);
    String weight = AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_WEIGHT);
    String height = AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_HEIGHT);

    private String shortHistoryString;
    private String existingDiseaseString;
    private String concernString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_reports);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        opinionTypeID = getIntent().getIntExtra("id", 0);

        shortHistoryString = getIntent().getStringExtra("short_history");
        existingDiseaseString = getIntent().getStringExtra("existing_disease");
        concernString = getIntent().getStringExtra("concern");

        System.out.println("Id is...." + opinionTypeID + " history: " +
                shortHistoryString + " concern " + concernString + " Disease: " + existingDiseaseString);

        buttonMedical = (Button) findViewById(R.id.button_medical);
        buttonLabResult = (Button) findViewById(R.id.button_lab_result);
        buttonReport = (Button) findViewById(R.id.button_report);
        buttonOthers = (Button) findViewById(R.id.button_others);
        buttonSubmit = (Button) findViewById(R.id.button_submit);

        buttonMedical.setOnClickListener(this);
        buttonLabResult.setOnClickListener(this);
        buttonReport.setOnClickListener(this);
        buttonOthers.setOnClickListener(this);
        buttonSubmit.setOnClickListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void submitReport(String medicalFile,
                              String labFile,
                              String reportFile,
                              String otherFile,
                              String age,
                              String fullName,
                              String weight,
                              String height,
                              String opinionId,
                              String shortHistory, String existingDisease, String concern) {

        Helpers.showProgressDialog(MedicalReports.this, "Please wait...");
        request = new HttpRequest(MedicalReports.this);
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("POST", String.format("%sopinion", AppGlobals.BASE_URL));
        request.setRequestHeader("Authorization", "Token " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        request.send(getReportData(medicalFile, labFile, reportFile, otherFile, age, fullName,
                weight, height, opinionId, shortHistory, existingDisease, concern));
    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                Helpers.dismissProgressDialog();
                Log.i("TAG", "Response " + request.getResponseText());
                switch (request.getStatus()) {
                    case HttpURLConnection.HTTP_CREATED:
                        Helpers.alertDialog(MedicalReports.this,
                                "Request Submitted!", "Your Request has been submitted", null);
                        dialogForPayment();
                }
        }
    }

    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {
        Helpers.dismissProgressDialog();
    }

    private void dialogForPayment() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Request Submitted");
        alertDialogBuilder.setMessage("Proceed for Payment")
                .setCancelable(false).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        startActivity(new Intent(MedicalReports.this, PaymentActivity.class));
                        finish();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private FormData getReportData(String medicalFile,
                                   String labFile,
                                   String reportFile,
                                   String otherFile,
                                   String age,
                                   String fullName,
                                   String weight,
                                   String height,
                                   String opinionId,
                                   String shortHistory, String existingDisease, String concern) {

        FormData formData = new FormData();

        if (medicalFileUri != null && !medicalFileUri.trim().isEmpty()) {
            formData.append(FormData.TYPE_CONTENT_FILE, "medical_file", medicalFile);
        }
        if (labResultFileUri != null && !labResultFileUri.trim().isEmpty()) {
            formData.append(FormData.TYPE_CONTENT_FILE, "lab_result_file", labFile);
        }
        if (reportFileUri != null && !reportFileUri.trim().isEmpty()) {
            formData.append(FormData.TYPE_CONTENT_FILE, "xray_file", reportFile);
        }
        if (otherFileUri != null && !otherFileUri.trim().isEmpty()) {
            formData.append(FormData.TYPE_CONTENT_FILE, "other_file", otherFile);
        }

        formData.append(FormData.TYPE_CONTENT_TEXT, "full_name", fullName);
        formData.append(FormData.TYPE_CONTENT_TEXT, "age", age);
        formData.append(FormData.TYPE_CONTENT_TEXT, "height", height);
        formData.append(FormData.TYPE_CONTENT_TEXT, "weight", weight);
        formData.append(FormData.TYPE_CONTENT_TEXT, "opinion_type", opinionId);

        formData.append(FormData.TYPE_CONTENT_TEXT, "short_history", shortHistory);
        formData.append(FormData.TYPE_CONTENT_TEXT, "existing_disease", existingDisease);
        formData.append(FormData.TYPE_CONTENT_TEXT, "concern", concern);

        return formData;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_medical:
                checkPermissions(MEDICAL_CODE);
                break;

            case R.id.button_lab_result:
                checkPermissions(LAB_CODE);
                break;

            case R.id.button_report:
                checkPermissions(REPORT_CODE);
                break;

            case R.id.button_others:
                checkPermissions(OTHER_CODE);
                break;
            case R.id.button_submit:
                submitReport(medicalFileUri, labResultFileUri, reportFileUri, otherFileUri,
                        age, name, weight, height, String.valueOf(opinionTypeID), shortHistoryString,
                        existingDiseaseString, concernString);
                break;
        }
    }

    private void openFileChooser(int requestCode) {
        new MaterialFilePicker()
                .withActivity(this)
                .withRequestCode(requestCode)
                .withHiddenFiles(true)
                .withTitle("Select File")
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MEDICAL_CODE && resultCode == RESULT_OK) {
            medicalFileUri = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            buttonMedical.setBackgroundColor(getResources().getColor(R.color.green_button_color));
        } else if (requestCode == LAB_CODE && resultCode == RESULT_OK) {
            labResultFileUri = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            buttonLabResult.setBackgroundColor(getResources().getColor(R.color.green_button_color));

        } else if (requestCode == REPORT_CODE && resultCode == RESULT_OK) {
            reportFileUri = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            buttonReport.setBackgroundColor(getResources().getColor(R.color.green_button_color));

        } else if (requestCode == OTHER_CODE && resultCode == RESULT_OK) {
            otherFileUri = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            buttonOthers.setBackgroundColor(getResources().getColor(R.color.green_button_color));
        }
    }

    public void checkPermissions(int requestCode) {
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MedicalReports.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(MedicalReports.this, listPermissionsNeeded.toArray(
                    new String[listPermissionsNeeded.size()]), requestCode);
        }

        if (listPermissionsNeeded.size() == 0) {
            openFileChooser(requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MEDICAL_CODE:
            case LAB_CODE:
            case OTHER_CODE:
            case REPORT_CODE:

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        // process the normal flow
                        //else any one or both the permissions are not granted
                        openFileChooser(requestCode);
                    } else {
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (Build.VERSION.SDK_INT >= M) {
                            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) ||
                                    shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                showDialogOK(getString(R.string.camera_storage_permission),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which) {
                                                    case DialogInterface.BUTTON_POSITIVE:
                                                        checkPermissions(requestCode);
                                                        break;
                                                    case DialogInterface.BUTTON_NEGATIVE:
                                                        // proceed with logic by disabling the related features or quit the app.
                                                        break;
                                                }
                                            }
                                        });
                            }
                            //permission is denied (and never ask again is  checked)
                            //shouldShowRequestPermissionRationale will return false
//                            else {
//                                Toast.makeText(getApplicationContext(), R.string.go_settings_permission, Toast.LENGTH_LONG)
//                                        .show();
//                                //                            //proceed with logic by disabling the related features or quit the
//                                Helpers.showSnackBar(buttonSubmit, R.string.permission_denied);
//                            }
                        }
                        break;

                    }

                }
        }
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(AppGlobals.getContext())
                .setMessage(message)
                .setPositiveButton(R.string.ok_button, okListener)
                .setNegativeButton(R.string.cancel, okListener)
                .create()
                .show();
    }

}
