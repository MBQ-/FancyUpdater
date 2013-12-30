package com.fancy.updater.asynctasks;

import android.os.AsyncTask;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public class RunSUCommand extends AsyncTask {

    private String command = null;
    private List<String> commands = null;

    public RunSUCommand(String command) {
        this.command = command;
    }

    public RunSUCommand(List<String> commands) {
        this.commands = commands;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        if (command != null) Shell.SU.run(command);
        if (commands != null) Shell.SU.run(commands);
        return null;
    }
}
