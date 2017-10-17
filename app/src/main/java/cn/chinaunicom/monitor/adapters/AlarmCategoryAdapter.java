package cn.chinaunicom.monitor.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.chinaunicom.monitor.ChinaUnicomApplication;
import cn.chinaunicom.monitor.R;
import cn.chinaunicom.monitor.beans.AlarmCategoryCenterEntity;
import cn.chinaunicom.monitor.beans.AlarmCategoryEntity;
import cn.chinaunicom.monitor.viewholders.AlarmCategoryViewHolder;

/**
 * Created by yfYang on 2017/9/8.
 */

public class AlarmCategoryAdapter extends BaseAdapter {
    private Context context;

    public AlarmCategoryAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return ChinaUnicomApplication.alarmCategoryEntities.size();
    }

    @Override
    public Object getItem(int position) {
        return ChinaUnicomApplication.alarmCategoryEntities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        AlarmCategoryViewHolder viewHolder;
        if (null != convertView) {
            viewHolder = (AlarmCategoryViewHolder)convertView.getTag();
        } else {
            convertView = View.inflate(context, R.layout.list_item_alarm_category, null);
            viewHolder = new AlarmCategoryViewHolder(context, convertView);
            convertView.setTag(viewHolder);
        }

        AlarmCategoryEntity entity = ChinaUnicomApplication.alarmCategoryEntities.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String alarmSendTimeStr = sdf.format(new Date(entity.sendTime));
        String messageContent = entity.mesContent;

        viewHolder.alarmCategoryTitle.setText(entity.title);
        viewHolder.alarmSendTime.setText(alarmSendTimeStr);
        //控制消息显示的长度
        viewHolder.latestAlarm.setText(messageContent);
        viewHolder.badgeView.setBadgeCount(ChinaUnicomApplication.badgeMap.get(entity.title));

        return convertView;
    }
}
