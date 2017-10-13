package com.byteshaft.doosra;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.byteshaft.doosra.utils.AppGlobals;
import com.byteshaft.requests.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public class HowItWorks extends Activity {

    private TextView howItWorksText;
    private TextView tvTitle;
    private HttpRequest request;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_it_works);
        setTitle("How it Works");
        howItWorksText = findViewById(R.id.how_it_works_text);
        tvTitle = findViewById(R.id.title_how_it_works);
        howItWorksText.setTypeface(AppGlobals.typeface);
        tvTitle.setTypeface(AppGlobals.typeface);
        howItWorksText.setMovementMethod(new ScrollingMovementMethod());
        request = new HttpRequest(getApplicationContext());
        request.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
            @Override
            public void onReadyStateChange(HttpRequest request, int readyState) {
                switch (readyState) {
                    case HttpRequest.STATE_DONE:
                        switch (request.getStatus()) {
                            case HttpURLConnection.HTTP_OK:
                                try {
                                    JSONObject jsonObject = new JSONObject(request.getResponseText());
                                    String aboutUS = jsonObject.getString("message");
                                    howItWorksText.setText(aboutUS);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                        }
                }
            }
        });
        request.open("GET", String.format("%show-it-works", AppGlobals.BASE_URL));
        request.send();
    }
}
