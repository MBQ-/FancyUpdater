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

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.fancy.updater.asynctasks.RunSUCommand;
import com.fancy.updater.R;
import com.fancy.updater.helper.FancyHelper;
import com.fancy.updater.helper.SettingsHelper;

public class CPUFragment extends Fragment implements View.OnClickListener {

    private SettingsHelper mSettingsHelper;

    private static final String cpu_dir1 = "/sys/devices/system/cpu/cpu0/cpufreq/";
    private static final String cpu_dir2 = "/sys/devices/system/cpu/cpu1/cpufreq/";
    private ArrayAdapter<String> available_frequencies;
    private ArrayAdapter<String> available_governors;

    private EditText cpu_min;
    private EditText cpu_max;
    private EditText cpu_min_screen_on;
    private EditText cpu_max_screen_off;
    private EditText cpu_governor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSettingsHelper = new SettingsHelper(getActivity());
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_cpu,
                container, false);

        cpu_min = (EditText) rootView.findViewById(R.id.cpu_min);
        cpu_max = (EditText) rootView.findViewById(R.id.cpu_max);
        cpu_min_screen_on = (EditText) rootView.findViewById(R.id.cpu_min_screen_on);
        cpu_max_screen_off = (EditText) rootView.findViewById(R.id.cpu_max_screen_off);
        cpu_governor = (EditText) rootView.findViewById(R.id.cpu_governor);

        cpu_governor.setOnClickListener(this);
        cpu_min.setOnClickListener(this);
        cpu_max.setOnClickListener(this);
        cpu_min_screen_on.setOnClickListener(this);
        cpu_max_screen_off.setOnClickListener(this);

        cpu_governor.setText(FancyHelper.getGovernor());
        cpu_min.setText(FancyHelper.getMin_freq());
        cpu_max.setText(FancyHelper.getMax_freq());
        cpu_min_screen_on.setText(FancyHelper.getScreen_on_min());
        cpu_max_screen_off.setText(FancyHelper.getScreen_off_max());

        available_frequencies = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_selectable_list_item);
        for (String cpu_frequency : FancyHelper.getAvailable_frequencies())
            available_frequencies.add(cpu_frequency);

        available_governors = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_selectable_list_item);
        for (String cpu_governor : FancyHelper.getAvailable_governors())
            available_governors.add(cpu_governor);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cpu_governor:
                new AlertDialog.Builder(getActivity()).setTitle(getResources().getString(R.string.cpu_governor)).setAdapter(available_governors, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new RunSUCommand("echo " + available_governors.getItem(which) + " > " + cpu_dir1 + "scaling_governor").execute();
                        new RunSUCommand("echo " + available_governors.getItem(which) + " > " + cpu_dir2 + "scaling_governor").execute();
                        cpu_governor.setText(available_governors.getItem(which));
                        mSettingsHelper.savePreference(SettingsHelper.PROPERTY_CPU_GOVERNOR, available_governors.getItem(which));
                    }
                }).show();
                break;
            case R.id.cpu_min:
                new AlertDialog.Builder(getActivity()).setTitle(getResources().getString(R.string.cpu_min)).setAdapter(available_frequencies, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new RunSUCommand("echo " + available_frequencies.getItem(which) + " > " + cpu_dir1 + "scaling_min_freq").execute();
                        new RunSUCommand("echo " + available_frequencies.getItem(which) + " > " + cpu_dir2 + "scaling_min_freq").execute();
                        cpu_min.setText(available_frequencies.getItem(which));
                        mSettingsHelper.savePreference(SettingsHelper.PROPERTY_CPU_MIN_FREQ, available_frequencies.getItem(which));
                    }
                }).show();
                break;
            case R.id.cpu_max:
                new AlertDialog.Builder(getActivity()).setTitle(getResources().getString(R.string.cpu_max)).setAdapter(available_frequencies, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new RunSUCommand("echo " + available_frequencies.getItem(which) + " > " + cpu_dir1 + "scaling_max_freq").execute();
                        new RunSUCommand("echo " + available_frequencies.getItem(which) + " > " + cpu_dir2 + "scaling_max_freq").execute();
                        cpu_max.setText(available_frequencies.getItem(which));
                        mSettingsHelper.savePreference(SettingsHelper.PROPERTY_CPU_MAX_FREQ, available_frequencies.getItem(which));
                    }
                }).show();
                break;
            case R.id.cpu_min_screen_on:
                new AlertDialog.Builder(getActivity()).setTitle(getResources().getString(R.string.cpu_min_screen_on)).setAdapter(available_frequencies, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new RunSUCommand("echo " + available_frequencies.getItem(which) + " > " + cpu_dir1 + "screen_on_min_freq").execute();
                        new RunSUCommand("echo " + available_frequencies.getItem(which) + " > " + cpu_dir2 + "screen_on_min_freq").execute();
                        cpu_min_screen_on.setText(available_frequencies.getItem(which));
                        mSettingsHelper.savePreference(SettingsHelper.PROPERTY_CPU_MIN_SCREEN_ON_FREQ, available_frequencies.getItem(which));
                    }
                }).show();
                break;
            case R.id.cpu_max_screen_off:
                new AlertDialog.Builder(getActivity()).setTitle(getResources().getString(R.string.cpu_max_screen_off)).setAdapter(available_frequencies, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new RunSUCommand("echo " + available_frequencies.getItem(which) + " > " + cpu_dir1 + "screen_off_max_freq").execute();
                        new RunSUCommand("echo " + available_frequencies.getItem(which) + " > " + cpu_dir2 + "screen_off_max_freq").execute();
                        cpu_max_screen_off.setText(available_frequencies.getItem(which));
                        mSettingsHelper.savePreference(SettingsHelper.PROPERTY_CPU_MAX_SCREEN_OFF_FREQ, available_frequencies.getItem(which));
                    }
                }).show();
                break;
        }
    }
}
