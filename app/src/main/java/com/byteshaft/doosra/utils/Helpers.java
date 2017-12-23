package com.byteshaft.doosra.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.ImageView;

import com.byteshaft.doosra.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.byteshaft.doosra.utils.AppGlobals.sImageLoader;


public class Helpers {

    private static ProgressDialog progressDialog;
    private static AlertDialog alertDialog;

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

    public static void getBitMap(String url, CircleImageView circleImageView) {
        if (url.length() > 31) {
            ImageLoadingListener animateFirstListener;
            DisplayImageOptions options;
            options = new DisplayImageOptions.Builder()
                    .showImageOnFail(R.mipmap.ic_launcher)
                    .showImageOnLoading(R.mipmap.ic_launcher)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                    .cacheInMemory(false)
                    .cacheOnDisc(false).considerExifParams(true).build();
            animateFirstListener = new AnimateFirstDisplayListener();
            sImageLoader.displayImage(url, circleImageView, options, animateFirstListener);
        } else {
            circleImageView.setImageResource(R.mipmap.ic_launcher);
        }
    }

    private static class AnimateFirstDisplayListener extends
            SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections
                .synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view,
                                      Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                FadeInBitmapDisplayer.animate(imageView, 500);
                displayedImages.add(imageUri);
            }
        }
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



    public static String getTimeForCompare() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
        return df.format(c.getTime());
    }
}
