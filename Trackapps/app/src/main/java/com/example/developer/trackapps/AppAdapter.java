package com.example.developer.trackapps;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

/**
 * Created by developer on 10/31/2016.
 */
public class AppAdapter extends BaseAdapter {
    private Activity appContext;
    private List<ApplicationInstalled> appList;
    private LayoutInflater layoutInflater;
    private PackageManager packManager;

    public AppAdapter(Activity context, List<ApplicationInstalled> objects){
        appContext = context;
        appList = objects;
        layoutInflater = (LayoutInflater) appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        packManager = context.getPackageManager();

    }

    @Override
    public int getCount() {
        return appList.size();
    }

    @Override
    public Object getItem(int position) {
        return appList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        CompleteListViewHolder viewHolder;

        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.list_view, null);
            viewHolder = new CompleteListViewHolder(view);
            view.setTag(viewHolder);
        }else {
            viewHolder = (CompleteListViewHolder) view.getTag();
        }
        ApplicationInstalled appInfo = appList.get(position);
        viewHolder.appIcon.setImageDrawable(appInfo.getAppInfo().loadIcon(packManager));
        viewHolder.appName.setText(appInfo.getAppInfo().loadLabel(packManager));
        viewHolder.aSwitch.setChecked(appInfo.isActive());
        return view;
    }


    class CompleteListViewHolder {
        public ImageView appIcon;
        public TextView appName;
        public Switch aSwitch;

        public CompleteListViewHolder(View base) {
            appIcon = (ImageView) base.findViewById(R.id.app_icon);
            appName = (TextView) base.findViewById(R.id.app_name);
            aSwitch = (Switch) base.findViewById(R.id.list_toggle);
        }
    }
}
