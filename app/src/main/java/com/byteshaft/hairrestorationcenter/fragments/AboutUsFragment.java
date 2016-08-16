package com.byteshaft.hairrestorationcenter.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.byteshaft.hairrestorationcenter.R;
import com.byteshaft.hairrestorationcenter.utils.WebServiceHelpers;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class AboutUsFragment extends Fragment {

    private View mBaseView;
    private TextView mAboutUsTextView;
    private String aboutUs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.aboutus_fragment, container, false);
        setHasOptionsMenu(true);
        mAboutUsTextView = (TextView) mBaseView.findViewById(R.id.textview_about_us);
        new AboutUsTask().execute();
        return mBaseView;
    }

    private class AboutUsTask extends AsyncTask<String, String, String> {

        private JSONObject jsonObject;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            if (WebServiceHelpers.isNetworkAvailable() && WebServiceHelpers.isInternetWorking()){
                try {
                    jsonObject = WebServiceHelpers.aboutUs();
                    if (jsonObject.getString("Message").equals("Successfully")) {
                        JSONObject data = jsonObject.getJSONObject("details");
                        aboutUs = data.getString("aboutus");
                    }
                    Log.e("TAG", String.valueOf(aboutUs));
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mAboutUsTextView.setText(aboutUs);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.aboutus, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about_us_actionbar:
        }
        return true;
    }
}
