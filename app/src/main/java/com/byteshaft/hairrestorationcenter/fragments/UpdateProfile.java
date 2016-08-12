package com.byteshaft.hairrestorationcenter.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.byteshaft.hairrestorationcenter.MainActivity;
import com.byteshaft.hairrestorationcenter.R;
import com.byteshaft.hairrestorationcenter.utils.AppGlobals;
import com.byteshaft.hairrestorationcenter.utils.WebServiceHelpers;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by husnain on 8/9/16.
 */
public class UpdateProfile extends Fragment {

    private View mBaseView;

    private Button mUpdateButton;
    private EditText mUsername;
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mEmailAddress;
    private EditText mZipCode;
    private EditText mPhoneNumber;

    private String mUsernameString;
    private String mFirstNameString;
    private String mLastNameString;
    private String mEmailAddressString;
    private String mZipCodeString;
    private String mUserIdString = AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_USER_ID);
    private String mPhoneNumberString;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.delegate_update_profile, container, false);
        mUsername = (EditText) mBaseView.findViewById(R.id.user_name);
        mFirstName = (EditText) mBaseView.findViewById(R.id.first_name);
        mLastName = (EditText) mBaseView.findViewById(R.id.last_name);
        mEmailAddress = (EditText) mBaseView.findViewById(R.id.email);
        mZipCode = (EditText) mBaseView.findViewById(R.id.zip_code);
        mPhoneNumber = (EditText) mBaseView.findViewById(R.id.phone);
        mUpdateButton = (Button) mBaseView.findViewById(R.id.update_button);
        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUsernameString = mUsername.getText().toString();
                mFirstNameString = mFirstName.getText().toString();
                mLastNameString = mLastName.getText().toString();
                mZipCodeString = mZipCode.getText().toString();
                mEmailAddressString = mEmailAddress.getText().toString();
                mPhoneNumberString = mPhoneNumber.getText().toString();
                new UpdateUserProfileTask().execute();

            }
        });
        return mBaseView;
    }

    class UpdateUserProfileTask extends AsyncTask<String, String, String> {
        private JSONObject jsonObject;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            WebServiceHelpers.showProgressDialog(getActivity(), "Updating User Profile");
        }

        @Override
        protected String doInBackground(String... strings) {

            if (WebServiceHelpers.isNetworkAvailable() && WebServiceHelpers.isInternetWorking()){

                try {
                    jsonObject = WebServiceHelpers.updateUserProfile(
                            mFirstNameString,
                            mLastNameString,
                            mEmailAddressString,
                            mPhoneNumberString,
                            mUserIdString,
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
                    AppGlobals.alertDialog(getActivity(), "Registration Failed!", "username or email already exits");

                } else if (jsonObject.getString("Message").equals("Successfully")) {
                    System.out.println(jsonObject + "working");
                    getActivity().finish();
                    startActivity(new Intent(AppGlobals.getContext(), MainActivity.class));
                    Toast.makeText(getActivity(), "Account Created Successfully", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
