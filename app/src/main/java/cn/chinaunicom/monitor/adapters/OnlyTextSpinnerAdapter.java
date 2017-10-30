package cn.chinaunicom.monitor.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import cn.chinaunicom.monitor.R;
import cn.chinaunicom.monitor.beans.HostIp;
import cn.chinaunicom.monitor.viewholders.OnlyTextViewHolder;

/**
 * Created by yfYang on 2017/10/30.
 */

public class OnlyTextSpinnerAdapter extends BaseAdapter {

    private Context context;
    private List<String> items;
    private OnlyTextViewHolder viewHolder;

    public OnlyTextSpinnerAdapter(Context context, List<String> items) {
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
        if (null != convertView) {
            viewHolder = (OnlyTextViewHolder) convertView.getTag();
        } else {
           convertView = View.inflate(context, R.layout.my_simple_spinner_item, null);
           viewHolder = new OnlyTextViewHolder(convertView);
           convertView.setTag(viewHolder);
        }

        viewHolder.text.setText(items.get(position));

        return convertView;
    }
}
