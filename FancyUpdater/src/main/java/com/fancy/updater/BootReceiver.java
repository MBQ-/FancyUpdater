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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.fancy.updater.asynctasks.ApplySettingsOnBoot;
import com.fancy.updater.asynctasks.KernelUpdateChecker;
import com.fancy.updater.helper.SettingsHelper;
import com.fancy.updater.helper.Utils;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (new SettingsHelper(context).getApplyOnBoot())
            new ApplySettingsOnBoot(context).execute();
        if (Utils.getDevice().equals("maguro") || Utils.getDevice().equals("toro") || Utils.getDevice().equals("toroplus")) {
            Utils.setAlarm(context, new SettingsHelper(context).getCheckTime()); // 5 hours
            if (Utils.isNetworkAvailable(context))
                new KernelUpdateChecker(null, context).execute();
        }
    }
}
