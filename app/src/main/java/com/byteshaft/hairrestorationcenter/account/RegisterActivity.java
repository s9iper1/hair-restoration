package com.byteshaft.hairrestorationcenter.account;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.hairrestorationcenter.R;
import com.byteshaft.hairrestorationcenter.utils.AppGlobals;
import com.byteshaft.hairrestorationcenter.utils.WebServiceHelpers;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mRegisterButton;
    private EditText mUsername;
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mEmailAddress;
    private EditText mZipCode;
    private EditText mPassword;
    private EditText mVerifyPassword;
    private EditText mPhoneNumber;
    private TextView mTermsAndCondition;
    private CheckBox mCheckBox;

    private String mUsernameString;
    private String mFirstNameString;
    private String mLastNameString;
    private String mEmailAddressString;
    private String mZipCodeString;
    private String mVerifyPasswordString;
    private String mPhoneNumberString;
    private String mPasswordString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        mTermsAndCondition = (TextView) findViewById(R.id.tv_terms_and_condition);
        mCheckBox = (CheckBox) findViewById(R.id.checkbox);
        mUsername = (EditText) findViewById(R.id.user_name);
        mFirstName = (EditText) findViewById(R.id.first_name);
        mLastName = (EditText) findViewById(R.id.last_name);
        mEmailAddress = (EditText) findViewById(R.id.email);
        mZipCode = (EditText) findViewById(R.id.zip_code);
        mPhoneNumber = (EditText) findViewById(R.id.phone);
        mVerifyPassword = (EditText) findViewById(R.id.verify_password);
        mPassword = (EditText) findViewById(R.id.password);
        mRegisterButton = (Button) findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(this);
        mTermsAndCondition.setOnClickListener(this);
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (compoundButton.isChecked()) {
                    mRegisterButton.setClickable(true);
                    mRegisterButton.setBackgroundColor(Color.parseColor("#05262F"));
                } else {
                    mRegisterButton.setClickable(false);
                    mRegisterButton.setBackgroundColor(Color.parseColor("#D3D3D3"));
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_button:
                mUsernameString = mUsername.getText().toString();
                mFirstNameString = mFirstName.getText().toString();
                mLastNameString = mLastName.getText().toString();
                mPhoneNumberString = mPhoneNumber.getText().toString();
                mZipCodeString = mZipCode.getText().toString();
                if (validateEditText()) {
                    new RegistrationTask().execute();
                }
                break;
            case R.id.tv_terms_and_condition:
                // missing 'http://' will cause crashed
                Uri uri = Uri.parse("http://www.affordablehairtransplants.com/terms-and-conditions");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
        }
    }

    private boolean validateEditText() {

        boolean valid = true;
        mPasswordString = mPassword.getText().toString();
        mVerifyPasswordString = mVerifyPassword.getText().toString();
        mEmailAddressString = mEmailAddress.getText().toString();


        if (mPasswordString.trim().isEmpty() || mPasswordString.length() < 3) {
            mPassword.setError("enter at least 3 characters");
            valid = false;
        } else {
            mPassword.setError(null);
        }

        if (mVerifyPasswordString.trim().isEmpty() || mVerifyPasswordString.length() < 3) {
            mVerifyPassword.setError("password does not match");
            valid = false;
        } else {
            mVerifyPassword.setError(null);
        }

        if (mEmailAddressString.trim().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(mEmailAddressString).matches()) {
            mEmailAddress.setError("please provide a valid email");
            valid = false;
        } else {
            mEmailAddress.setError(null);
        }
        return valid;
    }

    class RegistrationTask extends AsyncTask<String, String, String> {

        private boolean noInternet = false;
        private JSONObject jsonObject;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            WebServiceHelpers.showProgressDialog(RegisterActivity.this, "Registering");
        }

        @Override
        protected String doInBackground(String... strings) {

            if (WebServiceHelpers.isNetworkAvailable() && WebServiceHelpers.isInternetWorking()){

                try {
                    jsonObject = WebServiceHelpers.registerUser(
                            mFirstNameString,
                            mLastNameString,
                            mEmailAddressString,
                            mPhoneNumberString,
                            mVerifyPasswordString,
                            mPasswordString,
                            mUsernameString,
                            mZipCodeString);
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
                if (jsonObject.getString("Message").equals("Input is invalid;")) {
                    AppGlobals.alertDialog(RegisterActivity.this, "Registration Failed!", "username or email already exits");

                } else if (jsonObject.getString("Message").equals("Username or email already exits")) {
                    AppGlobals.alertDialog(RegisterActivity.this, "Already Exist!", jsonObject.getString("Message"));

                }else if (jsonObject.getString("Message").equals("Successfully")) {
                    JSONObject details = jsonObject.getJSONObject("details");
                    System.out.println(jsonObject + "working");
                    String username = details.getString(AppGlobals.KEY_USER_NAME);
                    String userId = details.getString(AppGlobals.KEY_USER_ID);
                    String firstName = details.getString(AppGlobals.KEY_FIRSTNAME);
                    String lastName = details.getString(AppGlobals.KEY_LASTNAME);
                    String email = details.getString(AppGlobals.KEY_EMAIL);
                    String phoneNumber = details.getString(AppGlobals.KEY_PHONE_NUMBER);
                    String zipCode = details.getString(AppGlobals.KEY_ZIP_CODE);

                    //saving values
                    AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_FIRSTNAME, firstName);
                    Log.i("First name", " " + AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_FIRSTNAME));
                    AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_LASTNAME, lastName);
                    AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_EMAIL, email);
                    AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_PHONE_NUMBER, phoneNumber);
                    AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_ZIP_CODE, zipCode);
                    AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_USER_ID, userId);
                    AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_USER_NAME, username);
                    AppGlobals.saveUserLogin(true);
                    LoginActivity.getInstance().finish();
                    finish();
                    Toast.makeText(RegisterActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.signup_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.health_information:
                Toast.makeText(RegisterActivity.this, "hello", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}
