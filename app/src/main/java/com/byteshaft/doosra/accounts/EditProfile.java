package com.byteshaft.doosra.accounts;

import android.Manifest;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.byteshaft.doosra.MainActivity;
import com.byteshaft.doosra.R;
import com.byteshaft.doosra.fragments.Dashboard;
import com.byteshaft.doosra.utils.AppGlobals;
import com.byteshaft.doosra.utils.Helpers;
import com.byteshaft.doosra.utils.RotateUtil;
import com.byteshaft.requests.FormData;
import com.byteshaft.requests.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class EditProfile extends Fragment implements View.OnClickListener,
        HttpRequest.OnReadyStateChangeListener, HttpRequest.OnErrorListener, RadioGroup.OnCheckedChangeListener {

    private View mBaseView;
    private CircleImageView mProfilePicture;
    private EditText mEmail;

    private EditText mCountry;
    private EditText mUserAge;
    private EditText mWeight;
    private EditText mUserHeight;

    private EditText mFirstName;
    private EditText mLastName;
    private EditText mPhoneNumber;

    private Button mUpdateButton;

    private String mWeightString;
    private String mUserheightString;
    private String mCountryString;
    private String mUserAgeString;

    private String firstNameString;
    private String lastNameString;
    private String mPhoneNumberString;

    private String mEmailAddressString;
    private String mGenderString = "Male";
    private HttpRequest request;
    private RadioGroup mGenderRadioGroup;
    private RadioButton mMale;
    private RadioButton mFemale;

    private static final int STORAGE_CAMERA_PERMISSION = 1;
    private static final int SELECT_FILE = 2;
    private static final int REQUEST_CAMERA = 3;

    private File destination;
    private Uri selectedImageUri;
    private static String imageUrl = "";
    private Bitmap profilePic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.edit_profile, container, false);
        setHasOptionsMenu(true);
        mProfilePicture = mBaseView.findViewById(R.id.profile_image);
        mFirstName = mBaseView.findViewById(R.id.first_name_edit_text);
        mLastName = mBaseView.findViewById(R.id.last_name_edit_text);
        mPhoneNumber = mBaseView.findViewById(R.id.phone_number_edit_text);

        mCountry = mBaseView.findViewById(R.id.country_edit_text);
        mUserAge = mBaseView.findViewById(R.id.age_edit_text);
        mWeight = mBaseView.findViewById(R.id.weight_edit_text);
        mUserHeight = mBaseView.findViewById(R.id.height_edit_text);

        mEmail = mBaseView.findViewById(R.id.email_edit_text);
        mUpdateButton = mBaseView.findViewById(R.id.update_button);

        mGenderRadioGroup = mBaseView.findViewById(R.id.gender_group);
        mMale = mBaseView.findViewById(R.id.male);
        mFemale = mBaseView.findViewById(R.id.female);

        // set typeface
        mFirstName.setTypeface(AppGlobals.typeface);
        mLastName.setTypeface(AppGlobals.typeface);
        mPhoneNumber.setTypeface(AppGlobals.typeface);
        mEmail.setTypeface(AppGlobals.typeface);

        mUpdateButton.setTypeface(AppGlobals.typeface);
        mMale.setTypeface(AppGlobals.typeface);

        mCountry.setTypeface(AppGlobals.typeface);
        mUserAge.setTypeface(AppGlobals.typeface);
        mWeight.setTypeface(AppGlobals.typeface);
        mUserHeight.setTypeface(AppGlobals.typeface);
        mFemale.setTypeface(AppGlobals.typeface);

        if (AppGlobals.isLogin()) {
            ((AppCompatActivity) getActivity()).getSupportActionBar()
                    .setTitle(getResources().getString(R.string.update_profile));
            String url = AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_SERVER_IMAGE);
            Helpers.getBitMap(url, mProfilePicture);
            mCountry.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_COUNTRY));
            mUserAge.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_USER_AGE));
            mWeight.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_WEIGHT));
            mUserHeight.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_HEIGHT));

            mFirstName.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_FIRST_NAME));
            mLastName.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_LAST_NAME));
            mEmail.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_EMAIL));
            mEmail.setEnabled(false);
            mPhoneNumber.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_MOBILE_NUMBER));
            if (AppGlobals.getGender().equals("Male")) {
                mGenderRadioGroup.check(R.id.male);
            } else {
                mGenderRadioGroup.check(R.id.female);
            }

        }
        mUpdateButton.setOnClickListener(this);
        mGenderRadioGroup.setOnCheckedChangeListener(this);
        mProfilePicture.setOnClickListener(this);
        return mBaseView;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.update_button:
                firstNameString = mFirstName.getText().toString();
                lastNameString = mLastName.getText().toString();
                mPhoneNumberString = mPhoneNumber.getText().toString();
                mEmailAddressString = mEmail.getText().toString();

                mCountryString = mCountry.getText().toString();
                mUserAgeString = mUserAge.getText().toString();
                mUserheightString = mUserHeight.getText().toString();
                mWeightString = mWeight.getText().toString();

                updateUserProfile(imageUrl, firstNameString, lastNameString, mPhoneNumberString,
                        mUserAgeString, mCountryString, mUserheightString, mWeightString, mGenderString);
                break;
            case R.id.profile_image:
                checkPermissions();
                break;
        }

    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                Helpers.dismissProgressDialog();
                Log.i("TAG", "Response " + request.getResponseText());
                switch (request.getStatus()) {
                    case HttpRequest.ERROR_NETWORK_UNREACHABLE:
                        AppGlobals.alertDialog(getActivity(), "Update Failed!", "please check your internet connection");
                        break;
                    case HttpURLConnection.HTTP_OK:
                        System.out.println(request.getResponseText() + "working ");
                        Toast.makeText(getActivity(), "Your profile has been updated", Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jsonObject = new JSONObject(request.getResponseText());

                            String userId = jsonObject.getString(AppGlobals.KEY_USER_ID);
                            String email = jsonObject.getString(AppGlobals.KEY_EMAIL);
                            String profileImage = jsonObject.getString(AppGlobals.KEY_SERVER_IMAGE);
                            String firstName = jsonObject.getString(AppGlobals.KEY_FIRST_NAME);
                            String lastName = jsonObject.getString(AppGlobals.KEY_LAST_NAME);

                            String phoneNumber = jsonObject.getString(AppGlobals.KEY_MOBILE_NUMBER);
                            String userAge = jsonObject.getString(AppGlobals.KEY_USER_AGE);
                            String userCountry = jsonObject.getString(AppGlobals.KEY_COUNTRY);
                            String userWeight = jsonObject.getString(AppGlobals.KEY_WEIGHT);
                            String userHeight = jsonObject.getString(AppGlobals.KEY_HEIGHT);

                            // saving values
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_EMAIL, email);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_FIRST_NAME, firstName);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_LAST_NAME, lastName);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_USER_ID, userId);

                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_WEIGHT, userWeight);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_HEIGHT, userHeight);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_MOBILE_NUMBER, phoneNumber);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_USER_AGE, userAge);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_COUNTRY, userCountry);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_SERVER_IMAGE, profileImage);

                            FragmentManager fragmentManager = getFragmentManager();
                            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            MainActivity.getInstance().loadFragment(new Dashboard());
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

    private void updateUserProfile(String profilePicture, String firstName, String lastName, String mobileNumber,
                                   String dob, String country, String height, String weight, String gender) {
        request = new HttpRequest(getActivity());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("PUT", String.format("%sme", AppGlobals.BASE_URL));
        request.setRequestHeader("Authorization", "Token " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        request.send(getProfileData(profilePicture, firstName, lastName, mobileNumber, dob, country,
                height, weight, gender));
        Helpers.showProgressDialog(getActivity(), "Updating Profile...");

    }

    private FormData getProfileData(String profilePicture, String firstName, String lastName, String mobileNumber,
                                    String dob, String country, String height, String weight, String gender) {
        FormData formData = new FormData();
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            formData.append(FormData.TYPE_CONTENT_FILE, "photo", profilePicture);
        }
        formData.append(FormData.TYPE_CONTENT_TEXT, "first_name", firstName);
        formData.append(FormData.TYPE_CONTENT_TEXT, "last_name", lastName);
        formData.append(FormData.TYPE_CONTENT_TEXT, "mobile_number", mobileNumber);
        formData.append(FormData.TYPE_CONTENT_TEXT, "date_of_birth", dob);
        formData.append(FormData.TYPE_CONTENT_TEXT, "country", country);
        formData.append(FormData.TYPE_CONTENT_TEXT, "gender", gender);
        formData.append(FormData.TYPE_CONTENT_TEXT, "height", height);
        formData.append(FormData.TYPE_CONTENT_TEXT, "weight", weight);

        return formData;

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case STORAGE_CAMERA_PERMISSION:
                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        // process the normal flow
                        //else any one or both the permissions are not granted
                    } else {
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) ||
                                    shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                                showDialogOK(getString(R.string.camera_storage_permission),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which) {
                                                    case DialogInterface.BUTTON_POSITIVE:
                                                        checkPermissions();
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
                            else {
                                Toast.makeText(getActivity(), R.string.go_settings_permission, Toast.LENGTH_LONG)
                                        .show();
                                //                            //proceed with logic by disabling the related features or quit the
                                Helpers.showSnackBar(getView(), R.string.permission_denied);
                            }
                        }
                        break;

                    }

                }
        }
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton(R.string.ok_button, okListener)
                .setNegativeButton(R.string.cancel, okListener)
                .create()
                .show();
    }

    public void checkPermissions() {
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(
                    new String[listPermissionsNeeded.size()]), STORAGE_CAMERA_PERMISSION);
        }

        if (listPermissionsNeeded.size() > 0) {
            System.out.println("working image");
            selectImage();
        }
    }

    private void selectImage() {
        final CharSequence[] items = {getString(R.string.take_photo), getString(R.string.choose_library), getString(R.string.cancel_photo)};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.select_photo);
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.take_photo))) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals(getString(R.string.choose_library))) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, getString(R.string.select_file)),
                            SELECT_FILE);
                } else if (items[item].equals(getString(R.string.photo_cancel))) {
                    dialog.dismiss();
                }

            }
        });
        System.out.println("show");
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");
                imageUrl = destination.getAbsolutePath();
                FileOutputStream fileOutputStream;
                try {
                    destination.createNewFile();
                    fileOutputStream = new FileOutputStream(destination);
                    fileOutputStream.write(bytes.toByteArray());
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                profilePic = Helpers.getBitMapOfProfilePic(destination.getAbsolutePath());
                Bitmap orientedBitmap = RotateUtil.rotateBitmap(destination.getAbsolutePath(), profilePic);
                mProfilePicture.setImageBitmap(orientedBitmap);
            } else if (requestCode == SELECT_FILE) {
                selectedImageUri = data.getData();
                String[] projection = {MediaStore.MediaColumns.DATA};
                CursorLoader cursorLoader = new CursorLoader(getActivity(),
                        selectedImageUri, projection, null, null,
                        null);
                Cursor cursor = cursorLoader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                String selectedImagePath = cursor.getString(column_index);
                profilePic = Helpers.getBitMapOfProfilePic(selectedImagePath);
                Bitmap orientedBitmap = RotateUtil.rotateBitmap(selectedImagePath, profilePic);
                mProfilePicture.setImageBitmap(orientedBitmap);
                imageUrl = String.valueOf(selectedImagePath);
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        if (checkedId == R.id.female) {
            mGenderString = "Female";
        } else if (checkedId == R.id.male) {
            mGenderString = "Male";
        }
        AppGlobals.saveGender(mGenderString);
    }
}
