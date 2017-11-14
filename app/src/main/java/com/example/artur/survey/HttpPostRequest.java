package com.example.artur.survey;

import android.net.Uri;
import android.os.AsyncTask;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * @author Artur Romasiuk
 */

public class HttpPostRequest extends AsyncTask<String, Void, String> {
    public static final String RATE_ACTION = "RateAction";
    public static final String SEND_ID_ACTION = "SendIDAction";
    public static final String SHARE_ACTION = "ShareAction";


    public HttpPostRequest() {
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected String doInBackground(String... params) {

        String urlString = params[0];
        String androidID = params[1];
        int selectedRating = Float.valueOf(params[2]).intValue();
        String query = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            Uri.Builder builder;
            switch (params[3]) {
                case RATE_ACTION:
                    builder = new Uri.Builder()
                            .appendQueryParameter("appid", androidID)
                            .appendQueryParameter("st", String.valueOf(selectedRating));
                    query = builder.build().getEncodedQuery();
                    break;
                case SEND_ID_ACTION:
                    builder = new Uri.Builder()
                            .appendQueryParameter("appid", androidID);
                    query = builder.build().getEncodedQuery();
                    break;
                case SHARE_ACTION:
                    builder = new Uri.Builder()
                            .appendQueryParameter("appid", androidID)
                            .appendQueryParameter("sh", "1");
                    query = builder.build().getEncodedQuery();
                    break;
            }

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();
            conn.connect();
            return conn.getResponseCode() + " " + conn.getResponseMessage();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Without clear response";
    }
}
