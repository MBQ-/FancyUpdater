package com.fancy.updater.helper;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class JSONParser {

    static InputStream mInputStream = null;
    static JSONObject mJSONObject = null;
    static String mJSON = "";

    public JSONObject getJSONFromUrl(String url) {

        // Making HTTP request
        try {
            DefaultHttpClient mDefaultHttpClient = new DefaultHttpClient();
            HttpGet mHttpGet = new HttpGet(url);

            HttpResponse mHttpResponse = mDefaultHttpClient.execute(mHttpGet);
            HttpEntity mHttpEntity = mHttpResponse.getEntity();
            mInputStream = mHttpEntity.getContent();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader mBufferedReader = new BufferedReader(new InputStreamReader(
                    mInputStream, "iso-8859-1"), 8);
            StringBuilder mStringBuilder = new StringBuilder();
            String mLine;
            while ((mLine = mBufferedReader.readLine()) != null) {
                mStringBuilder.append(mLine + "\n");
            }
            mInputStream.close();
            mJSON = mStringBuilder.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        // try to parse the string to a JSON object
        try {
            mJSONObject = new JSONObject(mJSON);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        return mJSONObject;
    }
}