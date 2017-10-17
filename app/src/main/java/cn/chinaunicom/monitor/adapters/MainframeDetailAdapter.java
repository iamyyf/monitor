package cn.chinaunicom.monitor.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.chinaunicom.monitor.R;
import cn.chinaunicom.monitor.beans.ItemEntity;
import cn.chinaunicom.monitor.utils.Utils;
import cn.chinaunicom.monitor.viewholders.MainframeDetailViewHolder;

/**
 * Created by yfYang on 2017/8/16.
 */

public class MainframeDetailAdapter extends BaseAdapter {

    private Context context;
    private List<ItemEntity> items;
    private MainframeDetailViewHolder viewHolder;

    public MainframeDetailAdapter(Context context, List<ItemEntity> items) {
        this.context = context;
        if (Utils.isListEmpty(items))
            this.items = new ArrayList<>();
        else
            this.items = new ArrayList<>(items);
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
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if (null != convertView) {
            viewHolder = (MainframeDetailViewHolder) convertView.getTag();
        } else {
            convertView = View.inflate(context, R.layout.list_item_mainframe_detail, null);
            viewHolder = new MainframeDetailViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long timeMillionSec = items.get(position).clock;
        String time = sdf.format(new Date(timeMillionSec*1000));

        viewHolder.monitorItem.setText(items.get(position).itemName);
        viewHolder.monitorTime.setText(time);
        viewHolder.monitorData.setText(items.get(position).value + " " +items.get(position).units);

        return convertView;
    }
}
