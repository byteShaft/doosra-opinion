package com.byteshaft.doosra.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;




public class Helpers {

    private static ProgressDialog progressDialog;
    private static AlertDialog alertDialog;

    // get default sharedPreferences.
    private static SharedPreferences getPreferenceManager() {
        return PreferenceManager.getDefaultSharedPreferences(AppGlobals.getContext());
    }

    public static void showProgressDialog(Activity activity, String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage(message);
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }
    }

    public static void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

    }

    public static AlertDialog getAlertDialog() {
        return alertDialog;
    }

    public static void alertDialog(Activity activity, String title, String msg, final SwitchCompat compat) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(msg).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (compat != null) {
                    compat.setChecked(false);
                    compat.setEnabled(true);
                }
                dialog.dismiss();
            }
        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public static Bitmap getBitMapOfProfilePic(String selectedImagePath) {
        Bitmap bm;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, options);
        final int REQUIRED_SIZE = 100;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(selectedImagePath, options);
        return bm;
    }

    public static void showSnackBar(View view, int id) {
        Snackbar.make(view, AppGlobals.getContext().getResources()
                .getString(id), Snackbar.LENGTH_SHORT)
                .setActionTextColor(AppGlobals.getContext().getResources().getColor(android.R.color.holo_red_light))
                .show();
    }

    public static void showSnackBar(View view, String text) {
        Snackbar.make(view, text, Snackbar.LENGTH_LONG)
                .setActionTextColor(AppGlobals.getContext().getResources().getColor(android.R.color.holo_red_light))
                .show();
    }

    public static boolean locationEnabled() {
        LocationManager lm = (LocationManager) AppGlobals.getContext()
                .getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        return gps_enabled || network_enabled;
    }


    public static String getAge(int year, int month, int day) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        dob.set(year, month, day);
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }


    public static String getDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        return df.format(c.getTime());
    }

    public static String getTime24HourFormat() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        return df.format(c.getTime());
    }

    public static String getTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm aa");
        return df.format(c.getTime());
    }

    public static String getDateNextSevenDays() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, 7);
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        return df.format(c.getTime());
    }

    public static String calculateAge(String dateOfBirth) {
        String[] dob = dateOfBirth.split("/");
        int date = Integer.parseInt(dob[0]);
        int month = Integer.parseInt(dob[1]);
        int year = Integer.parseInt(dob[2]);
        String years = Helpers.getAge(year, month, date);
        return years;
    }

    public static String getFormattedTime(String startTime) {
        SimpleDateFormat formatterFrom = new SimpleDateFormat("hh:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
        Date rawDate = null;
        try {
            rawDate = formatterFrom.parse(startTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String formattedDate = dateFormat.format(rawDate);
        return formattedDate;
    }


    public static String getCurrentTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return df.format(c.getTime());
    }

    public static String getCurrentTimeAndDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm aa");
        return df.format(c.getTime());
    }

    public static String getDateForHeader() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy");
        return df.format(c.getTime());
    }

    public static String getDateForComparison() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, 1);
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy");
        return df.format(c.getTime());
    }

    public static String getPreviousDate() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, -1);
        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy");
        return df.format(c.getTime());
    }

    public static boolean getDifference(String startTime) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
        try {
            Date startDate = simpleDateFormat.parse(startTime);
            Date current = simpleDateFormat.parse(simpleDateFormat.format(c.getTime()));
            Log.i("TAG", "start time" + startTime);
            Log.i("TAG", "end time" + getTimeForCompare());
            Log.e("TAG", "before " + String.valueOf(startDate.after(current)));
            return startDate.after(current);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getTimeForCompare() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
        return df.format(c.getTime());
    }
}
