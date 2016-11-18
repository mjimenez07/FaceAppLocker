package com.example.developer.trackapps;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AppAdapter extends BaseAdapter implements Filterable {
    private Activity appContext;
    private ArrayList<ApplicationInstalled> appList;
    private ArrayList<ApplicationInstalled> filtredList;
    private LayoutInflater layoutInflater;
    private PackageManager packManager;

    public AppAdapter(Activity context, ArrayList<ApplicationInstalled> objects) {
        appContext = context;
        appList = objects;
        filtredList = objects;
        layoutInflater = (LayoutInflater) appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        packManager = context.getPackageManager();

    }

    @Override
    public int getCount() {
        return filtredList.size();
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
        } else {
            viewHolder = (CompleteListViewHolder) view.getTag();
        }

        ApplicationInstalled appInfo = filtredList.get(position);
        viewHolder.appIcon.setImageDrawable(appInfo.getAppInfo().loadIcon(packManager));
        viewHolder.appName.setText(appInfo.getAppInfo().loadLabel(packManager));
        viewHolder.aSwitch.setChecked(appInfo.isActive());
        return view;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<ApplicationInstalled> appInstled = new ArrayList<>();
                if (TextUtils.isEmpty(constraint)) {
                    results.values = appList;
                    results.count = appList.size();
                } else {
                    for (ApplicationInstalled app : appList) {
                        if (app.getAppInfo().loadLabel(packManager).toString().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            appInstled.add(app);
                        }
                    }
                    results.values = appInstled;
                    results.count = appInstled.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filtredList = (ArrayList<ApplicationInstalled>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
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
