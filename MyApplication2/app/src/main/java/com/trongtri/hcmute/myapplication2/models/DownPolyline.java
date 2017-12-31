package com.trongtri.hcmute.myapplication2.models;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Dell on 12/29/2017.
 */

public class DownPolyline extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... params) {

        StringBuilder stringBuilder = new StringBuilder();
        try {
            URL url = new URL(params[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);


            String line = "";
            while ((line = bufferedReader.readLine()) != null)
            {
                stringBuilder.append(line);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
