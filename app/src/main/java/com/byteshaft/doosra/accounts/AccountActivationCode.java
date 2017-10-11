package com.byteshaft.doosra.accounts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.doosra.MainActivity;
import com.byteshaft.doosra.R;
import com.byteshaft.doosra.utils.AppGlobals;
import com.byteshaft.doosra.utils.Helpers;
import com.byteshaft.requests.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;


public class AccountActivationCode extends Fragment implements View.OnClickListener,
        HttpRequest.OnReadyStateChangeListener, HttpRequest.OnErrorListener {

    private View mBaseView;

    private EditText mEmail;
    private EditText mVerificationCode;
    private Button mLoginButton;
    private TextView mResendTextView;
    private String mEmailString;
    private String mVerificationCodeString;
    private HttpRequest request;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.fragment_account_activation_code, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle(getResources().getString(R.string.account_activation));
        setHasOptionsMenu(true);

        mEmail = mBaseView.findViewById(R.id.email_edit_text);
        mVerificationCode = mBaseView.findViewById(R.id.verification_code);
        mLoginButton = mBaseView.findViewById(R.id.button_activate);
        mResendTextView = mBaseView.findViewById(R.id.resend_text_view);

        /// Set Typeface
        mEmail.setTypeface(AppGlobals.typeface);
        mVerificationCode.setTypeface(AppGlobals.typeface);
        mLoginButton.setTypeface(AppGlobals.typeface);
        mResendTextView.setTypeface(AppGlobals.typeface);
        mEmail.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_EMAIL));
        mEmailString = AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_EMAIL);
        mLoginButton.setOnClickListener(this);
        mResendTextView.setOnClickListener(this);
        return mBaseView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_activate:
                if (validate()) {
                    activateUser(mEmailString, mVerificationCodeString);
                }
                break;
            case R.id.resend_text_view:
                if (validateFroResend()) {
                    resendVerificationCOde(mEmailString);
                }

                break;
        }
    }


    public boolean validateFroResend() {
        boolean valid = true;
        mEmailString = mEmail.getText().toString();
        mVerificationCodeString = mVerificationCode.getText().toString();

        System.out.println(mEmailString);
        System.out.println(mVerificationCodeString);

        if (mEmailString.trim().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(mEmailString).matches()) {
            mEmail.setError("please provide a valid email");
            valid = false;
        } else {
            mEmail.setError(null);
        }
        return valid;
    }

    public boolean validate() {
        boolean valid = true;
        mEmailString = mEmail.getText().toString();
        mVerificationCodeString = mVerificationCode.getText().toString();

        System.out.println(mEmailString);
        System.out.println(mVerificationCodeString);

        if (mEmailString.trim().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(mEmailString).matches()) {
            mEmail.setError("please provide a valid email");
            valid = false;
        } else {
            mEmail.setError(null);
        }
        if (mVerificationCodeString.trim().isEmpty() || mVerificationCodeString.length() < 6) {
            mVerificationCode.setError("Verification code must be 6 characters");
            valid = false;
        } else {
            mVerificationCode.setError(null);
        }
        return valid;
    }

    private void activateUser(String email, String emailOtp) {
        Helpers.showProgressDialog(getActivity(), "Activating User");
        request = new HttpRequest(getActivity());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("POST", String.format("%sactivate", AppGlobals.BASE_URL));
        request.send(getUserActivationData(email, emailOtp));
    }


    private String getUserActivationData(String email, String emailOtp) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("email_otp", emailOtp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                Log.i("TAG", "dismisss");
                Helpers.dismissProgressDialog();
                switch (request.getStatus()) {
                    case HttpURLConnection.HTTP_BAD_REQUEST:
                        Toast.makeText(getActivity(), "Please enter correct account Verification code", Toast.LENGTH_LONG).show();
                        break;
                    case HttpURLConnection.HTTP_OK:
                        Log.i("TAG", request.getResponseText());
                        try {
                            JSONObject jsonObject = new JSONObject(request.getResponseText());
                            String firstName = jsonObject.getString(AppGlobals.KEY_FIRST_NAME);
                            String lastName = jsonObject.getString(AppGlobals.KEY_LAST_NAME);
                            String gender = jsonObject.getString(AppGlobals.KEY_GENDER);
                            String userId = jsonObject.getString(AppGlobals.KEY_USER_ID);
                            String token = jsonObject.getString(AppGlobals.KEY_TOKEN);
                            String email = jsonObject.getString(AppGlobals.KEY_EMAIL);
                            String mobileNumber = jsonObject.getString(AppGlobals.KEY_MOBILE_NUMBER);


                            //saving values
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_FIRST_NAME, firstName);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_LAST_NAME, lastName);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_GENDER, gender);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_TOKEN, token);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_MOBILE_NUMBER, mobileNumber);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_EMAIL, email);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_USER_ID, userId);
                            AppGlobals.loginState(true);
                            startActivity(new Intent(getActivity(), MainActivity.class));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
        }

    }

    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {
        Helpers.dismissProgressDialog();
    }

    private void resendVerificationCOde(String email) {
        Helpers.showProgressDialog(getActivity(), "Resending Verification code ");
        request = new HttpRequest(getActivity());
        request.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
            @Override
            public void onReadyStateChange(HttpRequest request, int readyState) {
                switch (readyState) {
                    case HttpRequest.STATE_DONE:
                        Log.i("TAG", "dismisss");
                        Helpers.dismissProgressDialog();
                        switch (request.getStatus()) {
                            case HttpURLConnection.HTTP_BAD_REQUEST:
                                Toast.makeText(getActivity(), "Please enter correct account Verification code", Toast.LENGTH_LONG).show();
                                break;
                            case HttpURLConnection.HTTP_OK:
                                AppGlobals.alertDialog(getActivity(), "Sending successful !", "Verification code has been sent to you! Please check your Email");
                                break;
                            case HttpRequest.ERROR_NETWORK_UNREACHABLE:
                                AppGlobals.alertDialog(getActivity(), "Sending Failed!", "please check your internet connection !");
                                break;
                            case HttpURLConnection.HTTP_NOT_FOUND:
                                AppGlobals.alertDialog(getActivity(), "Sending Failed!", "provide a valid EmailAddress !");
                                break;
                            case HttpURLConnection.HTTP_FORBIDDEN:
                                AppGlobals.alertDialog(getActivity(), "Sending Failed!", "User deactivated by admin !");
                                break;
                            case HttpURLConnection.HTTP_NOT_MODIFIED:
                                AppGlobals.alertDialog(getActivity(), "Sending Failed!", "Your account is already activated !");
                                break;

                        }
                }
            }
        });
        request.setOnErrorListener(new HttpRequest.OnErrorListener() {
            @Override
            public void onError(HttpRequest request, int readyState, short error, Exception exception) {

            }
        });
        request.open("POST", String.format("%srequest-activation-key", AppGlobals.BASE_URL));
        request.send(getresendVerificationData(email));
    }


    private String getresendVerificationData(String email) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
