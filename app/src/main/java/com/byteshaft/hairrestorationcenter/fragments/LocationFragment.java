package com.byteshaft.hairrestorationcenter.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.byteshaft.hairrestorationcenter.R;
import com.byteshaft.hairrestorationcenter.utils.AnimatedExpandableListView;
import com.byteshaft.hairrestorationcenter.utils.AppGlobals;
import com.byteshaft.hairrestorationcenter.utils.Helpers;
import com.byteshaft.requests.HttpRequest;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class LocationFragment extends Fragment implements
        HttpRequest.OnReadyStateChangeListener {

    private AnimatedExpandableListView mExpandableListView;
    private View mBaseView;
    private HttpRequest mRequest;
    private ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.location_fragment, container, false);
        setHasOptionsMenu(true);
        mExpandableListView = (AnimatedExpandableListView) mBaseView.findViewById(R.id.list_view_locations);
        handleCollapseAndExpand();
        mProgressDialog = Helpers.getProgressDialog(getActivity());
        getLocationData();
        return mBaseView;
    }

    private void handleCollapseAndExpand() {
        mExpandableListView.setOnGroupExpandListener(
                new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int i) {

            }
        });
        mExpandableListView.setOnGroupCollapseListener(
                new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int i) {

            }
        });
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
                        ExpandableListAdapter adapter = new ExpandableLocationsAdapter(
                                getContext().getApplicationContext(),
                                parseJson(mRequest.getResponseText())
                        );
                        mExpandableListView.setAdapter(adapter);
                        mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

                            @Override
                            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                                // We call collapseGroupWithAnimation(int) and
                                // expandGroupWithAnimation(int) to animate group
                                // expansion/collapse.
                                if (mExpandableListView.isGroupExpanded(groupPosition)) {
                                    mExpandableListView.collapseGroupWithAnimation(groupPosition);
                                } else {
                                    mExpandableListView.expandGroupWithAnimation(groupPosition);
                                }
                                return true;
                            }

                        });
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

    static class ViewHolder {
        TextView headerTextView;
        ImageView collapseExpandIndicator;
    }

    static class SubItemsViewHolder {
        ImageView bannerImageView;
        TextView addressTextView;
        TextView phoneNumberTextView;
        TextView tollFreeTextView;
        ProgressBar progressBar;
    }

    class ExpandableLocationsAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {

        private ArrayList<JSONObject> mItems;
        private Context mContext;

        public ExpandableLocationsAdapter(Context context, ArrayList<JSONObject> items) {
            mContext = context;
            mItems = items;
        }

        @Override
        public int getGroupCount() {
            return mItems.size();
        }

//        @Override
//        public int getChildrenCount(int i) {
//            // Each header is supposed to have only one sub item.
//            return 1;
//        }

        @Override
        public Object getGroup(int i) {
            String groupName = null;
            try {
                groupName =  mItems.get(i).getString("title");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return groupName;
        }

        @Override
        public Object getChild(int i, int i1) {
            return null;
        }

        @Override
        public long getGroupId(int i) {
            return 0;
        }

        @Override
        public long getChildId(int i, int i1) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.delegate_location_header, null);
                holder = new ViewHolder();
                holder.headerTextView = (TextView) view.findViewById(
                        R.id.text_view_location_header);
                holder.collapseExpandIndicator = (ImageView) view.findViewById(
                        R.id.image_view_location_expand_collapse);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            String headerTitle = (String) getGroup(i);
            holder.headerTextView.setText(headerTitle);
            if (b) {
                holder.collapseExpandIndicator.setImageResource(R.mipmap.ic_collapse);
            } else {
                holder.collapseExpandIndicator.setImageResource(R.mipmap.ic_expand);
            }
            return view;
        }

        @Override
        public View getRealChildView(int i, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final SubItemsViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.delegate_location, null);
                holder = new SubItemsViewHolder();
                holder.bannerImageView = (ImageView) convertView.findViewById(R.id.location_image);
                holder.addressTextView = (TextView) convertView.findViewById(R.id.address);
                holder.phoneNumberTextView = (TextView) convertView.findViewById(
                        R.id.phone_number_text_view);
                holder.tollFreeTextView = (TextView) convertView.findViewById(R.id.toll_free_number);
                holder.progressBar = (ProgressBar) convertView.findViewById(
                        R.id.location_image_loading_progress_bar);
                convertView.setTag(holder);
            } else {
                holder = (SubItemsViewHolder) convertView.getTag();
            }
            try {
                holder.progressBar.setVisibility(View.VISIBLE);
                Picasso picasso = Picasso.with(getActivity());
                String url = "http:" + mItems.get(i).getString("photo").replaceAll(
                        "\"", "").replaceAll(" ", "%20");
                picasso.load(url).resize(900, 300).centerCrop().into(
                        holder.bannerImageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                holder.progressBar.setVisibility(View.GONE);
                            }
                        }
                );
                holder.addressTextView.setText(mItems.get(i).getString("address"));
                holder.phoneNumberTextView.setText(null);
                holder.phoneNumberTextView.append(getPhoneTitle("Phone: "));
                holder.phoneNumberTextView.append(mItems.get(i).getString("phone"));
                holder.tollFreeTextView.setText(null);
                holder.tollFreeTextView.append(getPhoneTitle("Toll Free: "));
                holder.tollFreeTextView.append(mItems.get(i).getString("toll_free"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return convertView;
        }

        @Override
        public int getRealChildrenCount(int groupPosition) {
            return 1;
        }

//        @Override
//        public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
//            final SubItemsViewHolder holder;
//            if (view == null) {
//                LayoutInflater inflater = (LayoutInflater) mContext
//                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                view = inflater.inflate(R.layout.delegate_location, null);
//                holder = new SubItemsViewHolder();
//                holder.bannerImageView = (ImageView) view.findViewById(R.id.location_image);
//                holder.addressTextView = (TextView) view.findViewById(R.id.address);
//                holder.phoneNumberTextView = (TextView) view.findViewById(
//                        R.id.phone_number_text_view);
//                holder.tollFreeTextView = (TextView) view.findViewById(R.id.toll_free_number);
//                holder.progressBar = (ProgressBar) view.findViewById(
//                        R.id.location_image_loading_progress_bar);
//                view.setTag(holder);
//            } else {
//                holder = (SubItemsViewHolder) view.getTag();
//            }
//            try {
//                holder.progressBar.setVisibility(View.VISIBLE);
//                Picasso picasso = Picasso.with(getActivity());
//                String url = "http:" + mItems.get(i).getString("photo").replaceAll(
//                        "\"", "").replaceAll(" ", "%20");
//                picasso.load(url).resize(900, 300).centerCrop().into(
//                        holder.bannerImageView, new Callback() {
//                            @Override
//                            public void onSuccess() {
//                                holder.progressBar.setVisibility(View.GONE);
//                            }
//
//                            @Override
//                            public void onError() {
//                                holder.progressBar.setVisibility(View.GONE);
//                            }
//                        }
//                );
//                holder.addressTextView.setText(mItems.get(i).getString("address"));
//                holder.phoneNumberTextView.setText(null);
//                holder.phoneNumberTextView.append(getPhoneTitle("Phone: "));
//                holder.phoneNumberTextView.append(mItems.get(i).getString("phone"));
//                holder.tollFreeTextView.setText(null);
//                holder.tollFreeTextView.append(getPhoneTitle("Toll Free: "));
//                holder.tollFreeTextView.append(mItems.get(i).getString("toll_free"));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            return view;
//        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return false;
        }
    }

    private SpannableString getPhoneTitle(String title) {
        SpannableString ss1=  new SpannableString(title);
        int colorPrimary = ContextCompat.getColor(
                getContext().getApplicationContext(), R.color.colorPrimary);
        ss1.setSpan(new StyleSpan(Typeface.BOLD), 0, ss1.length(), 0);
        ss1.setSpan(new ForegroundColorSpan(colorPrimary), 0, ss1.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss1;
    }
}
