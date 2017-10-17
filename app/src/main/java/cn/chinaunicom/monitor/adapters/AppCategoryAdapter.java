package cn.chinaunicom.monitor.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.chinaunicom.monitor.R;
import cn.chinaunicom.monitor.beans.AppEntity;
import cn.chinaunicom.monitor.utils.Utils;
import cn.chinaunicom.monitor.viewholders.AppCategoryViewHolder;

/**
 * Created by yfYang on 2017/8/15.
 */

public class AppCategoryAdapter extends BaseAdapter {

    private Context context;
    private List<AppEntity> appEntities;
    private AppCategoryViewHolder viewHolder;

    public AppCategoryAdapter(Context context, List<AppEntity> appEntities) {
        this.context = context;
        if (Utils.isListEmpty(appEntities))
            this.appEntities = new ArrayList<>();
        else
            this.appEntities = new ArrayList<>(appEntities);
    }

    @Override
    public int getCount() {
        return appEntities.size();
    }

    @Override
    public Object getItem(int position) {
        return appEntities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        if (null != convertView) {
            viewHolder = (AppCategoryViewHolder) convertView.getTag();
        } else {
            convertView = View.inflate(context, R.layout.list_item_app_category, null);
            viewHolder = new AppCategoryViewHolder(convertView);
            convertView.setTag(convertView);
        }
        String appName = appEntities.get(position).appName;
        if (Utils.isStringEmpty(appName))
            appName = "其他";

        viewHolder.appCategory.setText(appName);

        return convertView;
    }
}
