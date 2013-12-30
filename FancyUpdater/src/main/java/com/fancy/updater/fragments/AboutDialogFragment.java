package com.fancy.updater.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fancy.updater.R;

public class AboutDialogFragment extends DialogFragment {

    private PackageInfo mPackageInfo;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        ViewGroup mViewGroup = (ViewGroup) inflater.inflate(R.layout.dialog_about, null);

        try {
            mPackageInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.v("Error: ", e.toString());
        }

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(mViewGroup)
                .setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        AboutDialogFragment.this.getDialog().cancel();
                    }
                });

        TextView mDialogTitle = (TextView) mViewGroup.findViewById(R.id.about_dialog_title_version);
        mDialogTitle.setText(" v" +mPackageInfo.versionName);
        return builder.create();
    }
}
