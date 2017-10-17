package cn.chinaunicom.monitor.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.chinaunicom.monitor.R;
import cn.chinaunicom.monitor.beans.MainframeEntity;
import cn.chinaunicom.monitor.viewholders.MainframeListViewHolder;

/**
 * Created by yfyang on 2017/8/11.
 */

public class MainframeListAdapter extends BaseAdapter {

    private List<MainframeEntity> mainframeEntityList;
    private MainframeListViewHolder viewHolder;
    private Context context;

    public MainframeListAdapter(Context context, List<MainframeEntity> data) {
        this.mainframeEntityList = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return mainframeEntityList.size();
    }

    @Override
    public Object getItem(int position) {
        return mainframeEntityList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MainframeEntity e = mainframeEntityList.get(position);
        if (null != convertView) {
            viewHolder = (MainframeListViewHolder) convertView.getTag();
        } else {
            convertView = View.inflate(context, R.layout.list_item_mainframe_list, null);
            viewHolder = new MainframeListViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        viewHolder.appClassName.setText(e.appClassName);
        viewHolder.appName.setText(e.appName);
        viewHolder.ip.setText(e.IP);
        viewHolder.serverType.setText(e.serverTypeName);

        return convertView;
    }
}
