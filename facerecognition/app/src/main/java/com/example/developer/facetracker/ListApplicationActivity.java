package com.example.developer.facetracker;

import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class ListApplicationActivity extends ListActivity {
    private static final int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 100;
    private PackageManager packageManager = null;
    private List<ApplicationInfo> applist = null;
    private Vector<ApplicationInstalled> appInstaledlist = new Vector<ApplicationInstalled>();
    private AppAdapter listadapter = null;
    private String listToTrack = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_application);
        packageManager = getPackageManager();
        checkPermissions();
    }

    private void checkPermissions() {
        if (hasPermissions()) {
            new LoadApplications().execute();
        } else {
            requestPermission();
        }
    }

    private void requestPermission() {
        new AlertDialog.Builder(ListApplicationActivity.this)
                .setTitle("Request permissions")
                .setMessage("In order to enable the app work as expected please grant us the permissions to track the app data")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("MainActivity", "resultCode " + resultCode);
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS:
                checkPermissions();
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (hasPermissions()) {
            for (ApplicationInstalled singleApp: appInstaledlist) {
                if (singleApp.isActive()) {
                    listToTrack = listToTrack + singleApp.getAppInfo().packageName + ",";
                }
            }
            SharedPreferences.Editor editor = getEditor(getApplicationContext());
            editor.putString("ListToTrack", listToTrack);
            editor.commit();
            Intent callService = new Intent(this, AppTrackService.class);
            startService(callService);
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        ApplicationInstalled app = (ApplicationInstalled) listadapter.getItem(position);
        app.setIsActive(!app.isActive());
        listadapter.notifyDataSetChanged();
        Log.v("app name",app.getAppInfo().packageName + " enabled to track " + app.isActive());
    }

    private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
        ArrayList<ApplicationInfo> appList = new ArrayList<ApplicationInfo>();

        for (ApplicationInfo application : list) {
            try {
                if (((application.flags & ApplicationInfo.FLAG_SYSTEM) == 0) || (application.packageName.contains("google"))) {
                    appInstaledlist.add(new ApplicationInstalled(application, false));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Collections.sort(appList, new ApplicationInfo.DisplayNameComparator(packageManager));
        return appList;
    }

    private class LoadApplications extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progress = null;

        @Override
        protected Void doInBackground(Void... params) {
            applist = checkForLaunchIntent(packageManager.getInstalledApplications(packageManager.GET_META_DATA));
            listadapter = new AppAdapter(ListApplicationActivity.this, appInstaledlist);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            setListAdapter(listadapter);
            progress.dismiss();
            super.onPostExecute(result);
        }


        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(ListApplicationActivity.this, null, "loading apps...");
            super.onPreExecute();
        }
    }

    //Checking if the app has permission to use usage stats
    private boolean hasPermissions() {
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }


    public SharedPreferences getSharedPrerence(Context context) {
        SharedPreferences shrdprefences = context.getSharedPreferences("AppsToBeBlocked", Context.MODE_PRIVATE);
        return shrdprefences;
    }

    public SharedPreferences.Editor getEditor(Context context) {
        return getSharedPrerence(context).edit();
    }
}
