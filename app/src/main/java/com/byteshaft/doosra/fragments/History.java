package com.byteshaft.doosra.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.byteshaft.doosra.OpinionDetail;
import com.byteshaft.doosra.R;
import com.byteshaft.doosra.gettersetter.OpinionHistory;
import com.byteshaft.doosra.utils.AppGlobals;
import com.byteshaft.requests.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class History extends Fragment {

    private View mBaseView;
    private ListView listView;
    private TextView tvHistory;
    private ArrayList<OpinionHistory> opinionHistoryArrayList;
    private HistoryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.fragment_history, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle(getResources().getString(R.string.history));
        listView = mBaseView.findViewById(R.id.list_opinion_history);
        tvHistory = mBaseView.findViewById(R.id.text_history);
        getOpinionHistory();
        opinionHistoryArrayList = new ArrayList<>();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OpinionHistory history = opinionHistoryArrayList.get(position);
                Intent intent = new Intent(getActivity(), OpinionDetail.class);

                intent.putExtra("full_name", history.getFullName());
                intent.putExtra("type_name", history.getOpiniontyp());

                intent.putExtra("medical_file", history.getMedicalFileUrl());
                intent.putExtra("lab_file", history.getLabResultImageUrl());
                intent.putExtra("xray_file", history.getXrayFileUrl());
                intent.putExtra("other_file", history.getXrayFileUrl());

                intent.putExtra("concern", history.getConcern());
                intent.putExtra("disease", history.getExistingDisease());
                intent.putExtra("short_history", history.getShortHistory());

                intent.putExtra("height", history.getHeigh());
                intent.putExtra("weight", history.getWeight());

                startActivity(intent);

            }
        });
        return mBaseView;
    }

    public void getOpinionHistory() {
        HttpRequest request = new HttpRequest(AppGlobals.getContext());
        request.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
            @Override
            public void onReadyStateChange(HttpRequest request, int readyState) {
                switch (readyState) {
                    case HttpRequest.STATE_DONE:
                        switch (request.getStatus()) {
                            case HttpURLConnection.HTTP_OK:
                                Log.i("History", request.getResponseText());
                                try {
                                    JSONArray jsonArray = new JSONArray(request.getResponseText());
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        System.out.println("Test " + jsonArray.getJSONObject(i));
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        OpinionHistory opinionHistory = new OpinionHistory();
                                        opinionHistory.setOpiniontyp(jsonObject.getString("type_name"));
                                        opinionHistory.setDateCreated(jsonObject.getString("date_created"));
                                        opinionHistory.setFullName(jsonObject.getString("full_name"));
                                        opinionHistory.setConcern(jsonObject.getString("concern"));
                                        opinionHistory.setExistingDisease(jsonObject.getString("existing_disease"));
                                        opinionHistory.setShortHistory(jsonObject.getString("short_history"));
                                        opinionHistory.setHeigh(jsonObject.getString("height"));
                                        opinionHistory.setWeight(jsonObject.getString("weight"));
                                        opinionHistory.setMedicalFileUrl(jsonObject.getString("medical_file"));
                                        opinionHistory.setLabResultImageUrl(jsonObject.getString("lab_result_file"));
                                        opinionHistory.setXrayFileUrl(jsonObject.getString("xray_file"));
                                        opinionHistory.setOtherFileUrl(jsonObject.getString("other_file"));
                                        opinionHistory.setId(jsonObject.getInt("id"));
                                        opinionHistoryArrayList.add(opinionHistory);

                                    }
                                    System.out.println("length is : " + jsonArray.length());
                                    if (jsonArray.length() == 0) {
                                        tvHistory.setVisibility(View.VISIBLE);
                                    } else {
                                        tvHistory.setVisibility(View.GONE);
                                    }
                                    adapter = new HistoryAdapter(opinionHistoryArrayList);
                                    listView.setAdapter(adapter);

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
        request.open("GET", String.format("%sopinion", AppGlobals.BASE_URL));
        request.setRequestHeader("Authorization", "Token " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        request.send();
    }


    /// adapter

    class HistoryAdapter extends BaseAdapter {

        private ViewHolder viewHolder;
        private ArrayList<OpinionHistory> opinionHistories;

        public HistoryAdapter(ArrayList<OpinionHistory> opinionHistories) {
            this.opinionHistories = opinionHistories;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(
                        R.layout.delegate_opinion_history, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.name = convertView.findViewById(R.id.tv_opinion);
                viewHolder.date = convertView.findViewById(R.id.date_created);
                viewHolder.concern = convertView.findViewById(R.id.concern_text);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            // get data from getter/setters
            OpinionHistory history = opinionHistories.get(position);
            viewHolder.name.setText(history.getOpiniontyp());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            SimpleDateFormat newFormate = new SimpleDateFormat("dd MMM yyyy");
            Date date = null;
            try {
                date = format.parse(history.getDateCreated());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            newFormate.format(date);
            viewHolder.date.setText(newFormate.format(date));
            viewHolder.concern.setText(history.getConcern());
            // set Typeface
            viewHolder.name.setTypeface(AppGlobals.typeface);
            viewHolder.date.setTypeface(AppGlobals.typeface);
            viewHolder.concern.setTypeface(AppGlobals.typeface);
            return convertView;
        }

        @Override
        public int getCount() {
            return opinionHistories.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }
    }

    // view holder

    class ViewHolder {
        private TextView name;
        private TextView date;
        private TextView concern;
    }

}
