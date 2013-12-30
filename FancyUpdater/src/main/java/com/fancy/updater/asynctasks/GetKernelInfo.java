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

import android.os.AsyncTask;

import com.fancy.updater.helper.FancyHelper;

import java.util.ArrayList;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public class GetKernelInfo extends AsyncTask {

    private List<String> result;

    @Override
    protected Object doInBackground(Object[] objects) {
        final String command = "cat /sys/devices/system/cpu/cpu0/cpufreq/";

        List<String> commands = new ArrayList<String>();
        commands.add(command + "scaling_available_frequencies"); // Available frequencies
        commands.add(command + "scaling_available_governors"); // Available governors
        commands.add(command + "scaling_min_freq"); // Min frequency
        commands.add(command + "scaling_max_freq"); // Max frequency
        commands.add(command + "screen_on_min_freq"); // Min Screen on frequency
        commands.add(command + "screen_off_max_freq"); // Max Screen off frequency
        commands.add(command + "scaling_governor"); // Governor

        result = Shell.SU.run(commands);
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        FancyHelper.setAvailable_frequencies(result.get(0).split(" "));
        FancyHelper.setAvailable_governors(result.get(1).split(" "));
        FancyHelper.setMin_freq(result.get(2));
        FancyHelper.setMax_freq(result.get(3));
        FancyHelper.setScreen_on_min(result.get(4));
        FancyHelper.setScreen_off_max(result.get(5));
        FancyHelper.setGovernor(result.get(6));
    }
}