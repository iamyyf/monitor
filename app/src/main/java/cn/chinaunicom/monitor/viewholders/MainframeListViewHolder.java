package cn.chinaunicom.monitor.viewholders;

import android.view.View;
import android.widget.TextView;

import cn.chinaunicom.monitor.R;

/**
 * Created by yfyang on 2017/8/11.
 */

public class MainframeListViewHolder extends BaseViewHolder {

    public TextView appClassName; //应用大类
    public TextView appName;
    public TextView ip;
    public TextView serverType;


    public MainframeListViewHolder(View view) {
        super(view);
        appClassName = (TextView) view.findViewById(R.id.appClassName);
        appName = (TextView) view.findViewById(R.id.appName);
        ip = (TextView) view.findViewById(R.id.ip);
        serverType = (TextView) view.findViewById(R.id.serverType);
    }
}
