package com.byteshaft.hairrestorationcenter.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.byteshaft.hairrestorationcenter.R;
import com.byteshaft.hairrestorationcenter.utils.AppGlobals;
import com.byteshaft.hairrestorationcenter.utils.Helpers;
import com.byteshaft.hairrestorationcenter.utils.SimpleDividerItemDecoration;
import com.byteshaft.requests.HttpRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class EducationFragment extends Fragment implements HttpRequest.OnReadyStateChangeListener {

    private View mBaseView;
    private static EducationAdapter sAdapter;
    private RecyclerView mRecyclerView;
    private CustomView mViewHolder;
    private HttpRequest mRequest;
    private ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.fragment_education, container, false);
        mRecyclerView = (RecyclerView) mBaseView.findViewById(R.id.recycler_view_education);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.canScrollVertically(1);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        mProgressDialog = Helpers.getProgressDialog(getActivity());
        getEducationData();
        Log.i("KEY_USER_ID", AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_USER_ID));
        return mBaseView;
    }

    private void getEducationData() {
        mProgressDialog.show();
        mRequest = new HttpRequest(getActivity().getApplicationContext());
        mRequest.setOnReadyStateChangeListener(this);
        mRequest.open("GET", AppGlobals.EDUCATION_URL);
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
                            sAdapter = new EducationAdapter(parseJson(mRequest.getResponseText()));
                            mRecyclerView.setAdapter(sAdapter);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    private ArrayList<JSONObject> parseJson(String data) {
        ArrayList<JSONObject> dataList = new ArrayList<>();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(data);
            if (jsonObject.getString("Message").equals("Successfully")) {
                dataList.add(jsonObject.getJSONObject("details"));
            } else {
                AppGlobals.alertDialog(getActivity(), "Not Found", "Nothing found");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    // custom RecyclerView class for inflating customView
    class EducationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private ArrayList<JSONObject> data;

        public EducationAdapter(ArrayList<JSONObject> data)   {
            this.data = data;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.delegate_education,
                    parent, false);
            mViewHolder = new CustomView(view);
            return mViewHolder;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            holder.setIsRecyclable(false);
            try {
                mViewHolder.textViewOffers.setText(data.get(position).getString("title"));
                Log.i("Education","http:"+ data.get(position).getString("photo").replaceAll("\"", ""));
                Picasso.with(getActivity())
                        .load("http:"+data.get(position).getString("photo").replaceAll("\"", ""))
                        .resize(900, 300)
                        .centerCrop()
                        .into(mViewHolder.imageView);
                mViewHolder.textViewDescription.setText(data.get(position).getString("details"));
                mViewHolder.textViewDate.setText(data.get(position).getString("added_datetime"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    // custom class getting view data by giving view in constructor.
    public static class CustomView extends RecyclerView.ViewHolder {
        public TextView textViewOffers;
        public ImageView imageView;
        public TextView textViewDescription;
        public TextView textViewDate;

        public CustomView(View itemView) {
            super(itemView);
            textViewOffers = (TextView) itemView.findViewById(R.id.offers);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            textViewDescription = (TextView) itemView.findViewById(R.id.description);
            textViewDate = (TextView) itemView.findViewById(R.id.date);

        }
    }
}
