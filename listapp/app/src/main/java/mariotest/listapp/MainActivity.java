package mariotest.listapp;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class MainActivity extends ListActivity {

    private PackageManager packageManager = null;
    private List<ApplicationInfo> applist = null;
    private Vector<ApplicationInstalled> appInstaledlist = new Vector<ApplicationInstalled>();
    private AppAdapter listadapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        packageManager = getPackageManager();
        new LoadApplications().execute();

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        ApplicationInstalled app = (ApplicationInstalled) listadapter.getItem(position);
        app.setIsActive(!app.isActive());
        listadapter.notifyDataSetChanged();
        Log.v("app name",app.getAppInfo().packageName);
    }

    private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
        ArrayList<ApplicationInfo> appList = new ArrayList<ApplicationInfo>();

        for (ApplicationInfo application : list) {
            try {
                Log.v("app name", (application.flags & ApplicationInfo.FLAG_SYSTEM) + " " + application.packageName);
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
            listadapter = new AppAdapter(MainActivity.this, appInstaledlist);
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
            progress = ProgressDialog.show(MainActivity.this, null, "loading apps...");
            super.onPreExecute();
        }
    }
}
