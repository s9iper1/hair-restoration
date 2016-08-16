package com.byteshaft.hairrestorationcenter.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.hairrestorationcenter.R;
import com.byteshaft.hairrestorationcenter.utils.AppGlobals;
import com.byteshaft.hairrestorationcenter.utils.List;
import com.byteshaft.hairrestorationcenter.utils.WebServiceHelpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MessagesFragment extends Fragment implements View.OnClickListener {

    private View mBaseView;
    private ImageButton mSendButton;
    private EditText mMessageBody;
    private String mMessageBodyString;
    private String userId;
    private ArrayList<JSONObject> messagesArray;
    private ChatArrayAdapter arrayAdapter;
    private com.byteshaft.hairrestorationcenter.utils.List list;
    private static String sNextUrl = "";
    private static boolean loadMore = false;
    private boolean isScrollingUp = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.messages_frgament, container, false);
        setHasOptionsMenu(true);
        messagesArray = new ArrayList<>();
        mMessageBody = (EditText) mBaseView.findViewById(R.id.et_chat);
        mSendButton = (ImageButton) mBaseView.findViewById(R.id.button_chat_send);
        list = (com.byteshaft.hairrestorationcenter.utils.List) mBaseView.findViewById(R.id.lv_chat);
        mSendButton.setOnClickListener(this);
        userId = AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_USER_ID);
        new ReceiveMessageTask().execute();
        list.setOnDetectScrollListener(new List.OnDetectScrollListener() {
            @Override
            public void onUpScrolling() {
                if (!isScrollingUp && !loadMore) {
                    isScrollingUp = true;
                    if (!sNextUrl.trim().isEmpty()) {
                    }
                }
            }

            @Override
            public void onDownScrolling() {
                isScrollingUp = false;
            }
        });
        new ReceiveMessageTask().execute();
        return mBaseView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_chat_send:
                mMessageBodyString = mMessageBody.getText().toString();
                new SendMessageTask().execute();
        }
    }

    class SendMessageTask extends AsyncTask<String, String, String> {

        private String string;
        private JSONObject jsonObject;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... strings) {
            if (WebServiceHelpers.isNetworkAvailable() && WebServiceHelpers.isInternetWorking()) {
                try {
                    string = WebServiceHelpers.messageSend(
                            mMessageBodyString,
                            userId);
                    Log.e("Send message response", String.valueOf(string));
                    jsonObject = WebServiceHelpers.messageReceive(userId);
                    if (jsonObject.getString("Message").equals("Successfully")) {
                        JSONArray details = jsonObject.getJSONArray("details");
                        for (int i = 0; i < details.length(); i++) {
                            JSONObject json = details.getJSONObject(i);
                            if (!messagesArray.contains(json)) {
                                messagesArray.add(json);
                            }
                        }
                    }
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
            mMessageBody.setText("");
            arrayAdapter.notifyDataSetChanged();
        }
    }

    class ReceiveMessageTask extends AsyncTask<String, String, ArrayList<Integer>> {

        private JSONObject jsonObject;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Integer> doInBackground(String... strings) {
            if (WebServiceHelpers.isNetworkAvailable() && WebServiceHelpers.isInternetWorking()) {
                try {
                    jsonObject = WebServiceHelpers.messageReceive(userId);
                    if (jsonObject.getString("Message").equals("Successfully")) {
                        JSONArray details = jsonObject.getJSONArray("details");
                        for (int i = 0; i < details.length(); i++) {
                            JSONObject json = details.getJSONObject(i);
                            messagesArray.add(json);
                        }
                    }
                    Log.e("TAG", String.valueOf(jsonObject));
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Integer> integers) {
            super.onPostExecute(integers);
            arrayAdapter = new ChatArrayAdapter(AppGlobals.getContext(),R.layout.delegate_chat,messagesArray);
            list.setAdapter(arrayAdapter);

        }
    }


    class ChatArrayAdapter extends ArrayAdapter {

        private ViewHolder holder;
        private ArrayList<JSONObject> data;

        public ChatArrayAdapter(Context context, int resource, ArrayList<JSONObject> data) {
            super(context, resource);
            this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                convertView = inflater.inflate(R.layout.delegate_chat, parent, false);
                holder = new ViewHolder();
                holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
                holder.dateTime = (TextView) convertView.findViewById(R.id.message_date_time);
                holder.userNameSenderReceiver = (TextView) convertView.findViewById(R.id.user_name_sender_or_receiver);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            try {
                holder.messageBody.setText(data.get(position).getString("messege"));
                holder.dateTime.setText(data.get(position).getString("added_time"));
                holder.userNameSenderReceiver.setText(data.get(position).getString("name"));
                if (data.get(position).getInt("received_status") == 1) {
                    holder.messageBody.setBackgroundResource(R.mipmap.chat_bg_s);
                } else {
                    holder.messageBody.setBackgroundResource(R.mipmap.chat_bg_r);
                }
                if (data.get(position).getString("name").equals(AppGlobals.getStringFromSharedPreferences(
                        AppGlobals.KEY_USER_NAME))) {
                    holder.userNameSenderReceiver.setGravity(Gravity.RIGHT);
                } else {
                    holder.userNameSenderReceiver.setGravity(Gravity.LEFT);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return convertView;
        }

        @Override
        public int getCount() {
            return data.size();
        }
    }

    class ViewHolder {
        public TextView messageBody;
        public TextView dateTime;
        public TextView userNameSenderReceiver;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.message_actionbar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.message_actionbar:
        }
        return true;
    }
}
