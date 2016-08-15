package com.byteshaft.hairrestorationcenter;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.byteshaft.hairrestorationcenter.utils.AppGlobals;
import com.byteshaft.hairrestorationcenter.utils.Helpers;
import com.byteshaft.requests.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class HealthInformation extends AppCompatActivity implements
        HttpRequest.OnReadyStateChangeListener {

    private EditText gender;
    private EditText age;
    private ProgressDialog mProgressDialog;
    private HttpRequest mRequest;
    private ListView mListView;
    private ArrayList<JSONObject> fieldData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.health_information);
        fieldData = new ArrayList<>();
        age = (EditText) findViewById(R.id.age);
        gender = (EditText) findViewById(R.id.gender);
        mListView = (ListView) findViewById(R.id.fields_list_view);
        mProgressDialog = Helpers.getProgressDialog(HealthInformation.this);
        getFieldsDetails();
    }

    private void getFieldsDetails() {
        mProgressDialog.show();
        mRequest = new HttpRequest(getApplicationContext());
        mRequest.setOnReadyStateChangeListener(this);
        mRequest.open("GET", AppGlobals.QUESTION_LIST);
        mRequest.send();
    }

    @Override
    public void onReadyStateChange(HttpURLConnection httpURLConnection, int i) {
        switch (i) {
            case HttpRequest.STATE_DONE:
                mProgressDialog.dismiss();
                try {
                    switch (httpURLConnection.getResponseCode()) {
                        case HttpURLConnection.HTTP_OK:
                            parseJsonAndSetUi(mRequest.getResponseText());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

    }

    private void parseJsonAndSetUi(String data) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(data);
            if (jsonObject.getString("Message").equals("Successfully")) {
                JSONArray jsonArray = jsonObject.getJSONArray("details");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);
                    if (!json.getString("title").equals("Age")) {
                        if (!json.getString("title").equals("Gender")) {
                            fieldData.add(json);
                        }
                    } else if (json.getString("title").equals("Age")) {
//                        age.setId(json.getInt("id"));
                    } else if (json.getString("title").equals("Gender")) {
//                        gender.setId(json.getInt("id"));
                    }
                }
                Adapter adapter = new Adapter(getApplicationContext(), fieldData, R.layout.delegate_consultation_fields);
                mListView.setAdapter(adapter);
            } else {
                AppGlobals.alertDialog(HealthInformation.this, "Not Found", "Nothing found");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class Adapter extends ArrayAdapter {

        private ArrayList<JSONObject> fieldsDetail;

        public Adapter(Context context, ArrayList<JSONObject> fieldsDetail, int resource) {
            super(context, resource);
            this.fieldsDetail = fieldsDetail;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(R.layout.delegate_consultation_fields, parent, false);
                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.field_title);
                holder.editText = (EditText) convertView.findViewById(R.id.field_answer);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            try {
                holder.title.setText(fieldsDetail.get(position).getString("title"));
                holder.editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        try {
                            Log.i("TAG", String.valueOf(fieldsDetail.get(position).getInt("id")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return convertView;
        }

        @Override
        public int getCount() {
            return fieldsDetail.size();

        }
    }

    class ViewHolder {
        public TextView title;
        public EditText editText;
    }

}
