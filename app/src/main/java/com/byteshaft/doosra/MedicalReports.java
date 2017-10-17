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
import android.view.View;
import android.widget.Button;

import com.byteshaft.doosra.utils.AppGlobals;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MedicalReports extends AppCompatActivity implements View.OnClickListener {


    private static final int MEDICAL_CODE = 1;
    private static final int LAB_CODE = 2;
    private static final int REPORT_CODE = 3;
    private static final int OTHER_CODE = 4;

    private Button buttonMedical;
    private Button buttonLabResult;
    private Button buttonReport;
    private Button buttonOthers;
    private Button buttonSubmit;

    private int opinionTypeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_reports);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        opinionTypeID = getIntent().getIntExtra("id", 0);
        System.out.println(opinionTypeID);

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
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            System.out.println(" Ok OK OK" + filePath);
            // Do anything with file
        } else if (requestCode == LAB_CODE && resultCode == RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            System.out.println(" Ok OK OK" + filePath);
            // Do anything with file
        } else if (requestCode == REPORT_CODE && resultCode == RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            System.out.println(" Ok OK OK" + filePath);
            // Do anything with file
        } else if (requestCode == OTHER_CODE && resultCode == RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            System.out.println(" Ok OK OK" + filePath);
            // Do anything with file
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
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
