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
import com.fancy.updater.helper.Utils;

import eu.chainfire.libsuperuser.Shell;

public class RecoveryHelper extends AsyncTask {

    @Override
    protected Object doInBackground(Object[] objects) {
        Shell.SU.run("echo install /sdcard/fancy_updater/fancy-r" + FancyHelper.getLatest_version() + "-" + Utils.getSystemFancyEdition() + ".zip" + " >> /cache/recovery/openrecoveryscript");
        Shell.SU.run("echo wipe cache >> /cache/recovery/openrecoveryscript");
        Shell.SU.run("echo wipe dalvik >> /cache/recovery/openrecoveryscript");
        Shell.SU.run("reboot recovery");
        return null;
    }
}
