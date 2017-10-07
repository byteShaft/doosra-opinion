package com.byteshaft.doosra;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.byteshaft.doosra.accounts.AccountManager;
import com.byteshaft.doosra.utils.AppGlobals;

public class SplashScreen extends Activity {

    private TextView logo;
    private TextView des;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        logo = findViewById(R.id.app_name);
        des = findViewById(R.id.app_name_des);

        logo.setTypeface(AppGlobals.typeface);
        des.setTypeface(AppGlobals.typeface);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashScreen.this, AccountManager.class);
                startActivity(mainIntent);
                finish();
            }
        }, 3000);
    }
}
