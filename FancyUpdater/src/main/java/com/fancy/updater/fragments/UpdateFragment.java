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

package com.fancy.updater.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fancy.updater.MainActivity;
import com.fancy.updater.R;
import com.fancy.updater.helper.FancyHelper;
import com.fancy.updater.helper.Utils;

public class UpdateFragment extends Fragment {

    private MainActivity mMainActivity;
    public TextView mLatestKernelView;
    public TextView mStatusView;
    public Button mUpdateButton;

    public UpdateFragment(MainActivity mMainActivity) {
        this.mMainActivity = mMainActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_updates, container, false);

        mStatusView = (TextView) rootView.findViewById(R.id.status);
        TextView mInstalledKernelView = (TextView) rootView.findViewById(R.id.installed_kernel);
        mLatestKernelView = (TextView) rootView.findViewById(R.id.latest_kernel);
        mUpdateButton = (Button) rootView.findViewById(R.id.update_button);

        if (Utils.isFancyInstalled()) {
            mStatusView.setText(getResources().getString(R.string.checking_update));
            mInstalledKernelView.setText(getResources().getString(R.string.installed_kernel) + " fancy-" + Utils.getSystemFancyVersion());
        } else {
            mStatusView.setText(getResources().getString(R.string.unsupported_kernel));
            mUpdateButton.setText(getResources().getString(R.string.install_fancy_kernel));
            mUpdateButton.setVisibility(View.VISIBLE);
        }

        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isFancyInstalled() && !FancyHelper.isEdition_not_found()) {
                    mMainActivity.startDownload();
                    mUpdateButton.setText(getResources().getString(R.string.status_updating));
                    mUpdateButton.setEnabled(false);
                } else mMainActivity.switchUpdateChannel();
            }
        });

        mMainActivity.check();
        return rootView;
    }
}
