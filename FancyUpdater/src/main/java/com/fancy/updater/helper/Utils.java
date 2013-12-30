/*
 * Copyright 2013 Fancy Updater (Boy Petersen & Parthipan Ramesh)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fancy.updater.helper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.fancy.updater.BootReceiver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Utils {

    private static final String TAG = "Fancy Updater";
    private static final int ALARM_ID = 265498;

    public static String getDevice() {
        try {
            Process mProcess = Runtime.getRuntime().exec("getprop " + "ro.product.device");
            BufferedReader mBufferedReader = new BufferedReader(new InputStreamReader(
                    mProcess.getInputStream()));
            StringBuilder mLog = new StringBuilder();
            String line;
            while ((line = mBufferedReader.readLine()) != null) mLog.append(line);
            return mLog.toString();
        } catch (IOException e) {
            Log.v("Runtime Error: ", e.toString());
        }
        return null;
    }

    public static boolean isFancyInstalled() {
        StringBuilder mStringBuilder = new StringBuilder(System.getProperty("os.version"));
        return (mStringBuilder.indexOf("fancy_kernel") != -1);
    }

    public static String getSystemFancyVersion() {
        StringBuilder mStringBuilder = new StringBuilder(System.getProperty("os.version"));
        mStringBuilder.delete(0, mStringBuilder.lastIndexOf("fancy_kernel-") + 13);
        return mStringBuilder.toString();
    }

    public static boolean isFancyTestVersion() {
        StringBuilder mStringBuilder = new StringBuilder(getSystemFancyVersion());
        return (mStringBuilder.indexOf("pre") != -1);
    }

    public static float getSystemFancyVersionNumber() {
        StringBuilder mStringBuilder = new StringBuilder(getSystemFancyVersion());

        if (isFancyTestVersion()) {
            mStringBuilder.delete(mStringBuilder.indexOf("pre"), mStringBuilder.indexOf("pre") + 3);
            mStringBuilder.delete(mStringBuilder.lastIndexOf("-"), mStringBuilder.length());
            mStringBuilder.replace(mStringBuilder.indexOf("-"), mStringBuilder.indexOf("-") + 1, ".");

        } else {
            mStringBuilder.delete(mStringBuilder.indexOf("r"), mStringBuilder.indexOf("r") + 1);
            mStringBuilder.delete(mStringBuilder.indexOf("-"), mStringBuilder.length());
        }

        try {
            return Float.parseFloat(mStringBuilder.toString());
        } catch (NumberFormatException e) {
            Log.v(TAG, e.toString());
            return -1;
        }
    }

    public static String getSystemFancyEdition() {
        StringBuilder mStringBuilder = new StringBuilder(getSystemFancyVersion());
        mStringBuilder.reverse().delete(3, mStringBuilder.length()).reverse();
        return mStringBuilder.toString();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean deleteDirectory(File mFilePath) {
        if (mFilePath.exists()) {
            File[] files = mFilePath.listFiles();
            if (files == null) return true;
            for (File mFile : files) {
                if (mFile.isDirectory()) deleteDirectory(mFile);
                else if (!mFile.delete()) Log.d(TAG, "Couldn't delete directory/file!");
            }
        }
        return (mFilePath.delete());
    }

    public static void setAlarm(Context context, long time) {
        Intent mIntent = new Intent(context, BootReceiver.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent mPendingIntent = PendingIntent.getBroadcast(context, ALARM_ID, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.cancel(mPendingIntent);
        if (time > 0)
            mAlarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), time, mPendingIntent);
    }

    public static void cancelAlarm(Context context) {
        AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent mIntent = new Intent(context, BootReceiver.class);
        PendingIntent mPendingIntent = PendingIntent.getService(context, ALARM_ID, mIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        try {
            mAlarmManager.cancel(mPendingIntent);
        } catch (Exception e) {
            Log.e(TAG, "AlarmManager update was not canceled. " + e.toString());
        }
    }

    public static boolean alarmExists(Context context) {
        return (PendingIntent.getBroadcast(context, ALARM_ID, new Intent(context, BootReceiver.class),
                PendingIntent.FLAG_NO_CREATE) != null);
    }

    public static boolean isOnWifi(Context mContext) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return (mNetworkInfo.isConnected());
    }
}
