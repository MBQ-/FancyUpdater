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

package com.fancy.updater.asynctasks;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;

import com.fancy.updater.MainActivity;
import com.fancy.updater.R;
import com.fancy.updater.fragments.UpdateFragment;
import com.fancy.updater.helper.FancyHelper;
import com.fancy.updater.helper.JSONParser;
import com.fancy.updater.helper.SettingsHelper;
import com.fancy.updater.helper.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class KernelUpdateChecker extends AsyncTask {

    private static final String TAG = "Fancy Updater";
    private static final String JELLY_BEAN_MR2 = "http://boypetersen.de/update.php?directory=fancykernel43/";
    private static final String KITKAT = "http://boypetersen.de/update.php?directory=fancykernel44/";

    private Context mContext;
    private UpdateFragment mUpdateFragment;

    private JSONObject mJSONObject = null;

    public KernelUpdateChecker(UpdateFragment mUpdateFragment, Context mContext) {
        this.mUpdateFragment = mUpdateFragment;
        this.mContext = mContext;
        FancyHelper.reset();
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR2)
            mJSONObject = new JSONParser().getJSONFromUrl(JELLY_BEAN_MR2);
        else if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT)
            mJSONObject = new JSONParser().getJSONFromUrl(KITKAT);

        if (mJSONObject != null) {
            try {
                FancyHelper.setKernel_information(mJSONObject);
                FancyHelper.setLatest_version(mJSONObject.getInt("latest_version"));
//                Check for stable version
                if (FancyHelper.getLatest_version() > Utils.getSystemFancyVersionNumber() && !Utils.isFancyTestVersion())
                    FancyHelper.setKernel_url(mJSONObject.getString(Utils.getSystemFancyEdition()));
//                Check for tst build
                else if (!Utils.isFancyTestVersion() && new SettingsHelper(mContext).getCheckTstBuild()) {
                    StringBuilder mInstalledVersion = new StringBuilder(Float.toString(Utils.getSystemFancyVersionNumber()));
                    mInstalledVersion.delete(mInstalledVersion.indexOf("."), mInstalledVersion.length());
                    int tst_version_number = Integer.parseInt(mInstalledVersion.toString()) + 1;
                    if (mJSONObject.getString("tst").contains("pre" + tst_version_number)) {
                        FancyHelper.setKernel_url(mJSONObject.getString("tst"));
                    }
                } else if (Utils.isFancyTestVersion()) {
//                    Check if new stable version is available
                    StringBuilder mStringBuilder = new StringBuilder(String.valueOf(Utils.getSystemFancyVersionNumber()));
                    mStringBuilder.delete(mStringBuilder.indexOf("."), mStringBuilder.length());
                    if (FancyHelper.getLatest_version() >= Integer.parseInt(mStringBuilder.toString()))
                        FancyHelper.setKernel_url(mJSONObject.getString(Utils.getSystemFancyEdition()));
                    else {
//                        Check for new tst build
                        StringBuilder mStringBuilder_tst = new StringBuilder(String.valueOf(Utils.getSystemFancyVersionNumber()));
                        mStringBuilder_tst.replace(mStringBuilder_tst.indexOf("."), mStringBuilder_tst.indexOf(".") + 1, "-");

                        if (!mJSONObject.getString("tst").contains(mStringBuilder_tst.toString())) {
                            FancyHelper.setKernel_url(mJSONObject.getString("tst"));
                        }
                    }
                }

            } catch (JSONException e) {
                Log.v(TAG, e.toString());
                if (e.toString().contains(Utils.getSystemFancyEdition()))
                    FancyHelper.setEdition_not_found(true);
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
//        Update UI
        if (mUpdateFragment != null) {
            if (Utils.isFancyInstalled()) {
                if (FancyHelper.getKernel_url() != null) {
                    mUpdateFragment.mStatusView.setText(mContext.getResources().getString(R.string.update_available));
                    mUpdateFragment.mUpdateButton.setVisibility(View.VISIBLE);
                } else {
                    mUpdateFragment.mStatusView.setText(mContext.getResources().getString(R.string.up_to_date));
                    mUpdateFragment.mUpdateButton.setVisibility(View.INVISIBLE);
                }

//                Update mLatestKernelView | Get latest Kernel version number
                try {
                    int tst_version_number = Integer.parseInt(mJSONObject.get("latest_version").toString()) + 1;
                    if (mJSONObject.getString("tst").contains(String.valueOf(tst_version_number)) && new SettingsHelper(mContext).getCheckTstBuild()) {
                        StringBuilder mStringBuilder = new StringBuilder(mJSONObject.get("tst").toString());
                        mStringBuilder.delete(0, mStringBuilder.indexOf("pre"));
                        mStringBuilder.delete(mStringBuilder.indexOf(".zip"), mStringBuilder.length());
                        mUpdateFragment.mLatestKernelView.setText(mUpdateFragment.getResources().getString(R.string.latest_kernel) + " fancy-" + mStringBuilder);
                    } else
                        mUpdateFragment.mLatestKernelView.setText(mUpdateFragment.getResources().getString(R.string.latest_kernel) + " fancy-r" + FancyHelper.getLatest_version() + "-" + Utils.getSystemFancyEdition());

                } catch (JSONException e) {
                    Log.v(TAG, e.toString());
                }

                if (FancyHelper.isEdition_not_found()) {
                    mUpdateFragment.mStatusView.setText(mContext.getResources().getString(R.string.version_abandoned));
                    mUpdateFragment.mUpdateButton.setText(mContext.getResources().getString(R.string.switch_update_channel));
                    mUpdateFragment.mUpdateButton.setVisibility(View.VISIBLE);
                }

                if (mUpdateFragment.getActivity() != null)
                    mUpdateFragment.getActivity().setProgressBarVisibility(false);
            }
//        Create Notification (only when update available)
            else {
                if (Utils.isFancyInstalled()) {
                    if (FancyHelper.getKernel_url() != null) {
                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(mContext)
                                        .setSmallIcon(R.drawable.ic_launcher)
                                        .setContentTitle(mContext.getResources().getString(R.string.update_available))
                                        .setContentText(mContext.getResources().getString(R.string.notification_text));
                        Intent resultIntent = new Intent(mContext, MainActivity.class);

                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
                        stackBuilder.addParentStack(MainActivity.class);
                        stackBuilder.addNextIntent(resultIntent);
                        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setContentIntent(resultPendingIntent);
                        NotificationManager mNotificationManager =
                                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager.notify(FancyHelper.getNotificationId(), mBuilder.build());
                    }
                }
            }
        }
    }
}