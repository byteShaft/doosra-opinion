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

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInResult;
import com.byteshaft.doosra.utils.AppGlobals;
import com.byteshaft.doosra.utils.Helpers;
import com.byteshaft.requests.FormData;
import com.byteshaft.requests.HttpRequest;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.paytm.pgsdk.PaytmMerchant;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
    private int currentOpinionId;
    //paytm
    private int randomInt = 0;
    private PaytmPGService service = null;
    String checksum = "";

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

        buttonMedical = findViewById(R.id.button_medical);
        buttonLabResult = findViewById(R.id.button_lab_result);
        buttonReport = findViewById(R.id.button_report);
        buttonOthers = findViewById(R.id.button_others);
        buttonSubmit = findViewById(R.id.button_submit);
        buttonMedical.setOnClickListener(this);
        buttonLabResult.setOnClickListener(this);
        buttonReport.setOnClickListener(this);
        buttonOthers.setOnClickListener(this);
        buttonSubmit.setOnClickListener(this);
        Random randomGenerator = new Random();
        randomInt = randomGenerator.nextInt(1000000000);
    }

    private void doPayment(final int opinionID) {
        service = PaytmPGService.getStagingService();
        //below parameter map is required to construct PaytmOrder object, Merchant should replace below map values with his own values
        Map<String, String> paramMap = new HashMap<>();
        //these are mandatory parameters
        paramMap.put("ORDER_ID", "ORDER" + randomInt);
        paramMap.put("MID", "JBRFoo44539086147111");
        paramMap.put("CUST_ID", "CUST" + AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_USER_ID));
        paramMap.put("CHANNEL_ID", "WAP");
        paramMap.put("INDUSTRY_TYPE_ID", "Retail");
        paramMap.put("WEBSITE", "APP_STAGING");
        paramMap.put("TXN_AMOUNT", "10.00");
        paramMap.put("CHECKSUMHASH", checksum);
        paramMap.put("CALLBACK_URL", "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp");
        PaytmOrder order = new PaytmOrder(paramMap);
        service.initialize(order, null);
        service.enableLog(getApplicationContext());
        service.startPaymentTransaction(this, true, true,
                new PaytmPaymentTransactionCallback() {
                    @Override
                    public void someUIErrorOccurred(String inErrorMessage) {
                        // Some UI Error Occurred in Payment Gateway Activity.
                        // // This may be due to initialization of views in
                        // Payment Gateway Activity or may be due to //
                        // initialization of webview. // Error Message details
                        // the error occurred.
                        Toast.makeText(getApplicationContext(), "Ui/Webview error occured.", Toast.LENGTH_LONG).show();

                    }

//                    @Override
//                    public void onTransactionSuccess(Bundle inResponse) {
//                        // After successful transaction this method gets called.
//                        // // Response bundle contains the merchant response
//                        // parameters.
//                        Log.d("LOG", "Payment Transaction is successful " + inResponse);
//                        Toast.makeText(getApplicationContext(), "Payment Transaction is successful ", Toast.LENGTH_LONG).show();
//                    }
//
//                    @Override
//                    public void onTransactionFailure(String inErrorMessage,
//                                                     Bundle inResponse) {
//                        // This method gets called if transaction failed. //
//                        // Here in this case transaction is completed, but with
//                        // a failure. // Error Message describes the reason for
//                        // failure. // Response bundle contains the merchant
//                        // response parameters.
//                        Log.d("LOG", "Payment Transaction Failed " + inErrorMessage);
//                        Toast.makeText(getBaseContext(), "Payment Transaction Failed ", Toast.LENGTH_LONG).show();
//                        recreate();
//                    }

                    @Override
                    public void onTransactionResponse(Bundle bundle) {
                        Toast.makeText(MedicalReports.this, "Success!", Toast.LENGTH_SHORT).show();
                        Log.i("TAG", "user id " + AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_USER_ID));
                        Log.i("TAG", "------------------" + bundle.toString());
                        currentOpinionId = 0;
                        if (bundle.getString("STATUS").equals("TXN_SUCCESS")) {
                            updateServerAboutPayment(opinionID);
                            UserProfile.getInstance().finish();
                            OpinionActivity.getInstance().finish();
                            finish();
                        } else {
                            AlertDialog.Builder alertDialogBuilder =
                                    new AlertDialog.Builder(MedicalReports.this);
                            alertDialogBuilder.setTitle("Payment Failed!");
                            alertDialogBuilder.setMessage(getResources().getString(R.string.error_text))
                                    .setCancelable(false).setPositiveButton("Try again",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.dismiss();
                                            getCheckSum(opinionID);
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
                            Toast.makeText(MedicalReports.this, "there was some error", Toast.LENGTH_SHORT).show();
                            recreate();
                        }

                    }

                    @Override
                    public void networkNotAvailable() { // If network is not
                        // available, then this
                        // method gets called.
                        Toast.makeText(getBaseContext(), "No Internet connection.", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void clientAuthenticationFailed(String inErrorMessage) {
                        // This method gets called if client authentication
                        // failed. // Failure may be due to following reasons //
                        // 1. Server error or downtime. // 2. Server unable to
                        // generate checksum or checksum response is not in
                        // proper format. // 3. Server failed to authenticate
                        // that client. That is value of payt_STATUS is 2. //
                        // Error Message describes the reason for failure.
                        Log.e("TAG", "clientAuthenticationFailed");
                        Toast.makeText(getBaseContext(), "Client Authentication Failed.", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onErrorLoadingWebPage(int iniErrorCode,
                                                      String inErrorMessage, String inFailingUrl) {
                        Log.e("TAG", "onErrorLoadingWebPage");

                    }

                    // had to be added: NOTE
                    @Override
                    public void onBackPressedCancelTransaction() {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onTransactionCancel(String s, Bundle bundle) {

                    }

                });
    }

    private void getCheckSum(final int opinionid) {
        HttpRequest request = new HttpRequest(getApplicationContext());
        request.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
            @Override
            public void onReadyStateChange(HttpRequest request, int readyState) {
                switch (readyState) {
                    case HttpRequest.STATE_DONE:
                        switch (request.getStatus()) {
                            case HttpURLConnection.HTTP_OK:
                                Log.i("TAG", request.getResponseURL());
                                Log.i("TAG", request.getResponseText());
                                try {
                                    JSONObject jsonObject = new JSONObject(request.getResponseText());
                                    checksum = jsonObject.getString("CHECKSUMHASH");
                                    doPayment(opinionid);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                        }
                }
            }
        });
        request.setOnErrorListener(new HttpRequest.OnErrorListener() {
            @Override
            public void onError(HttpRequest request, int readyState, short error, Exception exception) {

            }
        });
        String param="ORDER_ID=" +"ORDER" +randomInt+
                "&MID=JBRFoo44539086147111"+
                "&CUST_ID="+"CUST"+AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_USER_ID)+
                "&CHANNEL_ID=WAP&INDUSTRY_TYPE_ID=Retail&WEBSITE=APP_STAGING&TXN_AMOUNT=10.00&CALLBACK_URL=https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";
        System.out.println(param);
        request.open("GET", "http://139.59.167.40/api/generatechecksum.cgi?"+param);
        request.send();
    }

    private void updateServerAboutPayment(int opinionID) {
        HttpRequest request = new HttpRequest(MedicalReports.this);
        request.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
            @Override
            public void onReadyStateChange(HttpRequest request, int readyState) {
                switch (readyState) {
                    case HttpRequest.STATE_DONE:
                        switch (request.getStatus()) {
                            case HttpURLConnection.HTTP_OK:
                        }
                }

            }
        });
        request.setOnErrorListener(new HttpRequest.OnErrorListener() {
            @Override
            public void onError(HttpRequest request, int readyState, short error, Exception exception) {

            }
        });
        request.open("POST", String.format("%spayments/pay", AppGlobals.BASE_URL));
        request.setRequestHeader("Authorization", "Token " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("opinion", opinionID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        request.send(jsonObject.toString());
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
//                        dialogForPayment();
                        dialogForPayment(currentOpinionId);
                        break;
                }
        }
    }

    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {
        Helpers.dismissProgressDialog();
    }

    private void dialogForPayment(final int opinionid) {
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MedicalReports.this);
                alertDialogBuilder.setTitle("Request Submitted");
                alertDialogBuilder.setMessage(getResources().getString(R.string.offer_text))
                        .setCancelable(false).setPositiveButton("Pay",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                getCheckSum(opinionid);
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

//    public void onBuyPressed() {
//        /*
//         * PAYMENT_INTENT_SALE will cause the payment to complete immediately.
//         * Change PAYMENT_INTENT_SALE to
//         *   - PAYMENT_INTENT_AUTHORIZE to only authorize payment and capture funds later.
//         *   - PAYMENT_INTENT_ORDER to create a payment for authorization and capture
//         *     later via calls from your server.
//         *
//         * Also, to include additional payment details and an item list, see getStuffToBuy() below.
//         */
//        PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE);
//
//        /*
//         * See getStuffToBuy(..) for examples of some available payment options.
//         */
//
//        Intent intent = new Intent(this, PaymentActivity.class);
//
//        // send the same configuration for restart resiliency
//        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
//
//        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);
//
//        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
//    }
//
//    private PayPalPayment getThingToBuy(String paymentIntent) {
//        return new PayPalPayment(new BigDecimal("30.00"), "USD", "Doosra opinion medical services",
//                paymentIntent);
//    }

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

//    public void doTransaction(String token) {
//        mBraintreeFragment.addListener(new PaymentMethodNonceCreatedListener() {
//            @Override
//            public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
//                Log.i("TAG", "payment methid nounce");
//            }
//        });
//        DropInRequest dropInRequest = new DropInRequest()
//                .clientToken(token);
//        Log.i("TAG", "enabled "+ dropInRequest.isPayPalEnabled());
//        dropInRequest.amount("30");
//        dropInRequest.collectDeviceData(true);
//        dropInRequest.disableAndroidPay();
//        dropInRequest.requestThreeDSecureVerification(true);
//        dropInRequest.disableVenmo();
//        dropInRequest.tokenizationKey(token);
//        dropInRequest.paypalAdditionalScopes(Collections.singletonList(PayPal.SCOPE_ADDRESS));
//        startActivityForResult(dropInRequest.getIntent(this), 101);
////        setupBraintreeAndStartExpressCheckout();
//    }

//    private void getTokenForPayment() {
//        HttpRequest request = new HttpRequest(this);
//        request.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
//            @Override
//            public void onReadyStateChange(HttpRequest request, int readyState) {
//                switch (readyState) {
//                    case HttpRequest.STATE_DONE:
//                        Helpers.dismissProgressDialog();
//                        switch (request.getStatus()) {
//                            case HttpURLConnection.HTTP_OK:
//                                Log.i("TAG", "token " + request.getResponseText());
//                                try {
//                                    JSONObject jsonObject = new JSONObject(request.getResponseText());
////                                    launchDropIn(jsonObject.getString("token"));
////                                    doTransaction(jsonObject.getString("token"));
//
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                                break;
//                        }
//                }
//
//            }
//        });
//        request.setOnErrorListener(this);
//        request.open("GET", String.format("%spayments/token", AppGlobals.BASE_URL));
//        request.setRequestHeader("Authorization", "Token " +
//                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
//        Log.i(":TAG", "token " + AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
//        request.send();
//    }

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
}
