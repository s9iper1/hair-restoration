package com.byteshaft.hairrestorationcenter.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.StringBuilderPrinter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by husnain on 8/8/16.
 */
public class WebServiceHelpers {

    private static ProgressDialog progressDialog;
    private static final String AND = "&";

    public WebServiceHelpers() {
    }

    public static HttpURLConnection openConnectionForUrl(String targetUrl, String method) throws IOException {
        URL url = new URL(targetUrl);
        System.out.println(targetUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("charset", "utf-8");
        connection.setRequestMethod(method);
        return connection;
    }

    private static void sendRequestData(HttpURLConnection connection, String body) throws IOException {
        byte[] outputInBytes = body.getBytes("UTF-8");
        OutputStream os = connection.getOutputStream();
        os.write(outputInBytes);
        os.close();
    }

    private static JSONObject readResponse(HttpURLConnection connection) throws IOException, JSONException {
        InputStream is = connection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        StringBuilder response = new StringBuilder();

        String line;
        while ((line = rd.readLine()) != null) {
            response.append(line);
            response.append('\r');
        }

        Log.i("TG", response.toString());
        return new JSONObject(response.toString());
    }

    public static JSONObject registerUser(String firstname,
                                          String lastname,
                                          String email,
                                          String phone,
                                          String verifypassword,
                                          String password,
                                          String username,
                                          String zipcode
    ) throws IOException, JSONException {
        StringBuilder builder = new StringBuilder();
        builder.append(AppGlobals.registerUrl);
        builder.append(String.format("email=%s", email));
        builder.append(AND);
        builder.append(String.format("username=%s", username));
        builder.append(AND);
        builder.append(String.format("password=%s", password));
        builder.append(AND);
        builder.append(String.format("repassword=%s", verifypassword));
        builder.append(AND);
        builder.append(String.format("firstname=%s", firstname));
        builder.append(AND);
        builder.append(String.format("lastname=%s", lastname));
        builder.append(AND);
        builder.append(String.format("phone=%s", phone));
        builder.append(AND);
        builder.append(String.format("zip_code=%s", zipcode));
        String data = builder.toString();
        Log.i("LOG", data);
        HttpURLConnection connection = openConnectionForUrl(data, "POST");
        sendRequestData(connection, data);
        AppGlobals.setResponseCode(connection.getResponseCode());
        System.out.println(connection.getResponseCode());
        return readResponse(connection);
    }

    public static JSONObject logInUser(
            String email,
            String password
    ) throws IOException, JSONException {
        StringBuilder builder = new StringBuilder();
        builder.append(AppGlobals.loginUrl);
        builder.append(String.format("user_name=%s", email));
        builder.append(AND);
        builder.append(String.format("password=%s", password));
        String data = builder.toString();
        Log.i("LOG", data);
        HttpURLConnection connection = openConnectionForUrl(data, "POST");
        sendRequestData(connection, data);
        AppGlobals.setResponseCode(connection.getResponseCode());
        System.out.println(connection.getResponseCode());
        return readResponse(connection);
    }

    public static JSONObject forgotPassword(String email) throws IOException, JSONException {
        StringBuilder builder = new StringBuilder();
        builder.append(AppGlobals.forgotPasswordUrl);
        builder.append(String.format("email=%s", email));
        String data = builder.toString();
        Log.i("LOG", data);
        HttpURLConnection connection = openConnectionForUrl(data, "POST");
        sendRequestData(connection, data);
        AppGlobals.setResponseCode(connection.getResponseCode());
        System.out.println(connection.getResponseCode());
        return readResponse(connection);
    }

    public static JSONObject resetPassword(String email, String oldPassword, String newPassword) throws IOException, JSONException {
        StringBuilder builder = new StringBuilder();
        builder.append(AppGlobals.resetPasswordUrl);
        builder.append(String.format("email=%s", email));
        builder.append(AND);
        builder.append(String.format("oldpassword=%s", oldPassword));
        builder.append(AND);
        builder.append(String.format("password=%s", newPassword));
        String data = builder.toString();
        Log.i("LOG", data);
        HttpURLConnection connection = openConnectionForUrl(data, "POST");
        sendRequestData(connection, data);
        AppGlobals.setResponseCode(connection.getResponseCode());
        System.out.println(connection.getResponseCode());
        return readResponse(connection);
    }

    public static JSONObject updateUserProfile(String firstname,
                                          String lastname,
                                          String email,
                                          String phone,
                                          String useid,
                                          String username,
                                          String zipcode
    ) throws IOException, JSONException {
        StringBuilder builder = new StringBuilder();
        builder.append(AppGlobals.updateProfileUrl);
        builder.append(String.format("email=%s", email));
        builder.append(AND);
        builder.append(String.format("username=%s", username));
        builder.append(AND);
        builder.append(String.format("user_id=%s", useid));
        builder.append(AND);
        builder.append(String.format("firstname=%s", firstname));
        builder.append(AND);
        builder.append(String.format("lastname=%s", lastname));
        builder.append(AND);
        builder.append(String.format("phone=%s", phone));
        builder.append(AND);
        builder.append(String.format("zip_code=%s", zipcode));
        String data = builder.toString();
        Log.i("LOG", data);
        HttpURLConnection connection = openConnectionForUrl(data, "POST");
        sendRequestData(connection, data);
        AppGlobals.setResponseCode(connection.getResponseCode());
        System.out.println(connection.getResponseCode());
        return readResponse(connection);
    }


    public static boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)
                AppGlobals.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static boolean isInternetWorking() {
        boolean success = false;

        try {
            URL e = new URL("https://google.com");
            HttpURLConnection connection = (HttpURLConnection) e.openConnection();
            connection.setConnectTimeout(10000);
            connection.connect();
            success = connection.getResponseCode() == 200;
        } catch (IOException var3) {
            var3.printStackTrace();
        }

        return success;
    }

    public static void showProgressDialog(Activity activity, String message) {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    public static void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }

    }
}
