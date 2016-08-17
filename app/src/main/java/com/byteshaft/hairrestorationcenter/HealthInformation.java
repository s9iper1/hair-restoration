package com.byteshaft.hairrestorationcenter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.hairrestorationcenter.fragments.ConsultationFragment;
import com.byteshaft.hairrestorationcenter.fragments.EducationFragment;
import com.byteshaft.hairrestorationcenter.utils.AppGlobals;
import com.byteshaft.hairrestorationcenter.utils.Helpers;
import com.byteshaft.requests.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

public class HealthInformation extends Fragment implements
        HttpRequest.OnReadyStateChangeListener, View.OnClickListener {

    private Spinner gender;
    private EditText age;
    private ProgressDialog mProgressDialog;
    private HttpRequest mRequest;
    private ListView mListView;
    private ArrayList<JSONObject> fieldData;
    private ArrayList<Integer> idsArray;
    private HashMap<Integer, String> answersList;
    private Button submitButton;
    private StringBuilder stringBuilder = new StringBuilder();
    private ArrayList<String> requiredFields;
    private int idForGender = 2;
    private static boolean sPostRequest = false;
    private View mBaseView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.health_information, container, false);
        fieldData = new ArrayList<>();
        idsArray = new ArrayList<>();
        answersList = new HashMap<>();
        requiredFields = new ArrayList<>();
        age = (EditText) mBaseView.findViewById(R.id.age);
        gender = (Spinner) mBaseView.findViewById(R.id.gender);
        submitButton = (Button) mBaseView.findViewById(R.id.submit_answers);
        submitButton.setOnClickListener(this);
        mListView = (ListView) mBaseView.findViewById(R.id.fields_list_view);
        mProgressDialog = Helpers.getProgressDialog(getActivity());
        age.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {

                } else {
                    if (answersList.containsKey(age.getId())
                            && age.toString().trim().isEmpty()) {
                        answersList.remove(age.getId());
                    }
                    answersList.put(age.getId(), age.getText().toString());
                    Log.i("TAG", String.valueOf(answersList));
                }
            }
        });
        gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        getFieldsDetails();
        return mBaseView;
    }



    private void getFieldsDetails() {
        sPostRequest = false;
        mProgressDialog.show();
        mRequest = new HttpRequest(getActivity().getApplicationContext());
        mRequest.setOnReadyStateChangeListener(this);
        mRequest.open("GET", AppGlobals.QUESTION_LIST);
        mRequest.send();
    }

    @Override
    public void onReadyStateChange(HttpRequest request, int i) {
        switch (i) {
            case HttpRequest.STATE_DONE:
                mProgressDialog.dismiss();
                switch (request.getStatus()) {
                    case HttpURLConnection.HTTP_OK:
                        mProgressDialog.dismiss();
                        if (sPostRequest) {
                            Log.i("TAG", mRequest.getResponseText());
                            try {
                                JSONObject jsonObject = new JSONObject(mRequest.getResponseText());
                                if (jsonObject.getString("Message").equals("Successfully")) {

                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                                    alertDialogBuilder.setTitle("Success");
                                    alertDialogBuilder.setMessage("Your details have uploaded successfully.")
                                            .setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            AppGlobals.sConsultationSuccess = true;
                                            ConsultationFragment.sUploaded = false;
                                            dialog.dismiss();
                                            MainActivity.loadFragment(new EducationFragment());
                                        }
                                    });
                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.e("TAG", stringBuilder.toString());
                        } else {
                            parseJsonAndSetUi(mRequest.getResponseText());
                        }
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
                    Log.i("TAG", "Boolean " +json.getString("title").equals("Gender"));
                    if (!json.getString("title").equals("Age")) {
                        if (!json.getString("title").equals("Gender")) {
                            fieldData.add(json);
                            idsArray.add(json.getInt("id"));
                            if (json.getInt("required") == 1) {
                                requiredFields.add(String.valueOf(json.getInt("id")));
                                Log.i("REQUIRED", "fields" + requiredFields);
                            }
                        }
                    } else if (json.getString("title").equals("Age")) {
                        idsArray.add(json.getInt("id"));
                        age.setId(json.getInt("id"));
                        if (json.getInt("required") == 1) {
                            if (!requiredFields.contains(String.valueOf(json.getInt("id")))) {
                                requiredFields.add(String.valueOf(json.getInt("id")));
                                Log.e("TAG", "added age");
                            }
                        }
                    }
                    if (json.getString("title").equals("Gender")) {
                        Log.e("GENDER", " gender");
                        idForGender = json.getInt("id");
                        if (json.getInt("required") == 1) {
                            if (!requiredFields.contains(String.valueOf(json.getInt("id")))) {
                                requiredFields.add(String.valueOf(json.getInt("id")));
                                Log.e("TAG", "added gender");
                            }
                        }
                    }
                    Log.e("required fields ", "test "+ requiredFields);
                }
            } else {
                AppGlobals.alertDialog(getActivity(), "Not Found", "Nothing found");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("TAG", String.valueOf(fieldData));
        Adapter adapter = new Adapter(getActivity().getApplicationContext(), fieldData, R.layout.delegate_consultation_fields);
        mListView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit_answers:
                submitButton.requestFocus();
                if (AppGlobals.sEntryId == 0) {
                    Toast.makeText(getActivity(), "Please try again process failed",
                            Toast.LENGTH_SHORT).show();
                    MainActivity.loadFragment(new ConsultationFragment());
//                    finish();
                } else {
                    boolean result = validateEditText();
                    Log.i("boolean", " " + result);
                    if (result) {
                        mProgressDialog.show();
                        sendConsultationData(stringBuilder.toString());
                    }
                }
                break;
        }
    }

    class Adapter extends ArrayAdapter<JSONObject> {

        private ArrayList<JSONObject> fieldsDetail;

        public Adapter(Context context, ArrayList<JSONObject> fieldsDetail, int resource) {
            super(context, resource);
            this.fieldsDetail = fieldsDetail;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                convertView = inflater.inflate(R.layout.delegate_consultation_fields, parent, false);
                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.field_title);
                holder.editText = (EditText) convertView.findViewById(R.id.field_answer);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            try {
                SpannableStringBuilder title = new SpannableStringBuilder();
                if (fieldsDetail.get(position).getInt("required") == 1) {
                    String red = "* ";
                    SpannableString redSpannable= new SpannableString(red);
                    redSpannable.setSpan(new ForegroundColorSpan(Color.RED), 0, red.length(), 0);
                    title.append(redSpannable);
                    String white = fieldsDetail.get(position).getString("title");
                    SpannableString whiteSpannable= new SpannableString(white);
                    whiteSpannable.setSpan(new ForegroundColorSpan(Color.WHITE), 0, white.length(), 0);
                    title.append(whiteSpannable);
                } else {
                    String white = fieldsDetail.get(position).getString("title");
                    SpannableString whiteSpannable= new SpannableString(white);
                    whiteSpannable.setSpan(new ForegroundColorSpan(Color.WHITE), 0, white.length(), 0);
                    title.append(whiteSpannable);
                }
                holder.title.setText(title, TextView.BufferType.SPANNABLE);
                holder.editText.setId(fieldsDetail.get(position).getInt("id"));
                holder.editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if (b) {

                        } else {
                            try {
                                if (answersList.containsKey(fieldsDetail.get(position).getInt("id"))
                                        && holder.editText.toString().trim().isEmpty()) {
                                    answersList.remove(fieldsDetail.get(position).getInt("id"));
                                } else {
                                    if (!holder.editText.getText().toString().trim().isEmpty()) {
                                        answersList.put(fieldsDetail.get(position).getInt("id"), holder.editText.getText().toString());
                                    }
                                    }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

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
            Log.e("TAG", "" + fieldsDetail.size());
            return fieldsDetail.size();
        }
    }

    private boolean validateEditText() {
        stringBuilder = new StringBuilder();
        boolean value = false;
        Log.i("TAG", "array" + answersList.size());
        Log.i("TAG", "required fields" + requiredFields);
        for (int id : idsArray) {
            if (answersList.size() >= (requiredFields.size()-1)) {
                if (answersList.containsKey(id)) {
                    value = true;
                    stringBuilder.append(String.format("[%d]=%s&", id, answersList.get(id)));
                }
            } else if (answersList.size() < requiredFields.size()) {
                value = false;
                Toast.makeText(getActivity(), "All required fields must be filled", Toast.LENGTH_SHORT).show();
                break;
            } else {
                value = false;
                Toast.makeText(getActivity(), "All required fields must be filled", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        stringBuilder.append(String.format("user_id=%s&", AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_USER_ID)));
        stringBuilder.append(String.format("entry_id=%s&", AppGlobals.sEntryId));
        stringBuilder.append(String.format("[%d]=%s", idForGender, gender.getSelectedItem().toString()));
        Log.i("String", stringBuilder.toString());
        return value;
    }

    class ViewHolder {
        public TextView title;
        public EditText editText;
    }

    private void sendConsultationData(String data) {
        sPostRequest = true;
        mRequest = new HttpRequest(getActivity().getApplicationContext());
        mRequest.setOnReadyStateChangeListener(this);
        mRequest.open("POST", AppGlobals.CONSULTATION_STEP_2);
        mRequest.send(data);
    }
}
