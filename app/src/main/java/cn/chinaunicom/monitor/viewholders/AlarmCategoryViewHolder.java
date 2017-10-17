package cn.chinaunicom.monitor.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jauker.widget.BadgeView;

import butterknife.Bind;
import cn.chinaunicom.monitor.R;

/**
 * Created by yfYang on 2017/9/8.
 */

public class AlarmCategoryViewHolder extends BaseViewHolder {

    private Context context;
    public ImageView alarmLogo;
    public TextView alarmCategoryTitle;
    public TextView latestAlarm;
    public TextView alarmSendTime;

    public BadgeView badgeView;

    public AlarmCategoryViewHolder(Context context, View view) {
        super(view);
        this.context = context;
        alarmCategoryTitle = (TextView) view.findViewById(R.id.alarmCategoryTitle);
        alarmLogo = (ImageView) view.findViewById(R.id.alarmLogo);
        latestAlarm = (TextView) view.findViewById(R.id.latestAlarm);
        alarmSendTime = (TextView) view.findViewById(R.id.alarmSendTime);


        badgeView = new BadgeView(context);
        badgeView.setTargetView(alarmLogo);
        badgeView.setBadgeCount(0);
    }
}
