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

package com.fancy.updater;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.fancy.updater.activities.SettingsActivity;
import com.fancy.updater.asynctasks.GetKernelInfo;
import com.fancy.updater.asynctasks.KernelUpdateChecker;
import com.fancy.updater.fragments.AboutDialogFragment;
import com.fancy.updater.fragments.CPUFragment;
import com.fancy.updater.fragments.ChangelogFragment;
import com.fancy.updater.fragments.UpdateFragment;
import com.fancy.updater.helper.FancyHelper;
import com.fancy.updater.helper.SettingsHelper;
import com.fancy.updater.helper.Utils;

import org.json.JSONException;

import java.io.File;
import java.util.Iterator;

import eu.chainfire.libsuperuser.Shell;

public class MainActivity extends Activity {

    private static final int CHECK_UPDATES = 0;
    private static final int CHANGELOG = 1;
    private static final int CPU = 2;

    private UpdateFragment mUpdateFragment;
    private ChangelogFragment mChangelogFragment;
    private CPUFragment mCPUFragment;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mDrawerTitles;
    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_PROGRESS);

        if (Shell.SU.available() && !Utils.getDevice().equals("maguro") && !Utils.getDevice().equals("toro") && !Utils.getDevice().equals("toroplus")) {
            Toast.makeText(this, getResources().getString(R.string.unsupported_device), Toast.LENGTH_SHORT).show();
            finish();
        }

        setContentView(R.layout.activity_main);
        mTitle = mDrawerTitle = getTitle();
        mDrawerTitles = getResources().getStringArray(R.array.drawer_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mDrawerTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }

        setProgressBarIndeterminate(true);
        dismissNotification();
    }

    public void startDownload() {
        File mDir = new File(Environment.getExternalStorageDirectory() + "/fancy_updater");
        Utils.deleteDirectory(mDir);
        DownloadManager mDownloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request mRequest = new DownloadManager.Request(
                Uri.parse(FancyHelper.getKernel_url()));
        if (new SettingsHelper(this).getWifiOnly())
            mRequest.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        mRequest.setVisibleInDownloadsUi(false);
        mRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        mRequest.setDestinationInExternalPublicDir("fancy_updater", "fancy-r" + FancyHelper.getLatest_version() + "-" + Utils.getSystemFancyEdition() + ".zip");
        mRequest.setTitle(getResources().getString(R.string.app_name));
        mRequest.setDescription(getResources().getString(R.string.status_updating));
        mDownloadManager.enqueue(mRequest);
    }

    public void check() {
        if (Shell.SU.available() && Utils.getDevice().equals("maguro") || Utils.getDevice().equals("toro") || Utils.getDevice().equals("toroplus")) {
            if (Utils.isNetworkAvailable(this) && !new SettingsHelper(this).getWifiOnly() || Utils.isOnWifi(this)) {
                new KernelUpdateChecker(mUpdateFragment, this).execute();
                setProgressBarVisibility(true);
            } else
                mUpdateFragment.mStatusView.setText(getResources().getString(R.string.no_connection));
            new GetKernelInfo().execute();
        }
    }

    private void dismissNotification() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.cancel(FancyHelper.getNotificationId());
    }

    public void switchUpdateChannel() {
        if (FancyHelper.getKernel_information() != null) {
            final ArrayAdapter<String> update_channels = new ArrayAdapter<String>(this, android.R.layout.simple_selectable_list_item);
            Iterator<?> keys = FancyHelper.getKernel_information().keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                if (!key.equals("latest_version") && !key.equals("tst")) update_channels.add(key);
            }

            AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(this);
            mAlertDialog.setTitle(getResources().getString(R.string.select_update_channel)).setAdapter(update_channels, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        FancyHelper.setKernel_url(FancyHelper.getKernel_information().getString(update_channels.getItem(which)));
                        startDownload();
                        mUpdateFragment.mUpdateButton.setText(getResources().getString(R.string.status_updating));
                        mUpdateFragment.mUpdateButton.setEnabled(false);
                    } catch (JSONException e) {
                        Log.v("JSON error:", e.toString());
                    }
                }
            }).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_refresh).setVisible(mPosition == CHECK_UPDATES || mPosition == CHANGELOG);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch (item.getItemId()) {
            case R.id.action_refresh:
                if (mPosition == CHECK_UPDATES) {
                    mUpdateFragment.mStatusView.setText(getResources().getString(R.string.checking_update));
                    check();
                } else mChangelogFragment.refresh();
                return true;
            case R.id.action_switch_update_channel:
                switchUpdateChannel();
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.action_about:
                new AboutDialogFragment().show(getFragmentManager(), getResources().getString(R.string.about));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void selectItem(int position) {
        Fragment fragment = null;

        switch (position) {
            case CHECK_UPDATES:
                if (mUpdateFragment == null) mUpdateFragment = new UpdateFragment(this);
                fragment = mUpdateFragment;
                break;
            case CHANGELOG:
                if (mChangelogFragment == null) mChangelogFragment = new ChangelogFragment();
                fragment = mChangelogFragment;
                break;
            case CPU:
                if (mCPUFragment == null) mCPUFragment = new CPUFragment();
                fragment = mCPUFragment;
                break;
        }
        mPosition = position;
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mDrawerTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }
}