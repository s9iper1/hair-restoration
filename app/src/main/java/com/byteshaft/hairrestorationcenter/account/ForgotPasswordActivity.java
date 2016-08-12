package com.byteshaft.hairrestorationcenter.account;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.byteshaft.hairrestorationcenter.R;
import com.byteshaft.hairrestorationcenter.fragments.EducationFragment;
import com.byteshaft.hairrestorationcenter.utils.AppGlobals;
import com.byteshaft.hairrestorationcenter.utils.WebServiceHelpers;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by husnain on 8/8/16.
 */
public class ForgotPasswordActivity extends AppCompatActivity {

    private Button mRecoverButton;
    private EditText mEmail;

    private String mEmailString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delegate_forgot_password_activity);
        mEmail = (EditText) findViewById(R.id.email_address);
        mRecoverButton = (Button) findViewById(R.id.recover);
        mRecoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    new ForgotPasswordTask().execute();
                }

            }
        });
    }

    public boolean validate() {
        boolean valid = true;
        mEmailString = mEmail.getText().toString();

        if (mEmailString.trim().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.
                matcher(mEmailString).matches()) {
            mEmail.setError("enter a valid email address");
            valid = false;
        }
        return valid;
    }

    class ForgotPasswordTask extends AsyncTask<String, String, String> {
        private JSONObject jsonObject;
        private boolean noInternet = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            WebServiceHelpers.showProgressDialog(ForgotPasswordActivity.this, "Sending Recovery Mail");
        }

        @Override
        protected String doInBackground(String... strings) {

            if (WebServiceHelpers.isNetworkAvailable() && WebServiceHelpers.isInternetWorking()) {

                try {
                    System.out.println(jsonObject == null);
                    jsonObject = WebServiceHelpers.forgotPassword(mEmailString);
                    Log.e("TAG", String.valueOf(jsonObject));
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            WebServiceHelpers.dismissProgressDialog();
            try {
                if (jsonObject.getString("Message").equals("Input is invalid")) {
                    AppGlobals.alertDialog(ForgotPasswordActivity.this, "Recovery Failed!", "User does not exist" );

                }else if (jsonObject.getString("Message").equals("Successfully")) {
                    Toast.makeText(ForgotPasswordActivity.this, "Please check your mail for new password", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
