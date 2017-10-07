package com.byteshaft.doosra;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.byteshaft.doosra.accounts.AccountManager;
import com.byteshaft.doosra.utils.AppGlobals;

public class MainActivity extends Activity implements View.OnClickListener {

    private Button buttonHowItWorks;
    private Button buttonAboutUs;
    private Button buttonBoD;
    private Button buttonGetSecOpinion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AccountManager.getInstance() != null) {
            AccountManager.getInstance().finish();
        }
        setContentView(R.layout.activity_main);
        buttonHowItWorks = findViewById(R.id.button_how_it_works);
        buttonAboutUs = findViewById(R.id.button_about_us);
        buttonBoD = findViewById(R.id.button_board_of_doctors);
        buttonGetSecOpinion = findViewById(R.id.button_get_a_second_opinion);

        buttonHowItWorks.setTypeface(AppGlobals.typeface);
        buttonAboutUs.setTypeface(AppGlobals.typeface);
        buttonBoD.setTypeface(AppGlobals.typeface);
        buttonGetSecOpinion.setTypeface(AppGlobals.typeface);

        buttonHowItWorks.setOnClickListener(this);
        buttonAboutUs.setOnClickListener(this);
        buttonBoD.setOnClickListener(this);
        buttonGetSecOpinion.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_how_it_works:
                System.out.println("how it works");
                break;
            case R.id.button_about_us:
                System.out.println("About Us");
                break;
            case R.id.button_board_of_doctors:
                break;
            case R.id.button_get_a_second_opinion:
                startActivity(new Intent(MainActivity.this, OpinionActivity.class));
                break;
        }
    }
}
