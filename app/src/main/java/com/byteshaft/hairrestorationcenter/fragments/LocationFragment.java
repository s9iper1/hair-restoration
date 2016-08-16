package com.byteshaft.hairrestorationcenter.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class LocationFragment extends Fragment implements HttpRequest.OnReadyStateChangeListener {

    private RecyclerView mRecyclerView;
    private static LocationAdapter sAdapter;
    private View mBaseView;
    private HttpRequest mRequest;
    private CustomView mViewHolder;
    private ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.location_fragment, container, false);
        setHasOptionsMenu(true);
        mRecyclerView = (RecyclerView) mBaseView.findViewById(R.id.recycler_view_location);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.canScrollVertically(1);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        mProgressDialog = Helpers.getProgressDialog(getActivity());
        getLocationData();
        return mBaseView;
    }

    private void getLocationData() {
        mProgressDialog.show();
        mRequest = new HttpRequest(getActivity().getApplicationContext());
        mRequest.setOnReadyStateChangeListener(this);
        mRequest.open("GET", AppGlobals.LOCATIONS_URL);
        mRequest.send();
    }

    @Override
    public void onReadyStateChange(HttpRequest request, int i) {
        switch (i) {
            case HttpRequest.STATE_DONE:
                mProgressDialog.dismiss();
                switch (request.getStatus()) {
                    case HttpURLConnection.HTTP_OK:
                        sAdapter = new LocationAdapter(parseJson(mRequest.getResponseText()));
                        mRecyclerView.setAdapter(sAdapter);
                }
        }
    }


    private ArrayList<JSONObject> parseJson(String data) {
        ArrayList<JSONObject> dataList = new ArrayList<>();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(data);
            if (jsonObject.getString("Message").equals("Successfully")) {
                JSONArray jsonArray = jsonObject.getJSONArray("details");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);
                    dataList.add(json);
                }
            } else {
                AppGlobals.alertDialog(getActivity(), "Not Found", "Nothing found");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    // custom RecyclerView class for inflating customView
    class LocationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private ArrayList<JSONObject> data;

        public LocationAdapter(ArrayList<JSONObject> data) {
            this.data = data;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.delegate_location,
                    parent, false);
            mViewHolder = new CustomView(view);
            return mViewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            holder.setIsRecyclable(false);
            try {
                Picasso.with(getActivity())
                        .load("http:" + data.get(position).getString("photo").replaceAll("\"", ""))
                        .resize(900, 300)
                        .centerCrop()
                        .into(mViewHolder.locationImage);
                mViewHolder.locationTitle.setText(data.get(position).getString("title"));
                mViewHolder.addressText.setText(data.get(position).getString("address"));
                mViewHolder.phoneText.setText("Phone: " + data.get(position).getString("phone"));
                mViewHolder.tollFreeNumber.setText("Toll Free" + data.get(position).getString("toll_free"));

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
    private static class CustomView extends RecyclerView.ViewHolder {

        public ImageView locationImage;
        public TextView locationTitle;
        public TextView addressText;
        public TextView phoneText;
        public TextView tollFreeNumber;

        public CustomView(View itemView) {
            super(itemView);
            locationImage = (ImageView) itemView.findViewById(R.id.location_image);
            locationTitle = (TextView) itemView.findViewById(R.id.location_title);
            addressText = (TextView) itemView.findViewById(R.id.address);
            phoneText = (TextView) itemView.findViewById(R.id.phone_number_text_view);
            tollFreeNumber = (TextView) itemView.findViewById(R.id.toll_free_number);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.health_actionbar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.location_actionbar:
        }
        return true;
    }
}
