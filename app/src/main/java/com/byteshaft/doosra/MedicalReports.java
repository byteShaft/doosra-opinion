package com.byteshaft.doosra;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.PayPal;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.byteshaft.doosra.utils.AppGlobals;
import com.byteshaft.doosra.utils.Helpers;
import com.byteshaft.requests.FormData;
import com.byteshaft.requests.HttpRequest;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.os.Build.VERSION_CODES.M;

public class MedicalReports extends AppCompatActivity implements View.OnClickListener,
        HttpRequest.OnReadyStateChangeListener, HttpRequest.OnErrorListener {

    private static MedicalReports sInstance;

    public static MedicalReports getInstance() {
        return sInstance;
    }

    private static final int MEDICAL_CODE = 1;
    private static final int LAB_CODE = 2;
    private static final int REPORT_CODE = 3;
    private static final int OTHER_CODE = 4;

    private ImageButton buttonMedical;
    private ImageButton buttonLabResult;
    private ImageButton buttonReport;
    private ImageButton buttonOthers;
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
    private BraintreeFragment mBraintreeFragment;
    private int currentOpinionId;
    private String mAuthorization;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sInstance = this;
        setContentView(R.layout.activity_medical_reports);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        opinionTypeID = getIntent().getIntExtra("id", 0);

        shortHistoryString = getIntent().getStringExtra("short_history");
        existingDiseaseString = getIntent().getStringExtra("existing_disease");
        concernString = getIntent().getStringExtra("concern");

        System.out.println("Id is...." + opinionTypeID + " history: " +
                shortHistoryString + " concern " + concernString + " Disease: " + existingDiseaseString);

        buttonMedical = (ImageButton) findViewById(R.id.button_medical);
        buttonLabResult = (ImageButton) findViewById(R.id.button_lab_result);
        buttonReport = (ImageButton) findViewById(R.id.button_report);
        buttonOthers = (ImageButton) findViewById(R.id.button_others);
        buttonSubmit = (Button) findViewById(R.id.button_submit);
        buttonMedical.setOnClickListener(this);
        buttonLabResult.setOnClickListener(this);
        buttonReport.setOnClickListener(this);
        buttonOthers.setOnClickListener(this);
        buttonSubmit.setOnClickListener(this);

//        AppGlobals.getApiClient(this).getClientToken("",
//                "m5hrg4dwnr27cjhj", new Callback<com.byteshaft.doosra.braintree.ClientToken>() {
//                    @Override
//                    public void success(com.byteshaft.doosra.braintree.ClientToken clientToken, Response response) {
//                        if (TextUtils.isEmpty(clientToken.getClientToken())) {
//                            Log.i("TAG", "empty");
//                        } else {
//                            Log.i("TAG", "SUCCESS");
//                            mAuthorization = clientToken.getClientToken();
//                        }
//                    }
//
//                    @Override
//                    public void failure(RetrofitError error) {
//                        Log.i("TAG", "error");
//                    }
//                });
        try {
            mBraintreeFragment = BraintreeFragment.newInstance(this, "sandbox_ddqqfs9x_m5hrg4dwnr27cjhj");
        } catch(InvalidArgumentException e) {
            // the authorization provided was of an invalid form
        }
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
                switch (request.getStatus()) {
                    case HttpURLConnection.HTTP_CREATED:
                        try {
                            JSONObject jsonObject = new JSONObject(request.getResponseText());
                            currentOpinionId = jsonObject.getInt("id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(sInstance, "Your Request has been submitted", Toast.LENGTH_SHORT).show();
//                        Helpers.alertDialog(MedicalReports.this,
//                                "Request Submitted!", "Your Request has been submitted", null);
                        dialogForPayment();
                        break;
                }
        }
    }

    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {
        Helpers.dismissProgressDialog();
    }

    private void dialogForPayment() {
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MedicalReports.this);
                alertDialogBuilder.setTitle("Request Submitted");
                alertDialogBuilder.setMessage("Proceed for Payment,2000 INR will be deducted")
                        .setCancelable(false).setPositiveButton("Pay",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                getTokenForPayment();
                            }
                        });
                alertDialogBuilder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Snackbar.make(findViewById(android.R.id.content), "your request will be ignored", Snackbar.LENGTH_SHORT);

                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        }, 1000);
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
            buttonMedical.setBackgroundDrawable(ContextCompat.getDrawable(AppGlobals.getContext(), R.drawable.ic_uploaded));
        } else if (requestCode == LAB_CODE && resultCode == RESULT_OK) {
            labResultFileUri = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            buttonLabResult.setBackgroundDrawable(ContextCompat.getDrawable(AppGlobals.getContext(), R.drawable.ic_uploaded));

        } else if (requestCode == REPORT_CODE && resultCode == RESULT_OK) {
            reportFileUri = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            buttonReport.setBackgroundDrawable(ContextCompat.getDrawable(AppGlobals.getContext(), R.drawable.ic_uploaded));

        } else if (requestCode == OTHER_CODE && resultCode == RESULT_OK) {
            otherFileUri = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            buttonOthers.setBackgroundDrawable(ContextCompat.getDrawable(AppGlobals.getContext(), R.drawable.ic_uploaded));
        } else    if (requestCode == 101) {
            if (resultCode == Activity.RESULT_OK) {
//                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
//                if (result.getPaymentMethodNonce() instanceof PayPalAccountNonce) {
//                    PayPalAccountNonce payPalAccountNonce = (PayPalAccountNonce) result.getPaymentMethodNonce();
//
//                    // Access additional information
//                    String email = payPalAccountNonce.getEmail();
//                    String firstName = payPalAccountNonce.getFirstName();
//                    String lastName = payPalAccountNonce.getLastName();
//                    String phone = payPalAccountNonce.getPhone();
//
//                    // See PostalAddress.java for details
//                    PostalAddress billingAddress = payPalAccountNonce.getBillingAddress();
//                    PostalAddress shippingAddress = payPalAccountNonce.getShippingAddress();
//                    Log.i("email" , email);
//                }
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                String paymentMethodNonce = result.getPaymentMethodNonce().getNonce();
                // send paymentMethodNonce to your server
                sendRequestToDoPayment(currentOpinionId, paymentMethodNonce);
                Log.i("paymentMethodNonce" , paymentMethodNonce);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // canceled
            } else {
                // an error occurred, checked the returned exception
                Exception exception = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                Log.i("exception" , exception.getMessage());
                exception.printStackTrace();
            }
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

    public void doTransaction(String token) {
        mBraintreeFragment.addListener(new PaymentMethodNonceCreatedListener() {
            @Override
            public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
                Log.i("TAG", "payment methid nounce");
            }
        });
        DropInRequest dropInRequest = new DropInRequest()
                .clientToken(token);
        Log.i("TAG", "enabled "+ dropInRequest.isPayPalEnabled());
        dropInRequest.amount("30");
        dropInRequest.collectDeviceData(true);
        dropInRequest.disableAndroidPay();
        dropInRequest.requestThreeDSecureVerification(true);
        dropInRequest.disableVenmo();
        dropInRequest.tokenizationKey(token);
        dropInRequest.paypalAdditionalScopes(Collections.singletonList(PayPal.SCOPE_ADDRESS));
        startActivityForResult(dropInRequest.getIntent(this), 101);
//        setupBraintreeAndStartExpressCheckout();
    }

    private void getTokenForPayment() {
        HttpRequest request = new HttpRequest(this);
        request.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
            @Override
            public void onReadyStateChange(HttpRequest request, int readyState) {
                switch (readyState) {
                    case HttpRequest.STATE_DONE:
                        Helpers.dismissProgressDialog();
                        switch (request.getStatus()) {
                            case HttpURLConnection.HTTP_OK:
                                Log.i("TAG", "token " + request.getResponseText());
                                try {
                                    JSONObject jsonObject = new JSONObject(request.getResponseText());
//                                    launchDropIn(jsonObject.getString("token"));
                                    doTransaction(jsonObject.getString("token"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                }

            }
        });
        request.setOnErrorListener(this);
        request.open("GET", String.format("%spayments/token", AppGlobals.BASE_URL));
        request.setRequestHeader("Authorization", "Token " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        Log.i(":TAG", "token " + AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        request.send();
    }

    private void sendRequestToDoPayment(int opinionId, String paymentMethodNonce) {
        HttpRequest request = new HttpRequest(this);
        request.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
            @Override
            public void onReadyStateChange(HttpRequest request, int readyState) {
                switch (readyState) {
                    case HttpRequest.STATE_DONE:
                        Helpers.dismissProgressDialog();
                        switch (request.getStatus()) {
                            case HttpURLConnection.HTTP_OK:
                                Log.i("TAG", "token " + request.getResponseText());
                                try {
                                    JSONObject jsonObject = new JSONObject(request.getResponseText());
//                                    doTransaction(jsonObject.getString("token"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                }

            }
        });
        request.setOnErrorListener(this);
        request.open("POST", String.format("%spayments/pay", AppGlobals.BASE_URL));
        request.setRequestHeader("Authorization", "Token " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("opinion", opinionId);
            jsonObject.put("payment_method_nonce", paymentMethodNonce);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        request.send(jsonObject.toString());
    }

//    private void getToken() {
//        AppGlobals.getApiClient(this).getClientToken("15",
//                "m5hrg4dwnr27cjhj", new Callback<com.byteshaft.doosra.braintree.ClientToken>() {
//                    @Override
//                    public void success(com.byteshaft.doosra.braintree.ClientToken clientToken, Response response) {
//                        if (TextUtils.isEmpty(clientToken.getClientToken())) {
//                            Log.i("TAG", "empty");
//                        } else {
//                            Log.i("TAG", "SUCCESS");
////                            doTransaction();
//                            mAuthorization = clientToken.getClientToken();
//                        }
//                    }
//
//                    @Override
//                    public void failure(RetrofitError error) {
//                        Log.i("TAG", "error");
//                    }
//                });
//        try {
//            mBraintreeFragment = BraintreeFragment.newInstance(this, "sandbox_ddqqfs9x_m5hrg4dwnr27cjhj");
//        } catch(InvalidArgumentException e) {
//            // the authorization provided was of an invalid form
//        }
//    }
}
