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
import cn.chinaunicom.monitor.viewholders.DeployListViewHolder;

/**
 * Created by yfyang on 2017/8/14.
 */

public class DeployListAdapter extends BaseAdapter {

    private DeployListViewHolder viewHolder;
    private List<AppEntity> items;
    private Context context;

    public DeployListAdapter(Context context, List<AppEntity> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AppEntity entity = items.get(position);
        if (null != convertView) {
            viewHolder = (DeployListViewHolder) convertView.getTag();
        } else {
            convertView = View.inflate(context, R.layout.list_item_deploy_list, null);
            viewHolder = new DeployListViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

//        viewHolder.category.setText(entity.appHierarchyId);
//        viewHolder.appBelongs.setText(entity.appClassName);
        viewHolder.appName.setText(entity.appName);
        viewHolder.backup.setText(entity.bakInfo);

        return convertView;
    }
}
