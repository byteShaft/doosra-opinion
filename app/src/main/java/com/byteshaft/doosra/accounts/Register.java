package com.byteshaft.doosra.accounts;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.doosra.R;
import com.byteshaft.doosra.utils.AppGlobals;
import com.byteshaft.doosra.utils.Helpers;
import com.byteshaft.requests.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public class Register extends Fragment implements View.OnClickListener,
        HttpRequest.OnReadyStateChangeListener, HttpRequest.OnErrorListener, RadioGroup.OnCheckedChangeListener {

    private View mBaseView;
    private EditText mEmail;
    private EditText mPassword;

    private EditText mFirstName;
    private EditText mLastName;
    private EditText mPhoneNumber;

    private EditText mVerifyPassword;
    private Button mSignUpButton;
    private TextView mLoginTextView;

    private String firstNameString;
    private String lastNameString;
    private String mPhoneNumberString;

    private String mEmailAddressString;
    private String mPasswordString;
    private String mVerifyPasswordString;
    private String mGenderString = "Male";
    private HttpRequest request;
    private RadioGroup mGenderRadioGroup;
    private RadioButton mMale;
    private RadioButton mFemale;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.fragment_sign_up, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle(getResources().getString(R.string.sign_up));
        setHasOptionsMenu(true);

        mFirstName = mBaseView.findViewById(R.id.first_name_edit_text);
        mLastName = mBaseView.findViewById(R.id.last_name_edit_text);
        mPhoneNumber = mBaseView.findViewById(R.id.phone_number_edit_text);
        mEmail = mBaseView.findViewById(R.id.email_edit_text);
        mPassword = mBaseView.findViewById(R.id.password_edit_text);
        mVerifyPassword = mBaseView.findViewById(R.id.verify_password_edit_text);
        mSignUpButton = mBaseView.findViewById(R.id.sign_up_button);
        mLoginTextView = mBaseView.findViewById(R.id.login_text_view);
        mGenderRadioGroup = mBaseView.findViewById(R.id.gender_group);
        mMale = mBaseView.findViewById(R.id.male);
        mFemale = mBaseView.findViewById(R.id.female);

        // set typeface
        mFirstName.setTypeface(AppGlobals.typeface);
        mLastName.setTypeface(AppGlobals.typeface);
        mPhoneNumber.setTypeface(AppGlobals.typeface);
        mEmail.setTypeface(AppGlobals.typeface);
        mPassword.setTypeface(AppGlobals.typeface);
        mVerifyPassword.setTypeface(AppGlobals.typeface);
        mSignUpButton.setTypeface(AppGlobals.typeface);
        mLoginTextView.setTypeface(AppGlobals.typeface);
        mMale.setTypeface(AppGlobals.typeface);
        mFemale.setTypeface(AppGlobals.typeface);

        mSignUpButton.setOnClickListener(this);
        mLoginTextView.setOnClickListener(this);
        mGenderRadioGroup.setOnCheckedChangeListener(this);

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
            case R.id.sign_up_button:
                System.out.println("signUp button");
                if (validateEditText()) {
                    registerUser(firstNameString, lastNameString, mPhoneNumberString,
                            mPasswordString, mEmailAddressString, mGenderString);
                }
                System.out.println("radio text   " + mGenderString);
                break;
            case R.id.login_text_view:
                AccountManager.getInstance().loadFragment(new Login());
                break;
        }
    }


    private boolean validateEditText() {
        boolean valid = true;

        firstNameString = mFirstName.getText().toString();
        lastNameString = mLastName.getText().toString();
        mPhoneNumberString = mPhoneNumber.getText().toString();

        mEmailAddressString = mEmail.getText().toString();
        mPasswordString = mPassword.getText().toString();
        mVerifyPasswordString = mVerifyPassword.getText().toString();

        if (firstNameString.trim().isEmpty()) {
            mFirstName.setError("Required");
            valid = false;
        } else {
            mEmail.setError(null);
        }

        if (lastNameString.trim().isEmpty()) {
            mLastName.setError("Required");
            valid = false;
        } else {
            mLastName.setError(null);
        }

        if (mPhoneNumberString.trim().isEmpty()) {
            mPhoneNumber.setError("Required");
            valid = false;
        } else {
            mPhoneNumber.setError(null);
        }

        if (mEmailAddressString.trim().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(mEmailAddressString).matches()) {
            mEmail.setError("please provide a valid email");
            valid = false;
        } else {
            mEmail.setError(null);
        }
        if (mPasswordString.trim().isEmpty() || mPasswordString.length() < 4) {
            mPassword.setError("enter at least 4 characters");
            valid = false;
        } else {
            mPassword.setError(null);
        }

        if (mVerifyPasswordString.trim().isEmpty() || mVerifyPasswordString.length() < 4 ||
                !mVerifyPasswordString.equals(mPasswordString)) {
            mVerifyPassword.setError("password does not match");
            valid = false;
        } else {
            mVerifyPassword.setError(null);
        }
        return valid;
    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                Helpers.dismissProgressDialog();
                Log.i("TAG", "Response " + request.getResponseText());
                switch (request.getStatus()) {
                    case HttpRequest.ERROR_NETWORK_UNREACHABLE:
                        AppGlobals.alertDialog(getActivity(), "Registration Failed!", "please check your internet connection");
                        break;
                    case HttpURLConnection.HTTP_BAD_REQUEST:
                        AppGlobals.alertDialog(getActivity(), "Registration Failed!", "Email already in use");
                        break;
                    case HttpURLConnection.HTTP_CREATED:
                        System.out.println(request.getResponseText() + "working ");
                        Toast.makeText(getActivity(), "Activation code has been sent to you! Please check your Email", Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jsonObject = new JSONObject(request.getResponseText());
                            System.out.println(jsonObject + "working ");

                            String userId = jsonObject.getString(AppGlobals.KEY_USER_ID);
                            String email = jsonObject.getString(AppGlobals.KEY_EMAIL);

                            // saving values
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_EMAIL, email);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_USER_ID, userId);

                            FragmentManager fragmentManager = getFragmentManager();
                            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            AccountManager.getInstance().loadFragment(new AccountActivationCode());
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

    private void registerUser(String firstName, String lastName, String mobileNumber
            , String password, String email, String gender) {
        request = new HttpRequest(getActivity());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("POST", String.format("%sregister", AppGlobals.BASE_URL));
        request.send(getRegisterData(firstName, lastName, mobileNumber, password, email, gender));
        Helpers.showProgressDialog(getActivity(), "Pleas wait...");
    }


    private String getRegisterData(String firstName, String lastName, String mobileNumber,
                                   String password, String email, String gender) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("first_name", firstName);
            jsonObject.put("last_name", lastName);
            jsonObject.put("mobile_number", mobileNumber);
            jsonObject.put("email", email);
            jsonObject.put("gender", gender);
            jsonObject.put("password", password);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();

    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        if (checkedId == R.id.female) {
            mGenderString = "Female";
        } else if (checkedId == R.id.male) {
            mGenderString = "Male";
        }
    }
}
