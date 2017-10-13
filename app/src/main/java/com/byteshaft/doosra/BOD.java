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

public class BOD extends Activity {
    private TextView bodText;
    private TextView tvBOD;
    private HttpRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bod);
        bodText = findViewById(R.id.bod_text);
        tvBOD = (findViewById(R.id.title_bod));

        bodText.setTypeface(AppGlobals.typeface);
        tvBOD.setTypeface(AppGlobals.typeface);

        bodText.setMovementMethod(new ScrollingMovementMethod());
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
                                    bodText.setText(aboutUS);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                        }
                }
            }
        });
        request.open("GET", String.format("%sboard-of-doctors", AppGlobals.BASE_URL));
        request.send();
    }
}
