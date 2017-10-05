package com.byteshaft.doosra.accounts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.byteshaft.doosra.MainActivity;
import com.byteshaft.doosra.R;
import com.byteshaft.doosra.utils.AppGlobals;
import com.byteshaft.doosra.utils.Helpers;
import com.byteshaft.requests.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Login extends Fragment implements View.OnClickListener, HttpRequest.OnErrorListener,
        HttpRequest.OnReadyStateChangeListener {

    private View mBaseView;
    private EditText mEmail;
    private EditText mPassword;
    private Button mLoginButton;
    private TextView mForgotPasswordTextView;
    private TextView mSignUpTextView;
    private String mPasswordString;
    private String mEmailString;
    private HttpRequest request;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.fragment_login, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle(getResources().getString(R.string.login));
        mEmail = mBaseView.findViewById(R.id.email_edit_text);
        mPassword = mBaseView.findViewById(R.id.password_edit_text);
        mLoginButton = mBaseView.findViewById(R.id.button_login);
        mForgotPasswordTextView = mBaseView.findViewById(R.id.forgot_password_text_view);
        mSignUpTextView = mBaseView.findViewById(R.id.sign_up_text_view);
        mLoginButton.setOnClickListener(this);
        mForgotPasswordTextView.setOnClickListener(this);
        mSignUpTextView.setOnClickListener(this);
        return mBaseView;
    }

    public boolean validate() {
        boolean valid = true;

        mEmailString = mEmail.getText().toString();
        mPasswordString = mPassword.getText().toString();

        if (mEmailString.trim().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(mEmailString).matches()) {
            mEmail.setError("please provide a valid email");
            valid = false;
        } else {
            mEmail.setError(null);
        }
        if (mPasswordString.isEmpty() || mPassword.length() < 4) {
            mPassword.setError("Enter minimum 4 alphanumeric characters");
            valid = false;
        } else {
            mPassword.setError(null);
        }
        return valid;
    }

    private void loginUser(String email, String password) {
        request = new HttpRequest(getActivity());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("POST", String.format("%slogin", AppGlobals.BASE_URL));
        request.send(getUserLoginData(email, password));
        Helpers.showProgressDialog(getActivity(), "Logging In");
    }


    private String getUserLoginData(String email, String password) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_login:
                if (validate()) {
                    loginUser(mEmailString, mPasswordString);
                }
                break;
            case R.id.forgot_password_text_view:
                AccountManager.getInstance().loadFragment(new ForgotPassword());
                break;
            case R.id.sign_up_text_view:
                AccountManager.getInstance().loadFragment(new SignUp());
                break;

        }
    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                Helpers.dismissProgressDialog();
                switch (request.getStatus()) {
                    case HttpRequest.ERROR_NETWORK_UNREACHABLE:
                        AppGlobals.alertDialog(getActivity(), getString(R.string.login_faild), getString(R.string.check_internet));
                        break;
                    case HttpURLConnection.HTTP_NOT_FOUND:
                        AppGlobals.alertDialog(getActivity(), getString(R.string.login_faild), getString(R.string.email_not_exist));
                        break;
                    case HttpURLConnection.HTTP_UNAUTHORIZED:
                        AppGlobals.alertDialog(getActivity(), getString(R.string.login_faild), getString(R.string.check_password));
                        break;
                    case HttpURLConnection.HTTP_FORBIDDEN:
                        JSONObject object = null;
                        try {
                            object = new JSONObject(request.getResponseText());
                            System.out.println(object.toString() + "working");
                            if (object.getString("detail").equals("User deactivated by admin.")) {
                                AppGlobals.alertDialog(getActivity(), getString(R.string.login_faild), "After admins approval you can use your Account !");

                            } else {
                                Helpers.showSnackBar(getView(), R.string.activate_your_account);
                                AccountManager.getInstance().loadFragment(new AccountActivationCode());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;

                    case HttpURLConnection.HTTP_OK:
                        try {
                            JSONObject jsonObject = new JSONObject(request.getResponseText());
                            String token = jsonObject.getString(AppGlobals.KEY_TOKEN);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_TOKEN, token);
                            String accountType = jsonObject.getString(AppGlobals.KEY_ACCOUNT_TYPE);
                            if (accountType.equals("doctor")) {
                                AppGlobals.userType(true);
                            }
                            gettingUserData(true);
                            Log.e("TAG", "BAMMMM " + jsonObject.getInt("id"));
                            String userId = jsonObject.getString(AppGlobals.KEY_USER_ID);
                            String email = jsonObject.getString(AppGlobals.KEY_EMAIL);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_EMAIL, email);

                            //saving values
                            AppGlobals.loginState(true);
                            AppGlobals.gotInfo(true);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_USER_ID, userId);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_TOKEN, token);
                            Log.i("token", " " + AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
        }
    }

    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {
        Helpers.dismissProgressDialog();
        AppGlobals.alertDialog(getActivity(), getString(R.string.login_faild), getResources().getString(R.string.check_internet));
    }

    public static void gettingUserData(final boolean callActivity) {
        HttpRequest request = new HttpRequest(AppGlobals.getContext());
        request.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
            @Override
            public void onReadyStateChange(HttpRequest request, int readyState) {
                switch (readyState) {
                    case HttpRequest.STATE_DONE:
                        switch (request.getStatus()) {
                            case HttpURLConnection.HTTP_OK:
                                try {
                                    JSONObject jsonObject = new JSONObject(request.getResponseText());
                                    String firstName = jsonObject.getString(AppGlobals.KEY_FIRST_NAME);
                                    String lastName = jsonObject.getString(AppGlobals.KEY_LAST_NAME);
                                    String gender = jsonObject.getString(AppGlobals.KEY_GENDER);
                                    String docID = jsonObject.getString(AppGlobals.KEY_DOC_ID);
                                    String dateOfBirth = jsonObject.getString(AppGlobals.KEY_DATE_OF_BIRTH);
                                    String address = jsonObject.getString(AppGlobals.KEY_ADDRESS);
                                    String phoneOne = jsonObject.getString(AppGlobals.KEY_PHONE_NUMBER_PRIMARY);
                                    String phoneTwo = jsonObject.getString(AppGlobals.KEY_PHONE_NUMBER_SECONDARY);
                                    String profileId = jsonObject.getString("id");
                                    String location = jsonObject.getString(AppGlobals.KEY_LOCATION);
                                    boolean notificationState = jsonObject.getBoolean(AppGlobals.KEY_SHOW_NOTIFICATION);
                                    boolean showNewsState = jsonObject.getBoolean(AppGlobals.KEY_SHOW_NEWS);
                                    boolean availableToChatState = jsonObject.getBoolean(AppGlobals.KEY_CHAT_STATUS);

                                    if (!AppGlobals.isDoctor()) {
                                        AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_EMERGENCY_CONTACT, jsonObject.getString("emergency_contact"));
                                    }
                                    AppGlobals.saveChatStatus(availableToChatState);
                                    AppGlobals.saveNotificationState(notificationState);
                                    AppGlobals.saveNewsState(showNewsState);

                                    JSONObject cityJson = jsonObject.getJSONObject("city");
                                    AppGlobals.saveDoctorProfileIds(AppGlobals.KEY_CITY_SELECTED,
                                            cityJson.getInt("id"));

                                    JSONObject stateJson = jsonObject.getJSONObject("state");
                                    AppGlobals.saveDoctorProfileIds(AppGlobals.KEY_STATE_SELECTED,
                                            stateJson.getInt("id"));
                                    if (AppGlobals.isDoctor()) {
                                        String expiryDate = jsonObject.getString("subscription_expiry_date");
                                        AppGlobals.saveSubscriptionState("Subscription Exp: " + expiryDate);
                                        JSONArray specialityJsonArray = jsonObject.getJSONArray("speciality");
                                        Log.i("Specialities", specialityJsonArray.toString());
                                        ArrayList<Integer> ids = new ArrayList<Integer>();
                                        Set<String> specialities = new HashSet<>();
                                        for (int i = 0; i < specialityJsonArray.length(); i++) {
                                            JSONObject jsonObject1 = specialityJsonArray.getJSONObject(i);
                                            specialities.add(jsonObject1.getString("name"));
                                            Log.i("TAG", "Login speciality " + jsonObject1.getString("name"));
                                            ids.add(jsonObject1.getInt("id"));
                                        }
                                        AppGlobals.saveSpecialityToSharedPreferences(specialities);
                                        Log.i("TAG", "Login saved specialities " + AppGlobals.getSpecialityFromSharedPreferences());
                                        AppGlobals.saveDoctorSpecialities(String.valueOf(ids));
                                        Log.i("TAG", "Login saved specialities ids " + AppGlobals.getDoctorSpecialities());

                                        JSONObject subscriptionPlanObject = jsonObject.getJSONObject("subscription_plan");
                                        AppGlobals.saveDoctorProfileIds(AppGlobals.KEY_SUBSCRIPTION_SELECTED,
                                                subscriptionPlanObject.getInt("id"));
                                        String subscriptionType = subscriptionPlanObject.getString("plan_type");

                                        JSONObject affiliateClinicObject = jsonObject.getJSONObject("affiliate_clinic");
                                        String affiliateClinic = affiliateClinicObject.getString("name");
                                        AppGlobals.saveDoctorProfileIds(AppGlobals.KEY_CLINIC_SELECTED,
                                                affiliateClinicObject.getInt("id"));
                                        String collageId = jsonObject.getString(AppGlobals.KEY_COLLEGE_ID);
                                        String consultationTime = jsonObject.getString(AppGlobals.KEY_CONSULTATION_TIME);

                                        AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_CONSULTATION_TIME, consultationTime);
                                        AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_COLLEGE_ID, collageId);
                                        AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_SUBSCRIPTION_TYPE, subscriptionType);
                                        AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_AFFILIATE_CLINIC, affiliateClinic);
                                    } else {

                                        JSONObject insuranceObject = jsonObject.getJSONObject("insurance_carrier");
                                        Log.i("TAG", "insurance " + insuranceObject.toString());
                                        AppGlobals.saveDoctorProfileIds(AppGlobals.KEY_INSURANCE_SELECTED,
                                                insuranceObject.getInt("id"));
                                        JSONObject affiliateClinicObject = jsonObject.getJSONObject("affiliate_clinic");
                                        AppGlobals.saveDoctorProfileIds(AppGlobals.KEY_CLINIC_SELECTED,
                                                affiliateClinicObject.getInt("id"));
                                    }
                                    String imageUrl = jsonObject.getString(AppGlobals.KEY_IMAGE_URL);

                                    //saving values
                                    AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_FIRST_NAME, firstName);
                                    AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_LAST_NAME, lastName);
                                    AppGlobals.saveDataToSharedPreferences(AppGlobals.SERVER_PHOTO_URL, imageUrl);
                                    AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_GENDER, gender);
                                    AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_DATE_OF_BIRTH, dateOfBirth);
                                    AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_ADDRESS, address);
                                    AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_PHONE_NUMBER_PRIMARY, phoneOne);
                                    AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_PHONE_NUMBER_SECONDARY, phoneTwo);
                                    AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_DOC_ID, docID);
                                    AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_PROFILE_ID, profileId);
                                    AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_LOCATION, location);
                                    AppGlobals.gotInfo(true);
                                    if (callActivity) {
                                        Intent intent = new Intent(AppGlobals.getContext(), MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        AppGlobals.getContext().startActivity(intent);
                                    }
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
        request.open("GET", String.format("%sprofile", AppGlobals.BASE_URL));
        request.setRequestHeader("Authorization", "Token " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        request.send();
    }

}
