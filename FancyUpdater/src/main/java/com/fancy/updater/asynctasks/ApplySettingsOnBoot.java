package com.fancy.updater.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.fancy.updater.helper.SettingsHelper;

import java.util.ArrayList;
import java.util.List;

public class ApplySettingsOnBoot extends AsyncTask {

    private Context mContext;
    private List<String> commands;

    public ApplySettingsOnBoot(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        commands = new ArrayList<String>();
        SettingsHelper mSettingsHelper = new SettingsHelper(mContext);

        if (mSettingsHelper.getCpuGovernor() != null)
            addCommand(mSettingsHelper.getCpuGovernor(), "scaling_governor");

        if (mSettingsHelper.getCpuMinFreq() != null)
            addCommand(mSettingsHelper.getCpuMinFreq(), "scaling_min_freq");

        if (mSettingsHelper.getCpuMaxFreq() != null)
            addCommand(mSettingsHelper.getCpuMaxFreq(), "scaling_max_freq");

        if (mSettingsHelper.getCpuMaxScreenOffFreq() != null)
            addCommand(mSettingsHelper.getCpuGovernor(), "screen_off_max_freq");

        if (mSettingsHelper.getCpuMinScreenOnFreq() != null)
            addCommand(mSettingsHelper.getCpuGovernor(), "screen_on_min_freq");

        try {
            Thread.sleep(mSettingsHelper.getGracePeriod() * 1000);
        } catch (InterruptedException e) {
            Log.v("Error: ", e.toString());
        }

        new RunSUCommand(commands).execute();
        return null;
    }

    private void addCommand(String value, String file) {
        commands.add("echo " + value + " > " + "/sys/devices/system/cpu/cpu0/cpufreq/" + file);
        commands.add("echo " + value + " > " + "/sys/devices/system/cpu/cpu1/cpufreq/" + file);
    }
}